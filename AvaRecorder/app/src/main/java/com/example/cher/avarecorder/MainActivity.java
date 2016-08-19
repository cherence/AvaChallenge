package com.example.cher.avarecorder;

import android.Manifest;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView playButton;
    private ImageView recordButton;
    private ImageView stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setPlayButton();
        setRecordButton();
        setStopButton();

    }

    private void initializeViews(){
        playButton = (ImageView) findViewById(R.id.play_button_id);
        recordButton = (ImageView) findViewById(R.id.record_button_id);
        stopButton = (ImageView) findViewById(R.id.stop_button_id);

    }

    private void setPlayButton(){
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setRecordButton(){
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setStopButton(){
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}


/*
#4AACEE
 */