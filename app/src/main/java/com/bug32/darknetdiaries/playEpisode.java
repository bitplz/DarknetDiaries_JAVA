package com.bug32.darknetdiaries;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class playEpisode extends AppCompatActivity {

    private ImageView imageView;
    private ImageButton play, replay, forward;
    private TextView desc, startDuration, endDuration;
    private TextView playbackSpeed;
    private SeekBar seekBar;
    private String imgUrl, description, audioUrl, title;

    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();
    private Runnable runnable;
    private RelativeLayout relativeLayout;
    private boolean isPause = false;
    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    int flag = 0;
    int seekTo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_episode);

        checkInternet();

        relativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        imageView = (ImageView) findViewById(R.id.image);
        play = (ImageButton) findViewById(R.id.play);
        replay = (ImageButton) findViewById(R.id.replay);
        forward = (ImageButton) findViewById(R.id.forward);
        desc = (TextView) findViewById(R.id.description);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        playbackSpeed = (TextView) findViewById(R.id.playbackSpeed);

        startDuration = (TextView) findViewById(R.id.startDuration);
        endDuration = (TextView) findViewById(R.id.endDuration);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        imgUrl = intent.getStringExtra("imgUrl");
        description = intent.getStringExtra("description");
        audioUrl = intent.getStringExtra("audioUrl");

        sharedPreferences = getSharedPreferences(""+title, Context.MODE_PRIVATE);

        getSupportActionBar().setTitle("Darknet Diaries");
        getSupportActionBar().setSubtitle(title);

        desc.setText(Html.fromHtml(description));
        Picasso.get().load(imgUrl).fit().into(imageView);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }else {
            replay.setEnabled(false);
            forward.setEnabled(false);
        }

        resumeStream();

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.reset();
                return true;
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                if (flag == 1){
                    seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.seekTo(seekTo);
                    mediaPlayer.start();
                    progressDialog.dismiss();
                    updateSeekbar();
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.ic_action_play);
                    isPause = true;
                } else {

                    seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    progressDialog.dismiss();
                    updateSeekbar();
                    play.setImageResource(R.drawable.ic_action_pause);
                }

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser){
                    mediaPlayer.seekTo(progress);
                    startDuration.setText(getDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(playEpisode.this);
                progressDialog.setMessage("Loading audio...");
                progressDialog.setCancelable(false);

                if (!mediaPlayer.isPlaying() && !isPause){
                    try {
                        mediaPlayer.setDataSource(audioUrl);
                        mediaPlayer.prepareAsync();
                        progressDialog.show();
                        replay.setEnabled(true);
                        forward.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (mediaPlayer.isPlaying()) {
                    isPause = true;
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.ic_action_play);
                } else if (isPause) {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.ic_action_pause);
                }
                checkInternet();
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()){
                    if (mediaPlayer.getCurrentPosition() <= 5000){
                        mediaPlayer.seekTo(0);
                    }
                    else mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()){
                    if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()){
                        mediaPlayer.seekTo(mediaPlayer.getDuration());
                    }
                    else mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });

        playbackSpeed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        switch (playbackSpeed.getText().toString()) {

                            case "1.0x":
                                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((float) 1.2));
                                playbackSpeed.setText("1.2x");
                                break;
                            case "1.2x":
                                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((float) 1.4));
                                playbackSpeed.setText("1.4x");
                                break;

                            case "1.4x":
                                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((float) 1.6));
                                playbackSpeed.setText("1.6x");
                                break;

                            case "1.6x":
                                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((float) 1.8));
                                playbackSpeed.setText("1.8x");
                                break;

                            case "1.8x":
                                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((float) 2.0));
                                playbackSpeed.setText("2.0x");
                                break;

                            case "2.0x":
                                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((float) 1.0));
                                playbackSpeed.setText("1.0x");
                                break;

                        }
                    }
                }
            }
        });

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                float selectSpeed = Float.parseFloat(adapterView.getItemAtPosition(i).toString());
//
//                if (mediaPlayer.isPlaying()) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(selectSpeed));
//                        playbackSpeed.setText("x" + selectSpeed);
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

    }

    private void resumeStream() {

        if (sharedPreferences.contains("seekPosition")){

            seekTo = sharedPreferences.getInt("seekPosition",0);

            if (seekTo > 0) {

                progressDialog = new ProgressDialog(playEpisode.this);
                try {
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.prepareAsync();
                    progressDialog.setMessage("Picking up where you left...");
                    progressDialog.show();
                    flag = 1;
                    replay.setEnabled(true);
                    forward.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_save,menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//       if(item.getItemId() == R.id.save){
//
//           getPermission();
//           AlertDialog.Builder builder = new AlertDialog.Builder(playEpisode.this);
//           builder.setTitle("Download");
//           builder.setMessage("Do you want to Save this episode Offline.");
//           builder.setCancelable(false);
//           builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//               @Override
//               public void onClick(DialogInterface dialogInterface, int i) {
//
//                   DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
//                   DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://feeds.megaphone.fm/darknetdiaries.json"));
//                   request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                   long qu = downloadManager.enqueue(request);
//
//                   Toast.makeText(playEpisode.this, "Download Has been started.", Toast.LENGTH_LONG).show();
//               }
//           });
//
//           builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//               @Override
//               public void onClick(DialogInterface dialogInterface, int i) {
//                   dialogInterface.cancel();
//               }
//           });
//           builder.show();
//       }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void updateSeekbar(){

        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            endDuration.setText(getDuration(mediaPlayer.getDuration()));
            startDuration.setText(getDuration(mediaPlayer.getCurrentPosition()));
            if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()){
                play.setImageResource(R.drawable.ic_action_play);
            }
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        sharedPreferences = getSharedPreferences("" + title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("seekPosition", mediaPlayer.getCurrentPosition());
        editor.apply();

        if (mediaPlayer.isPlaying())
            Toast.makeText(playEpisode.this, "Podcast stopped.", Toast.LENGTH_SHORT).show();
        mediaPlayer.reset();
        finish();
    }

    public String getDuration(int duration){
        long min, sec, milSec;
        milSec = duration/1000;
        min = milSec/60;
        sec = milSec - (min*60);

        return min+":"+sec;
    }

    public void checkInternet() {
        if (!isNetworkAvailable()) {
            relativeLayout.setVisibility(View.GONE);
            if (mediaPlayer.isPlaying()){
                mediaPlayer.reset();
                play.setImageResource(R.drawable.ic_action_play);
            }
            Snackbar.make(relativeLayout, "No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isNetworkAvailable()) {
                                relativeLayout.setVisibility(View.VISIBLE);
//                                parseJson();
                            } else {
                                checkInternet();
                            }
                        }
                    }).setActionTextColor(Color.parseColor("#009900")).show();
        }
    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

//    public void getPermission(){
//
//        if (ContextCompat.checkSelfPermission(playEpisode.this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(playEpisode.this,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(playEpisode.this,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        0);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
//
//        if (ContextCompat.checkSelfPermission(playEpisode.this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(playEpisode.this,
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(playEpisode.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        0);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
//    }
}
