package com.example.phoenixdroid.proyectoinge2;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.phoenixdroid.proyectoinge2.Utils.Config;

public class VideosActivity extends AppCompatActivity implements View.OnClickListener{

    private MediaController mediaController;
    private VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        Button boton = (Button) findViewById(R.id.play_Video);
        boton.setOnClickListener(this);
        video = (VideoView) findViewById(R.id.videoView);
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
}
