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

import com.example.igorcouto.augumentedreallity.Util.ObjLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class MyRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private final Context context;
    private Camera mCamera;
    private float mCameraRatio = 1.0f;
    private boolean updateSurface = false;
    private SurfaceTexture mSurface;
    private long mLastTime;

    private float rotationViewCamera = 0;

    // QUE MATRIZ É ESSA?
    private float[] mSTMatrix = new float[16];

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mScreenViewMatrix = new float[16];

    private float[] mCameraMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];


    private Triangle myTriangle;
    private TriangleGray macaco;
    private ScreenCamera screenCamera;

    float tempX = 0f;


    public MyRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig config) {

        Shaders.getInstance().loadShaders();

        myTriangle = new Triangle();

        macaco = new TriangleGray (context);
        macaco.Move(0f,0f,-5f);

        //myTriangle.Move(0.5f, 0.0f, -1.0f);

        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glCullFace(GLES20.GL_BACK);
        //GLES20.glFrontFace(GLES20.GL_CCW);

        //TODO o que é isso?
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glClearColor( 0.3f, 0.3f, 0.3f, 1.0f );
        Matrix.setLookAtM ( mViewMatrix, 0,
                0f, 0f , 5f,    // EYE
                0f, 0f , 0f,    //CENTER
                0f, 1f , 0f );  //UP

        Matrix.setLookAtM(mScreenViewMatrix, 0, 0, 0, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.setIdentityM(mCameraMatrix,0);
        Matrix.invertM(mCameraMatrix, 0, mViewMatrix, 0);

        screenCamera = new ScreenCamera();

        // Texture stuff
        mSurface = new SurfaceTexture( screenCamera.getTextureID() );
        mSurface.setOnFrameAvailableListener(this);
        try {
            mCamera.setPreviewTexture(mSurface);
        } catch (IOException t) {
            Log.e("ERRO", "Cannot set preview texture target!");
        }
        /* Start the camera */
        mCamera.startPreview();
        mLastTime = 0;

        screenCamera.setCameraRatio( mCameraRatio );

        synchronized(this) {
            updateSurface = false;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT );

        synchronized(this) {
            if (updateSurface) {
                mSurface.updateTexImage();
                mSurface.getTransformMatrix(mSTMatrix);
                //long timestamp = mSurface.getTimestamp();
                updateSurface = false;
            }
        }

        // Textura de fundo com a imagem da camera
        screenCamera.Draw(mScreenViewMatrix, mProjectionMatrix);


        // Triangulo
        //Matrix.setIdentityM(mViewMatrix, 0);
        //Matrix.rotateM(mViewMatrix,0,tempX,1,0,0);
        //tempX += 0.1f;
        /*
        Matrix.setLookAtM ( mViewMatrix, 0,
                            0 , 0   , 5f,       // EYE
                            0f, 0f  , 0f,       //CENTER
                            0f, 1.0f, 0.0f );   //UP
        */

        macaco.Draw(mRotationMatrix , mViewMatrix, mProjectionMatrix);

        //myTriangle.Draw(mRotationMatrix ,mViewMatrix, mProjectionMatrix);

        //myTriangle.Move(0f,0f,-0.1f);
        //axis.Draw(mViewMatrix, mProjectionMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        ajustCameraView(width, height);

        GLES20.glViewport(0, 0, width, height);
        float mRatio = (float) width / height;
        //Matrix.frustumM(mProjectionMatrix, 0, -mRatio, mRatio, -1, 1, 3, 1000);
        Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -1, 1, 3, 1000);
    }

    public void ajustCameraView(int width, int height){

        mCameraRatio = (float)width/height;
        screenCamera.setCameraRatio( mCameraRatio );

        Camera.Parameters parameters = mCamera.getParameters();

        // TODO: ERASE LATER
        //Log.d("CURRENT SIZE", "Current Height: " + height + " Current Width: " + width);
        List<Camera.Size> allSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : allSizes ) {
            //Log.d("SUPPORTED SIZES", "Supported Size: " + "Height: " + size.height + " Width: " + size.width + " Aspect Ratio: " + (float) size.width/size.height);
        }
        // TODO: END ERASE

        Display display = ((WindowManager)context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0) {
            Log.d("SURFACE ROTATION", "Surface.ROTATION_0");
            rotationViewCamera = 270;
            screenCamera.Rotate(rotationViewCamera);
            //parameters.setPreviewSize(height, width);
            mCamera.setDisplayOrientation(90);
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
            mCamera.setDisplayOrientation(180);
        }

        mCamera.setParameters(parameters);

        setCamera(mCamera);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        //Log.d("AQUIIIII", "camera width: "+ previewSize.width + " Camera height: " + previewSize.height);
        mCameraRatio = (float)previewSize.width/previewSize.height;
        if ( screenCamera != null)
            screenCamera.setCameraRatio( mCameraRatio );
    }

    public void setAcceleration(float[] acceleration) {
        //this.acceleration = acceleration;
    }

    public void setRotationMatrix(float[] mRotationMatrix){
        this.mRotationMatrix = mRotationMatrix;
        Matrix.rotateM(mRotationMatrix,0, 90f,1,0,0);
        //Matrix.multiplyMM(mViewMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0);
    }

    public void setRotationMatrix(float x, float y, float z) {

        Matrix.rotateM(mCameraMatrix,0,x,1,0,0);
        Matrix.rotateM(mCameraMatrix,0,y,0,1,0);
        Matrix.rotateM(mCameraMatrix,0,z,0,0,1);

        Matrix.invertM(mViewMatrix,0,mCameraMatrix,0);
    }

    public void setRotation(float pitch, float azimuth, float yaw){
        float cameraLookAtX = 0f + ( float ) Math.cos( Math.toRadians( yaw ) ) * ( float ) Math.cos( Math.toRadians( pitch ) );
        float cameraLookAtY = 0f - ( float ) Math.sin( Math.toRadians( pitch ) );
        float cameraLookAtZ = 5f + ( float ) Math.cos( Math.toRadians( pitch ) ) * ( float ) Math.sin( Math.toRadians( yaw ) );
        Matrix.setLookAtM ( mViewMatrix, 0,
                            0f, 0f, 5f,
                            cameraLookAtX, cameraLookAtY, cameraLookAtZ,
                            0f, 1.0f, 0.0f);
    }

    String matrixToString(float[] matrix){
        return  String.valueOf(matrix[0])  + " "  + String.valueOf(matrix[4])  + " " + String.valueOf(matrix[8])  + " " + String.valueOf(matrix[12])  + "\n" +
                String.valueOf(matrix[1])  + " "  + String.valueOf(matrix[5])  + " " + String.valueOf(matrix[9])  + " " + String.valueOf(matrix[13])  + "\n" +
                String.valueOf(matrix[2])  + " "  + String.valueOf(matrix[6])  + " " + String.valueOf(matrix[10]) + " " + String.valueOf(matrix[14]) + "\n" +
                String.valueOf(matrix[3])  + " "  + String.valueOf(matrix[7])  + " " + String.valueOf(matrix[11]) + " " + String.valueOf(matrix[15]) + "\n\n" ;
    }
}