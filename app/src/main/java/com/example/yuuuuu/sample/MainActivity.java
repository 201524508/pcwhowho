package com.example.yuuuuu.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    public ImageButton btn_cam;
    public ImageButton btn_search;
    public ImageButton btn_alram;
    public ImageButton btn_qna;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
        int firstviewShow = a.getInt("First",0);

        System.out.println("*********"+firstviewShow);

        if(firstviewShow!=1) {
            Intent intent = new Intent(getApplication(), TutorialActivity.class);
            startActivity(intent);

            SharedPreferences.Editor editor = a.edit();
            editor.putInt("First",1);
            editor.commit();

        }
        else {
            //IntroAct.finish();
        }

        btn_cam = (ImageButton) findViewById(R.id.btn_cam);
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_alram = (ImageButton) findViewById(R.id.btn_alram);
        btn_qna = (ImageButton) findViewById(R.id.btn_qna);

        btn_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        btn_alram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarView.class);
                startActivity(intent);
            }
        });

        btn_qna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// 웹사이트로 연결
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/%ED%8F%89%EC%B0%BD-%ED%9B%84-WHO-494206594262308/"));
                startActivity(intent);
            }
        });


        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
        finish();
    }

}
