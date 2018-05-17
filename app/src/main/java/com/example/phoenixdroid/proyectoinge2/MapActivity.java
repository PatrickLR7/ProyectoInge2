package com.example.phoenixdroid.proyectoinge2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import com.example.phoenixdroid.proyectoinge2.Utils.BaseDeDatos;
import com.example.phoenixdroid.proyectoinge2.Utils.Config;
import com.example.phoenixdroid.proyectoinge2.Utils.CopyFolder;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoEncuentro;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoRuta;
import com.example.phoenixdroid.proyectoinge2.Utils.RutaEvacuacion;
import com.example.phoenixdroid.proyectoinge2.Utils.SenalVertical;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements LocationListener, TextToSpeech.OnInitListener {

    MapView mapView; // Mapa

    MapController mapViewController; //Controlador para el mapa.

    GeoPoint routeCenter = new GeoPoint(9.91163,-84.1783); //Coordenadas de referencia.

    LocationManager locationmanager; //Controlador de ubicación

    ArrayList<PuntoEncuentro> puntosE; //Lista de los puntos seguros.

    ArrayList<SenalVertical> senalesV; // Lista de las señales verticales.

    List<RutaEvacuacion> rutasE; //Lista de rutas de evacuación.

    BaseDeDatos bdMapa; //Base de datos que guarda información clave del mapa.

    private TextToSpeech tts;
    double latActual = 0;
    double lonActual = 0;

    Marker markerUbi;

    PuntoEncuentro puntoMasCercano = null;


    /**
     * Metodo que se ejecuta cuando se crea esta actividad.
     * @param savedInstanceState: la instancia previa de esta actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(false);
        CopyFolder.copyAssets(this);

        mapViewController = (MapController) mapView.getController();
        mapViewController.setZoom(17);
        mapViewController.animateTo(routeCenter);
        mapView.setTileSource(new XYTileSource("tiles", 10, 18, 256, ".png", new String[0]));


        bdMapa = new BaseDeDatos(getApplicationContext());
        parseXML();
        //dibujarRutasEvacuacion();

        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            assert locationmanager != null;
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
            } catch (SecurityException ignored) {
            }

        tts = new TextToSpeech(this, this);
        markersPuntosE();
        //markersSenalesV();

        markerUbi = new Marker(mapView);

    }

    /**
     * Crea un marker y lo agrega al mapa.
     * @param Center Coordenadas del marker
     * @param nombre Descripción del marker.
     * @param tipo Descripción del tipo(persona 1, señal vertical 2, zona segura 3).
     */
    public void addMarker(GeoPoint Center, String nombre, int tipo) {
        Marker marker = new Marker(mapView);
        marker.setPosition(Center);
        marker.setTitle(nombre);
        String markerID = marker.getId();
        if(tipo == 1) {
            Drawable d = getResources().getDrawable(R.drawable.radius_circle);
            marker.setIcon(d);
        }else  if(tipo == 2) {
            Drawable d = getResources().getDrawable(R.drawable.icon_senal);
            marker.setIcon(d);
        }else  if(tipo == 3) {
            Drawable d = getResources().getDrawable(R.drawable.icon_zona_segura);
            marker.setIcon(d);
        }

        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    /**
     * Agrega un marker en el mapa.
     * @param m Marker que se quiere agregar.
     */
    public void addMarker(Marker m) {
        mapView.getOverlays().add(m);
        mapView.invalidate();
    }

    /**
     * Borra un marker del mapa.
     * @param m Marker que se quiere borrar.
     */
    public void deleteMarker(Marker m) {
        mapView.getOverlays().remove(m);
        mapView.invalidate();
    }

    /**
     * Metodo para leer datos desde un archivo XML.
     */
    private void parseXML() {
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
    public void processParsingRE(XmlPullParser parser) throws IOException, XmlPullParserException {
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
     * Dibuja en el mapa las diferentes rutas de evacuación.
     */
    public void dibujarRutasEvacuacion() {
        if (rutasE != null && !rutasE.isEmpty()) {

            for (int x = 0; x < rutasE.size(); x++) {
                Polyline polyline = new Polyline();
                List<GeoPoint> pathPoints = rutasE.get(x).camino;

                int brownColorValue = Color.parseColor("#B6523C");
                polyline.setColor(brownColorValue);
                mapView.getOverlays().add(polyline);
                polyline.setPoints(pathPoints);
            }
        }
    }

    /**
     * Dibuja en el mapa la ruta de evacuación más cercana a un punto dado.
     * @param gp: El punto desde el que se calcula la ruta más corta.
     */
    public void dibujarRutasEvacuacion(GeoPoint gp) {
        if (rutasE != null && !rutasE.isEmpty()) {
            double menorDistancia = Double.MAX_VALUE;
            List<GeoPoint> rutaMasCercana = rutasE.get(0).camino;
            GeoPoint puntoCercanoRuta; // Punta dentro la ruta más cercano a gp
            for (int x = 0; x < rutasE.size(); x++) {
                List<GeoPoint> rutaActual = rutasE.get(x).camino;
                for (int y = 0; y < rutaActual.size(); y++) {
                    double aux = rutaActual.get(y).distanceToAsDouble(gp);
                    if (aux < menorDistancia) {
                        menorDistancia = aux;
                        rutaMasCercana = rutaActual;
                        puntoCercanoRuta = rutaActual.get(y);
                    }
                }
            }

            Polyline polyline = new Polyline();
            int brownColorValue = Color.parseColor("#B6523C");
            polyline.setColor(brownColorValue);
            mapView.getOverlays().add(polyline);
            polyline.setPoints(rutaMasCercana);
        }
    }

    /**
     * Coloca marcadores en el mapa en la posición en la que se ubican los puntos de encuentro.
     */
    public void markersPuntosE() {

        if (puntosE != null && !puntosE.isEmpty()) {
            for (int i = 0; i < puntosE.size(); i++) {
                routeCenter.setLatitude(puntosE.get(i).latitud);
                routeCenter.setLongitude(puntosE.get(i).longitud);
                addMarker(routeCenter, puntosE.get(i).nombre,3);
            }
        }
    }

    /**
     * Coloca marcadores en el mapa en la posición en la que se ubican las señales verticales.
     */
    public void markersSenalesV(){
        if(senalesV != null && !senalesV.isEmpty()) {
            for (int i = 0; i < senalesV.size(); i++) {
                routeCenter.setLatitude(senalesV.get(i).latSV);
                routeCenter.setLongitude(senalesV.get(i).lonSV);
                addMarker(routeCenter, "Señal: " + Integer.toString(senalesV.get(i).id),2);
            }
        }
    }

    /**
     * Coloca marcadores en el mapa en la posición en la que se ubican las señales verticales.
     */
    public void markersSenalesV(int senal){
        if (senalesV != null && !senalesV.isEmpty()) {
            SenalVertical aux = senalesV.get(senal);
            routeCenter.setLatitude(aux.latSV);
            routeCenter.setLongitude(aux.lonSV);
            addMarker(routeCenter, "Señal: " + Integer.toString(aux.id),2);
        }
    }


    /**
     * Metodo que revisa los cambios en la ubicación del usuario.
     * @param location ubicación del usuario.
     */
    @Override
    public void onLocationChanged(Location location) {
        GeoPoint miPosicion = new GeoPoint(location.getLatitude(),location.getLongitude());
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

            Toast.makeText(this,"Distancia a la zona segura más cercana: " + Double.toString(distanciaMin)  + " metros." ,Toast.LENGTH_LONG).show();
            dibujarRutasEvacuacion(miPosicion);
            addMarker(miPosicion, "Mi ubicacion", 1);
            markersSenalesV(pos);
            speakOut(distanciaMin);
        }
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

    /**
     * Metodo que se ejecuta cuando el activity se cierra, para terminar todos los procesos que estuvieran en ejecución.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationmanager != null) {
            locationmanager.removeUpdates(this);
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            Locale locSpanish = new Locale("spa", "MEX");
            tts.setLanguage(locSpanish);
        }
    }

    public void speakOut(double distancia)
    {
        int api = Integer.valueOf(android.os.Build.VERSION.SDK);
        String texto = "La distancia es" + (int) distancia + "metros";
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }
}