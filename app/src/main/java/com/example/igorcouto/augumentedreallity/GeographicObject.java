package com.example.igorcouto.augumentedreallity;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.igorcouto.augumentedreallity.Util.Geographic;

public abstract class GeographicObject {

    protected float[] position = new float[3];
    protected double latitude;
    protected  double longitude;
    protected float[] modelMatrix = new float[16];

    public GeographicObject(){
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public GeographicObject(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        //this.position = Geographic.convertGPStoPoint3(this);
    }

    protected void move(float[] newPosition){
	    move( newPosition[0], newPosition[1], newPosition[2] );
    }

    protected void move(float x, float y, float z){
        float[] TranslateMatrix = new float[16];
        Matrix.setIdentityM( TranslateMatrix, 0 );
        Matrix.translateM( TranslateMatrix, 0, x, y, z);
        Matrix.multiplyMM( modelMatrix, 0, modelMatrix, 0, TranslateMatrix, 0);
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }
}