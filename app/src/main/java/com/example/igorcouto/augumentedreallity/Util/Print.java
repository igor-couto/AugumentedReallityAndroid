package com.example.igorcouto.augumentedreallity.Util;


import android.hardware.Camera;
import android.util.Log;

import java.util.List;

public class Print {

    /**
     * Conver a given matrix into string and output it with Log
     * @param matrix Matrix that will be converted into string
     * @param name The matrix name
     */
    static void showMatrix(float[] matrix, String name){
        String matrixString =
                        String.valueOf(matrix[0])  + " "  + String.valueOf(matrix[4])  + " " + String.valueOf(matrix[8])  + " " + String.valueOf(matrix[12])  + "\n" +
                        String.valueOf(matrix[1])  + " "  + String.valueOf(matrix[5])  + " " + String.valueOf(matrix[9])  + " " + String.valueOf(matrix[13])  + "\n" +
                        String.valueOf(matrix[2])  + " "  + String.valueOf(matrix[6])  + " " + String.valueOf(matrix[10]) + " " + String.valueOf(matrix[14]) + "\n" +
                        String.valueOf(matrix[3])  + " "  + String.valueOf(matrix[7])  + " " + String.valueOf(matrix[11]) + " " + String.valueOf(matrix[15]) + "\n\n" ;
        Log.d("Matrix " + name, matrixString);
    }

    /**
     * Conver a given matrix into string and output it with Log
     * @param matrix Matrix that will be converted into string
     */
    static void showMatrix(float[] matrix){
        showMatrix(matrix, "");
    }

    static void showSupportedResolutions(Camera.Parameters parameters){
        List<Camera.Size> allSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : allSizes ) {
            Log.d("Camera Supported Sizes", "Height: " + size.height + " Width: " + size.width + " Aspect Ratio: " + (float) size.width/size.height);
        }
    }
}
