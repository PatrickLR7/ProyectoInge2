package com.example.phoenixdroid.proyectoinge2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phoenixdroid.proyectoinge2.Utils.BaseDeDatos;
import com.example.phoenixdroid.proyectoinge2.Utils.Config;
import com.example.phoenixdroid.proyectoinge2.Utils.CopyFolder;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoEncuentro;
import com.example.phoenixdroid.proyectoinge2.Utils.SenalVertical;
import com.example.phoenixdroid.proyectoinge2.Utils.SintetizadorVoz;
import com.example.phoenixdroid.proyectoinge2.Utils.Zona;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    MapView mapView; // Mapa
    MapController mapViewController; //Controlador para el mapa.
    GeoPoint routeCenter = new GeoPoint(9.91163,-84.1783); //Coordenadas de referencia.
    LocationManager locationmanager; //Controlador de ubicación
    ArrayList<PuntoEncuentro> puntosE; //Lista de los puntos seguros.
    ArrayList<SenalVertical> senalesV; // Lista de las señales verticales.
    List<List<GeoPoint>> rutasE = new ArrayList<>(59); //Lista de rutas de evacuación.
    BaseDeDatos bdMapa; //Base de datos que guarda información clave del mapa.
    SensorManager sensorManager;
    Sensor sensor;
    RotationGestureOverlay mRotationGestureOverlay; // Para utilizar gestos para rotar el mapa.

    SintetizadorVoz sv;
    double latActual = 0;
    double lonActual = 0;
    ImageView brujula;

    int markerUbi = -1;

    GeoPoint puntoEMasCercano = null;
    List<GeoPoint> rutaALaZonaSegura = null;
    Polyline rutaActual = null;
    ImageView orientacionUsuario;
    float gradosAux = 0f;

    private ImageButton buttonEnable;
    private static final int CAMERA_REQUEST = 50;
    private boolean flashLightStatus = false;

    /**
     * Metodo que se ejecuta cuando se crea esta actividad.
     * @param savedInstanceState: la instancia previa de esta actividad.
     */
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        //mRotationGestureOverlay = new RotationGestureOverlay(mapView);
       // mRotationGestureOverlay.setEnabled(true);
        mapView.setMultiTouchControls(true);
       // mapView.getOverlays().add(this.mRotationGestureOverlay);
        mapView.setUseDataConnection(false);
        CopyFolder.copyAssets(this);

        mapViewController = (MapController) mapView.getController();
        mapViewController.setZoom(19);
        mapView.setTileSource(new XYTileSource("tiles", 10, 18, 256, ".png", new String[0]));


        bdMapa = new BaseDeDatos(getApplicationContext());
        parseXML();

        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            assert locationmanager != null;
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
        } catch (SecurityException ignored) { }

        orientacionUsuario = findViewById(R.drawable.icon_persona); //CAMBIAR POR CONO
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); //Sensor de la orientación del teléfono
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        sv = new SintetizadorVoz(this, 2);
        //dibujarRutasEvacuacion();
        markersPuntosE();
        brujula = findViewById(R.id.brujumas);

        buttonEnable = findViewById(R.id.luz);
        buttonEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flashLightStatus) {
                    flashLightOn();
                    buttonEnable.setImageResource(R.drawable.light1);
                } else {
                    flashLightOff();
                    buttonEnable.setImageResource(R.drawable.light2);
                }
                flashLightStatus ^= true;
            }
        });
    }

    /**
     * Metodo para crear el menu.
     * @param menu layout con el menu.
     * @return true si se crea correctamente.
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    /**
     * Metodo para realizar una accion al seleccionar items del menu.
     * @param item una opcion del menu.
     * @return true si realiza la accion correctamente.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){
            case R.id.nav_Inicio:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.nav_Ayuda:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MapActivity.this);
                View mView1 = getLayoutInflater().inflate(R.layout.ayuda_mapa, null);
                Button bt1 = (Button) mView1.findViewById(R.id.btnA2);
                TextView title1 = new TextView(this);
                title1.setText("Ayuda");
                title1.setBackgroundColor(getColor(android.R.color.white));
                title1.setPadding(10, 10, 10, 10);
                title1.setGravity(Gravity.CENTER);
                title1.setTextColor(getColor(R.color.colorPrimary));
                title1.setTextSize(20);
                builder1.setCustomTitle(title1);
                builder1.setView(mView1);
                final AlertDialog ad1 = builder1.create();
                bt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad1.dismiss();
                    }
                });
                ad1.show();
                break;
            case R.id.nav_Acerca:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(MapActivity.this);
                View mView2 = getLayoutInflater().inflate(R.layout.acercade, null);
                Button bt2 = (Button) mView2.findViewById(R.id.btnA);
                TextView title2 = new TextView(this);
                title2.setText("Desarrollado por: \n PhoenixDroid");
                title2.setBackgroundColor(getColor(android.R.color.white));
                title2.setPadding(10, 10, 10, 10);
                title2.setGravity(Gravity.CENTER);
                title2.setTextColor(getColor(R.color.colorPrimary));
                title2.setTextSize(20);
                builder2.setCustomTitle(title2);
                builder2.setView(mView2);
                final AlertDialog ad2 = builder2.create();
                bt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad2.dismiss();
                    }
                });
                ad2.show();
        }
        return true;
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

        if(tipo == 1) {
            if(0 <= markerUbi) {
                mapView.getOverlays().remove(markerUbi);
            }
            markerUbi = mapView.getOverlays().size();

            Drawable d = getResources().getDrawable(R.drawable.radius_circle_person);
            marker.setIcon(d);
        } else if (tipo == 2) {
            Drawable d = getResources().getDrawable(R.drawable.icon_senal);
            marker.setIcon(d);
        } else if (tipo == 3) {
            Drawable d = getResources().getDrawable(R.drawable.icon_zona_segura);
            marker.setIcon(d);
        }
        mapView.getOverlays().add(marker);
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
        } catch (XmlPullParserException | IOException ignored) { }
    }

    /**
     * Lee desde un archivo XML las rutas de evacuación y  y las enlista.
     * @param parser XmlPullParser que contiene los datos leidos desde el archivo xml de rutas de evacuación.
     */
    public void processParsingRE(XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType = parser.getEventType();
        List<GeoPoint> rEActual = null;
        int id = 0;
        boolean flagID = false;
        boolean flagPunto = false;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tag = parser.getName();
                    if ("way".equals(tag)) { rEActual = new ArrayList<>(); }
                    if ("id".equals(tag)) { flagID = true; }
                    if ("punto".equals(tag)) { flagPunto = true; }
                    break;

                case XmlPullParser.TEXT:
                    String text = parser.getText();
                    if (flagPunto) {
                        String[] coordenadas = text.split(" ");
                        double latitud = Double.parseDouble(coordenadas[0].substring(4));
                        double longitud = Double.parseDouble(coordenadas[1].substring(4));

                        assert rEActual != null;
                        rEActual.add(new GeoPoint(latitud, longitud));
                    } else if (flagID) {
                        id = Integer.parseInt(text.substring(1));
                    }
                    break;

                case XmlPullParser.END_TAG:
                    tag = parser.getName();
                    if (tag.equals("way")) { rutasE.add(id, rEActual); }
                    if ("id".equals(tag)) { flagID = false; }
                    if ("punto".equals(tag)) { flagPunto = false; }
                    break;
            }
            eventType = parser.next();
        }
        Config.rutasE = rutasE;
    }

    /**
     * Lee los puntos de encuentro desde un archivo XML y los guarda en un ArrayList.
     * @param parser XmlPullParser que contiene los datos leidos desde el archivo xml de puntos de encuentro.
     */
    public void processParsingPE(XmlPullParser parser) throws IOException, XmlPullParserException {
        puntosE = new ArrayList<>();
        int eventType = parser.getEventType();
        PuntoEncuentro puntoEActual = null;
        int ID = 0;
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
                            puntoEActual.id = ID;
                            ++ID;
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
        Config.puntosEncuentro = puntosE;
    }

    /**
     * Lee las  señales verticales desde un archivo XML y los guarda en un ArrayList.
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
        Config.senalesVerticales = senalesV;
    }

    /**
     * Dibuja en el mapa la ruta de evacuación más cercana a un punto dado.
     */
    private void dibujarRutasEvacuacion(GeoPoint puntoUsuario, List<GeoPoint> ruta, int posPuntoSeguro, int posUsuario, int tipoRuta) {
        List<GeoPoint> rutaSegura = new ArrayList<>();
        if (tipoRuta == 0) {
            for (int x = 0; x < posUsuario; x++) {
                rutaSegura.add(ruta.get(x));
            }
            rutaSegura.add(puntoUsuario);
        } else if (tipoRuta == 1) {
            rutaSegura.add(puntoUsuario);
            for (int x = posUsuario; x < ruta.size(); x++) {
                rutaSegura.add(ruta.get(x));
            }
        } else {
            if (posPuntoSeguro < posUsuario) {
                rutaSegura.add(puntoUsuario);
                for (int x = posPuntoSeguro; x < posUsuario; x++) {
                    rutaSegura.add(ruta.get(x));
                }

            } else {
                rutaSegura.add(puntoUsuario);
                for (int x = posUsuario; x < posPuntoSeguro; x++) {
                    rutaSegura.add(ruta.get(x));
                }
            }
        }

        if (rutaActual != null) {
            rutaActual.setVisible(false);
        }

        rutaALaZonaSegura = rutaSegura;
        Config.rutaHaciaLaZonaSegura = rutaALaZonaSegura;

        Polyline polyline = new Polyline();
        polyline.setColor(Color.parseColor("#B6523C"));
        mapView.getOverlays().add(polyline);
        polyline.setPoints(rutaSegura);
        rutaActual = polyline;
        polyline.setVisible(true);
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
        if (senalesV != null && !senalesV.isEmpty()) {
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
    private void markersSenalesRuta(){
        if (senalesV != null && !senalesV.isEmpty()) {
            for (int i = 0; i < senalesV.size(); i++) {
                GeoPoint senal = new GeoPoint(senalesV.get(i).latSV, senalesV.get(i).lonSV);
                for (int j = 0;  j < rutaALaZonaSegura.size(); j++) {
                    if (senal.distanceToAsDouble(rutaALaZonaSegura.get(j)) < 5) {
                        addMarker(senal, "Señal: " + Integer.toString(senalesV.get(i).id),2);
                    }
                }
            }
        }
    }

    /**
     * Inicia el activity de Simple Camera.
     * @param v
     */
    public void iSimpleCamera(View v){
        Intent i = new Intent(this, SimpleCameraActivity.class);
        startActivity(i);
    }

    /**
     * Metodo que revisa los cambios en la ubicación del usuario.
     * @param location ubicación del usuario.
     */
    @Override
    public void onLocationChanged(Location location) {
        GeoPoint miPosicion = new GeoPoint(location.getLatitude(),location.getLongitude());
        Config.usuarioLat = location.getLatitude();
        Config.usuarioLon = location.getLongitude();
        routeCenter = miPosicion;
        mapViewController.animateTo(routeCenter);
        if (latActual != miPosicion.getLatitude() || lonActual != miPosicion.getLongitude()) {
            latActual = miPosicion.getLatitude();
            lonActual = miPosicion.getLongitude();

            //Se obtiene la ruta cercana al usuario
            int posUsuarioEnRuta = 0;
            double distanciaMin = Double.MAX_VALUE;
            for (int x = 0; x < rutasE.size(); x++) {
                List<GeoPoint> rutaTemp = rutasE.get(x);
                for (int y = 0; y < rutaTemp.size(); y++) {
                    double dist = rutaTemp.get(y).distanceToAsDouble(miPosicion);
                    if (dist < distanciaMin) {
                        posUsuarioEnRuta = y;
                        distanciaMin = dist;
                        rutaALaZonaSegura = rutaTemp;
                    }
                }
            }

            //Basado en la ruta calculada anteriormente, se obtiene el punto seguro
            int posPuntoSeguro = buscarPuntoSeguro(rutaALaZonaSegura.get(0));
            int tipoRuta = -1;
            if (posPuntoSeguro != -1) {
                puntoEMasCercano = new GeoPoint(puntosE.get(posPuntoSeguro).latitud, puntosE.get(posPuntoSeguro).longitud);
                Config.puntoEncuentroMasCercano = puntosE.get(posPuntoSeguro);
                distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
                tipoRuta = 0;
            } else {
                posPuntoSeguro = buscarPuntoSeguro(rutaALaZonaSegura.get(rutaALaZonaSegura.size()-1));
                if (posPuntoSeguro != -1) {
                    puntoEMasCercano = new GeoPoint(puntosE.get(posPuntoSeguro).latitud, puntosE.get(posPuntoSeguro).longitud);
                    Config.puntoEncuentroMasCercano = puntosE.get(posPuntoSeguro);
                    distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
                    tipoRuta = 1;

                } else {
                    for (int x = 0; x < puntosE.size(); x++) {
                        PuntoEncuentro pETemp =puntosE.get(x);
                        for (int y = 0; y < rutaALaZonaSegura.size(); y++) {
                            GeoPoint temp2 = rutaALaZonaSegura.get(y);
                            if (pETemp.compareTo(temp2)) {
                                posPuntoSeguro = y;
                                puntoEMasCercano = new GeoPoint(pETemp.latitud, pETemp.longitud);
                                Config.puntoEncuentroMasCercano = puntosE.get(posPuntoSeguro);
                                distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
                                tipoRuta = 2;
                                y = 1000000;
                                x = 1000000;
                            }
                        }
                    }
                }
            }
            dibujarRutasEvacuacion(miPosicion, rutaALaZonaSegura, posPuntoSeguro, posUsuarioEnRuta, tipoRuta);
            Toast.makeText(this,"Distancia a la zona segura más cercana: " + Integer.toString((int)distanciaMin)  + " metros." ,Toast.LENGTH_LONG).show();
            markersSenalesRuta();
            addMarker(miPosicion, "Mi ubicacion", 1);
            //verificarCercaniaZona(distanciaMin2, distanciaMin);
        }
    }

    /**
     * Revisa si el geoPoint que recibe como parametro es una zona segura.
     * @param gp el punto que se quiere verificar
     * @return si gp es punto seguro, retorna el indice del mismo en la lista de puntos seguros. Si no es punto seguro, retorna -1;
     */
    private int buscarPuntoSeguro(GeoPoint gp) {
        for (int x = 0; x < puntosE.size(); x++) {
            PuntoEncuentro pETemp = puntosE.get(x);
            if (pETemp.compareTo(gp)) {
                return x;
            }
        }
        return -1;
    }

    private void verificarCercaniaZona(double distSenal, double distZona) {
        String texto = "";
        if (distSenal <= 20) {
            texto = texto + "Se está aproximando a una señal vertical. Por favor, revísela. ";
        }
        if (distZona <= 20) {
            texto = texto + "Ha llegado a la zona segura.";
        }
        sv.hablar(texto);
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
        sv.stop();
    }

    /**
     * Metodo que se encarga de manejar los cambios en el estado del sensor.
     * @param event: el tipo de evento registrado por el sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float grados = Math.round(event.values[0]);
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                gradosAux,
                -grados,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        brujula.startAnimation(ra);
        gradosAux = -grados;
        mapView.setMapOrientation(gradosAux);

    }

    /**
     * Metodo que no se usa, pero es necesario tener escrito por el SensorEventListener.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    /**
     * Metodo que vuelva a activar el sensor de cambio de orientación.
     */
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Metodo que desactiva el sensor para ahorrar batería
     */
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Metodo encargado de encender la linterna.
     */
    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            assert cameraManager != null;
            String cameraId = cameraManager.getCameraIdList()[0];

            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException ignored) { }
    }

    /**
     * Metodo encargado de apagar la linterna.
     */
    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            assert cameraManager != null;
            String cameraId = cameraManager.getCameraIdList()[0];

            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException ignored) { }
    }

    /**
     * Pruebas Menu
     */
    public void menuAct(View v){
        Intent i = new Intent(getApplicationContext(), Menu.class);
        startActivity(i);
    }

    public void instructions(View v){
        Intent i = new Intent(getApplicationContext(), VideosActivity.class);
        Config.zona = Zona.INSTRUCCIONES;
        startActivity(i);
    }

    /**
     * Metodo encargado de solicitar permiso para el uso de la camara. Invocado luego de que el usuario elija pasarse al entorno de realidad aumentada.
     * @param requestCode: Identificador del hardware requerido.
     * @param permissions: Permisos requeridos.
     * @param grantResults: Resultados de la solicitud.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buttonEnable.setEnabled(false);
                } else {
                    Toast.makeText(MapActivity.this, "Permission Denied for the Camera",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}