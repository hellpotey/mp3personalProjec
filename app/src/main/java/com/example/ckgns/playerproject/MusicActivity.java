package com.example.ckgns.playerproject;

import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    MyDBHelper myHelper;
    SQLiteDatabase sqlDB;

    static int mSeek;

    MediaPlayer mediaPlayer;


    LinearLayout linLay;
    static TextView textView, title, singer, sbStart, sbEnd, tvInit;
    static ImageView album, play,like;
    static SeekBar seekBar;
    static int bsic;

    ImageView playList, back, next;
    static boolean flag = true;
    //1=순차재생, 2=랜덤재생, 3=1곡반복
    static int abc = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        if (flag) {
            flag = false;
            finish();
        }

        linLay = findViewById(R.id.linLay);
        tvInit = findViewById(R.id.tvInit);
        textView = findViewById(R.id.textView);
        title = findViewById(R.id.title);
        singer = findViewById(R.id.singer);
        album = findViewById(R.id.album);
        playList = findViewById(R.id.playList);
        back = findViewById(R.id.back);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        sbStart = findViewById(R.id.sbStart);
        sbEnd = findViewById(R.id.sbEnd);
        like = findViewById(R.id.like);

        textView.setSelected(true);
        title.setSelected(true);
        album.setSelected(true);


        playList.setOnClickListener(this);
        back.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);

        like.setOnClickListener(this);
        tvInit.setOnClickListener(this);

        MainActivity.btnPlay.callOnClick();
        MainActivity.btnPlay.callOnClick();
        myHelper = new MyDBHelper(this);

        mediaPlayer = new MediaPlayer();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeek = seekBar.getProgress();

                mediaPlayer.seekTo(mSeek);
                mediaPlayer.start();
            }
        });


    }

    @Override
    public void onClick(View v) {
        sqlDB = myHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.playList:
                finish();
                break;
            case R.id.back:
                MainActivity.btnBack.callOnClick();
                break;
            case R.id.play:
                MainActivity.btnPlay.callOnClick();
                break;
            case R.id.next:
                MainActivity.btnNext.callOnClick();
                break;

            case R.id.like:
                int a = 1;
                int b = 2;
                if(tvInit.getText().equals("2")){
                    sqlDB.execSQL("INSERT INTO musicTBL VALUES ('"+
                            title.getText().toString()+"',"+a+");");
                    sqlDB.close();
                    tvInit.setText("1");
                    like.setImageResource(R.mipmap.liket);
                    break;
                }else if(tvInit.getText().equals("1")){
                    sqlDB.execSQL("UPDATE musicTBL SET joayo ="
                            + b + " WHERE title = '"
                            + title.getText().toString() + "';");
                    sqlDB.close();
                    tvInit.setText("2");

                    like.setImageResource(R.mipmap.likef);
                    break;
                }
            case R.id.tvInit:
                myHelper.onUpgrade(sqlDB, 1, 2);
                sqlDB.close();
                like.setImageResource(R.mipmap.likef);
                tvInit.setText("2");
                break;
        }
    }
}
