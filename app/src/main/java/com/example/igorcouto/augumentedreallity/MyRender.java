package com.example.igorcouto.augumentedreallity;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import java.io.IOException;
import static android.content.Context.WINDOW_SERVICE;
import com.example.igorcouto.augumentedreallity.Util.Print;

public class MyRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

//region Variables

    private final float RADIAN_TO_DEGREE = (float) (Math.PI/180.0d);

    private final Context context;
    private Camera camera;
    private float cameraRatio = 1.0f;
    private boolean updateSurface = false;
    private SurfaceTexture mSurface;
    private long mLastTime;

    // Matrices:
    private float[] mSTMatrix = new float[16]; // What is this?

    //private float[] projectionMatrix = new float[16];
    //private float[] viewMatrix = new float[16];
    private float[] screenViewMatrix = new float[16];

    private float[] mRotXMatrix = new float[16];
    private float[] mRotYMatrix = new float[16];
    private float[] mRotZMatrix = new float[16];

    //private float[] mTempMatrix = new float[16];

    // Drawable objects:
    private MarioHat marioHat;
    private Mesh meshMarioHat;
    private ScreenCamera screenCamera;

    // World Camera Object:
    private WorldCamera worldCamera;

    float pitch = 0f;
    float roll = 0f;
    float azimuth = 0f;

