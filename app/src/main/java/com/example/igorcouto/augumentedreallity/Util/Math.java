package com.example.igorcouto.augumentedreallity.Util;

public class Math {

    /**
     * Calculate the point between two points according to a given distance.
     */
    public static float[] linearInterpolate(float x_start, float y_start, float z_start, float x_end,
                                         float y_end, float z_end, float newDistance, float totalDistanceToMove) {

        float[] dstPoint = new float[3];

        float x = (x_start + newDistance * (x_end - x_start) / (float) totalDistanceToMove);
        float y = (y_start + newDistance * (y_end - y_start) / (float) totalDistanceToMove);
        float z = (z_start + newDistance * (z_end) / (float) totalDistanceToMove);

        dstPoint[0] = x;
        dstPoint[1] = y;
        dstPoint[2] = z;

        return dstPoint;
    }

    /**
     * Calculate the point between two points according to a given distance.
     *
     * */
    public static void linearInterpolate(double x_start, double y_start, double z_start, double x_end,
                                         double y_end, double z_end, double newDistance, double totalDistanceToMove, double[] dstPoint) {

        double x = (x_start + newDistance * (x_end - x_start) / totalDistanceToMove);
        double y = (y_start + newDistance * (y_end - y_start) / totalDistanceToMove);
        double z = (z_start + newDistance * (z_end) / totalDistanceToMove);

        dstPoint[0] = x;
        dstPoint[1] = y;
        dstPoint[2] = z;
    }
}
