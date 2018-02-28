package com.prpr894.cplayer.ui.activities;

import android.os.Bundle;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getToolbarTitle().setText("关于");
    }
}
