package com.example.phoenixdroid.proyectoinge2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ZonasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zonas);
    }

    public void actVideos (View v){
        Intent i = new Intent(getApplicationContext(), VideosActivity.class);
        startActivity(i);
    }


}
