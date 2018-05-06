package com.example.phoenixdroid.proyectoinge2.Utils;

import org.osmdroid.util.GeoPoint;
import java.util.LinkedList;
import java.util.List;

/** Clase que representa las rutas de evacuación. */
public class RutaEvacuacion {
    /** Identificador de la ruta. */
    public int id;
    /** Lista de puntos que conforman la ruta de evacuación. */
    public List<GeoPoint> camino = new LinkedList<>();
}
