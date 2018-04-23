package com.example.igorcouto.augumentedreallity;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.example.igorcouto.augumentedreallity.Util.ObjLoader;
import com.example.igorcouto.augumentedreallity.Util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MarioHat {

    private float[] modelMatrix = new float[16];

    private int m_program;
    private int muMVPMatrixHandle;
    private int mTextureCoordinateHandle;
    private int mTextureUniformHandle;

    private int mTextureDataHandle;

    private FloatBuffer geometryBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureCoordinateBuffer;

    private int numFaces;

    public MarioHat(){

        m_program = Shaders.getInstance().getShader(Shaders.ShaderType.MODEL);

        Matrix.setIdentityM(modelMatrix, 0);

        muMVPMatrixHandle = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");

        mTextureUniformHandle = GLES20.glGetUniformLocation(m_program, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(m_program, "a_TexCoordinate");

        mTextureDataHandle = TextureHelper.loadTexture(R.mipmap.hat_mario_color);

        ObjLoader objLoader = new ObjLoader("hat_mario_model.obj");

        numFaces = objLoader.numFaces;

        // Initialize the buffers.
        geometryBuffer = ByteBuffer.allocateDirect(objLoader.positions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        geometryBuffer.put(objLoader.positions).position(0);
        geometryBuffer.rewind();

        normalBuffer = ByteBuffer.allocateDirect(objLoader.normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer.put(objLoader.normals).position(0);
        normalBuffer.rewind();

        textureCoordinateBuffer = ByteBuffer.allocateDirect(objLoader.textureCoordinates.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordinateBuffer.put(objLoader.textureCoordinates).position(0);
        textureCoordinateBuffer.rewind();
    }

    void Draw(float[] viewMatrix, float[] projectionMatrix){
        GLES20.glUseProgram( m_program );

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, true, 3 * 4, geometryBuffer);
        GLES20.glEnableVertexAttribArray(0);

        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 2 * 4, textureCoordinateBuffer);
        GLES20.glEnableVertexAttribArray(1);

        float[] mMVPMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);

        float[] ViewProjectionMatrix = new float[16];
        Matrix.setIdentityM(ViewProjectionMatrix,0);
        Matrix.multiplyMM(ViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, ViewProjectionMatrix , 0, modelMatrix, 0);

        //float rotationSpeed = 1f;
        //Rotate(rotationSpeed,0f,1f,0f);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numFaces);
    }

    public void Move(float x, float y, float z) {
        float[] TranslateMatrix = new float[16];
        Matrix.setIdentityM( TranslateMatrix, 0 );
        Matrix.translateM( TranslateMatrix, 0, x, y, z);
        Matrix.multiplyMM( modelMatrix, 0, modelMatrix, 0, TranslateMatrix, 0);
    }

    public void Rotate(float angle, float x, float y, float z){
        Matrix.rotateM(modelMatrix, 0, angle, x, y, z);
    }
}