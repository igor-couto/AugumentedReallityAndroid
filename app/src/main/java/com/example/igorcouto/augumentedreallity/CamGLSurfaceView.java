package com.example.igorcouto.augumentedreallity;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.example.igorcouto.augumentedreallity.Util.Filters;

import static android.content.Context.WINDOW_SERVICE;

public class CamGLSurfaceView extends GLSurfaceView implements SensorEventListener {

    MyRender mRenderer;
    Camera mCamera;

    SensorManager mSensorManager;
    Sensor acelerometerSensor;
    Sensor magneticSensor;
    Sensor rotationVectorSensor;

    float[] gravity = new float[3];
    float[] geomag = new float[3];
    float[] vectorRotation = new float[5];
    float[] inclination = new float[16]; // necessario?

    public static float x; //pitch
    public static float y; //roll
    public static float z; //azimuth
    float orientation[] = new float[3];
    private final float rad2deg = 180 / (float) Math.PI;
    float[] inOrientMatrix = new float[16];
    float[] outOrientMatrix= new float[16];

    public CamGLSurfaceView (Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser( 8, 8, 8, 8, 16, 0);
        mRenderer = new MyRender(context);
        setRenderer(mRenderer);

        mSensorManager = (SensorManager)getContext().getSystemService(getContext().SENSOR_SERVICE);
        acelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
        mCamera.release();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        mSensorManager.registerListener(this, acelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);

        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();

        // No changes to default camera parameters
        mCamera.setParameters( parameters );
        queueEvent(new Runnable(){
            public void run() {
                mRenderer.setCamera(mCamera);
            }});
        super.onResume();
    }

    @Override
    public void onSensorChanged (SensorEvent event) {

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        /*
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            final float[] accelerationVector = event.values;
            queueEvent(new Runnable() {
                public void run() {
                    mRenderer.setAcceleration(accelerationVector);
                }
            });
        }
        */
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                //gravity = event.values.clone();
                gravity = Filters.lowPass(event.values, gravity);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                //geomag = event.values.clone();
                geomag = Filters.lowPass(event.values, geomag);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                vectorRotation = Filters.lowPass(event.values, vectorRotation);
                float[] mRotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
                mRenderer.setRotationMatrix(mRotationMatrix);
                break;
        }

        if (gravity != null && geomag != null) {
            SensorManager.getInclination(inclination);
            if (SensorManager.getRotationMatrix(inOrientMatrix, inclination, gravity, geomag)) {

                // Using the camera (Y axis along the camera's axis) for an augmented reality application where the rotation angles are needed:
                SensorManager.remapCoordinateSystem(inOrientMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, outOrientMatrix);

                //Using the device as a mechanical compass when rotation is Surface.ROTATION_90:
                //SensorManager.remapCoordinateSystem(inOrientMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outOrientMatrix);

                SensorManager.getOrientation(outOrientMatrix, orientation);
                x = orientation[1] * rad2deg; //pitch
                y = orientation[0] * rad2deg; //azimuth
                z = orientation[2] * rad2deg; //roll
                /*
                Log.d("ORIENTACAO","pitch: " + String.valueOf(orientation[1] * rad2deg));
                Log.d("ORIENTACAO","azimuth: " + String.valueOf(orientation[0] * rad2deg));
                Log.d("ORIENTACAO","roll: " + String.valueOf(orientation[2] * rad2deg) + "\n");

                */
                //mRenderer.setRotation(x ,y, z);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}