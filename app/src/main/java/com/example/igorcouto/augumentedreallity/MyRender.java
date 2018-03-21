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

    private final Context context;
    private Camera camera;
    private float cameraRatio = 1.0f;
    private boolean updateSurface = false;
    private SurfaceTexture mSurface;
    private long mLastTime;

    private float rotationViewCamera = 0;
    private float[] cameraQuaternion= new float[4];

    // Matrices:
    private float[] mSTMatrix = new float[16]; // What is this?

    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] screenViewMatrix = new float[16];

    private float[] cameraMatrix = new float[16];
    private float[] cameraRotationMatrix = new float[16];

    // Drawable objects:
    private Triangle triangle;
    private MarioHat marioHat;
    private ScreenCamera screenCamera;
    private Axis axis;


    float pitch = 0f;
    float roll = 0f;
    float azimuth = 0f;

    private float[] mRotXMatrix = new float[16];
    private float[] mRotYMatrix = new float[16];
    private float[] mRotZMatrix = new float[16];

    private float[] mTempMatrix = new float[16];

//endregion

    public MyRender(Context context) {this.context = context;}

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig config) {

        Shaders.getInstance().loadShaders();

        axis = new Axis();
        triangle = new Triangle();
        // TODO: não passar o context na inicializacao do obj
        marioHat = new MarioHat(context);
        
        //marioHat.Move(0f,0f,-150f);

        marioHat.Move(0f,0f,-5f);
        triangle.Move(5.0f, 0.0f, 0.0f);


        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glFrontFace(GLES20.GL_CCW);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // TODO: testar este metodo
        //GLES20.glDepthFunc(GLES20.GL_LESS); // O que é isso? Testar

        //TODO o que é isso?
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glClearColor( 0.1f, 0.1f, 0.1f, 1.0f );
        Matrix.setLookAtM ( viewMatrix, 0,
                0f, 0f , 5f,    // EYE
                0f, 0f , 0f,    // CENTER
                0f, 1f , 0f );  // UP

        Matrix.setLookAtM(screenViewMatrix, 0, 0, 0, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.setIdentityM(cameraMatrix,0);
        Matrix.invertM(cameraMatrix, 0, viewMatrix, 0);

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
        screenCamera.Draw(screenViewMatrix, projectionMatrix);

        //region METODO NOVO

        //Create a rotation matrix for x, y, z axis (orientation values are in Radians) 57 is 180/pi
        Matrix.setRotateM(mRotXMatrix, 0, pitch*57, 1.0f, 0.0f, 0.0f);//Rotate the model: Pitch (X axis)
        Matrix.setRotateM(mRotYMatrix, 0, azimuth*57, 0.0f, 1.0f, 0.0f);//Rotate the model: Yawl (Y axis)
        Matrix.setRotateM(mRotZMatrix, 0, roll*57,0.0f, 0.0f, 1.0f);//Rotate the model: Roll (Z axis)


        //Set camera like the default position, with 2.0f for Z instead of 1.0f
        Matrix.setLookAtM(  viewMatrix, 0,
                            0.0f, 0.0f, 0.0f,   // Eye (Camera position)
                            0.0f, 0.0f, 0.0f,   // Center
                            0.0f, 1.0f, 0.0f);  // Up




        Matrix.multiplyMM(viewMatrix, 0, mRotYMatrix, 0, mRotXMatrix, 0);//multiply X by Y rotation
        mTempMatrix= viewMatrix.clone();// We should avoid using same matrix for source and destination
        Matrix.multiplyMM(viewMatrix, 0, mRotZMatrix, 0, mTempMatrix, 0);//multiply the result by Z rotation
        mTempMatrix = viewMatrix.clone();//Save last rotation combining


        Matrix.rotateM(viewMatrix, 0, 90,1f,0f,0f);

        marioHat.Draw(viewMatrix, projectionMatrix);

        //marioHat.Draw(mRotXMatrix, mRotYMatrix, mRotZMatrix, viewMatrix, projectionMatrix);


         //endregion




        // METODO QUATERNION
        //Matrix.multiplyMM(vp, 0, projectionMatrix, 0, viewMatrix, 0);
        //Matrix.setRotateM(mCube.mModelMatrix, 0, (float) ((2.0f * Math.acos(quat[0]) * 180.0f) / Math.PI), quat[1], quat[2], quat[3]);


        //marioHat.Rotate((float) ((2.0f * Math.acos(cameraQuaternion[0]) * 180.0f) / Math.PI), cameraQuaternion[1], cameraQuaternion[2], cameraQuaternion[3]);
        //triangle.Rotate((float) ((2.0f * Math.acos(cameraQuaternion[0]) * 180.0f) / Math.PI), cameraQuaternion[1], cameraQuaternion[2], cameraQuaternion[3]);

       //Matrix.rotateM(viewMatrix, 0, (float) ((2.0f * Math.acos(cameraQuaternion[0]) * 180.0f) / Math.PI), cameraQuaternion[1], cameraQuaternion[2], cameraQuaternion[3]);

        //marioHat.Draw(cameraRotationMatrix , viewMatrix, projectionMatrix);
        triangle.Draw(cameraRotationMatrix ,viewMatrix, projectionMatrix);
        //axis.Draw(viewMatrix,projectionMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        ajustCameraView(width, height);

        GLES20.glViewport(0, 0, width, height);
        float mRatio = (float) width / height;
        /*
        //Matrix.frustumM(projectionMatrix, 0, -mRatio, mRatio, -1, 1, 3, 1000);
        Matrix.frustumM(    projectionMatrix, 0,
                            -1, 1, -1, 1,          // Left Right Bottom Top
                            1f, 1000);            // near, far
        */

        // Duas opcoes: Iniciar a projection com frustrumM ou perspectiveM
        // https://stackoverflow.com/questions/8891289/opengl-es-2-0-camera-issues
        float near = 1.0f;
        float far = 1000.0f;
        Matrix.perspectiveM(projectionMatrix, 0, 60.0f, mRatio, near, far);

    }

    public void ajustCameraView(int width, int height){

        cameraRatio = (float)width/height;
        screenCamera.setCameraRatio( cameraRatio );

        Camera.Parameters parameters = camera.getParameters();
        //TODO: Remove this print
        Print.showSupportedResolutions(parameters);

        Display display = ((WindowManager)context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0) {
            Log.d("SURFACE ROTATION", "Surface.ROTATION_0");
            rotationViewCamera = 270;
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

    public void setRotationMatrix(float[] cameraRotationMatrix){
        this.cameraRotationMatrix = cameraRotationMatrix;
        Matrix.rotateM(cameraRotationMatrix,0, 90f,1,0,0);
        //Matrix.multiplyMM(viewMatrix, 0, viewMatrix, 0, cameraRotationMatrix, 0);
    }

    public void setRotationMatrix(float x, float y, float z) {

        Matrix.rotateM(cameraMatrix,0,x,1,0,0);
        Matrix.rotateM(cameraMatrix,0,y,0,1,0);
        Matrix.rotateM(cameraMatrix,0,z,0,0,1);

        Matrix.invertM(viewMatrix,0,cameraMatrix,0);
    }

    public void novoSetRotation(float pitch, float roll, float azimuth){
        this.pitch = pitch;
        this.roll = roll;
        this.azimuth = azimuth;
    }

    // Metodo pitch yaw azim
    public void setRotation(float pitch, float azimuth, float yaw){
        float cameraLookAtX = 0f + ( float ) Math.cos( Math.toRadians( yaw ) ) * ( float ) Math.cos( Math.toRadians( pitch ) );
        float cameraLookAtY = 0f - ( float ) Math.sin( Math.toRadians( pitch ) );
        float cameraLookAtZ = 5f + ( float ) Math.cos( Math.toRadians( pitch ) ) * ( float ) Math.sin( Math.toRadians( yaw ) );
        Matrix.setLookAtM ( viewMatrix, 0,
                            0f, 0f, 5f,
                            cameraLookAtX, cameraLookAtY, cameraLookAtZ,
                            0f, 1.0f, 0.0f);
    }

    public void setQuaternion(float[] quaternion){
        this.cameraQuaternion = quaternion;
    }
}