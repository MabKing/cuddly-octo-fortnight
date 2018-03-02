package com.prpr894.cplayer.ui.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.utils.SPUtil;

import static com.prpr894.cplayer.utils.AppConfig.EXIT_NOTIFICATION_DIALOG;
import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE;
import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE_BAI_DU;
import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE_SAO_ZI;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private RadioButton mRadioButtonBaiduPlayer, mRadioButtonJiaoZiPlayer;
    private CheckBox mCheckBoxExitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView() {
        getToolbarTitle().setText("设置");
        mRadioButtonBaiduPlayer = findViewById(R.id.rb_baidu_player);
        mRadioButtonJiaoZiPlayer = findViewById(R.id.rb_jiaozi_player);
        mCheckBoxExitDialog = findViewById(R.id.cb_show_exit_dialog);
        if (SPUtil.getBoolen(MyApp.getInstance(), EXIT_NOTIFICATION_DIALOG, true)) {
            mCheckBoxExitDialog.setChecked(true);
        } else {
            mCheckBoxExitDialog.setChecked(false);
        }
        switch (SPUtil.getString(MyApp.getInstance(), PLAY_TYPE, PLAY_TYPE_BAI_DU)) {
            case PLAY_TYPE_BAI_DU:
                mRadioButtonBaiduPlayer.setChecked(true);
                break;
            case PLAY_TYPE_SAO_ZI:
                mRadioButtonJiaoZiPlayer.setChecked(true);
                break;
        }
        mRadioButtonBaiduPlayer.setOnCheckedChangeListener(this);
        mRadioButtonJiaoZiPlayer.setOnCheckedChangeListener(this);
        mCheckBoxExitDialog.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_show_exit_dialog:
                mCheckBoxExitDialog.setChecked(isChecked);
                SPUtil.putBoolen(MyApp.getInstance(), EXIT_NOTIFICATION_DIALOG, isChecked);
                break;
            case R.id.rb_baidu_player:
                if (isChecked) {
                    SPUtil.putString(MyApp.getInstance(), PLAY_TYPE, PLAY_TYPE_BAI_DU);
                }
                break;
            case R.id.rb_jiaozi_player:
                if (isChecked) {
                    SPUtil.putString(MyApp.getInstance(), PLAY_TYPE, PLAY_TYPE_SAO_ZI);
                }
                break;

        }
    }
}
