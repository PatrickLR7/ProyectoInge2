package com.example.phoenixdroid.proyectoinge2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.phoenixdroid.proyectoinge2.Utils.Config;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Actualmente no disponible." ,Toast.LENGTH_LONG).show();
            }
        });

        askPermission();
    }


    /**
     * Metodo para iniciar el activity para seleccionar entre las distintas zonas de Santa Ana.
     * @param v recibe el boton con el que se ejecutará el método.
     */
    public void iniciarActZonas (View v){
        Intent i = new Intent(getApplicationContext(), ZonasActivity.class);
        startActivity(i);
    }

    /**
     * Método para solicitar permiso para utilizar los servicios de ubicación.
     */
    private boolean askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 112);
            return false;
        }
        else return true;
    }

}
