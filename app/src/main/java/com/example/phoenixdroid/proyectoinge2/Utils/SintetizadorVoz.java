package com.example.phoenixdroid.proyectoinge2.Utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by admlab105 on 5/17/2018.
 */

public class SintetizadorVoz implements TextToSpeech.OnInitListener
{
    private TextToSpeech tts;
    Context context;

    public SintetizadorVoz(Context contexto)
    {
        context = contexto;
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            Locale locSpanish = new Locale("spa", "MEX");
            tts.setLanguage(locSpanish);
        }
    }

    public void decirDistancia(double distancia)
    {
        String texto = "La distancia es" + (int) distancia + "metros";
        hablar(texto);
    }

    public void decirOrientacion(int grados)
    {
        String texto = "La orientacion es" + grados;
        hablar(texto);
    }

    private void hablar(String texto)
    {
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stop()
    {
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
    }
}
