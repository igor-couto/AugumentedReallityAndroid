package com.example.igorcouto.augumentedreallity.Util;


import org.json.JSONArray;

public class PlacesHelper {

    private static PlacesHelper instance;
    private JSONArray Places;

    private PlacesHelper(){
        Places = null;
    }

    public static PlacesHelper getInstance(){
        if(instance == null){
            instance = new PlacesHelper();
        }
        return instance;
    }

    public JSONArray getPlaces() {
        return Places;
    }

    public void SetPlaces(JSONArray places){
        this.Places = places;
    }

}
