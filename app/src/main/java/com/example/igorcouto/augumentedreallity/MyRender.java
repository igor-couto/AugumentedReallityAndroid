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

//endregion

    public MyRender(Context context) {this.context = context;}

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig config) {

        Shaders.getInstance().loadShaders();

        triangle = new Triangle();
        marioHat = new MarioHat(context);
        
        marioHat.Move(0f,0f,-150f);
        triangle.Move(10.0f, 0.0f, -1.0f);

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

        // METODO QUATERNION
        //Matrix.multiplyMM(vp, 0, projectionMatrix, 0, viewMatrix, 0);
        //Matrix.setRotateM(mCube.mModelMatrix, 0, (float) ((2.0f * Math.acos(quat[0]) * 180.0f) / Math.PI), quat[1], quat[2], quat[3]);


        //marioHat.Rotate((float) ((2.0f * Math.acos(cameraQuaternion[0]) * 180.0f) / Math.PI), cameraQuaternion[1], cameraQuaternion[2], cameraQuaternion[3]);
        //triangle.Rotate((float) ((2.0f * Math.acos(cameraQuaternion[0]) * 180.0f) / Math.PI), cameraQuaternion[1], cameraQuaternion[2], cameraQuaternion[3]);

        Matrix.rotateM(viewMatrix, 0, (float) ((2.0f * Math.acos(cameraQuaternion[0]) * 180.0f) / Math.PI), cameraQuaternion[1], cameraQuaternion[2], cameraQuaternion[3]);

        marioHat.Draw(cameraRotationMatrix , viewMatrix, projectionMatrix);
        triangle.Draw(cameraRotationMatrix ,viewMatrix, projectionMatrix);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        ajustCameraView(width, height);

        GLES20.glViewport(0, 0, width, height);
        float mRatio = (float) width / height;
        //Matrix.frustumM(projectionMatrix, 0, -mRatio, mRatio, -1, 1, 3, 1000);
        Matrix.frustumM(projectionMatrix, 0, -1, 1, -1, 1, 3, 1000);
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