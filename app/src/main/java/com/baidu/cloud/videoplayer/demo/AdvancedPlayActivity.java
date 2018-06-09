package com.baidu.cloud.videoplayer.demo;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.cloud.media.player.IMediaPlayer;
import com.baidu.cloud.videoplayer.demo.bar.AdvancedMediaController;
import com.baidu.cloud.videoplayer.demo.info.SharedPrefsStore;
import com.baidu.cloud.videoplayer.demo.popview.FullScreenUtils;
import com.baidu.cloud.videoplayer.widget.BDCloudVideoView;
import com.baidu.cloud.videoplayer.widget.BDCloudVideoView.PlayerState;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.bean.LiveRoomItemDataBean;
import com.prpr894.cplayer.utils.CollectionHelper;

import java.util.Timer;
import java.util.TimerTask;

public class AdvancedPlayActivity extends BaseActivity implements IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener,
        BDCloudVideoView.OnPlayerStateListener {
    private static final String TAG = "AdvancedPlayActivity";

    /**
     * 您的AK 请到http://console.bce.baidu.com/iam/#/iam/accesslist获取
     */
    private String ak = ""; // 请录入您的AK !!!


    private BDCloudVideoView mVV = null;
    private AdvancedMediaController mediaController = null;
    private RelativeLayout headerBar = null;
    private RelativeLayout fullHeaderRl = null;
    private RelativeLayout fullControllerRl = null;
    private RelativeLayout normalHeaderRl = null;
    private RelativeLayout normalControllerRl = null;

    private RelativeLayout mViewHolder = null;

    private Timer barTimer;
    private volatile boolean isFullScreen = false;
    private LiveRoomItemDataBean mLiveRoomItemDataBean;
    private CollectionHelper mCollectionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(0xff282828);
        }
        /**
         * 防闪屏
         */
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_advanced_video_playing);
        initBundle();
        initUI();

    }

    private void initBundle() {
        mLiveRoomItemDataBean = new LiveRoomItemDataBean();
        mLiveRoomItemDataBean.setImg(getIntent().getStringExtra("imgUrl"));
        mLiveRoomItemDataBean.setTitle(getIntent().getStringExtra("title"));
        mLiveRoomItemDataBean.setAddress(getIntent().getStringExtra("videoUrl"));
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        mViewHolder = findViewById(R.id.view_holder);
        mediaController = findViewById(R.id.media_controller_bar);
        fullHeaderRl = findViewById(R.id.rl_fullscreen_header);
        fullControllerRl = findViewById(R.id.rl_fullscreen_controller);
        normalHeaderRl = findViewById(R.id.rl_normalscreen_header);
        normalControllerRl = findViewById(R.id.rl_normalscreen_controller);
        headerBar = findViewById(R.id.rl_header_bar);
        /**
         * 设置ak
         */
        BDCloudVideoView.setAK(ak);

        mVV = new BDCloudVideoView(this);
        /**
         * 注册listener
         */
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        mVV.setOnBufferingUpdateListener(this);
        mVV.setOnPlayerStateListener(this);

        if (SharedPrefsStore.isPlayerFitModeCrapping(getApplicationContext())) {
            mVV.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        } else {
            mVV.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        }

        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(-1, -1);
        rllp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mViewHolder.addView(mVV, rllp);
        mediaController.setMediaPlayerControl(mVV);
        mCollectionHelper=new CollectionHelper(mediaController.getCollectionView(),mLiveRoomItemDataBean);
        mVV.setVideoPath(mLiveRoomItemDataBean.getAddress());
        mVV.setLogEnabled(false);
//        mVV.setDecodeMode(BDCloudMediaPlayer.DECODE_SW);

        mVV.setMaxProbeTime(2000); // 设置首次缓冲的最大时长
        mVV.setTimeoutInUs(1000000);
        // Options for live stream only
//        mVV.setMaxProbeSize(1 * 2048);
//        mVV.setMaxProbeTime(40); // 设置首次缓冲的最大时长
//        mVV.setMaxCacheSizeInBytes(32 * 1024);
//        mVV.setBufferTimeInMs(100);
//        mVV.toggleFrameChasing(true);

        // 初始化好之后立即播放（您也可以在onPrepared回调中调用该方法）
        mVV.start();
        initOtherUI();
    }

    private void initOtherUI() {
        // header
        final ImageButton ibBack = this.findViewById(R.id.ibtn_back);
        ibBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
        RelativeLayout rlback = this.findViewById(R.id.rl_back);
        rlback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ibBack.performClick();
            }

        });
        TextView tvTitle = this.findViewById(R.id.tv_top_title);
        tvTitle.setText(mLiveRoomItemDataBean.getTitle());
        final ImageButton ibScreen = this.findViewById(R.id.ibtn_screen_control);
        ibScreen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    FullScreenUtils.toggleHideyBar(AdvancedPlayActivity.this);
                    // to mini size, to portrait
                    fullHeaderRl.removeAllViews();
                    fullControllerRl.removeAllViews();
                    normalHeaderRl.addView(headerBar);
                    normalControllerRl.addView(mediaController);
                    isFullScreen = false;
                    ibScreen.setBackgroundResource(R.drawable.btn_to_fullscreen);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    FullScreenUtils.toggleHideyBar(AdvancedPlayActivity.this);
                    normalHeaderRl.removeAllViews();
                    normalControllerRl.removeAllViews();
                    fullHeaderRl.addView(headerBar);
                    fullControllerRl.addView(mediaController);

                    isFullScreen = true;
                    ibScreen.setBackgroundResource(R.drawable.btn_to_mini);
                    hideOuterAfterFiveSeconds();
                }
            }

        });


        if (!SharedPrefsStore.isDefaultPortrait(this)) {
            ibScreen.performClick();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        // 当home键切出，想暂停视频的话，反注释下面的代码。同时要反注释onResume中的代码
//        if (mVV.isPlaying()) {
//            isPausedByOnPause = true;
//            mVV.pause();
//        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.v(TAG, "onResume");

        // 当home键切出，暂停了视频此时想回复的话，反注释下面的代码
//        if (isPausedByOnPause) {
//            isPausedByOnPause = false;
//            mVV.start();
//        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
        if (mVV != null) {
            mVV.enterForeground();
        }
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        // enterBackground should be invoke before super.onStop()
        if (mVV != null) {
            mVV.enterBackground();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mVV != null) {
            mVV.stopPlayback(); // 释放播放器资源
            mVV.release(); // 释放播放器资源和显示资源
        }
        mVV = null;

        if (mediaController != null) {
            mediaController.release();
        }
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    /**
     * 检测'点击'空白区的事件，若播放控制控件未显示，设置为显示，否则隐藏。
     *
     * @param v
     */
    public void onClickEmptyArea(View v) {
//        if (!isFullScreen) {
//            return;
//        }
        if (barTimer != null) {
            barTimer.cancel();
            barTimer = null;
        }
        if (this.mediaController != null) {
            if (mediaController.getVisibility() == View.VISIBLE) {
                mediaController.hide();
                headerBar.setVisibility(View.GONE);
            } else {
                mediaController.show();
                headerBar.setVisibility(View.VISIBLE);
                hideOuterAfterFiveSeconds();
            }
        }
    }

    private void hideOuterAfterFiveSeconds() {
        if (!isFullScreen) {
            return;
        }
        if (barTimer != null) {
            barTimer.cancel();
            barTimer = null;
        }
        barTimer = new Timer();
        barTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (!isFullScreen) {
                    return;
                }
                if (mediaController != null && mediaController.getMainThreadHandler() != null) {
                    mediaController.getMainThreadHandler().post(new Runnable() {

                        @Override
                        public void run() {
                            mediaController.hide();
                            headerBar.setVisibility(View.GONE);
                        }

                    });
                }
            }

        }, 5 * 1000);

    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

    }

    @Override
    public void onPlayerStateChanged(PlayerState nowState) {
        if (mediaController != null) {
            mediaController.changeState();
        }
    }

}
