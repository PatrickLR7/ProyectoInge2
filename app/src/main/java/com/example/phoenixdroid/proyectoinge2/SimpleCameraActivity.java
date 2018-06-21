package com.example.phoenixdroid.proyectoinge2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.phoenixdroid.proyectoinge2.Utils.Config;
import com.example.phoenixdroid.proyectoinge2.Utils.CustomWorldHelper;
import com.example.phoenixdroid.proyectoinge2.Utils.PuntoEncuentro;
import com.example.phoenixdroid.proyectoinge2.Utils.SenalVertical;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class SimpleCameraActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, android.location.LocationListener, OnClickBeyondarObjectListener {

    /** Fragmento de BeyondAR, permite la interacción con los objetos del mundo */
    private BeyondarFragmentSupport mBeyondarFragment;

    /** Mundo. */
    private World mWorld;

    /** Entorno del juego. */
    private CustomWorldHelper customWorldHelper;

    /** Vista del radar y otras utilidades que utiliza */
    private RadarView mRadarView;
    private RadarWorldPlugin mRadarPlugin;
    private SeekBar mSeekBarMaxDistance;
    private TextView mTextviewMaxDistance;

    /** Lista de los puntos seguros. */
    ArrayList<PuntoEncuentro> puntosE;

    /** Lista de las señales verticales. */
    ArrayList<SenalVertical> senalesV;

    /** Lista de objetos que actualmente se muestran en la camara. */
    private ArrayList<GeoObject> geoObjects;

    GeoPoint puntoEMasCercano = null;
    List<GeoPoint> rutaALaZonaSegura = null;

    public static List<List<GeoPoint>> rutasE; //Lista de rutas de evacuación.

    double latActual = 0;
    double lonActual = 0;

    /** Usuario. */
    private GeoObject user;

    /** Para poner distancia de PuntoE mas cercano*/
    private double distPuntoMC = 0;
    private String nombrePuntoMC = Config.puntoEncuentroMasCercano.nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String Permiso[] = {"android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION"};

        // Start home activity
        requestPermission(Permiso, 1);


        askPermission();

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_simple_camera);
        customWorldHelper = new CustomWorldHelper();

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);

        // We create the world and fill it ...
        mWorld = customWorldHelper.generateObjects(this); // BeyondarOdjects

        // Parametros para variar la distancia de los objetos
        mBeyondarFragment.setMaxDistanceToRender(3000); // Asigno distancia máxima de renderización de objetos
        mBeyondarFragment.setDistanceFactor(4); // El factor de distancia de objetos (más cerca entre mayor valor)
        mBeyondarFragment.setPushAwayDistance(0); // Para alejar un poco los objetos que están muy cerca
        mBeyondarFragment.setPullCloserDistance(0); // Para acercar un poco los objetos que están muy lejos
        mBeyondarFragment.setWorld(mWorld);
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);

        mWorld.onResume();

        //Posicion del usuario.
        user = new GeoObject(1000l);
        user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
        user.setImageResource(R.drawable.icon_persona);
        user.setName("Posicion del usuario");
        mWorld.addBeyondarObject(user);


        //Permitimos que BeyondAR actualice automáticamente la posición del mundo con respecto al usuario
        BeyondarLocationManager.addWorldLocationUpdate(mWorld);

        //Asigna la posicion inicial del usuario al GeoObjeto correspondiente.
        BeyondarLocationManager.addGeoObjectLocationUpdate(user);

        // Le pasamos el LocationManager al BeyondarLocationManager.
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
        }
        BeyondarLocationManager.setLocationManager(locationManager);

        //Activa los servicios de ubicacion para el ayudante BeyondarLocationManager.
        BeyondarLocationManager.enable();

        mTextviewMaxDistance = (TextView) findViewById(R.id.textViewMax);
        mSeekBarMaxDistance = (SeekBar) findViewById(R.id.seekBar4);
        mRadarView = (RadarView) findViewById(R.id.radarView);

        // Create the Radar plugin
        mRadarPlugin = new RadarWorldPlugin(this);

        // set the radar view in to our radar plugin
        mRadarPlugin.setRadarView(mRadarView);

        // Set how far (in meters) we want to display in the view
        mRadarPlugin.setMaxDistance(100);

        // We can customize the color of the items
        mRadarPlugin.setListColor(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, Color.RED);

        // and also the size
        mRadarPlugin.setListDotRadius(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, 3);

        // add the plugin
        mWorld.addPlugin(mRadarPlugin);

        mSeekBarMaxDistance.setOnSeekBarChangeListener(this);
        mSeekBarMaxDistance.setMax(300);
        mSeekBarMaxDistance.setProgress(23);
        mRadarPlugin.setMaxDistance(45);



        puntosE = Config.puntosEncuentro;
        senalesV = Config.senalesVerticales;
        geoObjects = Config.geoObjetos;
        rutasE = Config.rutasE;
    }



    /**
     * Coloca marcadores en el mapa en la posición en la que se ubican las señales verticales.
     */
    public void markersSenalesV(){
        int id = Config.idGeoObjects;
        if (senalesV != null && !senalesV.isEmpty()) {
            for (int i = 0; i < senalesV.size(); i++) {
                GeoPoint senal = new GeoPoint(senalesV.get(i).latSV, senalesV.get(i).lonSV);
                int senalID = 0;
                for (int j = 0;  j < rutaALaZonaSegura.size(); j++) {
                    if (senal.distanceToAsDouble(rutaALaZonaSegura.get(j)) < 5) {
                        // SenalVertical aux = senalesV.get(senal);
                        //  routeCenter.setLatitude(aux.latSV);
                        //  routeCenter.setLongitude(aux.lonSV);
                        //  addMarker(routeCenter, "Señal: " + Integer.toString(aux.id),2);

                        GeoObject go1 = new GeoObject(id++);
                        go1.setGeoPosition(Config.senalesVerticales.get(senalID).latSV, Config.senalesVerticales.get(senalID).lonSV);
                        go1.setImageResource(R.drawable.icon_senal);
                        go1.setName("Señal: " + senal);
                        mWorld.addBeyondarObject(go1);
                        geoObjects.add(go1);
                    }
                }
            }
        }

        Config.geoObjetos = geoObjects;
        Config.idGeoObjects = id;

    }

    /**
     * Metodo encargado de mostrar los dialogos de solicitud de permisos si es necesario.
     * @param permiso: hilera de permisos por pedir
     * @param permissionRequestCode: resultado de obtencion de permisos
     */
    public void requestPermission(String permiso[], int permissionRequestCode) {
        //Preguntar por permiso
        if (askPermissions()) {
            ActivityCompat.requestPermissions(this, permiso, permissionRequestCode);
        }
    }

    /**
     * Metodo encargado de cerciorarse si es o no necesaria la solicitud dinamica de permisos.
     * @return verdadero si android del dispositivo es mayor a Lollipop, en caso contrario falso
     */
    private boolean askPermissions() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
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
                puntoEMasCercano = new GeoPoint(puntosE.get(posPuntoSeguro).latitud, puntosE.get(posPuntoSeguro).longitud);
                distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
                distPuntoMC = distanciaMin;
                nombrePuntoMC = puntosE.get(posPuntoSeguro).nombre;
            } else {
                posPuntoSeguro = buscarPuntoSeguro(rutaALaZonaSegura.get(rutaALaZonaSegura.size()-1));
                if (posPuntoSeguro != -1) {
                    puntoEMasCercano = new GeoPoint(puntosE.get(posPuntoSeguro).latitud, puntosE.get(posPuntoSeguro).longitud);
                    distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
                    distPuntoMC = distanciaMin;
                    nombrePuntoMC = puntosE.get(posPuntoSeguro).nombre;
                } else {
                    for (int x = 0; x < puntosE.size(); x++) {
                        PuntoEncuentro pETemp =puntosE.get(x);
                        for (int y = 0; y < rutaALaZonaSegura.size(); y++) {
                            GeoPoint temp2 = rutaALaZonaSegura.get(y);
                            if (pETemp.compareTo(temp2)) {
                                puntoEMasCercano = new GeoPoint(pETemp.latitud, pETemp.longitud);;
                                distanciaMin = miPosicion.distanceToAsDouble(puntoEMasCercano);
                                distPuntoMC = distanciaMin;
                                nombrePuntoMC = pETemp.nombre;
                                y = 1000000;
                                x = 1000000;
                            }
                        }
                    }
                }
            }
            markersSenalesV();
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

    /**
     * Metodo para manejar si el usuario toca un geo objeto presente en la camara.
     * @param arrayList: Lista de los geo objetos presentes. El primer elemento es el objeto que ha sido clickeado.
     */
    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> arrayList) {
            String nombre = arrayList.get(0).getName();

            if(("Punto de Encuentro: " + nombrePuntoMC).equals(nombre)){
                Toast toast1 = Toast.makeText(this, nombre + " Distancia: " + Integer.toString((int)distPuntoMC) + " metros", Toast.LENGTH_SHORT);
                View customT = toast1.getView();
                customT.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                TextView t = customT.findViewById(android.R.id.message);
                t.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                toast1.show();

            } else{

                Toast toast1 = Toast.makeText(this, nombre, Toast.LENGTH_SHORT);
                View customT = toast1.getView();
                customT.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                TextView t = customT.findViewById(android.R.id.message);
                t.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                toast1.show();
            }

    }

    /**
     *
     * @param provider:
     * @param status:
     * @param extras:
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    /**
     *
     * @param provider:
     */
    @Override
    public void onProviderDisabled(String provider) {}

    /**
     *
     * @param provider:
     */
    @Override
    public void onProviderEnabled(String provider) {}


    /**
     * Utilizado para cambiar la distancia máxima del radar mediante un seekBar (no se utiliza en la versión final del juego).
     * @param seekBar: Barra para cambiar la distancia máxima.
     * @param progress: Distancia máxima seleccionada en la barra.
     * @param fromUser: Indica si el cambio en la barra (seekbar) fue inicializado por el usuario.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mRadarPlugin == null)
            return;
        if (seekBar == mSeekBarMaxDistance) {
            String str = "Max distance Value: " + progress;
            mTextviewMaxDistance.setText(str);
            mRadarPlugin.setMaxDistance(progress);
        }
    }

    /**
     * Activado cuando el usuario toca el seekBar para cambiar la distancia máxima
     * @param seekBar: La barra en la cual se cambia la distancia.
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    /**
     * Activado cuando el usuario deja de tocar el seekBar.
     * @param seekBar: La barra en la cual se cambia la distancia.
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    /**
     * Método para solicitar permiso para utilizar los servicios de ubicación.
     */
    private boolean askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 112);
            return false;
        }
        else return true;
    }
}