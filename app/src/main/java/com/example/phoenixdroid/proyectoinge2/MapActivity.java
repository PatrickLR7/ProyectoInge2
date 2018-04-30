package com.example.phoenixdroid.proyectoinge2;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoEncuentro;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoRuta;
import com.example.phoenixdroid.proyectoinge2.Utils.RutaEvacuacion;
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

public class MapActivity extends AppCompatActivity implements LocationListener {

    MapView mapView;
    MapController mapViewController;
    GeoPoint routeCenter = new GeoPoint(9.91163,-84.1783);
    GeoPoint routeCenter2 = new GeoPoint(9.910699,-84.176321);
    LocationManager locationmanager;
    ArrayList<PuntoEncuentro> puntosE;
    List<RutaEvacuacion> rutasE;

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

        //addMarker(routeCenter, routeCenter2);
        //addMarker(routeCenter2,routeCenter);

        parseXML();
        dibujarRutasEvacuacion();
        markersPuntosE();
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            assert locationmanager != null;
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        } catch (SecurityException ignored) { }
    }

    public void addMarker(GeoPoint Center, GeoPoint Center2) {
        Marker marker = new Marker(mapView);
        marker.setPosition(Center);

        //marker.setIcon(getDrawable(R.drawable.loc));
        // marker.setAnchor(1000,1000);

        Toast.makeText(this,"Distacia: " + Double.toString(Center.distanceToAsDouble(Center2))  + " metros" ,Toast.LENGTH_LONG).show();

        mapView.getOverlays().add(marker);

        //Limpia, barre
        //mapView.getOverlays().clear();

        mapView.invalidate();
    }

    public void addMarker(GeoPoint Center, String nombre) {
        Marker marker= new Marker(mapView);
        marker.setPosition(Center);
        marker.setTitle(nombre);

        //marker.setIcon(getDrawable(R.drawable.loc));
        // marker.setAnchor(1000,1000);

        //Toast.makeText(this,Double.toString(Center.distanceToAsDouble(Center2)),Toast.LENGTH_LONG).show();
        mapView.getOverlays().add(marker);

        //mapView.getOverlays().clear();
        mapView.invalidate();
    }

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
        } catch (XmlPullParserException ignored) { } catch (IOException ignored) { }
    }

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

    private int buscar (LinkedList<PuntoRuta> lista, int ID) {
        int resultado = -1;
        for (int x = 0; x < lista.size(); x++) {
            if (lista.get(x).id == ID) {
                resultado = x;
                x = lista.size() * 5;
            }
        }
        return resultado;
    }

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
                    } else if (puntoEActual != null) {
                        if ("nombre".equals(tag)){
                            puntoEActual.nombre = parser.nextText();
                        } else if ("lat".equals(tag)) {
                            puntoEActual.latitud = Double.parseDouble(parser.nextText());
                        } else if ("lon".equals(tag)) {
                            puntoEActual.longitud = Double.parseDouble(parser.nextText());
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    public void dibujarRutasEvacuacion() {
        if (rutasE != null && !rutasE.isEmpty()) {
            for (int x = 0; x < rutasE.size(); x++) {
                Polyline polyline = new Polyline();
                List<GeoPoint> pathPoints = rutasE.get(x).camino;

                polyline.setColor(Color.RED);
                mapView.getOverlays().add(polyline);
                polyline.setPoints(pathPoints);
            }
        }
    }

    public void markersPuntosE() {
        if (puntosE != null && !puntosE.isEmpty()) {
            for (int i = 0; i < puntosE.size(); i++) {
                routeCenter.setLatitude(puntosE.get(i).latitud);
                routeCenter.setLongitude(puntosE.get(i).longitud);
                addMarker(routeCenter, puntosE.get(i).nombre);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //GeoPoint center = new GeoPoint(location.getLatitude(),location.getLongitude());

        //mapViewController.animateTo(center);
        //addMarker(center, "Mi ubicacion");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationmanager != null) {
            locationmanager.removeUpdates(this);
        }
    }
}