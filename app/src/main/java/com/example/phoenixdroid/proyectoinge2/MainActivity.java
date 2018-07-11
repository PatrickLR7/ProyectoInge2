package com.example.phoenixdroid.proyectoinge2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phoenixdroid.proyectoinge2.Utils.Config;

public class MainActivity extends AppCompatActivity
{
    Button btn_no_vidente;

    /**
     * Metodo que se ejecuta cuando se crea esta actividad.
     * @param savedInstanceState: la instancia previa de esta actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        //
        setContentView(R.layout.activity_main);

        btn_no_vidente = (Button) findViewById(R.id.button2);
        btn_no_vidente.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Actualmente no disponible." ,Toast.LENGTH_LONG).show();
                iniciarNoVidente();
            }
        });

        askPermission();
    }

    /**
     * Metodo para crear el menu.
     * @param menu layout con el menu.
     * @return true si se crea correctamente.
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        MenuItem item = menu.getItem(1);
        item.setVisible(false);
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
                //
                break;
            case R.id.nav_Acerca:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.acercade, null);
                Button bt = (Button) mView.findViewById(R.id.btnA);
                TextView title = new TextView(this);
                title.setText("Desarrollado por: PhoenixDroid");
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
     * Metodo para iniciar el activity para seleccionar entre las distintas zonas de Santa Ana.
     * @param v recibe el boton con el que se ejecutará el método.
     */
    public void iniciarActZonas (View v)
    {
        Intent i = new Intent(getApplicationContext(), ZonasActivity.class);
        startActivity(i);
    }

    private void iniciarNoVidente()
    {
        Intent i = new Intent(getApplicationContext(), NoVidente.class);
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
