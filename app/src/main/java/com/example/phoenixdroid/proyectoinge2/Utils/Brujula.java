package com.example.phoenixdroid.proyectoinge2.Utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by admlab105 on 5/17/2018.
 */

public class Brujula implements SensorEventListener
{
    private float orientacion;
    private SensorManager manager;
    private Context contexto;
    private SintetizadorVoz sv;

    public Brujula(Context context)
    {
        orientacion = 0f;
        contexto = context;
        sv = new SintetizadorVoz(contexto);
        Resume();
    }

    public void Resume()
    {
        // for the system's orientation sensor registered listeners
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void Pause()
    {
        // to stop the listener and save battery
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // get the angle around the z-axis rotated
        float grados = Math.round(event.values[0]);
        sv.decirOrientacion(grados);
        //tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // No se usa, pero es necesario declararlo
    }
}
