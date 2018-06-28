package com.example.phoenixdroid.proyectoinge2.Utils;

import com.beyondar.android.world.GeoObject;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static boolean esVidente; // Valor que indica si se usa el modo vidente o no.
    public static Zona zona; // la zona donde se reside el usuario
    public static BaseDeDatos basededatos; // Base de datos que guarda las rutas, puntos de encuentro y señales verticales.
    public static ArrayList<PuntoEncuentro> puntosEncuentro; //Lista de los puntos seguros.
    public static ArrayList<SenalVertical> senalesVerticales; //Lista de las señales verticales.
    public static ArrayList<GeoObject> geoObjetos; //Lista de objetos que actualmente se muestran en la camara.
    public static double usuarioLat; //Latitud del usuario justo antes iniciar SimpleCamera.
    public static double usuarioLon; //Longitud del usuario justo antes de iniciar SimpleCamera.
    public static int idGeoObjects = 0;  //Id usados para los geoObjetos.
    public static List<List<GeoPoint>> rutasE = new ArrayList<>(59); //Lista de rutas de evacuación.
    public static PuntoEncuentro puntoEncuentroMasCercano;
    public static List<GeoPoint> rutaHaciaLaZonaSegura;
    public static boolean termino = false;
}
