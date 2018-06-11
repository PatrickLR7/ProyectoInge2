package com.example.phoenixdroid.proyectoinge2.Utils;

import org.osmdroid.util.GeoPoint;

/**
 * Clase para representar los puntos de encuentro (zonas seguras) de Santa Ana.
 */

public class PuntoEncuentro {
    /** Nombre del punto seguro. */
    public String nombre;
    /** Ubicación del punto seguro. */
    public double latitud, longitud;

    public int id;

    public boolean compareTo(GeoPoint gp) {
        return gp.getLatitude() == latitud && gp.getLongitude() == longitud;
    }
}