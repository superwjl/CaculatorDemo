package com.tik.caculatordemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tik.caculatordemo.base.BaseActivity;
import com.tik.caculatordemo.custom.CustomSelectDialog;
import com.tik.caculatordemo.custom.PieChartView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.R.attr.data;


/**
 * @auth tik
 * @date 17/1/5 下午5:25
 */

public class CaculatorActivity extends BaseActivity {

    @BindView(R.id.rg_payment)
    RadioGroup rgPayment;

    @BindView(R.id.rb_benjin)
    RadioButton rbBenjin;

    @BindView(R.id.rb_benxi)
    RadioButton rbBenxi;

    @BindView(R.id.et_amount)
    EditText etAmount;

    @BindView(R.id.tv_year)
    TextView tvYear;

    @BindView(R.id.tv_rate)
    TextView tvRate;

    @BindView(R.id.btn_caculate)
    Button btnCaculate;

    @BindView(R.id.piechart)
    PieChartView pieChartView;

    @BindView(R.id.tv_sum_benjin_value)
    TextView tvSumBenjinValue;

    @BindView(R.id.tv_sum_lixi_value)
    TextView tvSumLixiValue;

    @BindView(R.id.tv_lixi_value)
    TextView tvLixiValue;

    @BindView(R.id.tv_yuegong_value)
    TextView tvYuegongValue;

    @BindView(R.id.tv_reduce)
    TextView tvReduce;

    @BindView(R.id.tv_reduce_value)
    TextView tvReduceValue;

    @BindView(R.id.ll_pie)
    LinearLayout llPie;

    @BindView(R.id.seperate_view)
    View seperateView;


    private int mYear = 20;//贷款年数
    private int mType = 0;//0:等额本息  1:等额本金
    private int mTotal = 0;//贷款总额
    private String mRateStr = "4.17";

    /** 支付利息 */
    private int mSumLixi;
    /** 参考月供 */
    private int mCankaoYuegong;

    private int mReduce;

    private boolean mAnimComplete = true;


