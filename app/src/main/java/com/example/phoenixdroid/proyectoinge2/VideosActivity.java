package com.example.phoenixdroid.proyectoinge2;

import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.phoenixdroid.proyectoinge2.Utils.Config;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;




public class VideosActivity extends AppCompatActivity implements View.OnClickListener{

    private MediaController mediaController;
    private VideoView video;

    private final static int PERMISSION_CODE = 111;
    private final static int PERMISSION_CODE2 = 112;

    private Button botonMapa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        Button boton = (Button) findViewById(R.id.play_Video);
        boton.setOnClickListener(this);
        video = (VideoView) findViewById(R.id.videoView);



        botonMapa = (Button) findViewById(R.id.botonMapa);
        botonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapActivity();
            }
        });

        //botonMapa.setOnClickListener(this);

        botonMapa.setEnabled(askPermission());

        //botonMapa.setEnabled(askPermission2());

    }

    public void ejecutarVideo(View v){
        mediaController = new MediaController(this);
        String ruta = "android.resource://com.cpt.sample/raw/";
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
        video.start();
    }

    @Override
    public void onClick(View view) {
        ejecutarVideo(view);
    }





    /**
     * This check if permission is granted for this application, if this is not and we are running the application
     * on a device that is api >= 23 this will trigger a user request where we caught the result in
     * onRequestPermissionResult
     * @return
     */
    private boolean askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);


            return false;
        }
        else return true;

    }
    private boolean askPermission2(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE2);

            return false;
        }
        else return true;

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
            case PERMISSION_CODE: {
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
