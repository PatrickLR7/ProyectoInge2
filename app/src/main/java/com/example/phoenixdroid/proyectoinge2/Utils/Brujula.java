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
    private SensorManager manager;
    private Context contexto;
    private int grados;

    public Brujula(Context context)
    {
        grados = 0;
        contexto = context;
        Resume();
    }

    public int getGrados()
    {
        return grados;
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
        grados = Math.round(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // No se usa, pero es necesario declararlo
    }
}
