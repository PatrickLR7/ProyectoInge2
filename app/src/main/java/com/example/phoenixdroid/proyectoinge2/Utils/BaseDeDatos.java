package com.example.phoenixdroid.proyectoinge2.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDeDatos extends SQLiteOpenHelper {

    /** Nombres de columnas de las tablas. Representan el esquema de la base de datos.*/
    private static final String BASE_DE_DATOS = "Evac_Santa_Ana";

    // Esquema tabla Puntos
    private static final String TABLA_1 = "Puntos";
    private static final String PUNTO_ID = "PuntoID";
    private static final String LATITUD = "Latitud";
    private static final String LONGITUD = "Longitud";
    private static final String RUTA_PERTENECE = "RutaFK";

    // Esquema tabla Ruta
    private static final String TABLA_2 = "Rutas";
    private static final String RUTA_ID = "RutaID";

    // Esquema tabla de Puntos seguros, incluye atributos de latitud y longitud
    private static final String TABLA_3 = "Puntos_Seguros";
    private static final String DESCRIPION = "Descripcion";

    // Esquema tabla para las señales verticales
    private static final String TABLA_4 = "Señales_Verticales";
    private static final String NUMERO_SE = "NumeroDeSeñal";
    private static final String LADO_SE = "LadoDeSeñal";

    /**
     * Clase que maneja la conexion con la base de datos.
     */
    public BaseDeDatos(Context context){
        super(context, BASE_DE_DATOS, null, 1);
    }

    /**
     * Metodo que se ejecuta la primera vez para crear la base de datos.
     * @param sqLiteDatabase Instancia de la base de datos
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tablaPuntos = "CREATE TABLE " + TABLA_1 + " (" + PUNTO_ID + " INTEGER PRIMARY KEY, " +
                LATITUD + " REAL, " + LONGITUD + " REAL, " + RUTA_PERTENECE + " INTEGER, "+
                "FOREIGN KEY(" + RUTA_PERTENECE + ") REFERENCES " + TABLA_2 + "(" + RUTA_ID + "))";
        String tablaRutas = "CREATE TABLE " + TABLA_2 + " (" + RUTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT)";
        String tabla_Puntos_Seguros = "CREATE TABLE " + TABLA_3 + " (" + PUNTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LATITUD + " REAL, " + LONGITUD + " REAL, " +  DESCRIPION + " TEXT)";
        String tabla_Se = "CREATE TABLE " + TABLA_4 + " (" + NUMERO_SE + " INTEGER PRIMARY KEY, " +
                LATITUD + " REAL, " + LONGITUD + " REAL, " + LADO_SE + "TEXT)";
        sqLiteDatabase.execSQL(tablaPuntos);
        sqLiteDatabase.execSQL(tablaRutas);
        sqLiteDatabase.execSQL(tabla_Puntos_Seguros);
        sqLiteDatabase.execSQL(tabla_Se);
    }

    /**
     * Método llamado para recrear la base de datos.
     * @param sqLiteDatabase: instancia de la base de datos asociada a esta aplicacion
     * @param i:
     * @param i1:
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLA_1);
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLA_2);
        onCreate(sqLiteDatabase);
    }

    /**
     * Metodo usado para agregar un punto a la base de datos.
     * @param latitud: Coordenada latitud.
     * @param longitud: Coordenada longitud.
     * @param id: Identificador del punto.
     * @return true si se agrega el punto exitosamente.
     */
    public boolean agregarPunto(double latitud, double longitud, int id){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LATITUD, latitud);
        contentValues.put(LONGITUD, longitud);
        contentValues.put(PUNTO_ID, id);
        long result = database.insert(TABLA_1, null, contentValues);
        return result != -1;
    }

    /**
     * Metodo usado para agregar puntos seguros a la base de datos.
     * @param latitud: Coordenada latitud.
     * @param longitud: Coordenada longitud.
     * @param descripcion: Descripcion o nombre del punto.
     * @return true si el punto se agrega exitosamente.
     */
    public boolean agregarPuntoSeguro(double latitud, double longitud, String descripcion){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LATITUD, latitud);
        contentValues.put(LONGITUD, longitud);
        contentValues.put(DESCRIPION, descripcion);
        long result = database.insert(TABLA_3, null, contentValues);
        return result != -1;
    }

    /**
     * Metodo usado para cargar una ruta a la base de datos.
     * @param nombre: El nombre de la ruta o NULL si no tiene.
     * @return true si la ruta se agrega exitosamente.
     */
    public boolean agregarRuta(String nombre){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long result = database.insert(TABLA_2, null, contentValues);
        return result != -1;
    }

    /**
     * Metodo usado para agregar señales verticales.
     * @param num: Numero de la señal, es un valor unico.
     * @param lado: Orientacion de la señal.
     * @param latitud: Coordenada latitud.
     * @param longitud: Coordenada longitud.
     * @return true si la señal se agrega exitosamente
     */
    public boolean agregarSeñalVertical(int num, String lado, double latitud, double longitud){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LATITUD, latitud);
        contentValues.put(LONGITUD, longitud);
        contentValues.put(NUMERO_SE, num);
        contentValues.put(LADO_SE, lado);
        long result = database.insert(TABLA_4, null, contentValues);
        return result != -1;
    }

    /**
     * Metodo para asignarle a un punto dado la ruta a la que pertenece.
     * @param Ruta_ID: Identificador de la ruta.
     * @param Punto_ID: Identificador del punto.
     */
    public void ajustarPunto_Ruta(int Ruta_ID, int Punto_ID){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "UPDATE " + TABLA_1 + " SET " + RUTA_PERTENECE + " = " +  Ruta_ID + " WHERE " +
                PUNTO_ID + " = " + Punto_ID;
        database.execSQL(query);
    }
}
