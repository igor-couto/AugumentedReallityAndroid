package com.example.igorcouto.augumentedreallity;

import android.opengl.Matrix;

public class WorldCamera extends GeographicObject{

    private float[] lookAtPoint = new float[3];
    private float[] vectorUp = new float[3];

    private float ratio;
    private final float near = 1.0f;
    private final float far = 1000f;
    private final float FOV = 60.0f;

    private float pitch = 0f;
    private float roll = 0f;
    private float azimuth = 0f;

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];

    private float[] rotationTempMatrix = new float[16];

    public WorldCamera(){

        super();

        //lookAtPoint[0] = lookAtPoint[1] = lookAtPoint[2] = 0f;

        vectorUp[1] = 1f;
        vectorUp[0] = vectorUp[2] = 0f;

        setLookAt ( 0f, 0f, 5f,
                0f, 0f, 0f);
    }

    public void setLookAt ( float positionX, float positionY, float positionZ,
                            float lookX, float lookY, float lookZ
                           ){
        Matrix.setLookAtM ( viewMatrix, 0,
                positionX, positionY , positionZ,
                lookX, lookY , lookZ,
                vectorUp[0], vectorUp[1] , vectorUp[2] );
    }

    public void resetLookAt(){
        Matrix.setLookAtM ( viewMatrix, 0,
                0f, 0f , 5f,
                0f, 0f , 0f,
                vectorUp[0], vectorUp[1] , vectorUp[2] );
    }

    public void setPerspective(){
        Matrix.perspectiveM(projectionMatrix, 0, FOV, ratio, near, far);
    }

    public void setRatio(float ratio){
        this.ratio = ratio;
        setPerspective();
    }

    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }

    public float[] getViewMatrix(){
        return viewMatrix;
    }


    public void rotate(float[] mRotXMatrix, float[] mRotYMatrix, float[] mRotZMatrix){
        resetLookAt();

        Matrix.multiplyMM(viewMatrix, 0, mRotYMatrix, 0, mRotXMatrix, 0); //multiply X by Y rotation
        rotationTempMatrix = viewMatrix.clone(); // We should avoid using same matrix for source and destination
        Matrix.multiplyMM(viewMatrix, 0, mRotZMatrix, 0, rotationTempMatrix, 0); //multiply the result by Z rotation
        rotationTempMatrix = viewMatrix.clone(); //Save last rotation combining

        // TODO Do I have to rotate my view matrix to make things go in front of the device?
        Matrix.rotateM(viewMatrix, 0, 90,1f,0f,0f);
    }

    public void move(){

    }
}
