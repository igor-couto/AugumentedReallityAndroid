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
    Sensor rotationVectorSensor;
    float[] vectorRotation = new float[5];
    float orientation[] = new float[3];

    public CamGLSurfaceView (Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser( 8, 8, 8, 8, 16, 0);
        mRenderer = new MyRender(context);
        setRenderer(mRenderer);

        mSensorManager = (SensorManager)getContext().getSystemService(getContext().SENSOR_SERVICE);
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

        if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            vectorRotation = Filters.lowPass(event.values, vectorRotation);
            float[] mRotationMatrix = new float[16]; // Criar apenas uma vez
            SensorManager.getRotationMatrixFromVector(mRotationMatrix, vectorRotation);

            SensorManager.getOrientation(mRotationMatrix, orientation);//Get yaw/pitch/roll from matrix
            float pitch = orientation[1];
            float roll = orientation[2];
            float azimuth = orientation[0];
            mRenderer.setRotation(pitch, roll, azimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}