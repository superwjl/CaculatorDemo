package com.tik.caculatordemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tik.caculatordemo.adapter.recyclerview.CommonAdapter;
import com.tik.caculatordemo.adapter.recyclerview.DividerItemDecoration;
import com.tik.caculatordemo.adapter.recyclerview.MultiItemTypeAdapter;
import com.tik.caculatordemo.adapter.recyclerview.base.ViewHolder;
import com.tik.caculatordemo.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.tik.caculatordemo.base.BaseActivity;
import com.tik.caculatordemo.util.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * @auth tik
 * @date 17/1/11 下午4:11
 */

public class RateActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private CommonAdapter<String> mAdatper;
    private HeaderAndFooterWrapper headerAndFooterWrapper;
    private List<String> mDatas = new ArrayList<>();
    private List<String> mDatasValue = new ArrayList<>();
    private String mRateStr;
    private String mRateStandard = "4.9";
    private Intent mIntent;
    private EditText mEtRate;

    @Override
    protected void beforeBindViews(Bundle savedInstanceState) {
        mIntent = getIntent();
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            mRateStr = extras.getString("rate");
            mRateStr = new BigDecimal(mRateStr).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"";
        }
        Log.e("tag", "rateStr="+mRateStr);
        String[] rate = getResources().getStringArray(R.array.rate);
        String[] rateValue = getResources().getStringArray(R.array.rate_value);
        mDatas = Arrays.asList(rate);
        for (int i = 0; i < rateValue.length; i++){
            String s = new BigDecimal(mRateStandard).multiply(new BigDecimal(rateValue[i])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            mDatasValue.add(s);
            Log.e("tag", rate[i]+"="+s);
        }
    }

    @Override
    protected void afterBindViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdatper = new CommonAdapter<String>(this, R.layout.item_rate, mDatas) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.tv_rate, s);
//                Log.e("tag", "position="+position+",adapterpos="+holder.getAdapterPosition()+",layoutpos="+holder.getLayoutPosition()+",oldpos="+holder.getOldPosition());
                //header占据了一个position
                if(mRateStr.equals(mDatasValue.get(position - 1))){
                    holder.setVisible(R.id.iv_rate, true);
                }else{
                    holder.setVisible(R.id.iv_rate, false);
                }

            }
        };

        headerAndFooterWrapper = new HeaderAndFooterWrapper(mAdatper);
        View header = LayoutInflater.from(this).inflate(R.layout.rate_header, null);
        mEtRate = (EditText) header.findViewById(R.id.et_rate);
        mEtRate.setText(mRateStr);
        headerAndFooterWrapper.addHeaderView(header);

        mAdatper.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                String rate = mDatasValue.get(position - 1);
                mRateStr = rate;
                mEtRate.setText(mRateStr);
                headerAndFooterWrapper.notifyDataSetChanged();
                finishWithData();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setAdapter(headerAndFooterWrapper);
    }

    @Override
    protected int getResId() {
        return R.layout.activity_rate;
    }

    @Override
    public void onBackPressed() {
        finishWithData();
    }

    private void finishWithData(){
        Bundle b = new Bundle();
        b.putString("rate", mRateStr);
        mIntent.putExtras(b);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK, mIntent);
                finish();
            }
        }, 200);
    }
}
