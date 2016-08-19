package com.example.cher.avarecorder;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1224;
    private ImageView playButton;
    private ImageView recordButton;
    private ImageView stopButton;
    private AudioRecord audioRecord;
    private Boolean recording;
    private DataOutputStream dataOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        disableButtons();
        checkPermissionStatus();
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
        } else {
            requestUserForPermission();
        }
    }

    private void setPlayButton(){
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");

                int shortSizeInBytes = Short.SIZE/Byte.SIZE;

                int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
                short[] audioData = new short[bufferSizeInBytes];

                try {
                    InputStream inputStream = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

                    int i = 0;
                    while(dataInputStream.available() > 0){
                        audioData[i] = dataInputStream.readShort();
                        i++;
                    }

                    dataInputStream.close();

                    AudioTrack audioTrack = new AudioTrack(
                            AudioManager.STREAM_MUSIC,
                            11025,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            bufferSizeInBytes,
                            AudioTrack.MODE_STREAM);

                    audioTrack.play();
                    audioTrack.write(audioData, 0, bufferSizeInBytes);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setRecordButton(){
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recording = true;
                File file = new File(Environment.getExternalStorageDirectory(), "test.pcm"); //may change test.pcm to time + ".pcm"

                try {
                    file.createNewFile();

                    OutputStream outputStream = new FileOutputStream(file);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                    dataOutputStream = new DataOutputStream(bufferedOutputStream);

                    int minBufferSize = AudioRecord.getMinBufferSize(11025,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);

                    short[] audioData = new short[minBufferSize];

                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                            11025,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            minBufferSize);

                    audioRecord.startRecording();

                    while(recording){
                        int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
                        for(int i = 0; i < numberOfShort; i++){
                            dataOutputStream.writeShort(audioData[i]);
                        }
                    }
//                    audioRecord.stop();
//                    dataOutputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void setStopButton(){
        recording = false;
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    audioRecord.stop();
                    dataOutputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
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
     * Note that permissions is a String array and grantResults is an integer Array.
     * This means that:
     * - permissions[0] relates to grantResults[0]
     * - permissions[1] relates to grantResults[1]
     * - permissions[2] relates to grantResults[2]
     * - etc
     *
     * For EACH permission asked. So if you ask for three permissions, both of these arrays
     * will have size 3 like shown above.
     *
     * In our case, we only asked for one permission, so we only check permissions[0] and grantResults[0]
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

/*
#4AACEE
 */

