package com.example.phoenixdroid.proyectoinge2.Utils;

import java.util.ArrayList;

public class Config {
    public static boolean esVidente; // Valor que indica si se usa el modo vidente o no.
    public static Zona zona; // la zona donde se reside el usuario
    public static BaseDeDatos basededatos; // Base de datos que guarda las rutas, puntos de encuentro y señales verticales.
    public static ArrayList<PuntoEncuentro> puntosEncuentro; //Lista de los puntos seguros.
    public static double usuarioLat; //Latitud del usuario justo antes iniciar SimpleCamera.
    public static double usuarioLon; //Longitud del usuario justo antes de iniciar SimpleCamera.
}
