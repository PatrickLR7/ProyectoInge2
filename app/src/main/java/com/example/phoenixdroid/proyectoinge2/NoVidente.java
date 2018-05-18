package com.example.phoenixdroid.proyectoinge2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.phoenixdroid.proyectoinge2.Utils.SintetizadorVoz;

public class NoVidente extends AppCompatActivity implements View.OnClickListener, SensorEventListener
{
    private Button btn_guiar;
    private int grados, puntoCardinal;
    private SintetizadorVoz sv;
    private SensorManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_vidente);

        btn_guiar = findViewById(R.id.btn_guiarNoVidente);
        btn_guiar.setOnClickListener(this);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sv = new SintetizadorVoz(this);

        grados = 0;
        puntoCardinal = 0;
    }

    @Override
    public void onClick(View view)
    {
        guiar();
    }

    private void guiar()
    {
        String texto = "La orientación es ";
        texto = puntoCardinal(texto);
        sv.hablar(texto);
    }

    private String puntoCardinal(String texto)
    {
        texto = texto + grados;

        if ((grados >= 337.5 && grados <= 360) || (grados >= 0 && grados < 22.5))
        {
            texto = texto + ". Esta viendo hacia el norte";
            puntoCardinal = 1;
        }
        else if (grados >= 22.5 &&  grados < 67.5)
        {
            texto = texto + ". Esta viendo hacia el noreste";
            puntoCardinal = 2;
        }
        else if (grados >= 67.5 &&  grados < 112.5)
        {
            texto = texto + ". Esta viendo hacia el este";
            puntoCardinal = 3;
        }
        else if (grados >= 112.5 &&  grados < 157.5)
        {
            texto = texto + ". Esta viendo hacia el sureste";
            puntoCardinal = 4;
        }
        else if (grados >= 157.5 &&  grados < 202.5)
        {
            texto = texto + ". Esta viendo hacia el sur";
            puntoCardinal = 5;
        }
        else if (grados >= 202.5 &&  grados < 247.5)
        {
            texto = texto + ". Esta viendo hacia el suroeste";
            puntoCardinal = 6;
        }
        else if (grados >= 247.5 &&  grados < 292.5)
        {
            texto = texto + ". Esta viendo hacia el oeste";
            puntoCardinal = 7;
        }
        else if (grados >= 292.5 &&  grados < 337.5)
        {
            texto = texto + ". Esta viendo hacia el noroeste";
            puntoCardinal = 8;
        }
        return texto;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // for the system's orientation sensor registered listeners
        manager.registerListener(this,
                                    manager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // to stop the listener and save battery
        manager.unregisterListener(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        sv.stop();
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        grados = Math.round(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {
        //No se usa
    }
}
