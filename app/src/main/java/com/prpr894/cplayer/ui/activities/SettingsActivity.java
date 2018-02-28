package com.prpr894.cplayer.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;

public class SettingsActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView() {
        getToolbarTitle().setText("设置");
    }
}
