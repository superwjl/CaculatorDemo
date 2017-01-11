package com.tik.caculatordemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tik.caculatordemo.base.BaseActivity;
import com.tik.caculatordemo.custom.CustomSelectDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


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

    private int mYear = 20;//贷款年数
    private int mType = 0;//0:等额本息  1:等额本金
    private int mTotal = 0;//贷款总额
    private String mRateStr = "4.17";


    @OnClick({R.id.tv_year, R.id.tv_rate, R.id.btn_caculate})
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
                    Bundle extras = new Bundle();
                    extras.putInt("total", mTotal);
                    extras.putInt("year", mYear);
                    extras.putString("rate", mRateStr);
                    extras.putInt("type", mType);
                    toActivity(CaculatorDetailActivity.class, extras);
                }else{
                    Toast.makeText(this, "请检查您的信息是否输入完整", Toast.LENGTH_SHORT).show();
                }

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


    private List<String> getYearList(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++){
            list.add((i+1)+"年("+(12 * (i+1))+"期)");
        }
        return list;
    }

//    private void formatRate(){
//        mRate = new BigDecimal(mRateStr).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
//        tvRate.setText(mRateStr+"%");
//    }

    @Override
    protected void onResume() {
        super.onResume();
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
            }
        }
    }
}
