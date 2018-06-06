package com.example.phoenixdroid.proyectoinge2.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.phoenixdroid.proyectoinge2.R;

import java.util.ArrayList;

/**
 * Clase que crea el entorno de realidad aumentada.
 */
@SuppressLint("SdCardPath")
public class CustomWorldHelper {

    public static final int LIST_TYPE_EXAMPLE_1 = 1;
    /** Representa el ambiente o entorno de la RA. */
    private World sharedWorld;
    /** Lista de objetos que actualmente se muestran en la camara. */
    private ArrayList<GeoObject> geoObjects;

    /**
     * Class Constructor.
     */
    public CustomWorldHelper(){
        geoObjects = new ArrayList<>();
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
        int id = Config.idGeoObjects;

        for(int i = 0; i < Config.puntosEncuentro.size(); i++){
            GeoObject go1 = new GeoObject(id++);
            go1.setGeoPosition(Config.puntosEncuentro.get(i).latitud, Config.puntosEncuentro.get(i).longitud);
            go1.setImageResource(R.drawable.icon_zona_segura);
            go1.setName("" + Config.puntosEncuentro.get(i).nombre);

            sharedWorld.addBeyondarObject(go1);
            geoObjects.add(go1);

        }

        Config.geoObjetos = geoObjects;
        Config.idGeoObjects = id;
        sharedWorld.setGeoPosition(Config.usuarioLat, Config.usuarioLon);

        return sharedWorld;
    }
}