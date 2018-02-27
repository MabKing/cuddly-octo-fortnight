package com.prpr894.cplayer.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.custommediaplayer.JZMediaIjkplayer;
import com.squareup.picasso.Picasso;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class MainPlayerActivity extends AppCompatActivity {
    private String videoUrl = "";
    private String imgUrl = "";
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        initBundle();
        initView();
    }

    private void initBundle() {
        videoUrl = getIntent().getStringExtra("videoUrl");
        imgUrl = getIntent().getStringExtra("imgUrl");
        title = getIntent().getStringExtra("title");
    }

    private void initView() {
        JZVideoPlayer.setMediaInterface(new JZMediaIjkplayer());
        JZVideoPlayerStandard JZVideoPlayerStandard = findViewById(R.id.video_player);
        JZVideoPlayerStandard.setUp(videoUrl, JZVideoPlayer.SCREEN_WINDOW_NORMAL, title);
        Picasso.with(this)
                .load(imgUrl)
                .into(JZVideoPlayerStandard.thumbImageView);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
