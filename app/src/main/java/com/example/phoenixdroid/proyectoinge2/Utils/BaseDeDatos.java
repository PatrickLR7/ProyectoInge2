package com.example.phoenixdroid.proyectoinge2.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDeDatos extends SQLiteOpenHelper {

    /** Nombres de columnas de las tablas. Representan el esquema de la base de datos.*/
    private static final String BASE_DE_DATOS = "Santa_Ana";

    // Esquema tabla Puntos
    private static final String TABLA_1 = "Puntos";
    private static final String PUNTO_ID = "PuntoID";
    private static final String DESCRIPION = "Descripcion";
    private static final String LATITUD = "Latitud";
    private static final String LONGITUD = "Longitud";
    private static final String RUTA_PERTENECE = "RutaFK";

    // Esquema tabla Rutas
    private static final String TABLA_2 = "Rutas";
    private static final String RUTA_ID = "RutaID";
    private static final String NOMBRE = "Nombre";

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
        String tablaPuntos = "CREATE TABLE " + TABLA_1 + " (" + PUNTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DESCRIPION + " TEXT, " + LATITUD + " REAL, " + LONGITUD + " REAL, " + RUTA_PERTENECE + " INTEGER, "+
                "FOREIGN KEY(" + RUTA_PERTENECE + ") REFERENCES " + TABLA_2 + "(" + RUTA_ID + "))";
        String tablaRutas = "CREATE TABLE " + TABLA_2 + " (" + RUTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOMBRE + " TEXT)";
        sqLiteDatabase.execSQL(tablaPuntos);
        sqLiteDatabase.execSQL(tablaRutas);
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
     * @param desc: Una descripcion o el propio nombre del lugar.
     * @param latitud: Coordenada latitud.
     * @param longitud: Coordenada longitud.
     * @param rutaID: ID de la ruta a la que pertenece o NULL si no pertenece a ninguna
     * @return true si se agrega el punto exitosamente.
     */
    public boolean agregarPunto(String desc, double latitud, double longitud, Integer rutaID){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESCRIPION, desc);
        contentValues.put(LATITUD, latitud);
        contentValues.put(LONGITUD, longitud);
        contentValues.put(RUTA_PERTENECE, rutaID);
        long result = database.insert(TABLA_1, null, contentValues);
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
        contentValues.put(NOMBRE, nombre);
        long result = database.insert(TABLA_2, null, contentValues);
        return result != -1;
    }


}
