package com.tik.caculatordemo.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.aitangba.swipeback.SwipeBackActivity;

import butterknife.ButterKnife;

/**
 * @auth tik
 * @date 17/1/4 下午4:38
 */

public abstract class BaseActivity extends SwipeBackActivity {

    protected abstract void beforeBindViews(Bundle savedInstanceState);

    protected abstract void afterBindViews();

    protected abstract int getResId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeBindViews(savedInstanceState);
        setContentView(getResId());
        ButterKnife.bind(this);
        afterBindViews();
    }

    protected void toActivity(Class<?> cls){
        toActivity(cls, null);
    }

    protected void toActivity(Class<?> cls, Bundle extras){
        Intent intent = new Intent(getApplicationContext(), cls);
        if(extras != null){
            intent.putExtras(extras);
        }
        startActivity(intent);
    }

    protected void hideKeyboard(){
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
