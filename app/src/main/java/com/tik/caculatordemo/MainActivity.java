package com.tik.caculatordemo;

import android.os.Bundle;
import android.view.View;

import com.tik.caculatordemo.base.BaseActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @OnClick(R.id.house)
    void onClick(){
        toActivity(CaculatorActivity.class);
    }

    @Override
    protected void beforeBindViews(Bundle savedInstanceState) {

    }

    @Override
    protected void afterBindViews() {

    }

    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

}
