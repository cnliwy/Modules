package com.liwy.music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mzule.activityrouter.annotation.Router;

@Router("music/:musicName")
public class MusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity_music);
        String musicName = getIntent().getExtras().getString("musicName");
        System.out.println("正在播放：" + musicName);
        TextView contentTV = (TextView) findViewById(R.id.tv_content);
        contentTV.setText("正在播放：" + musicName);
    }
}
