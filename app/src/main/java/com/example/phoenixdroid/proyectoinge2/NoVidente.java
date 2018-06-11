package com.example.phoenixdroid.proyectoinge2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.phoenixdroid.proyectoinge2.Utils.BaseDeDatos;
import com.example.phoenixdroid.proyectoinge2.Utils.Config;
import com.example.phoenixdroid.proyectoinge2.Utils.CopyFolder;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoCardinal;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoEncuentro;
import com.example.phoenixdroid.proyectoinge2.Utils.SenalVertical;
import com.example.phoenixdroid.proyectoinge2.Utils.SintetizadorVoz;
import com.example.phoenixdroid.proyectoinge2.Utils.XmlParser;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class NoVidente extends AppCompatActivity implements View.OnClickListener, SensorEventListener, LocationListener {
    BaseDeDatos bdMapa; //Base de datos que guarda información clave del mapa.
    double latActual, lonActual, distancia;
    GeoPoint puntoUsuario, puntoProximo; //Puntos necesarios para determinar puntos cardinales
    int grados, puntoCardinalTel, puntoCardinalZona; //Grados de 0 a 360 de la orientación y puntos cardinales de posiciones geográficas
    LocationManager locationManager; //Controlador de ubicación
    PuntoCardinal pc; //Clase que determina un punto cardinal según dos GeoPoints
    PuntoEncuentro puntoMasCercano; //Siguiente punto al que se debe dirigir el usuario
    SintetizadorVoz sv; //Clase con TextToSpeech
    SensorManager sensorManager; //Controlador de la orientación del teléfono
    XmlParser parser;
    List<List<GeoPoint>> rutasE = new ArrayList<>(59); //Lista de rutas de evacuación.

    GeoPoint puntoEMasCercano = null;
    List<GeoPoint> rutaALaZonaSegura = null;

    /**
     * Metodo que se ejecuta cuando se crea esta actividad.
     * @param savedInstanceState: la instancia previa de esta actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_vidente);

        CopyFolder.copyAssets(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
        }
        catch (SecurityException ignored) { }

        bdMapa = new BaseDeDatos(getApplicationContext());
        parser = new XmlParser(this);

        Button btn_guiar = findViewById(R.id.btn_guiarNoVidente);
        btn_guiar.setOnClickListener(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); //Sensor de la orientación del teléfono
        sv = new SintetizadorVoz(this); //Clase con TextToSpeech

        grados = 0; //Orientación del teléfono
        puntoCardinalTel = 0; //Punto cardinal según la orientación
        latActual = 0;
        lonActual = 0;
        puntoMasCercano = null;
        puntoUsuario = null;
        puntoProximo = null;
        distancia = 0;
        pc = new PuntoCardinal();
        rutasE = parser.rutasE;
    }

    /**
     * Metodo que se ejecuta cuando se hace tap.
     * @param view: objeto sobre el que se hizo tap.
     */
    @Override
    public void onClick(View view)
    {
        guiar();
    }

    /**
     * Metodo que arma la información completa que requiere el usuario y luego la dice
     */
    private void guiar()
    {
        String texto = "";
        if(distancia <= 20)
        {
            texto = texto + "Ha llegado a la zona segura.";
        }
        else
        {
            texto = puntoCardinalTel(texto) + ", "; //Orientación del teléfono.
            texto = puntoCardinalPunto(texto) + ". "; //Orientación en la que se encuentra el punto
            texto = instruccion(texto); //Instrucción más útil para el no vidente
            texto = texto + " y la distancia es " + (int) distancia + " metros. "; //Distancia hasta el punto
        }
        sv.hablar(texto); //Llama a la clase con el TextToSpeech
    }

    /**
     * Metodo que determina la orientación del dispositivo según el registro en grados devuelto por el sensor.
     * @param texto texto al que se le va a concatenar la información sobre la orientación.
     */
    private String puntoCardinalTel(String texto)
    {
        if ((grados >= 337.5 && grados <= 360) || (grados >= 0 && grados < 22.5))
        {
            texto = texto + "Esta viendo hacia el norte";
            puntoCardinalTel = 0;
        }
        else if (grados >= 22.5 &&  grados < 67.5)
        {
            texto = texto + "Esta viendo hacia el noreste";
            puntoCardinalTel = 1;
        }
        else if (grados >= 67.5 &&  grados < 112.5)
        {
            texto = texto + "Esta viendo hacia el este";
            puntoCardinalTel = 2;
        }
        else if (grados >= 112.5 &&  grados < 157.5)
        {
            texto = texto + "Esta viendo hacia el sureste";
            puntoCardinalTel = 3;
        }
        else if (grados >= 157.5 &&  grados < 202.5)
        {
            texto = texto + "Esta viendo hacia el sur";
            puntoCardinalTel = 4;
        }
        else if (grados >= 202.5 &&  grados < 247.5)
        {
            texto = texto + "Esta viendo hacia el suroeste";
            puntoCardinalTel = 5;
        }
        else if (grados >= 247.5 &&  grados < 292.5)
        {
            texto = texto + "Esta viendo hacia el oeste";
            puntoCardinalTel = 6;
        }
        else if (grados >= 292.5 &&  grados < 337.5)
        {
            texto = texto + "Esta viendo hacia el noroeste";
            puntoCardinalTel = 7;
        }
        return texto;
    }

    /**
     * Metodo que determina el punto cardinal hacia el que se encuentra el siguiente punto
     * @param texto texto al que se le va a concatenar el lugar del punto.
     */
    private String puntoCardinalPunto(String texto)
    {
        int direccion = pc.determinateDirection(puntoUsuario, puntoProximo);
        puntoCardinalZona = direccion;
        switch (direccion)
        {
            case 0:
                texto = texto  + " el punto esta hacia el norte";
                break;
            case 1:
                texto = texto  + " el punto esta hacia el noreste";
                break;
            case 2:
                texto = texto  + " el punto esta hacia el este";
                break;
            case 3:
                texto = texto  + " el punto esta hacia el sureste";
                break;
            case 4:
                texto = texto  + " el punto esta hacia el sur";
                break;
            case 5:
                texto = texto  + " el punto esta hacia el suroeste";
                break;
            case 6:
                texto = texto  + " el punto esta hacia el oeste";
                break;
            case 7:
                texto = texto  + " el punto esta hacia el noroeste";
                break;
        }
        return texto;
    }

    /**
     * Metodo que genera la instrucción más específica que tendrá que seguir el usuario no vidente.
     * @param texto texto al que se le va a concatenar la instrucción.
     */
    private String instruccion(String texto)
    {
        if(puntoCardinalTel == puntoCardinalZona) //El dispositivo está viendo hacia donde está el punto
        {
            texto = texto + "Siga hacia adelante. ";
        }
        else if(puntoCardinalTel - puntoCardinalZona == 4 || puntoCardinalZona - puntoCardinalTel == 4 ||
                puntoCardinalTel - puntoCardinalZona == 5 || puntoCardinalZona - puntoCardinalTel == 5 ||
                puntoCardinalTel - puntoCardinalZona == 3 || puntoCardinalZona - puntoCardinalTel == 3)
        { //El dispositivo está viendo hacia el sentido contrario
            texto = texto + "Gire 180 grados. ";
        }
        else if(puntoCardinalTel - puntoCardinalZona == 2 || puntoCardinalZona - puntoCardinalTel == 6)
        { //El dispositivo está girado
            texto = texto + "Gire 90 grados a la izquierda. ";
        }
        else if(puntoCardinalTel - puntoCardinalZona == 6 || puntoCardinalZona - puntoCardinalTel == 2)
        { //El dispositivo está girado
            texto = texto + "Gire 90 grados a la derecha. ";
        }
        else if(puntoCardinalTel - puntoCardinalZona == 1 || puntoCardinalZona - puntoCardinalTel == 7)
        { //El dispositivo está un poco girado
            texto = texto + "Gire 45 grados a la izquierda. ";
        }
        else if(puntoCardinalTel - puntoCardinalZona == 7 || puntoCardinalZona - puntoCardinalTel == 1)
        { //El dispositivo está un poco girado
            texto = texto + "Gire 45 grados a la derecha. ";
        }
        return texto;
    }

    /**
     * Metodo que se ejecuta por evento de cuando se registra un cambio en la orientación del dispositivo
     * @param event evento de cambio en la orientación.
     */
    @Override
    public void onSensorChanged(SensorEvent event) //Sensor de la brújula
    {
        grados = Math.round(event.values[0]);
    }

    /**
     * Metodo que revisa los cambios en la ubicación del usuario.
     * @param location ubicación del usuario.
     */
    @Override
    public void onLocationChanged(Location location) {
        GeoPoint miPosicion = new GeoPoint(location.getLatitude(), location.getLongitude());
        Config.usuarioLat = location.getLatitude();
        Config.usuarioLon = location.getLongitude();
        puntoUsuario = miPosicion;
        if (latActual != miPosicion.getLatitude() || lonActual != miPosicion.getLongitude()) {
            latActual = miPosicion.getLatitude();
            lonActual = miPosicion.getLongitude();

            //Se obtiene la ruta cercana al usuario
            double distanciaMin = Double.MAX_VALUE;
            for (int x = 0; x < rutasE.size(); x++) {
                List<GeoPoint> rutaTemp = rutasE.get(x);
                for (int y = 0; y < rutaTemp.size(); y++) {
                    double dist = rutaTemp.get(y).distanceToAsDouble(miPosicion);
                    if (dist < distanciaMin) {
                        distanciaMin = dist;
                        rutaALaZonaSegura = rutaTemp;
                    }
                }
            }

            //Basado en la ruta calculada anteriormente, se obtiene el punto seguro
            int posPuntoSeguro = buscarPuntoSeguro(rutaALaZonaSegura.get(0));
            if (posPuntoSeguro != -1) {
                puntoEMasCercano = new GeoPoint(parser.getPuntosE().get(posPuntoSeguro).latitud, parser.getPuntosE().get(posPuntoSeguro).longitud);
                distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
            } else {
                posPuntoSeguro = buscarPuntoSeguro(rutaALaZonaSegura.get(rutaALaZonaSegura.size() - 1));
                if (posPuntoSeguro != -1) {
                    puntoEMasCercano = new GeoPoint(parser.getPuntosE().get(posPuntoSeguro).latitud, parser.getPuntosE().get(posPuntoSeguro).longitud);
                    distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
                } else {
                    for (int x = 0; x < parser.getPuntosE().size(); x++) {
                        PuntoEncuentro pETemp = parser.getPuntosE().get(x);
                        for (int y = 0; y < rutaALaZonaSegura.size(); y++) {
                            GeoPoint temp2 = rutaALaZonaSegura.get(y);
                            if (pETemp.compareTo(temp2)) {
                                puntoEMasCercano = new GeoPoint(pETemp.latitud, pETemp.longitud);
                                ;
                                distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
                                y = 1000000;
                                x = 1000000;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Revisa si el geoPoint que recibe como parametro es una zona segura.
     * @param gp el punto que se quiere verificar
     * @return si gp es punto seguro, retorna el indice del mismo en la lista de puntos seguros. Si no es punto seguro, retorna -1;
     */
    private int buscarPuntoSeguro(GeoPoint gp) {
        for (int x = 0; x < parser.getPuntosE().size(); x++) {
            PuntoEncuentro pETemp = parser.getPuntosE().get(x);
            if (pETemp.compareTo(gp)) {
                return x;
            }
        }
        return -1;
    }

    /**
     * Metodo que vuelva a activar el sensor de cambio de orientación.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Metodo que desactiva el sensor para ahorrar batería
     */
    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Metodo que se ejecuta cuando el activity se cierra, para terminar todos los procesos que estuvieran en ejecución.
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        sv.stop();
        if (locationManager != null)
        {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Metodo que no se usa, pero es necesario tener escrito por el SensorEventListener.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    /**
     * Metodo que se ejecuta cuando el provedor cambia de estado. No se usa, pero es necesario tener por el SensorEventListener.
     * @param provider provedor de ubicación.
     * @param status estado.
     * @param extras extras.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    /**
     * Metodo que se ejecuta cuando el provedor es activado por el usuario.
     * @param provider provedor de ubicación.
     */
    @Override
    public void onProviderEnabled(String provider) {}

    /**
     * Metodo que se ejecuta cuando el provedor es desactivado por el usuario.
     * @param provider provedor de ubicación.
     */
    @Override
    public void onProviderDisabled(String provider) {}
}
