package com.example.igorcouto.augumentedreallity;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MeshObject {

    protected float[] modelMatrix = new float[16];

    protected int m_program;
    protected int muMVPMatrixHandle;

    protected FloatBuffer geometryBuffer;


    protected Shaders.ShaderType shaderType = Shaders.ShaderType.PINPOINT;
    protected float[] geometry = null;

    public MeshObject(Shaders.ShaderType shaderType, float[] geometry){

        this.shaderType = shaderType;
        this.geometry= geometry;

        m_program = Shaders.getInstance().getShader(shaderType);

        Matrix.setIdentityM(modelMatrix, 0);
        muMVPMatrixHandle = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");

        ByteBuffer geometryByteBuffer = ByteBuffer.allocateDirect(geometry.length * 4);
        geometryByteBuffer.order(ByteOrder.nativeOrder());
        geometryBuffer = geometryByteBuffer.asFloatBuffer();
        geometryBuffer.put(geometry);
        geometryBuffer.rewind();

        GLES20.glVertexAttribPointer(0, 4, GLES20.GL_FLOAT, false, 4 * 4, geometryBuffer);
        GLES20.glEnableVertexAttribArray(0);
    }

    void Draw(float[] mVMatrix, float[] mProjMatrix){

        GLES20.glUseProgram( m_program );

        float[] mMVPMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    public void Move(float x, float y, float z) {
        float[] TranslateMatrix = new float[16];
        Matrix.setIdentityM( TranslateMatrix, 0 );
        Matrix.translateM( TranslateMatrix, 0, x, y, z);
        Matrix.multiplyMM( modelMatrix, 0, modelMatrix, 0, TranslateMatrix, 0);
    }
}