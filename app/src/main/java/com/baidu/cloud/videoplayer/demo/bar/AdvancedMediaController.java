package com.baidu.cloud.videoplayer.demo.bar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cloud.videoplayer.demo.info.SharedPrefsStore;
import com.baidu.cloud.videoplayer.widget.BDCloudVideoView;
import com.baidu.cloud.videoplayer.widget.BDCloudVideoView.PlayerState;
import com.prpr894.cplayer.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义播放控制类
 *
 * @author baidu
 */
public class AdvancedMediaController extends RelativeLayout implements OnClickListener {

    private static final String TAG = "AdvancedMediaController";


    private ImageButton playButton;
    private ImageButton snapshotButton;
    private Button fitButton;
    private TextView infoResolutionView;
    // private TextView infoBitrateView;
    private TextView infoNetworkView;


    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public Handler getMainThreadHandler() {
        return mainThreadHandler;
    }

    private BDCloudVideoView mVideoView = null;

    private static final int POSITION_REFRESH_TIME = 500;

    public AdvancedMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public AdvancedMediaController(Context context) {
        super(context);
        initUI();
    }

    private void initUI() {

        // inflate controller bar
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = Objects.requireNonNull(inflater).inflate(R.layout.bar_advanced_media_controller, this);

        playButton =  layout.findViewById(R.id.ibtn_play);
        playButton.setOnClickListener(this);

        snapshotButton =  layout.findViewById(R.id.ibtn_snapshot);
        snapshotButton.setOnClickListener(this);

        fitButton =  layout.findViewById(R.id.btn_fitmode);
        if (SharedPrefsStore.isPlayerFitModeCrapping(this.getContext())) {
            fitButton.setText("裁剪");
        } else {
            fitButton.setText("填充");
        }
        fitButton.setOnClickListener(this);


        infoResolutionView = layout.findViewById(R.id.tv_resolution);
        infoNetworkView = layout.findViewById(R.id.tv_netspeed);

        enableControllerBar(false);
    }

    private Timer positionTimer;

    private void startPositionTimer() {
        if (positionTimer != null) {
            positionTimer.cancel();
            positionTimer = null;
        }
        positionTimer = new Timer();
        positionTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // update speed
                        infoNetworkView.setText((mVideoView.getDownloadSpeed() >> 10) + "KB/s");
                    }
                });

            }

        }, 0, POSITION_REFRESH_TIME);
    }

    private void stopPositionTimer() {
        if (positionTimer != null) {
            positionTimer.cancel();
            positionTimer = null;
        }
    }


    /**
     * if you don't want to use Controller when still playing, invoke release to stop timer!
     */
    public void release() {
        stopPositionTimer();
    }

    /**
     *
     */
    public void changeState() {
        final PlayerState state = mVideoView.getCurrentPlayerState();
        Log.d(TAG, "mediaController: changeStatus=" + state.name());
        mainThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                if (state == PlayerState.STATE_IDLE || state == PlayerState.STATE_ERROR) {
                    stopPositionTimer();
                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                } else if (state == PlayerState.STATE_PREPARING) {
                    playButton.setEnabled(false);
//                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                } else if (state == PlayerState.STATE_PREPARED) {

                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);

                    // set width
                    String resolutionStr = mVideoView.getVideoWidth() + "x" + mVideoView.getVideoHeight();
                    if (mVideoView.getVideoWidth() > 0) { // 可能为音频文件，width=-1
                        infoResolutionView.setText(resolutionStr);
                    }
                } else if (state == PlayerState.STATE_PLAYBACK_COMPLETED) {
                    stopPositionTimer();
                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
//                    resolutionButton.setEnabled(false);
                } else if (state == PlayerState.STATE_PLAYING) {
                    startPositionTimer();
                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_pause);
                } else if (state == PlayerState.STATE_PAUSED) {
                    stopPositionTimer();
                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                }
            }

        });

    }


    /**
     * BVideoView implements VideoViewControl, so it's a BVideoView object
     * actually
     *
     * @param player
     */
    public void setMediaPlayerControl(BDCloudVideoView player) {
        mVideoView = player;
    }

    /**
     * Show the controller on screen. It will go away automatically after 3
     * seconds of inactivity.
     */
    public void show() {
        if (mVideoView == null) {
            return;
        }
        this.setVisibility(View.VISIBLE);
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        this.setVisibility(View.GONE);
    }


    /**
     * @param isEnable
     * @hide
     */
    public void enableControllerBar(boolean isEnable) {
        playButton.setEnabled(isEnable);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_play:
                if (mVideoView == null) {
                    Log.d(TAG, "playButton checkstatus changed, but bindView=null");
                } else {
                    if (mVideoView.isPlaying()) {
                        Log.d(TAG, "playButton: Will invoke pause()");
                        playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                        mVideoView.pause();
                    } else {
                        Log.d(TAG, "playButton: Will invoke resume()");
                        playButton.setBackgroundResource(R.drawable.toggle_btn_pause);
                        mVideoView.start();
                    }
                }
                break;
            case R.id.ibtn_snapshot:
                // take snapshot
                File sdDir = null;
                String strpath = null;
                Bitmap bitmap = null;
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSSS");
                String time = formatter.format(date);

                // TODO 您可以指定存储路径，以下逻辑是保存在sdcard根目录下
                sdDir = Environment.getExternalStorageDirectory();
                strpath = sdDir.toString() + "/" + time + ".jpg";
                Log.d(TAG, "snapshot save path=" + strpath);

                bitmap = mVideoView.getBitmap();
                if (bitmap != null) {
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(strpath, false);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.flush();
                        os.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (Throwable t) {
                                Log.e(TAG, "Error occurred while saving snapshot!");
                            }
                        }
                    }
                    os = null;
                    Toast.makeText(this.getContext(), "截图保存在sd卡根目录下, 文件名为" + strpath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getContext(), "抱歉，当前无法截图", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.btn_fitmode:
                if (fitButton.getText().equals("填充")) {
                    // 转为 裁剪模式：视频保持比例缩放，不留黑边，填满显示区域的两边
                    mVideoView.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    fitButton.setText("裁剪");
                    SharedPrefsStore.setPlayerFitMode(getContext(), true);
                } else {
                    // 转为 填充模式：视频保持比例缩放，至少一边与显示区域相同，另一边有黑边
                    mVideoView.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                    fitButton.setText("填充");
                    SharedPrefsStore.setPlayerFitMode(getContext(), false);
                }
                break;

        }
    }

}
