package com.example.igorcouto.augumentedreallity.Util;

import com.example.igorcouto.augumentedreallity.GeographicObject;
import com.example.igorcouto.augumentedreallity.WorldCamera;

public class Geographic {

    public static final double METERS_TO_GEOPOINT = 107817.51838439942;
    public static final double EARTH_RADIUS_KM = 6384;// km
    public static final float DISTANCE_FACTOR = 1f; // original é 2

    public static float[] convertGPStoPoint3(GeographicObject geoObject, WorldCamera camera) {
        float x, z, y;

        float mMaxDistanceSizePoints;
        float mMinDistanceSizePoints;

        x = (float) ( fastConversionGeopointsToMeters(geoObject.getLongitude() - camera.getLongitude()) / DISTANCE_FACTOR );

        //z = (float) (Distance.fastConversionGeopointsToMeters(geoObject.getAltitude() - mWorld.getAltitude()) / mDistanceFactor);

        y = (float) (fastConversionGeopointsToMeters(geoObject.getLatitude() - camera.getLatitude()) / DISTANCE_FACTOR);

        //TODO: Refatorar. z tem que valer 1 mesmo? se sim, melhore o codigo --igorcouto 27-03-2018

        float[] result = new float[3];
        result[0] = x;
        result[1] = y;
        result[2] = 1f;

        return result;
    }


    /**
     * This method do an approximation form geopoints to meters. Do not use it
     * for long distances (> 5 km)
     */
    public static double fastConversionMetersToGeoPoints(double meters) {
        return meters / METERS_TO_GEOPOINT;
    }

    /**
     * This method do an approximation form geopoints to meters. Do not use it
     * for long distances (> 5 km)
     */
    public static double fastConversionGeopointsToMeters(double geoPoints) {
        return geoPoints * METERS_TO_GEOPOINT;
    }

}
