package com.example.igorcouto.augumentedreallity;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ScreenCamera {

    private Shaders.ShaderType shaderType = Shaders.ShaderType.SCREEN;

    private float[] modelMatrix = new float[16];

    private int m_program;

    private float mCameraRatio = 1.0f;

    private float[] mMVPMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mSTMatrix = new float[16];
    private int mTextureID;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int muCRatioHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    // Magic key
    private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private FloatBuffer mTriangleVertices;
    private final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
            1.0f, -1.0f, 0, 1.f, 0.f,
            -1.0f,  1.0f, 0, 0.f, 1.f,
            1.0f,   1.0f, 0, 1.f, 1.f,
    };

    public ScreenCamera(){

        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);
        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(mMMatrix, 0);

        Matrix.translateM(mMMatrix, 0, 0f, 0f, 0f);
        //Matrix.scaleM(mMMatrix, 0, 2,2,0);

        m_program = Shaders.getInstance().getShader(shaderType);

        maPositionHandle  = GLES20.glGetAttribLocation  (m_program, "aPosition");
        maTextureHandle   = GLES20.glGetAttribLocation  (m_program, "aTextureCoord");
        muMVPMatrixHandle = GLES20.glGetUniformLocation (m_program, "uMVPMatrix");
        muSTMatrixHandle  = GLES20.glGetUniformLocation (m_program, "uSTMatrix");
        muCRatioHandle    = GLES20.glGetUniformLocation (m_program, "uCRatio");

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mTextureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        // Can't do mipmapping with camera source
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // Clamp to edge is the only option
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    public void Rotate(float rotation){
        Matrix.rotateM(mMMatrix, 0, rotation, 0f,0f,1f);
        Matrix.scaleM(mMMatrix, 0, 1f, -1f, 0f);
    }

    public void Draw(float[] mVMatrix, float[] mProjMatrix){

        GLES20.glUseProgram(m_program);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glUniform1f(muCRatioHandle, mCameraRatio);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    public int getTextureID() {
        return mTextureID;
    }

    public void setCameraRatio(float cameraRatio) {
        mCameraRatio = cameraRatio;
    }
}