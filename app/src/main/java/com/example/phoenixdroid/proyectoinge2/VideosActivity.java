package com.example.phoenixdroid.proyectoinge2;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.example.phoenixdroid.proyectoinge2.Utils.Config;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

public class VideosActivity extends AppCompatActivity implements View.OnClickListener{

    private VideoView video; // Reproductor de video
    private MediaController mediaController; // controlador del medio de video
    private Button botonMapa; // boton para mostrar mapa

    /**
     * Metodo que se ejecuta cuando se crea esta actividad.
     * @param savedInstanceState: la instancia previa de esta actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        Button boton = (Button) findViewById(R.id.play_Video);
        boton.setOnClickListener(this);
        video = (VideoView) findViewById(R.id.videoView);
        mediaController = new MediaController(this);

        botonMapa = (Button) findViewById(R.id.botonMapa);
        botonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapActivity();
            }
        });

        // Se habilita el boton del mapa luego de recibir los permisos correspondientes.
        botonMapa.setEnabled(resultPermission());
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
                Toast.makeText(this, "Pendiente. ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_Acerca:
                AlertDialog.Builder builder = new AlertDialog.Builder(VideosActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.acercade, null);
                Button bt = (Button) mView.findViewById(R.id.btnA);
                TextView title = new TextView(this);
                title.setText("Desarrollado por: \n PhoenixDroid");
                title.setBackgroundColor(getColor(android.R.color.white));
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(getColor(R.color.colorPrimary));
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
        return true;
    }

    /**
     * Utiliza esta vista para ubicar un marco donde se va a desplegar el video.
     * @param v: vista sobre la que se ubica el reproductor de video.
     */
    public void ejecutarVideo(View v){
        mediaController = new MediaController(this);
        String ruta = "android.resource://com.example.phoenixdroid.proyectoinge2/";
        int numZona;
        switch (Config.zona){
            case MATINILLA:
                numZona = R.raw.matinilla;
                break;
            case PASO_MACHETE:
                numZona = R.raw.paso_machete;
                break;
            case CRUCE_PABELLON:
                numZona = R.raw.cruce_pabellon;
                break;
            case CALLE_LA_CANADA:
                numZona = R.raw.calle_la_canada;
                break;
            case QUEBRADA_CANOAS:
                numZona = R.raw.quebrada_canoas;
                break;
            case QUEBRADA_NAVAJAS:
                numZona = R.raw.quebrada_navajas;
                break;
            case QUEBRADA_TAPEZCO:
                numZona = R.raw.quebrada_tapezco;
                break;
            case CALLE_LOS_DELGADO:
                numZona = R.raw.calle_los_delgado;
                break;
            case QUEBRADA_SANGIJUELA:
                numZona = R.raw.quebrada_sanguijuela;
                break;
            case SALIDA_QUEBRADA_PITIER:
                numZona = R.raw.salida_quebrada_pitier;
                break;
            case PUENTE_SECTOR_LA_FUENTE:
                numZona = R.raw.puente_sector_la_fuente;
                break;
            case CALLE_PARALELA_RIO_URUCA:
                numZona = R.raw.calle_paralela_rio_uruca;
                break;
            case ENTRADA_CALLE_LOS_ALVAREZ:
                numZona = R.raw.entrada_calle_los_alvarez;
                break;
            default:
                numZona = R.raw.instrucciones;
                break;
        }
        ruta += numZona;
        Uri uri = Uri.parse(ruta);
        video.setVisibility(View.VISIBLE);
        video.setVideoURI(uri);
        video.setMediaController(mediaController);
        mediaController.setAnchorView(video);
        video.start();
    }

    /**
     * Metodo para manejar el clickeo del boton para empezar el video.
     * @param view: La vista donde se ubica el objeto clickeado.
     */
    @Override
    public void onClick(View view) {
        ejecutarVideo(view);
    }

    /**
     * Se encarga de obtener permisos del usuario para acceso a ubicacion y uso de memoria del dispositivo.
     * @return true, si el usuario concedio permisos, false en caso contrario.
     */
    private boolean resultPermission(){
        boolean concedidos = true;
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            concedidos = false;
        }
        else if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            concedidos = false;
        }

        if(!concedidos){
            Toast.makeText(this,"Los permisos de acceso a ubicación y memoria del dispositivo son necesarios!",Toast.LENGTH_LONG).show();
        }
        return concedidos;
    }

    /**
     * This is the callback response from calling ActivityCompat.requestPermission,
     * if the user discard the permission the application will be closed
     * @param requestCode   It should be PERMISSION_CODE
     * @param permissions   The permission
     * @param grantResults  The result that we check
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 111: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    botonMapa.setEnabled(true);
                }
                else {
                    finish();
                }
            }
            case 112: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    botonMapa.setEnabled(true);
                }
                else {
                    finish();
                }
            }
        }
    }

    /**
     * Go to the map activity
     */
    public void goToMapActivity(){
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }
}
