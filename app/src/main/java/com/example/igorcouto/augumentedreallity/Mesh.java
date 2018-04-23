package com.example.igorcouto.augumentedreallity;

import android.opengl.GLES20;
import android.opengl.Matrix;
import com.example.igorcouto.augumentedreallity.Util.ObjLoader;
import com.example.igorcouto.augumentedreallity.Util.TextureHelper;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mesh extends GeographicObject{

	private float[] vertices;
    private int numberOfVertices;
    private int numberOfTriangles;
    private float[] verticesNormals;
    private float[] faceNormals;

    private int shaderProgram;

    // Buffers
    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureCoordinateBuffer;

    // Handles
    private int mTextureDataHandle;
    private int mTextureCoordinateHandle;
    private int mTextureUniformHandle;
    private int muMVPMatrixHandle;

    // Constants
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final float ROTATION_SPEED = 1.0f;

	public Mesh ( String modelName ){

	    super();

        shaderProgram = Shaders.getInstance().getShader(Shaders.ShaderType.MODEL);

        muMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(shaderProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "a_TexCoordinate");

        mTextureDataHandle = TextureHelper.loadTexture(R.mipmap.hat_mario_color);

        ObjLoader objLoader = new ObjLoader(modelName);

        numberOfTriangles = objLoader.numFaces;

        // Initialize the buffers.
        vertexBuffer = ByteBuffer.allocateDirect(objLoader.positions.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(objLoader.positions).position(0);
        vertexBuffer.rewind();

        normalBuffer = ByteBuffer.allocateDirect(objLoader.normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer.put(objLoader.normals).position(0);
        normalBuffer.rewind();

        textureCoordinateBuffer = ByteBuffer.allocateDirect(objLoader.textureCoordinates.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordinateBuffer.put(objLoader.textureCoordinates).position(0);
        textureCoordinateBuffer.rewind();
    }

	void draw(float[] viewMatrix, float[] projectionMatrix){

        GLES20.glUseProgram( shaderProgram );

	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, true, 3 * FLOAT_SIZE_BYTES, vertexBuffer);
        GLES20.glEnableVertexAttribArray(0);

        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 2 * FLOAT_SIZE_BYTES, textureCoordinateBuffer);
        GLES20.glEnableVertexAttribArray(1);

        float[] mMVPMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);

        float[] ViewProjectionMatrix = new float[16];
        Matrix.setIdentityM(ViewProjectionMatrix,0);
        Matrix.multiplyMM(ViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, ViewProjectionMatrix , 0, modelMatrix, 0);

        //rotate(modelMatrix, ROTATION_SPEED,0f,1f,0f);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numberOfTriangles);
	}

    public void rotate(float[] modelMatrix, float angle, float x, float y, float z){
        Matrix.rotateM(modelMatrix, 0, angle, x, y, z);
    }
}
