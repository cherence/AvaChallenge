package com.example.cher.avarecorder;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private static final int SAMPLE_RATE = 8000;
    private ImageView playButton;
    private ImageView recordButton;
    private ImageView stopButton;
    private ProgressBar progressBar;
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
        setAudioLogistics();
        setPlayButton();
        setRecordButton();
        setStopButton();

    }

    private void initializeViews(){
        playButton = (ImageView) findViewById(R.id.play_button_id);
        recordButton = (ImageView) findViewById(R.id.record_button_id);
        stopButton = (ImageView) findViewById(R.id.stop_button_id);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_id);
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

    private void setAudioLogistics(){

    }

    private void setPlayButton(){
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayAsync async = new PlayAsync();
                async.execute();
            }
        });

    }

    private void setRecordButton(){
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recording = true;
                RecordAsync async = new RecordAsync();
                async.execute();
            }
        });
    }


    private void setStopButton(){
        recording = false;
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopAsync async = new StopAsync();
                async.execute();
            }
        });
    }

    private class RecordAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "test.pcm"); //may change test.pcm to time + ".pcm"

                file.createNewFile();

                OutputStream outputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                dataOutputStream = new DataOutputStream(bufferedOutputStream);

                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE){
                    bufferSize = SAMPLE_RATE * 2;
                }

                short[] audioBuffer = new short[bufferSize/2];

                audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                audioRecord.startRecording();

                while(recording){
                    int numberOfShort = audioRecord.read(audioBuffer, 0, bufferSize);
                    for(int i = 0; i < numberOfShort; i++){
                        dataOutputStream.writeShort(audioBuffer[i]);
                    }
                }
                    audioRecord.stop();
                    dataOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
        }
    }

    private class PlayAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");

            int shortSizeInBytes = Short.SIZE/Byte.SIZE;

            int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
            short[] audioBuffer = new short[bufferSizeInBytes];

            try {
                InputStream inputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

                int i = 0;
                while(dataInputStream.available() > 0){
                    audioBuffer[i] = dataInputStream.readShort();
                    i++;
                }

                dataInputStream.close();

                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSizeInBytes,
                        AudioTrack.MODE_STREAM);

                audioTrack.play();
                Log.i(TAG, "onClick: play Audio streaming started");
                audioTrack.write(audioBuffer, 0, bufferSizeInBytes);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
        }
    }

    private class StopAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                audioRecord.stop();
                dataOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
        }
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

public void flac(View view) throws java.io.IOException
    {
        recordFlacButtonPressed = !recordFlacButtonPressed;

        if (recordFlacButtonPressed)
        {
            File flacFile = new File(filepath + "/temp.flac");
            final FLACEncoder flacEncoder = new FLACEncoder();
            EncodingConfiguration encodingConfiguration = new EncodingConfiguration();
            StreamConfiguration streamConfiguration = new StreamConfiguration(1, 16, 65536, sampleRate, bitsPerSample);
            FLACFileOutputStream flacFileOutputStream = new FLACFileOutputStream(flacFile);

            flacEncoder.setStreamConfiguration(streamConfiguration);
            flacEncoder.setEncodingConfiguration(encodingConfiguration);
            flacEncoder.setOutputStream(flacFileOutputStream);

            flacEncoder.openFLACStream();

            bufferSizeInBytes = sampleRate * bitsPerSample / 8 / 10 * 3;//300 milliseconds in bytes

            audioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSizeInBytes);
            audioRecorder.startRecording();

            recordingThread = new Thread(new Runnable()
            {
                public void run()
                {
                    while (recordFlacButtonPressed)
                    {
                        audioRecorder.read(data, 0, bufferSizeInBytes);

                        //it is not the best way to convert every two bytes of byte array to int array
                        short[] shorts = new short[data.length / 2];
                        ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(shorts);

                        int[] dataInt = new int[shorts.length];
                        for (int i = 0; i < shorts.length; i++)
                        {
                            dataInt[i] = (int) shorts[i];
                        }

                        flacEncoder.addSamples(dataInt, bufferSizeInBytes / 2); //for signed ints -32656 + 32656
                        //flacEncoder.addSamples(dataInt, bufferSizeInBytes); //for signed ints - 127 + 128

                        bytesToEncode += bufferSizeInBytes;
                    }

                    audioRecorder.stop();
                    audioRecorder.release();
                    audioRecorder = null;

                    try
                    {
                        flacEncoder.encodeSamples(bytesToEncode / 2, false);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    flacEncoder.clear();
                }
            });

            if (recordFlacButtonPressed)
            {
                recordingThread.start();
            }
        }
    }


 */

