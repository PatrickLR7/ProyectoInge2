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
import android.widget.Toast;

import com.example.phoenixdroid.proyectoinge2.Utils.BaseDeDatos;
import com.example.phoenixdroid.proyectoinge2.Utils.CopyFolder;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoCardinal;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoEncuentro;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoRuta;
import com.example.phoenixdroid.proyectoinge2.Utils.RutaEvacuacion;
import com.example.phoenixdroid.proyectoinge2.Utils.SenalVertical;
import com.example.phoenixdroid.proyectoinge2.Utils.SintetizadorVoz;

import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NoVidente extends AppCompatActivity implements View.OnClickListener, SensorEventListener, LocationListener
{
    ArrayList<PuntoEncuentro> puntosE; //Lista de los puntos seguros.
    ArrayList<SenalVertical> senalesV; // Lista de las señales verticales.
    BaseDeDatos bdMapa; //Base de datos que guarda información clave del mapa.
    double latActual, lonActual, distancia;
    GeoPoint puntoUsuario, puntoProximo;
    int grados, puntoCardinalTel; //Grados de 0 a 360 de la orientación,
    List<RutaEvacuacion> rutasE; //Lista de rutas de evacuación.
    LocationManager locationManager; //Controlador de ubicación
    PuntoCardinal pc;
    PuntoEncuentro puntoMasCercano;
    SintetizadorVoz sv;
    SensorManager sensorManager;

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
        parseXML();

        Button btn_guiar = findViewById(R.id.btn_guiarNoVidente);
        btn_guiar.setOnClickListener(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); //Sensor de la orientación del teléfono
        sv = new SintetizadorVoz(this); //Clase con TextToSpeech

        grados = 0; //Orientación del teléfono
        puntoCardinalTel = 0; //
        latActual = 0;
        lonActual = 0;
        puntoMasCercano = null;
        puntoUsuario = null;
        puntoProximo = null;
        distancia = 0;
        pc = new PuntoCardinal();
    }

    @Override
    public void onClick(View view)
    {
        guiar();
    }

    private void guiar()
    {
        String texto = "";
        //texto = "La orientación es " + grados + " grados. ";
        texto = puntoCardinalTel(texto) + ", ";
        texto = puntoCardinalPunto(texto);
        texto = texto + " y la distancia es " + (int) distancia + " metros. ";
        sv.hablar(texto);
    }

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

    private String puntoCardinalPunto(String texto)
    {
        int direccion = pc.determinateDirection(puntoUsuario, puntoProximo);
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
        GeoPoint miPosicion = new GeoPoint(location.getLatitude(),location.getLongitude());
        puntoUsuario = miPosicion;
        if (latActual != miPosicion.getLatitude() || lonActual != miPosicion.getLongitude()) {
            latActual = miPosicion.getLatitude();
            lonActual = miPosicion.getLongitude();

            double distanciaMin = Integer.MAX_VALUE;
            for (int x = 0; x < puntosE.size(); x++) {
                PuntoEncuentro puntoSeguro = puntosE.get(x);
                GeoPoint aux = new GeoPoint(puntoSeguro.latitud, puntoSeguro.longitud);
                double dist = miPosicion.distanceToAsDouble(aux);
                if (dist < distanciaMin) {
                    puntoMasCercano = puntoSeguro;
                    distanciaMin = dist;
                }
            }
            distancia = distanciaMin;
            puntoProximo = new GeoPoint(puntoMasCercano.latitud, puntoMasCercano.longitud);

            int pos = 0;
            double distanciaMin2 = Integer.MAX_VALUE;
            for (int x = 0; x < senalesV.size(); x++) {
                SenalVertical senal = senalesV.get(x);
                GeoPoint aux = new GeoPoint(senal.latSV, senal.lonSV);
                double dist = miPosicion.distanceToAsDouble(aux);
                if (dist < distanciaMin2) {
                    pos = x;
                    distanciaMin2 = dist;
                }
            }

        }
    }

    /**
     * Metodo para leer datos desde un archivo XML.
     */
    private void parseXML()
    {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getAssets().open("puntos_encuentro.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsingPE(parser);

            parserFactory = XmlPullParserFactory.newInstance();
            parser = parserFactory.newPullParser();
            is = getAssets().open("rutasEvacuacion.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsingRE(parser);

            parserFactory = XmlPullParserFactory.newInstance();
            parser = parserFactory.newPullParser();
            is = getAssets().open("senalesVerticales.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsingSV(parser);
        } catch (XmlPullParserException ignored) { } catch (IOException ignored) { }
    }

    /**
     * Lee desde un archivo XML las rutas de evacuación y  y las enlista.
     * @param parser XmlPullParser que contiene los datos leidos desde el archivo xml de rutas de evacuación.
     */
    public void processParsingRE(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        int eventType = parser.getEventType();
        PuntoRuta pRActual = null;
        LinkedList<PuntoRuta> listaPR = new LinkedList<>();

        RutaEvacuacion rEActual = null;
        rutasE = new LinkedList<>();
        boolean idFlag = false;
        boolean puntoFlag = false;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tag = parser.getName();

                    if ("node".equals(tag)) {
                        pRActual = new PuntoRuta();
                    }
                    if ("way".equals(tag)) {
                        rEActual = new RutaEvacuacion();
                    }
                    if ("id".equals(tag)) {
                        idFlag = true;
                    }
                    if ("punto".equals(tag)) {
                        puntoFlag = true;
                    }
                    break;

                case XmlPullParser.TEXT:
                    if (pRActual != null) {
                        String aux = parser.getText();
                        String[] partes = aux.split(" ");
                        pRActual.id = Integer.parseInt(partes[0].substring(4, partes[0].length() - 1));
                        pRActual.lat = Double.parseDouble(partes[1].substring(5, partes[1].length() - 1));
                        pRActual.lon = Double.parseDouble(partes[2].substring(5, partes[2].length() - 1));
                    }
                    if (idFlag) {
                        if (rEActual != null) {
                            rEActual.id = Integer.parseInt(parser.getText());
                        }
                    }
                    if (puntoFlag) {
                        int miID = Integer.parseInt(parser.getText());
                        int pos = buscar(listaPR, miID);
                        if (pos != -1) {
                            PuntoRuta aux = listaPR.get(pos);
                            if (rEActual != null) {
                                rEActual.camino.add(new GeoPoint(aux.lat, aux.lon));
                            }
                            //listaPR.remove(pos);
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    tag = parser.getName();
                    if (tag.equals("node")) {
                        listaPR.add(pRActual);
                        pRActual = null;
                    }
                    if (tag.equals("way")) {
                        rutasE.add(rEActual);
                        rEActual = null;
                    }
                    if ("id".equals(tag)) {
                        idFlag = false;
                    }
                    if ("punto".equals(tag)) {
                        puntoFlag = false;
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    /**
     * Lee los puntos de encuentro desde un archivo XML y los guarda en un ArrayList.
     * @param parser XmlPullParser que contiene los datos leidos desde el archivo xml de puntos de encuentro.
     */
    public void processParsingPE(XmlPullParser parser) throws IOException, XmlPullParserException {
        puntosE = new ArrayList<>();
        int eventType = parser.getEventType();
        PuntoEncuentro puntoEActual = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag;

            switch(eventType){
                case XmlPullParser.START_TAG:
                    tag = parser.getName();

                    if ("node".equals(tag)) {
                        puntoEActual = new PuntoEncuentro();
                        puntosE.add(puntoEActual);

                    } else {
                        if ("nombre".equals(tag)){
                            puntoEActual.nombre = parser.nextText();
                        } else if ("lat".equals(tag)) {
                            puntoEActual.latitud = Double.parseDouble(parser.nextText());
                        } else if ("lon".equals(tag)) {
                            puntoEActual.longitud = Double.parseDouble(parser.nextText());
                            bdMapa.agregarPuntoSeguro(puntoEActual.latitud, puntoEActual.longitud, puntoEActual.nombre);
                        }

                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    /**
     * Lee las señales verticales desde un archivo XML y los guarda en un ArrayList.
     * @param parser XmlPullParser que contiene los datos leidos desde el archivo xml de puntos de encuentro.
     */
    public void processParsingSV(XmlPullParser parser) throws IOException, XmlPullParserException {
        senalesV = new ArrayList<>();
        int eventType = parser.getEventType();
        SenalVertical senalVActual = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag;

            switch(eventType){
                case XmlPullParser.START_TAG:
                    tag = parser.getName();

                    if ("node".equals(tag)) {
                        senalVActual = new SenalVertical();
                        senalesV.add(senalVActual);
                    } else if (senalVActual != null) {
                        if ("num".equals(tag)){
                            senalVActual.id = Integer.parseInt(parser.nextText());
                        } else if ("lat".equals(tag)) {
                            senalVActual.latSV = Double.parseDouble(parser.nextText());
                        } else if ("lon".equals(tag)) {
                            senalVActual.lonSV = Double.parseDouble(parser.nextText());
                        } else if ("lado".equals(tag)){
                            senalVActual.lado = parser.nextText();
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    /**
     * Busca un punto en la lista de puntos de las rutas de evacuación.
     * @param lista Lista de puntos de la ruta de evacuación.
     * @param ID Punto que se quiere buscar.
     * @return Indice en la lista del punto que se busca; -1 si el punto no se encuentra.
     */
    private int buscar (List<PuntoRuta> lista, int ID) {
        int resultado = -1;
        for (int x = 0; x < lista.size(); x++) {
            if (lista.get(x).id == ID) {
                resultado = x;
                x = lista.size() * 5;
            }
        }
        return resultado;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {
        //No se usa pero hace falta tenerlo
    }

    /**
     * Metodo que se ejecuta cuando el provedor cambia de estado.
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
