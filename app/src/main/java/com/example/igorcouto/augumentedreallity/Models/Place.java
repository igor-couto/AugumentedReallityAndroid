package com.example.igorcouto.augumentedreallity.Models;

public class Place {

    private String Name;
    private float Latitude;
    private float Longitude;
    private String Area;
    private String ModelName;
    private String IconName;

    public String getName() { return Name; }

    public void setName(String name) { Name = name; }

    public float getLatitude() { return Latitude; }

    public void setLatitude(float latitude) { Latitude = latitude; }

    public float getLongitude() { return Longitude; }

    public void setLongitude(float longitude) { Longitude = longitude; }

    public String getArea() { return Area; }

    public void setArea(String area) { Area = area; }

    public String getModelName() { return ModelName; }

    public void setModelName(String modelName) { ModelName = modelName; }

    public String getIconName() { return IconName; }

    public void setIconName(String iconName) { IconName = iconName; }

}
