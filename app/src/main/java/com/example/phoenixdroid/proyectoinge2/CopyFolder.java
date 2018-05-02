package com.example.phoenixdroid.proyectoinge2;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class CopyFolder {
    /**
     * Aprovecha el almacenamiento externo para copiar tiles proveniente de los assets.
     *
     * @param activity actividad que posee los assets
     */
    public static void copyAssets(Activity activity) {

        InputStream in;
        OutputStream out;
        try {
            in = activity.getAssets().open("tiles.zip");

            Log.i(TAG, ": " + Environment.getExternalStorageDirectory());
            File dir = new File(Environment.getExternalStorageDirectory(),
                    "osmdroid");
            Log.i(TAG, "existe: " + dir.exists());
            if (!dir.exists())
                dir.mkdirs();
            File fileZip = new File(dir, "tiles.zip");
            Log.i(TAG, "existe : " + fileZip.exists());

            out = new FileOutputStream(fileZip);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e("tag", "Error al copiar el archivo: " + e.getMessage());
        }
    }

    /**
     * Copia bytes de una entrada a una salida.
     *
     * @param in flujo de entrada
     * @param out flujo de salida
     * @throws IOException si ocurre un error de E/S
     */
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}