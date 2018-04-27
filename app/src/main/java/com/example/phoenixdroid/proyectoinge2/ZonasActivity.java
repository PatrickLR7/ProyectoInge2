package com.example.phoenixdroid.proyectoinge2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.phoenixdroid.proyectoinge2.Utils.Config;
import com.example.phoenixdroid.proyectoinge2.Utils.Zona;

public class ZonasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zonas);
    }

    public void actVideos (View v){
        Intent i = new Intent(getApplicationContext(), VideosActivity.class);
        switch(v.getId())
        {
            case R.id.btnAlvarez:
                Config.zona = Zona.ENTRADA_CALLE_LOS_ALVAREZ;
                startActivity(i);
                break;
            case R.id.btnCañada:
                Config.zona = Zona.CALLE_LA_CANADA;
                startActivity(i);
                break;
            case R.id.btnCDelgado:
                Config.zona = Zona.CALLE_LOS_DELGADO;
                startActivity(i);
                break;
            case R.id.btnCPRioUruca:
                Config.zona = Zona.CALLE_PARALELA_RIO_URUCA;
                startActivity(i);
                break;
            case R.id.btnCruceP:
                Config.zona = Zona.CRUCE_PABELLON;
                startActivity(i);
                break;
            case R.id.btnLaFuente:
                Config.zona = Zona.PUENTE_SECTOR_LA_FUENTE;
                startActivity(i);
                break;
            case R.id.btnMatinilla:
                Config.zona = Zona.MATINILLA;
                startActivity(i);
                break;
            case R.id.btnPMachete:
                Config.zona = Zona.PASO_MACHETE;
                startActivity(i);
                break;
            case R.id.btnQCanoas:
                Config.zona = Zona.QUEBRADA_CANOAS;
                startActivity(i);
                break;
            case R.id.btnQNavajas:
                Config.zona = Zona.QUEBRADA_NAVAJAS;
                startActivity(i);
                break;
            case R.id.btnQTapezco:
                Config.zona = Zona.QUEBRADA_TAPEZCO;
                startActivity(i);
                break;
            case R.id.btnSPitier:
                Config.zona = Zona.SALIDA_QUEBRADA_PITIER;
                startActivity(i);
                break;
            case R.id.btnSanguijuela:
                Config.zona = Zona.QUEBRADA_SANGIJUELA;
                startActivity(i);
                break;
            default:
                throw new RuntimeException("Id de boton desconocido.");
        }
    }


}
