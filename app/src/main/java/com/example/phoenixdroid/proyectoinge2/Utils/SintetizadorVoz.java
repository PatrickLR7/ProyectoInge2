package com.example.phoenixdroid.proyectoinge2.Utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Carlos Portuguez on 5/17/2018.
 */

public class SintetizadorVoz implements TextToSpeech.OnInitListener
{
    private TextToSpeech tts;
    Context context;
    private int pantallaUso;

    /**
     * Constructor de la clase a través del cual se le envía el contexto del activity que lo ocupa.
     */
    public SintetizadorVoz(Context contexto, int pantalla)
    {
        pantallaUso = pantalla;
        context = contexto;
        tts = new TextToSpeech(context, this);

    }

    /**
     * Metodo que inicializa el servicio.
     * @param status: estado en el que logró inicializarse el TextToSpeech.
     */
    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            Locale locSpanish = new Locale("spa", "MEX");
            tts.setLanguage(locSpanish);
            if(pantallaUso == 1)
            {
                String aviso = "Advertencia: En esta pantalla se le dan instrucciones de cómo avanzar, mas no sobre obstáculos en la vía como huecos o alcantarillas. ";
                hablar2(aviso);
            }

        }
    }

    /**
     * Metodo para decir la distancia que se debe recorrer.
     * @param distancia: distancia en metros que hay hasta el siguiente punto.
     */
    public void decirDistancia(double distancia)
    {
        String texto = "La distancia es" + (int) distancia + "metros";
        hablar(texto);
    }

    /**
     * Metodo para decir la orientación del teléfono.
     * @param grados: orientación en grados el dispositivo.
     */
    public void decirOrientacion(int grados)
    {
        String texto = "La orientacion es" + grados;
        hablar(texto);
    }

    /**
     * Metodo que simplemente lee una cadena.
     */
    public void hablar(String texto)  {



        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);


    }


    /**
     * Metodo que simplemente lee una cadena.
     */
    private void hablar2(String texto)  {



        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        //Config.termino = true;
    }

    /**
     * Metodo para detener el servicio de TextToSpeech.
     */
    public void stop()
    {
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
    }
}
