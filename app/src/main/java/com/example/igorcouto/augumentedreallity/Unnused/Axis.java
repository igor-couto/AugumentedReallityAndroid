package com.example.igorcouto.augumentedreallity.Unnused;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.igorcouto.augumentedreallity.Shaders;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Axis {

    private static final int FLOAT_SIZE = 4;
    float[] mMMatrix = new float[16];
    private int muMVPMatrixHandle;
    int m_program;
    private FloatBuffer geometryBuffer;
    private FloatBuffer colorBuffer;

    private final float[] geometry =
    {
        // X:
        0.0f, 0.0f, 0.0f, 1.0f,
        10.0f, 0.0f, 0.0f, 1.0f,
        // Y:
        0.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 10.0f, 0.0f, 1.0f,
        // Z:
        0.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 10.0f, 1.0f
    };

    private final float[] color =
    {
        // X:
        1.0f, 0.0f, 0.0f,
        // Y:
        0.0f, 1.0f, 0.0f,
        // Z:
        0.0f, 0.0f, 1.0f
    };

    public Axis(){

        m_program = Shaders.getInstance().getShader(Shaders.ShaderType.AXIS);

        Matrix.setIdentityM(mMMatrix, 0);
        muMVPMatrixHandle = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");

        // Set Geometry
        ByteBuffer geometryByteBuffer = ByteBuffer.allocateDirect(geometry.length * FLOAT_SIZE );
        geometryByteBuffer.order(ByteOrder.nativeOrder());
        geometryBuffer = geometryByteBuffer.asFloatBuffer();
        geometryBuffer.put(geometry);
        geometryBuffer.rewind();

        GLES20.glVertexAttribPointer(0, 4, GLES20.GL_FLOAT, false, 4 * FLOAT_SIZE, geometryBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glBindAttribLocation(m_program, 0, "aPosition");

        // Set Color
        ByteBuffer colorByteBuffer = ByteBuffer.allocateDirect(color.length * FLOAT_SIZE );
        colorByteBuffer.order(ByteOrder.nativeOrder());
        colorBuffer = colorByteBuffer.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.rewind();

        GLES20.glVertexAttribPointer(1, 4, GLES20.GL_FLOAT, false, 3 * FLOAT_SIZE, colorBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glBindAttribLocation(m_program, 1, "inputColor");

    }

    public void Draw(float[] mVMatrix, float[] mProjMatrix){
        GLES20.glUseProgram( m_program );

        float[] mMVPMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);
    }
}
