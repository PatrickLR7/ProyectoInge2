package com.example.phoenixdroid.proyectoinge2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phoenixdroid.proyectoinge2.Utils.Config;
import com.example.phoenixdroid.proyectoinge2.Utils.Zona;

public class ZonasActivity extends AppCompatActivity {

    /**
     * Metodo que se ejecuta cuando se crea esta actividad.
     * @param savedInstanceState: la instancia previa de esta actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zonas);
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
                Toast.makeText(this, "Pendiente. ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_Acerca:
                AlertDialog.Builder builder = new AlertDialog.Builder(ZonasActivity.this);
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
     * Metodo para seleccionar entre las distintas zonas de Santa Ana y guardar la seleccion en la clase Config.
     * @param v recibe el boton con el que se ejecutará el método.
     */
    public void actVideos (View v){
        Intent i = new Intent(getApplicationContext(), VideosActivity.class);
        switch(v.getId())
        {
            case R.id.btnAlvarez:
                Config.zona = Zona.ENTRADA_CALLE_LOS_ALVAREZ;
                startActivity(i);
                break;
            case R.id.btnCanada:
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

    /**
     * This check if permission is granted for this application, if this is not and we are running the application
     * on a device that is api >= 23 this will trigger a user request where we caught the result in
     * onRequestPermissionResult
     * @return
     */
    private boolean askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return false;
        }
        else return true;
    }
}