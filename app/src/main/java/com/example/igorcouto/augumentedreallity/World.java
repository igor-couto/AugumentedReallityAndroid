package com.example.igorcouto.augumentedreallity;

import java.util.List;

public class World {

    List<GeographicObject> objects;

    void Render(){
        for (GeographicObject object : objects) {
            object.draw();
        }
    }
}
