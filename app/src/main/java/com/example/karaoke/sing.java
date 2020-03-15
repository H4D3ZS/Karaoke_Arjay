package com.example.karaoke;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class sing extends AppCompatActivity {

    /////////////////////////////
    //////////////////////////
    //////////////////////////////
    ////////// Audio Input / Output
    boolean isRecording = false;
    AudioManager am = null;
    AudioRecord record = null;
    AudioTrack track = null;
    //////////////////////////////////


    ////////////////////////////////
    ////////////////////////////////
    /////Karaoke Stream////////////
    ///////////////////////////////
    ProgressDialog mDialog;
    VideoView videoView;

    ///LocalHost
//    String video_URL = "http://192.168.0.111/songs/youll_be_safe_here.mp4";

    String video_URL = "http://paytrixx.com/songs/youll_be_safe_here.mp4";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing);
        setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);



        videoView = findViewById(R.id.videoView);

        initRecordAndTrack();

        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);

        (new Thread()
        {
            @Override
            public void run()
            {
                recordAndPlay();
            }
        }).start();

        Button startButton = (Button) findViewById(R.id.start_btn);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDialog = new ProgressDialog(sing.this);
                mDialog.setMessage("Please Wait...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                if (!isRecording)
                {
                    startRecordAndPlay();
                }

                try {
                    if (!videoView.isPlaying()) {
                        Uri uri = Uri.parse(video_URL);
                        videoView.setVideoURI(uri);
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {

                            }
                        });
                    }
                    else {
                        videoView.pause();

                    }
                }


                catch (Exception E){

                }
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mDialog.dismiss();
                        mp.setLooping(true);
                        videoView.start();
                    }
                });

            }





        });
        Button stopButton = (Button) findViewById(R.id.stop_btn);
        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isRecording)
                {
                    stopRecordAndPlay();
                }
            }
        });
    }

    private void initRecordAndTrack()
    {
        int min = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        record = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                min);
        if (AcousticEchoCanceler.isAvailable())
        {
            AcousticEchoCanceler echoCancler = AcousticEchoCanceler.create(record.getAudioSessionId());
            echoCancler.setEnabled(true);
        }
        int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, maxJitter,
                AudioTrack.MODE_STREAM);
    }

    private void recordAndPlay()
    {
        short[] lin = new short[1024];
        int num = 0;
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        while (true)
        {
            if (isRecording)
            {
                num = record.read(lin, 0, 1024);
                track.write(lin, 0, num);
            }
        }
    }

    private void startRecordAndPlay()
    {
        record.startRecording();
        track.play();
        isRecording = true;
    }

    private void stopRecordAndPlay()
    {
        record.stop();
        track.pause();
        isRecording = false;
    }

    }



