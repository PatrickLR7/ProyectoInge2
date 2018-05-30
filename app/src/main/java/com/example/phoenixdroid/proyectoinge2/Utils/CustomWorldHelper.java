package com.example.phoenixdroid.proyectoinge2.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

/**
 * Clase que crea el entorno de realidad aumentada.
 */
@SuppressLint("SdCardPath")
public class CustomWorldHelper {

    public static final int LIST_TYPE_EXAMPLE_1 = 1;
    /** Representa el ambiente o entorno del juego. */
    private World sharedWorld;

    /**
     * Class Constructor.
     */
    public CustomWorldHelper(){

    }

    /**
     * Genera el mundo donde se juega en caso de que no haya sido generado anteriormente.
     * @param context: Actividad desde donde se llama.
     * @return el mundo creado
     */
    public World generateObjects(Context context) {
        if (sharedWorld != null) {
            return sharedWorld;
        }

        sharedWorld = new World(context);

        // Hacerle aquí cambios al World que se va a generar
        // Posiblemente agregar los puntos de encuentro y señales verticales
        //

        return sharedWorld;
    }
}