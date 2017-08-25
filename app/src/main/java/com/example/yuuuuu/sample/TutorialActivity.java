package com.example.yuuuuu.sample;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HOME on 2017-08-24.
 */

public class TutorialActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        MyVideoView videoView = (MyVideoView) findViewById(R.id.videoView);

        String uriPath = "android.resource://" + "com.example.yuuuuu.sample" + "/" + R.raw.video_;
        Uri uri = Uri.parse(uriPath);
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer arg0) {
                finish();
            }
        });
    }

}
