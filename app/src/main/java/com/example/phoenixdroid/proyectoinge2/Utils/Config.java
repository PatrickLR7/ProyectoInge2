package com.example.phoenixdroid.proyectoinge2.Utils;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.beyondar.android.world.GeoObject;
import org.osmdroid.util.GeoPoint;
import com.example.phoenixdroid.proyectoinge2.R;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public static Zona zona; // la zona donde se reside el usuario
    public static ArrayList<PuntoEncuentro> puntosEncuentro; //Lista de los puntos seguros.
    public static ArrayList<SenalVertical> senalesVerticales; //Lista de las señales verticales.
    public static ArrayList<GeoObject> geoObjetos; //Lista de objetos que actualmente se muestran en la camara.
    public static double usuarioLat; //Latitud del usuario justo antes iniciar SimpleCamera.
    public static double usuarioLon; //Longitud del usuario justo antes de iniciar SimpleCamera.
    public static int idGeoObjects = 0;  //Id usados para los geoObjetos.
    public static List<List<GeoPoint>> rutasE = new ArrayList<>(59); //Lista de rutas de evacuación.
    public static PuntoEncuentro puntoEncuentroMasCercano;
    public static List<GeoPoint> rutaHaciaLaZonaSegura;

    /**
     * Metodo encargado de mostar la barra del menu.
     * @param activity: La actividad desde la que se intenta desplegar el menu.
     */
    public static void mostrarAcercaDe(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.acercade, null);
        Button bt = mView.findViewById(R.id.btnA);
        TextView title = new TextView(activity.getApplicationContext());
        title.setText("Desarrollado por: \n PhoenixDroid");
        title.setBackgroundColor(activity.getColor(android.R.color.white));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(activity.getColor(R.color.colorPrimary));
        title.setTextSize(20);
        builder.setCustomTitle(title);
        builder.setView(mView);
        final AlertDialog ad = builder.create();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });
        ad.show();
    }
}
