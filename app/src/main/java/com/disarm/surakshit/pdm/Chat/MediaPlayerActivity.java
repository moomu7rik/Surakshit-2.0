package com.disarm.surakshit.pdm.Chat;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.disarm.surakshit.pdm.R;

import android.media.MediaPlayer;

import android.util.Log;
import android.view.View;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class MediaPlayerActivity extends Activity {

    Button start,pause,stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        start=(Button)findViewById(R.id.button1);
        pause=(Button)findViewById(R.id.button2);
        stop=(Button)findViewById(R.id.button3);
        //creating media player
        final MediaPlayer mp=new MediaPlayer();
        try{
            String source = getIntent().getStringExtra("url");
            //File src = Environment.getExternalStoragePublicDirectory(source);

               String name = FilenameUtils.getName(source);
            
            //you can change the path, here path is external directory(e.g. sdcard) /Music/maine.mp3
            mp.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/DMS/Working/SurakshitAudio/"+name);

            mp.prepare();
        }catch(Exception e){e.printStackTrace();}

        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
            }
        });
        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();
            }
        });
        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
            }
        });

    }

}

