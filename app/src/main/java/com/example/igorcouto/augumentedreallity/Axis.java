package com.example.igorcouto.augumentedreallity;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Axis {

    float[] mMMatrix = new float[16];
    private int muMVPMatrixHandle;
    int m_program;
    private FloatBuffer geometryBuffer;

    private final float[] geometry =
    {
                    1.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f
    };

    public Axis(){

        m_program = Shaders.getInstance().getShader(Shaders.ShaderType.AXIS);

        Matrix.setIdentityM(mMMatrix, 0);
        muMVPMatrixHandle = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");

        ByteBuffer geometryByteBuffer = ByteBuffer.allocateDirect(geometry.length * 4);
        geometryByteBuffer.order(ByteOrder.nativeOrder());
        geometryBuffer = geometryByteBuffer.asFloatBuffer();
        geometryBuffer.put(geometry);
        geometryBuffer.rewind();

        GLES20.glVertexAttribPointer(0, 4, GLES20.GL_FLOAT, false, 4 * 4, geometryBuffer);
        GLES20.glEnableVertexAttribArray(0);
    }

    public void Draw(float[] mVMatrix, float[] mProjMatrix){
        GLES20.glUseProgram( m_program );

        float[] mMVPMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);

        //Matrix.rotateM(mMMatrix, 0, 1, 0, 1, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 3);
    }

}