    @OnClick({R.id.tv_year, R.id.tv_rate, R.id.btn_caculate, R.id.tv_todetail})
    void onClick(View view){
        etAmount.clearFocus();
        hideKeyboard();
        switch (view.getId()){
            case R.id.tv_year:
                new CustomSelectDialog(this, "按揭年数", mYear-1, getYearList(), new CustomSelectDialog.OnBtnClickListener() {
                    @Override
                    public void select(String s, int position) {
//                        Toast.makeText(CaculatorActivity.this, "s="+s+",position="+position, Toast.LENGTH_SHORT).show();
                        tvYear.setText(s);
                        mYear = position + 1;
                        caculate();
                    }
                }).show();
                break;
            case R.id.tv_rate:
                Bundle bundle = new Bundle();
                bundle.putString("rate", mRateStr);
                Intent intent = new Intent(this, RateActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_caculate:
                if(checkValues()){
                    mTotal = Integer.parseInt(etAmount.getText().toString()) * 10000;
                    double rate = new BigDecimal(mRateStr).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    new ComputeTask(mTotal, mYear, rate, mType).execute();
                }else{
                    Toast.makeText(this, "请检查您的信息是否输入完整", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_todetail:
                mTotal = Integer.parseInt(etAmount.getText().toString()) * 10000;
                Bundle extras = new Bundle();
                extras.putInt("total", mTotal);
                extras.putInt("year", mYear);
                extras.putString("rate", mRateStr);
                extras.putInt("type", mType);
                toActivity(CaculatorDetailActivity.class, extras);
                break;
        }
    }

    private boolean checkValues(){
        if(etAmount.getText().length() == 0 ||
                tvYear.getText().length() == 0 ||
                tvRate.getText().length() == 0){
            return false;
        }
        return true;
    }

    private void caculate(){
        if(checkValues()){
            mTotal = Integer.parseInt(etAmount.getText().toString()) * 10000;
            double rate = new BigDecimal(mRateStr).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
            new ComputeTask(mTotal, mYear, rate, mType).execute();
        }
    }


    private List<String> getYearList(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++){
            list.add((i+1)+"年("+(12 * (i+1))+"期)");
        }
        return list;
    }

    private void bindDataToView(){
        seperateView.setVisibility(View.VISIBLE);
        llPie.setVisibility(View.VISIBLE);
        tvSumBenjinValue.setText(etAmount.getText().toString()+"万元");
        double sumLixi = new BigDecimal(mSumLixi).divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Log.i("tag", "sumlixi="+mSumLixi);
        tvSumLixiValue.setText(sumLixi + "万元");
        tvLixiValue.setText(tvRate.getText().toString()+"%");
        tvYuegongValue.setText(mCankaoYuegong+"元/月");
        if(mType == 0){
            tvReduce.setVisibility(View.GONE);
            tvReduceValue.setVisibility(View.GONE);
        }else{
            tvReduce.setVisibility(View.VISIBLE);
            tvReduceValue.setVisibility(View.VISIBLE);
            tvReduceValue.setText(mReduce+"元");
        }
        int percent1 = 100 * mSumLixi / (mTotal + mSumLixi);
        pieChartView.startAnimation(percent1, 100 - percent1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSumLixi > 0){
            pieChartView.clearCavas();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int percent1 = 100 * mSumLixi / (mTotal + mSumLixi);
                    pieChartView.startAnimation(percent1, 100 - percent1);
                }
            }, 500);

        }
    }

    @Override
    protected void beforeBindViews(Bundle savedInstanceState) {
        setTitle(getString(R.string.house_caculate));
    }

    @Override
    protected void afterBindViews() {
        tvYear.setText(mYear+"年("+(mYear*12)+"期)");
        tvRate.setText(mRateStr+"%");

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 过滤0开头的数值（0除外）
                String temp = s.toString();
                if(temp.length() == 2 && temp.startsWith("0")){
                    s.delete(0, 1);
                }
            }
        });

        rgPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_benxi){
                    mType = 0;
                }else{
                    mType = 1;
                }
                if(mTotal != 0){
                    caculate();
                }
            }
        });
    }

    @Override
    protected int getResId() {
        return R.layout.activity_caculator;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            if(bundle != null){
                mRateStr = bundle.getString("rate");
                tvRate.setText(mRateStr+"%");
                caculate();
            }
        }
    }

    private void computeBenxi(int total, int year, double rate){
        /**
         * 贷款50万， 时间20年，利率按照利率5.9%计算：
         每月应还款额＝500000×5.9%/12×（1＋5.9%/12）^240/[（1＋5.9%/12）^240－1]
         =3553.37元
         */
        //月利率
        double rateM = rate/12;
        double d1 = Math.pow(1 + rateM, year * 12);
        int yuegong = (int) (((total * rateM * d1) / (d1 - 1)) + 0.5d);
        mCankaoYuegong = yuegong;
        //已还利息总和
        int sumLixi = 0;
        //已还本金总额
        int sumBenjin = 0;
        //期数
        int size = year * 12;
        for (int i = 0; i < size; i++){
            int lixi = (int) ((total - sumBenjin) * rateM + 0.5d);
            sumLixi += lixi;
            int benjin = yuegong - lixi;
            sumBenjin += benjin;
        }
        mSumLixi = sumLixi;
    }
    private void computeBenjin(int total, int year, double rate){
        //月利率
        double rateM = rate/12;
        //月还本金
        int dx = total/year/12;
        //已还利息总和
        int sumRate = 0;
        //期数
        int size = year * 12;
        for (int i = 0; i < size; i++){
            int r = (int) ((total - dx * i) * rateM + 0.5d);
            sumRate += r;
        }
        mSumLixi = sumRate;
        mCankaoYuegong = (total + mSumLixi)/size;
        mReduce = (int) (dx * rateM + 0.5f);
    }

    class ComputeTask extends AsyncTask<Void, Void, Void> {

        private int mTotal;
        private int mYear;
        private double mRate;
        private int mType;

        public ComputeTask(int mTotal, int mYear, double mRate, int mType) {
            this.mTotal = mTotal;
            this.mYear = mYear;
            this.mRate = mRate;
            this.mType = mType;
        }


        @Override
        protected Void doInBackground(Void... params) {
            if(mType == 0){
                computeBenxi(mTotal, mYear, mRate);
            }else{
                computeBenjin(mTotal, mYear, mRate);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bindDataToView();
        }

    }
}
