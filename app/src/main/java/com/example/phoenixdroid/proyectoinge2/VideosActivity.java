package com.example.phoenixdroid.proyectoinge2;

import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.phoenixdroid.proyectoinge2.Utils.Config;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;




public class VideosActivity extends AppCompatActivity implements View.OnClickListener{

    private VideoView video;
    private MediaController mediaController;
    private Button botonMapa;


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


        botonMapa.setEnabled(resultPermission());
    }

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
            default:
                numZona = R.raw.entrada_calle_los_alvarez;
                break;
        }
        ruta += numZona;
        Uri uri = Uri.parse(ruta);
        video.setVideoURI(uri);
        video.setMediaController(mediaController);
        mediaController.setAnchorView(video);
        video.start();
    }

    @Override
    public void onClick(View view) {
        ejecutarVideo(view);
    }


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
     * Go to the main activity
     */
    public void goToMapActivity(){
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }



}