//endregion

    public MyRender(Context context) {this.context = context;}

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig config) {

        Shaders.getInstance().loadShaders();

        meshMarioHat = new Mesh("hat_mario_model.obj");
        meshMarioHat.move(0f,0f,-5f);

        marioHat = new MarioHat();
        marioHat.Move(0f,0f,-5f);

        /*
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glFrontFace(GLES20.GL_CCW);
         */

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // TODO: testar este metodo
        //GLES20.glDepthFunc(GLES20.GL_LESS);

        //TODO o que é isso?
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glClearColor( 0.1f, 0.1f, 0.1f, 1.0f );

        worldCamera = new WorldCamera();

        /*
        Matrix.setLookAtM ( viewMatrix, 0,
                0f, 0f , 5f,    // EYE
                0f, 0f , 0f,    // CENTER
                0f, 1f , 0f );  // UP
        */

        Matrix.setLookAtM(screenViewMatrix, 0, 0, 0, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        screenCamera = new ScreenCamera();

        // Texture stuff
        mSurface = new SurfaceTexture( screenCamera.getTextureID() );
        mSurface.setOnFrameAvailableListener(this);
        try {
            camera.setPreviewTexture(mSurface);
        } catch (IOException t) {
            Log.e("ERRO", "Cannot set preview texture target!");
        }
        /* Start the camera */
        camera.startPreview();
        mLastTime = 0;

        screenCamera.setCameraRatio( cameraRatio );

        synchronized(this) {
            updateSurface = false;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        ajustCameraView(width, height);

        GLES20.glViewport(0, 0, width, height);
        float mRatio = (float) width / height;

        // Duas opcoes: Iniciar a projection com frustrumM ou perspectiveM
        // https://stackoverflow.com/questions/8891289/opengl-es-2-0-camera-issues
        /*
        //Matrix.frustumM(projectionMatrix, 0, -mRatio, mRatio, -1, 1, 3, 1000);
        Matrix.frustumM(    projectionMatrix, 0,
                            -1, 1, -1, 1,          // Left Right Bottom Top
                            1f, 1000);            // near, far
        */

        /*
        float near = 1.0f;
        float far = 1000.0f;
        Matrix.perspectiveM(projectionMatrix, 0, 60.0f, mRatio, near, far);
        */
        worldCamera.setRatio(mRatio);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT  | GLES20.GL_DEPTH_BUFFER_BIT);

        synchronized(this) {
            if (updateSurface) {
                mSurface.updateTexImage();
                mSurface.getTransformMatrix(mSTMatrix);
                //long timestamp = mSurface.getTimestamp();
                updateSurface = false;
            }
        }

        // Textura de fundo com a imagem da camera
        screenCamera.Draw(screenViewMatrix, worldCamera.getProjectionMatrix() );

        //Create a rotation matrix for x, y, z axis (orientation values are in Radians) 57 is 180/pi
        Matrix.setRotateM(mRotXMatrix, 0, pitch   * RADIAN_TO_DEGREE, 1.0f, 0.0f, 0.0f); //Rotate the model: Pitch (X axis)
        Matrix.setRotateM(mRotYMatrix, 0, azimuth * RADIAN_TO_DEGREE, 0.0f, 1.0f, 0.0f); //Rotate the model: Yawl  (Y axis)
        Matrix.setRotateM(mRotZMatrix, 0, roll    * RADIAN_TO_DEGREE, 0.0f, 0.0f, 1.0f); //Rotate the model: Roll  (Z axis)


        worldCamera.rotate(mRotXMatrix , mRotYMatrix , mRotZMatrix);

        /*
        //Set camera like the default position, with 2.0f for Z instead of 1.0f
        Matrix.setLookAtM(  viewMatrix, 0,
                            0.0f, 0.0f, 0.0f,   // Eye (Camera position)
                            0.0f, 0.0f, 0.0f,   // Center
                            0.0f, 1.0f, 0.0f);  // Up

        Matrix.multiplyMM(viewMatrix, 0, mRotYMatrix, 0, mRotXMatrix, 0);//multiply X by Y rotation
        mTempMatrix= viewMatrix.clone();// We should avoid using same matrix for source and destination
        Matrix.multiplyMM(viewMatrix, 0, mRotZMatrix, 0, mTempMatrix, 0);//multiply the result by Z rotation
        mTempMatrix = viewMatrix.clone();//Save last rotation combining

        // TODO Do I have to rotate my view matrix to make things go in front of the device?
        Matrix.rotateM(viewMatrix, 0, 90,1f,0f,0f);
        */
        marioHat.Draw( worldCamera.getViewMatrix() ,worldCamera.getProjectionMatrix() );
        meshMarioHat.draw( worldCamera.getViewMatrix() ,worldCamera.getProjectionMatrix() );
    }

    //TODO: Esse metodo nao deveria estar aqui
    public void ajustCameraView(int width, int height){

        cameraRatio = (float)width/height;
        Log.d("CAMERA", "Camera Ratio is: " + cameraRatio);
        screenCamera.setCameraRatio( cameraRatio );

        Camera.Parameters parameters = camera.getParameters();
        //TODO: Remove this print
        Print.showSupportedResolutions(parameters);

        Display display = ((WindowManager)context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0) {
            Log.d("SURFACE ROTATION", "Surface.ROTATION_0");
            float rotationViewCamera = 270;
            screenCamera.Rotate(rotationViewCamera);
            //parameters.setPreviewSize(height, width);
            camera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90) {
            Log.d("SURFACE ROTATION", "Surface.ROTATION_90");
            //parameters.setPreviewSize(width, height);
        }

        if(display.getRotation() == Surface.ROTATION_180) {
            Log.d("SURFACE ROTATION", "Surface.ROTATION_180");
            //parameters.setPreviewSize(height, width);
        }

        if(display.getRotation() == Surface.ROTATION_270) {
            Log.d("SURFACE ROTATION", "Surface.ROTATION_270");
            //parameters.setPreviewSize(width, height);
            camera.setDisplayOrientation(180);
        }

        camera.setParameters(parameters);
        setCamera(camera);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        cameraRatio = (float)previewSize.width/previewSize.height;
        if ( screenCamera != null)
            screenCamera.setCameraRatio( cameraRatio );
    }

    public void setAcceleration(float[] acceleration) {
        //this.acceleration = acceleration;
    }

    public void setRotation(float pitch, float roll, float azimuth){
        this.pitch = pitch;
        this.roll = roll;
        this.azimuth = azimuth;
    }
}