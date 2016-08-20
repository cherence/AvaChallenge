package com.example.cher.mediarecorder;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1224;
    private ImageView playButton;
    private ImageView recordButton;
    private ImageView stopButton;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        checkPermissionStatus();
        disableButtons();
        setPlayButton();
        setRecordButton();
        setStopButton();
    }

    private void initializeViews(){
        playButton = (ImageView) findViewById(R.id.play_button_id);
        recordButton = (ImageView) findViewById(R.id.record_button_id);
        stopButton = (ImageView) findViewById(R.id.stop_button_id);
    }

    private void disableButtons(){
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private void enableButtons(){
        playButton.setEnabled(true);
        recordButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    private void checkPermissionStatus(){
        if(permissionExists()){
            enableButtons();
            createRecorder();
        } else {
            requestUserForPermission();
        }
    }

    private void setPlayButton(){
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,SecurityException,IllegalStateException {
                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(outputFile);
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setRecordButton(){
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the recording
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                }

                catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                recordButton.setEnabled(false);
                stopButton.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setStopButton(){
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop the recording of the audio
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder  = null;

                stopButton.setEnabled(false);
                playButton.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createRecorder(){
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }

    /**
     * Returns true if the permission is granted. False otherwise.
     *
     * NOTE: If we detect that this phone is an older OS then Android M, we assume
     * the permission is true because they are granted at INSTALL time.
     *
     * @return
     */
    @TargetApi(23)
    private boolean permissionExists(){
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion < Build.VERSION_CODES.M){
            // Permissions are already granted during INSTALL TIME for older OS version
            return true;
        }

        int recordGranted = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        int saveGranted = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int playGranted = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (recordGranted == PackageManager.PERMISSION_GRANTED && saveGranted == PackageManager.PERMISSION_GRANTED && playGranted == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    /**
     * This method will request the user for the following permissions: record audio,
     * write to external storage, and read external storage.
     *
     * If a phone is running older OS then Android M, we simply return because
     * those phone are using the OLD permission model and permissions are granted at
     * INSTALL time.
     */
    @TargetApi(23)
    private void requestUserForPermission(){
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion < Build.VERSION_CODES.M){
            // This OS version is lower then Android M, therefore we have old permission model and should not ask for permission
            return;
        }
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    /**
     * This method returns to us the results of our permission request
     *
     * @param requestCode The original code we sent the request with. If this code doesn't match, its not our result.
     * @param permissions Array of permissions in the order they were asked
     * @param grantResults Results for each permission ( granted or not granted ) in the same order of permission array
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if (permissions.length < 0){
                    return;
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    // all permissions were granted! Do whatcha gotta do
                    enableButtons();
                    createRecorder();
                } else {
                    // 1 or more permissions were denied, lets warn the user that we need this permission!
                    Toast.makeText(getApplicationContext(), "You need to grant permission to record " +
                            "audio, write to external storage, and read external storage", Toast.LENGTH_SHORT).show();
                    requestUserForPermission();
                }
                break;
        }
    }
}
