package com.example.igorcouto.augumentedreallity;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GeographicObject {

	Mesh mesh;
	float[] position = new float[3];
	//LatLng coordinate;
    int shaderProgram;

    float[] modelMatrix = new float[16];

    public GeographicObject(Shaders.ShaderType shader, Mesh mesh){
        shaderProgram = Shaders.getInstance().getShader(shader);
        this.mesh = mesh;

    }

	void draw(){
        GLES20.glUseProgram( shaderProgram );
		mesh.draw();
	}

	void move(float[] newPosition){
	    move(newPosition[0], newPosition[1], newPosition[2] );
    }

	void move(float x, float y, float z){
        float[] TranslateMatrix = new float[16];
        Matrix.setIdentityM( TranslateMatrix, 0 );
        Matrix.translateM( TranslateMatrix, 0, x, y, z);
        Matrix.multiplyMM( modelMatrix, 0, modelMatrix, 0, TranslateMatrix, 0);
    }

}