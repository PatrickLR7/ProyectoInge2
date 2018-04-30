package com.example.phoenixdroid.proyectoinge2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity {

    private static final String TAG = "MainActivity";
    
    YouTubePlayerView mYoutubeView;
    Button btnPlay;
    YouTubePlayer.OnInitializedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");
        btnPlay = (Button) findViewById(R.id.playerButton);
        mYoutubeView = (YouTubePlayerView) findViewById(R.id.videoPlayer);

        mListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "onClick: Initialization done");

                youTubePlayer.loadVideo("PXdjpBfTQFU");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onClick: Initialization failed");
            }
        };

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Initializing Youtube Player");
                mYoutubeView.initialize(YoutubeConfig.getApiKey(), mListener);
            }
        });
    }

    public void iniciarActZonas (View v){
        Intent i = new Intent(getApplicationContext(), activity_zonas.class);
        startActivity(i);
    }
}
