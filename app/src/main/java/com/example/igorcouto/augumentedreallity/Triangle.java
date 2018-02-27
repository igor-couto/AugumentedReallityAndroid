package com.example.igorcouto.augumentedreallity;

import android.opengl.GLES20;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private float[] modelMatrix = new float[16];

    private int m_program;
    private int muMVPMatrixHandle;

    private FloatBuffer geometryBuffer;

    private Shaders.ShaderType shaderType = Shaders.ShaderType.PINK_TEST;


    private int sizeOfFloatInBytes = 4;
    private final float[] geometry =
            {
                -1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f
            };

    public Triangle(){

        m_program = Shaders.getInstance().getShader(shaderType);

        Matrix.setIdentityM(modelMatrix, 0);

        muMVPMatrixHandle = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");

        ByteBuffer geometryByteBuffer = ByteBuffer.allocateDirect(geometry.length * sizeOfFloatInBytes);
        geometryByteBuffer.order(ByteOrder.nativeOrder());
        geometryBuffer = geometryByteBuffer.asFloatBuffer();
        geometryBuffer.put(geometry);
        geometryBuffer.rewind();

    }

    void Draw(float[] mRotationMatrix, float[] mVMatrix, float[] mProjMatrix){

        GLES20.glUseProgram( m_program );

        GLES20.glVertexAttribPointer(0, 4, GLES20.GL_FLOAT, false, 4 * sizeOfFloatInBytes, geometryBuffer);
        GLES20.glEnableVertexAttribArray(0);

        float[] mMVPMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);

        //Matrix.rotateM(modelMatrix, 0, 1, 0, 1, 0);
        //Matrix.rotateM (modelMatrix, 0, 1, 0, 0, 1);

        //region Old Code

        //Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, modelMatrix, 0);   // V M
        //Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);  // P

        //endregion

/*
        //region VMRP
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix , 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix , 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix , 0, mMVPMatrix, 0);
        //endregion
*/


/*
        //region RMVP
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix , 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix , 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix , 0, mMVPMatrix, 0);
        //endregion
*/

/*
        //region MRVP
        Matrix.multiplyMM(mMVPMatrix, 0, modelMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix , 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix , 0, mMVPMatrix, 0);
        //endregion
*/

/*
        //region PVRM
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix , 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, modelMatrix , 0, mMVPMatrix, 0);
        //endregion
*/
        //region MVRP
        Matrix.multiplyMM(mMVPMatrix, 0, modelMatrix, 0, mVMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix , 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix , 0, mMVPMatrix, 0);
        //endregion


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