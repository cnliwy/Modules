package com.liwy.modules;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mzule.activityrouter.router.Routers;
import com.liwy.music.MusicActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent  = new Intent(MainActivity.this, MusicActivity.class);
//                startActivity(intent);
                Routers.open(MainActivity.this, Uri.parse("web://music/似水流年"));
            }
        });

        findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent  = new Intent(MainActivity.this, BookActivity.class);
//                startActivity(intent);
                Routers.open(MainActivity.this, Uri.parse("web://book"));
            }
        });
    }
}
