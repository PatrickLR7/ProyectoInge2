package com.example.phoenixdroid.proyectoinge2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoVidente extends AppCompatActivity implements View.OnClickListener
{
    private Button btn_guiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_vidente);
        btn_guiar = findViewById(R.id.btn_guiarNoVidente);
        btn_guiar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        guiar();
    }

    private void guiar()
    {

    }
}
