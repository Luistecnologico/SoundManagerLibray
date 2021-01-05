package com.nippontic.soundmanagerlibray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private String filePath = "";
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private boolean isSpeaker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePath = getExternalCacheDir().getAbsolutePath();
        filePath += "/audiorecordtest.3gp";

        // Comportamiento de los botones

        findViewById(R.id.btnStartPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    ((Button)v).setText(R.string.btn_stop_play);
                    findViewById(R.id.btnStartRecord).setEnabled(false);
                } else {
                    ((Button)v).setText(R.string.btn_start_play);
                    findViewById(R.id.btnStartRecord).setEnabled(true);
                }

                // TODO: implementar inicio de musica
                if (!isPlaying)
                    startPlaying();
                else
                    stopPlaying();

                isPlaying = !isPlaying;
            }
        });

        findViewById(R.id.btnStartRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    ((Button)v).setText(R.string.btn_stop_record);
                    findViewById(R.id.btnStartPlay).setEnabled(false);
                } else {
                    ((Button)v).setText(R.string.btn_start_record);
                    findViewById(R.id.btnStartPlay).setEnabled(true);
                }

                // TODO: implementar grabacion
                if (!isRecording)
                    startRecorder();
                else
                    stopRecording();

                isRecording = !isRecording;
            }
        });

        ((SwitchCompat)findViewById(R.id.swtAudioMode)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText(R.string.swt_audio_mode_earpiece);
                    isSpeaker = false;
                } else {
                    buttonView.setText(R.string.swt_audio_mode_speaker);
                    isSpeaker = true;
                }
            }
        });
    }

    private void startRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // Not working on earpiece -> ACC_ADTS,
        recorder.setOutputFile(filePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("soundManagerLibray", "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(filePath);
            player.setAudioAttributes(getAudioAtributes());
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("SoundManagerLibrary", "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private AudioAttributes getAudioAtributes() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage( isSpeaker ? AudioAttributes.USAGE_MEDIA : AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(isSpeaker ? AudioAttributes.CONTENT_TYPE_MUSIC : AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        return audioAttributes;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
}