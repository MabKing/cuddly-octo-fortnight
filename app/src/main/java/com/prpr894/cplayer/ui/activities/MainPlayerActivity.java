package com.prpr894.cplayer.ui.activities;

import android.os.Bundle;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.custommediaplayer.JZMediaIjkplayer;
import com.squareup.picasso.Picasso;

import cn.jzvd.JZMediaSystem;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class MainPlayerActivity extends BaseActivity {
    private String videoUrl = "";
    private String imgUrl = "";
    private String title = "";
    private JZVideoPlayerStandard mJzVideoPlayerStandard;

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
        getToolbarTitle().setText(title);
        JZVideoPlayer.setMediaInterface(new JZMediaIjkplayer());
        mJzVideoPlayerStandard = findViewById(R.id.video_player);
        mJzVideoPlayerStandard.setUp(videoUrl, JZVideoPlayer.SCREEN_WINDOW_NORMAL, title);
        Picasso.with(this)
                .load(imgUrl)
                .into(mJzVideoPlayerStandard.thumbImageView);
        mJzVideoPlayerStandard.startVideo();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JZVideoPlayer.releaseAllVideos();
    }
}
