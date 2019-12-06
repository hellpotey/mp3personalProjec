package com.example.ckgns.playerproject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int plus;
    ConstraintLayout conLay;

    //리사이클러뷰
    LinearLayoutManager linearLayoutManager;
    MainAdapter mainAdapter;
    ArrayList<MainData> dataList = new ArrayList<MainData>();
    RecyclerView recyclerViewMP3;
    //데이터에이스
    static MyDBHelper myHelper;
    static SQLiteDatabase sqlDB;
    Cursor cursor;
    int good;
    //인텐트
    static int now = 0;
    static boolean check = true;
    private ContentResolver res;
    //메인
    LinearLayout linLay;
    static ImageButton btnPlay, btnNext, btnBack;
    LinearLayout linearLayout;
    ImageView imageView;
    TextView tvTitle, tvSinger;
    static SeekBar pbMP3;
    MediaPlayer mediaPlayer;
    String selectedMP3;
    Boolean flag = true;
    int playbackPosition = 0;
    private long time = 0;
    static final String MP3_PATH = Environment.getExternalStorageDirectory().getPath() + "/Music/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("CH Player");

        getMusicList();
        res = getContentResolver();
        mediaPlayer = new MediaPlayer();

        myHelper = new MyDBHelper(this);
        linearLayout = findViewById(R.id.linearLayout);
        conLay = findViewById(R.id.conLay);
        recyclerViewMP3 = findViewById(R.id.recyclerViewMP3);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMP3.setLayoutManager(linearLayoutManager);
        mainAdapter = new MainAdapter(dataList, this);
        recyclerViewMP3.setAdapter(mainAdapter);


        btnPlay = findViewById(R.id.btnPlay);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        tvTitle = findViewById(R.id.tvTitle);
        tvSinger = findViewById(R.id.tvSinger);
        pbMP3 = findViewById(R.id.pbMP3);
        imageView = findViewById(R.id.imageView);
        ActivityCompat.requestPermissions(this, new String[]
                {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);


        btnPlay.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        pbMP3.setProgress(0);
        selectedMP3 = dataList.get(0).getTitle();

        pbMP3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(),"보조시간"+ MusicActivity.mSeek,Toast.LENGTH_SHORT).show();
                // mediaPlayer.seekTo(MusicActivity.mSeek);
                mediaPlayer.start();
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (now + 1 < dataList.size()) {
                    flag = true;
                    check = true;
                    now++;
                    btnPlay.callOnClick();
                }
            }
        });

        Intent intent = new Intent(MainActivity.this, MusicActivity.class);
        startActivity(intent);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getMusicList() {
        dataList.removeAll(dataList);
        //가져오고 싶은 컬럼 명을 나열합니다. 음악의 아이디, 앰블럼 아이디, 제목, 아스티스트 정보를 가져옵니다.
        String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Audio.Media.DATA + " like ? ",
                new String[]{"%mp3%"}, null);

        while (cursor.moveToNext()) {
            MainData mainData = new MainData();
            mainData.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            mainData.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            mainData.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            mainData.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            mainData.setLike(false);
            dataList.add(mainData);
        }
        cursor.close();
    }

    public void playMusic(MainData mainData) {
        try {
            tvTitle.setSelected(true);
            tvSinger.setSelected(true);
            tvTitle.setText(mainData.getTitle());
            tvSinger.setText(mainData.getArtist());
            cursor = sqlDB.rawQuery("SELECT joayo FROM musicTBL wHERE title ='" + mainData.getTitle() + "';", null);
            if (cursor.getCount() == 0) {
                MusicActivity.like.setImageResource(R.mipmap.likef);
                MusicActivity.tvInit.setText("2");
            } else {
                while (cursor.moveToNext()) {
                    if (cursor.getInt(0) == 1) {
                        MusicActivity.like.setImageResource(R.mipmap.liket);
                        MusicActivity.tvInit.setText("1");
                    } else if (cursor.getInt(0) == 2) {
                        MusicActivity.like.setImageResource(R.mipmap.likef);
                        MusicActivity.tvInit.setText("2");
                    }
                }
            }
            MusicActivity.textView.setText(mainData.getTitle());
            MusicActivity.title.setText(mainData.getTitle());
            MusicActivity.singer.setText(mainData.getArtist());
            Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(mainData.getAlbumId()), getApplication()));
            imageView.setImageBitmap(bitmap);
            MusicActivity.album.setImageBitmap(bitmap);
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + mainData.getId());
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //프로그래스바 쓰레드
            Thread thread = new Thread() {
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

                @Override
                public void run() {
                    if (mediaPlayer == null) {
                        return;
                    }
                    //쓰레드 안에서는 위젯값을 바꾸면 안된다
                    //1.총 재생시간
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbMP3.setMax(mediaPlayer.getDuration());
                            MusicActivity.seekBar.setMax(mediaPlayer.getDuration());
                            MusicActivity.sbEnd.setText(sdf.format(mediaPlayer.getDuration()));
                        }
                    });
                    while (mediaPlayer.isPlaying()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pbMP3.setProgress(mediaPlayer.getCurrentPosition());
                                MusicActivity.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                MusicActivity.sbStart.setText(sdf.format(mediaPlayer.getCurrentPosition()));
                            }
                        });
                        SystemClock.sleep(200);
                    }
                }
            };
            thread.start();
        } catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
    }

    public String getCoverArtPath(long albumId, Context context) {

        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }

    @Override
    public void onClick(View v) {
        sqlDB = myHelper.getWritableDatabase();

        if (MusicActivity.abc == 1) {
            plus = 1;
        } else if (MusicActivity.abc == 2) {
            plus = (int) (Math.random() * dataList.size());
        } else if (MusicActivity.abc == 3) {
            plus = 0;
        }
        switch (v.getId()) {
            case R.id.btnPlay:
                //처음부터 재생(다른거)
                if (check) {
                    flag = false;
                    check = false;
                    playbackPosition = 0;
                    btnPlay.setImageResource(R.mipmap.pause);
                    MusicActivity.play.setImageResource(R.mipmap.pause);
                    playMusic(dataList.get(now));
                    mediaPlayer.seekTo(playbackPosition);

                    //다시재생(같은거 정지중)
                } else {
                    if (flag) {
                        flag = false;
                        btnPlay.setImageResource(R.mipmap.pause);
                        MusicActivity.play.setImageResource(R.mipmap.pause);
                        playMusic(dataList.get(now));
                        mediaPlayer.seekTo(playbackPosition);
                        //중지(같은거 재생중)
                    } else {
                        flag = true;
                        btnPlay.setImageResource(R.mipmap.play);
                        MusicActivity.play.setImageResource(R.mipmap.play);
                        mediaPlayer.pause();
                        playbackPosition = mediaPlayer.getCurrentPosition();
                    }
                }
                break;
            case R.id.btnNext:
                playbackPosition = 0;
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                flag = false;
                if (now + plus > dataList.size() - 1) {
                    now = now + plus - (dataList.size() - 1);
                } else {
                    now = now + plus;
                }
                playMusic(dataList.get(now));
                break;
            case R.id.btnBack:
                playbackPosition = 0;
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                flag = false;
                if (now - plus < 0) {
                    now = now - plus + (dataList.size() - 1);
                } else {
                    now = now - plus;
                }
                playMusic(dataList.get(now));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all_list:
                getMusicList();
                mainAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "전체리스트", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.myList:
                int likeit = 0;
                dataList.removeAll(dataList);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                sqlDB = myHelper.getWritableDatabase();


                //가져오고 싶은 컬럼 명을 나열. 음악의 아이디, 앰블럼 아이디, 제목, 아스티스트 정보
                String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};

                Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, MediaStore.Audio.Media.DATA + " like ? ",
                        new String[]{"%mp3%"}, null);

                while (cursor.moveToNext()) {
                    MainData mainData = new MainData();

                    mainData.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                    mainData.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    mainData.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    mainData.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));

                    //곡 제목이 데이터베이스에 들어있냐?(좋아요 한거냐)
                    Cursor cursor1 = sqlDB.rawQuery("SELECT * FROM musicTBL WHERE title = '" + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) + "';", null);
                    if (cursor1.getCount() != 0) {
                        //있으면 리스트에 추가함
                        likeit = likeit + 1;
                        dataList.add(mainData);
                        cursor1.close();
                    }
                }
                Toast.makeText(getApplicationContext(), "좋아요 리스트", Toast.LENGTH_SHORT).show();
                cursor.close();
                sqlDB.close();
                mainAdapter.notifyDataSetChanged();
                btnPlay.setImageResource(R.mipmap.play);
                now = 0;
                flag = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            mediaPlayer.stop();
            finish();
        }
    }
}