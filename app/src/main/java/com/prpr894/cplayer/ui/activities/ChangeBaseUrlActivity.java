package com.prpr894.cplayer.ui.activities;

import android.os.Bundle;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;

public class ChangeBaseUrlActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_base_url);
        getToolbarTitle().setText("更换服务器地址");
        initView();
    }

    private void initView() {

    }
}
