package com.example.igorcouto.augumentedreallity.Util;


public class Filters {

    public static float[] lowPass(float[] input, float[] prev){
        float alpha = 0.03f;

        if (input == null || prev == null)
            throw new NullPointerException("input and prev float arrays must be non-NULL");
        if (input.length != prev.length)
            throw new IllegalArgumentException("input and prev must be the same length");

        for (int i = 0; i < input.length; i++) {
            prev[i] = prev[i] + alpha * (input[i] - prev[i]);
        }

        return prev;
    }

}
