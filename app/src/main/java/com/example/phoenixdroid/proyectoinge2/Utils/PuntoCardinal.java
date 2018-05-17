package com.example.phoenixdroid.proyectoinge2.Utils;

import org.osmdroid.util.GeoPoint;

/**
 * Created by admlab105 on 5/17/2018.
 */

public class PuntoCardinal {

    public PuntoCardinal(){

    }

    public int determinateDirection(GeoPoint usuario, GeoPoint zona) {
        double latitudeU = usuario.getLatitude();
        double longitudeU = usuario.getLongitude();
        double latitudeZ = zona.getLatitude();
        double longitudeZ = zona.getLongitude();
        boolean north = (latitudeU <= latitudeZ);
        boolean east = (longitudeU <= longitudeZ);
        double distanceY;
        double distanceX;

        if(north){
            distanceY = latitudeZ - latitudeU;
        }
        else {
            distanceY = latitudeU - latitudeZ;
        }

        if(east){
            distanceX = longitudeZ - longitudeU;
        }
        else {
            distanceX = longitudeU - longitudeZ;
        }

        boolean longer = (distanceY <= distanceX);
        double percentage;

        if(longer) {
            percentage = distanceY/distanceX;
        }
        else {
            percentage = distanceX/distanceY;
        }

        int verticalDominance = 2;

        if(percentage < 0.25){
            verticalDominance = 0;
        }
        else if((0.25 <= percentage) && (percentage <= 0.75)){
            verticalDominance = 1;
        } // else -> verticalDominance = 2

        int Ultimate = 0;

        switch (verticalDominance) {
            case 0:  if(north){ Ultimate = 0; } else{ Ultimate = 4; }
                break;
            case 1:  if(north && east){ Ultimate = 1; } else if(!north && east){ Ultimate = 3; } else if(!north && !east) { Ultimate = 5; } else { Ultimate = 7; }
                break;
            case 2:  if(east){ Ultimate = 2; } else{ Ultimate = 6; }
                break;
        }

        return Ultimate;
    }
}
