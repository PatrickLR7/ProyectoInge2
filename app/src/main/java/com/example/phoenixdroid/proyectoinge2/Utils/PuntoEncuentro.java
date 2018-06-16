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
    /** Identificador del punto seguro*/
    public int id;

    /**
     * Metodo que permite compara Punto de encuentro con cualquier geopunto para saber si son iguales.
     * @param gp: El geopunto para comparar.
     * @return true si son iguales, false en caso contrario.
     */
    public boolean compareTo(GeoPoint gp) {
        return gp.getLatitude() == latitud && gp.getLongitude() == longitud;
    }
}