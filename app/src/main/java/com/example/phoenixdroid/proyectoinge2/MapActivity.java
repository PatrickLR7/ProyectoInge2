package com.example.phoenixdroid.proyectoinge2;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.phoenixdroid.proyectoinge2.Utils.PuntoEncuentro;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements LocationListener {


    MapView mapView;
    MapController mapViewController;
    GeoPoint routeCenter = new GeoPoint(9.91163,-84.1783);
    GeoPoint routeCenter2 = new GeoPoint(9.910699,-84.176321);
    LocationManager locationmanager;
    ArrayList<PuntoEncuentro> puntosE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.mapview);

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(false);
        CopyFolder.copyAssets(this);

        mapViewController = (MapController) mapView.getController();
        mapViewController.setZoom(17);
        mapViewController.animateTo(routeCenter);
        mapView.setTileSource(new XYTileSource("tiles", 10, 18, 256, ".png", new String[0]));


        addMarker(routeCenter, routeCenter2);
        addMarker(routeCenter2,routeCenter);

        parseXML();
        markersPuntosE();
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        try {
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        } catch (SecurityException e) {

        }

    }


    public void addMarker(GeoPoint Center, GeoPoint Center2) {



        Marker marker= new Marker(mapView);
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

    private void parseXML(){
        XmlPullParserFactory parserFactory;
        try{
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getAssets().open("puntos_encuentro.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processParsing(parser);
        } catch (XmlPullParserException e){

        } catch (IOException e){

        }
    }

    public void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        puntosE = new ArrayList<>();
        int eventType = parser.getEventType();
        PuntoEncuentro puntoEActual = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String tag = null;

            switch(eventType){
                case XmlPullParser.START_TAG:
                    tag = parser.getName();

                    if("node".equals(tag)){
                        puntoEActual = new PuntoEncuentro();
                        puntosE.add(puntoEActual);
                    } else if (puntoEActual != null){
                        if("nombre".equals(tag)){
                            puntoEActual.nombre = parser.nextText();
                        } else if("lat".equals(tag)){
                            puntoEActual.latitud = Double.parseDouble(parser.nextText());
                        } else if ("lon".equals(tag)){
                            puntoEActual.longitud = Double.parseDouble(parser.nextText());
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }

    }

    public void markersPuntosE(){
        if(!puntosE.isEmpty()) {
            for (int i = 0; i < puntosE.size(); i++) {
                routeCenter.setLatitude(puntosE.get(i).latitud);
                routeCenter.setLongitude(puntosE.get(i).longitud);
                addMarker(routeCenter, puntosE.get(i).nombre);
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        GeoPoint center = new GeoPoint(location.getLatitude(),location.getLongitude());

        mapViewController.animateTo(center);
        addMarker(center, "Mi ubicacion");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if(locationmanager != null){
            locationmanager.removeUpdates(this);
        }
    }


    //https://stackoverflow.com/questions/21874351/how-to-display-route-on-my-osm-application








}
