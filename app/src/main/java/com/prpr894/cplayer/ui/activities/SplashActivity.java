package com.prpr894.cplayer.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.service.ListenNetStateService;
import com.prpr894.cplayer.utils.NetWorkUtils;


public class SplashActivity extends AppCompatActivity {

    private ImageView mImageView;
    private static final int SETTING_NETWORK_CODE = 11; //设置requestCode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        startService(new Intent(this, ListenNetStateService.class));
        mImageView = findViewById(R.id.img_welcome);
        loadAnimation();
    }

    /**
     * 加载渐变动画
     */
    private void loadAnimation() {
        //渐变动画
        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        //设置动画时间
        aa.setDuration(2000L);
        //设置动画监听
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startNetwork();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //进行动画
        mImageView.startAnimation(aa);
    }

    /**
     * 网络初始化，判断网络连接状态
     */

    private void startNetwork() {
        if (!NetWorkUtils.NETWORK) { //无网络
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setMessage("当前网络不可用，是否设置网络?");

            builder.setPositiveButton("设置网络", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //打开设置
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivityForResult(intent, SETTING_NETWORK_CODE);
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
            return;
        }

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTING_NETWORK_CODE) {
            loadAnimation();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
