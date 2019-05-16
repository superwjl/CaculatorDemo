package com.tik.caculatordemo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tik.caculatordemo.adapter.recyclerview.CommonAdapter;
import com.tik.caculatordemo.adapter.recyclerview.DividerItemDecoration;
import com.tik.caculatordemo.adapter.recyclerview.MultiItemTypeAdapter;
import com.tik.caculatordemo.adapter.recyclerview.base.ViewHolder;
import com.tik.caculatordemo.base.BaseActivity;
import com.tik.caculatordemo.bean.LoanBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * @auth tik
 * @date 17/1/10 下午2:41
 */
public class CaculatorDetailActivity extends BaseActivity {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.rg_payment)
    RadioGroup radioGroup;

    /**
     * 总贷款
     */
    @BindView(R.id.tv_sum_benjin)
    TextView tvSumBenjin;

    /**
     * 总利息
     */
    @BindView(R.id.tv_sum_lixi)
    TextView tvSumLixi;

    /**
     * 利率
     */
    @BindView(R.id.tv_rate)
    TextView tvRate;

    private CommonAdapter<LoanBean> mAdapter;
    /**
     * 等额本金计算得出的还款明细列表
     */
    private List<LoanBean> mBenjinDatas = new ArrayList<>();
    /**
     * 等额本息计算得出的还款明细列表
     */
    private List<LoanBean> mBenxiDatas = new ArrayList<>();

    /**
     * 贷款总额
     */
    private int mTotal;
    /**
     * 贷款年数
     */
    private int mYear;
    /**
     * 利率(double类型)
     */
    private double mRate;
    /**
     * 利率(String类型)
     */
    private String mRateStr;
    /**
     * 贷款类型 0:等额本息  1:等额本金
     */
    private int mType;
    /**
     * 当前选择的年数所在列表位置
     */
    private int mSelectPosition = -1;
    /**
     * 已还本金
     */
    private int mSumBenjin;
    /**
     * 已还本息
     */
    private int mSumBenxi;


    @Override
    protected void beforeBindViews(Bundle savedInstanceState) {
        setTitle("还款详情");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mTotal = extras.getInt("total");
            mYear = extras.getInt("year");
            mRateStr = extras.getString("rate");
            mRate = new BigDecimal(mRateStr).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
            mType = extras.getInt("type");
            Log.e("tag", extras.toString() + "mRate=" + mRate);
        }
    }

    @Override
    protected void afterBindViews() {
        radioGroup.check(mType == 0 ? R.id.rb_benxi : R.id.rb_benjin);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_benxi) {
                    mType = 0;
                    if (mBenxiDatas != null && mBenxiDatas.size() != 0) {
                        mAdapter.setData(mBenxiDatas);
                        mAdapter.notifyDataSetChanged();
                        setLixiTextColor(mSumBenxi);
                    } else {
                        new ComputeTask(mTotal, mYear, mRate, mType).execute();
                    }
                } else {
                    mType = 1;
                    if (mBenjinDatas != null && mBenjinDatas.size() != 0) {
                        mAdapter.setData(mBenjinDatas);
                        mAdapter.notifyDataSetChanged();
                        setLixiTextColor(mSumBenjin);
                    } else {
                        new ComputeTask(mTotal, mYear, mRate, mType).execute();
                    }
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        List<LoanBean> list;
        if (mType == 0) {
            list = mBenxiDatas;
        } else {
            list = mBenjinDatas;
        }
        mAdapter = new CommonAdapter<LoanBean>(this, R.layout.item_caculator, list) {
            @Override
            protected void convert(ViewHolder holder, LoanBean loanBean, int position) {
                holder.setText(R.id.stage, loanBean.getStage() + "");
                holder.setText(R.id.repayTotal, loanBean.getYuegong() + "");
                holder.setText(R.id.repayPrincipal, loanBean.getYuegongbenjin() + "");
                holder.setText(R.id.repayInterest, loanBean.getYuegonglixi() + "");
                holder.setText(R.id.repaidTotal, loanBean.getTotalRepaid() + "");
                holder.setText(R.id.leftPrincipal, loanBean.getBenjinLeft() + "");
                holder.setText(R.id.repaidPrincipal, loanBean.getBenjinRepaid() + "");
                holder.setText(R.id.repaidInterest, loanBean.getLixiRepaid() + "");
                if (mSelectPosition == position) {
                    holder.setBackgroundColor(R.id.ll_detail, getResources().getColor(R.color.uc_cdcdcd));
                    holder.setTextColor(R.id.stage, Color.RED);
                    holder.setTextColor(R.id.repayTotal, Color.RED);
                    holder.setTextColor(R.id.repayPrincipal, Color.RED);
                    holder.setTextColor(R.id.repayInterest, Color.RED);
                    holder.setTextColor(R.id.repaidTotal, Color.RED);
                    holder.setTextColor(R.id.leftPrincipal, Color.RED);
                    holder.setTextColor(R.id.repaidPrincipal, Color.RED);
                    holder.setTextColor(R.id.repaidInterest, Color.RED);
                } else {
                    holder.setBackgroundColor(R.id.ll_detail, getResources().getColor(android.R.color.transparent));
                    holder.setTextColor(R.id.stage, Color.BLACK);
                    holder.setTextColor(R.id.repayTotal, Color.BLACK);
                    holder.setTextColor(R.id.repayPrincipal, Color.BLACK);
                    holder.setTextColor(R.id.repayInterest, Color.BLACK);
                    holder.setTextColor(R.id.repaidTotal, Color.BLACK);
                    holder.setTextColor(R.id.leftPrincipal, Color.BLACK);
                    holder.setTextColor(R.id.repaidPrincipal, Color.BLACK);
                    holder.setTextColor(R.id.repaidInterest, Color.BLACK);
                }

            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mSelectPosition = position;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        /* 显示总贷款和利率 */
        String total = String.valueOf(mTotal);
        SpannableString ss = new SpannableString("总贷款：" + mTotal + "元");
        ss.setSpan(new ForegroundColorSpan(Color.RED), 4, 4 + total.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSumBenjin.setText(ss);
        SpannableString ss2 = new SpannableString("利    率：" + mRateStr + "%");
        ss2.setSpan(new ForegroundColorSpan(Color.RED), 7, 7 + mRateStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRate.setText(ss2);
        new ComputeTask(mTotal, mYear, mRate, mType).execute();

    }

    @Override
    protected int getResId() {
        return R.layout.activity_caculator_detail;
    }


    private List<LoanBean> computeBenxi(int total, int year, double rate) {
        /**
         * 贷款50万， 时间20年，利率按照利率5.9%计算：
         每月应还款额＝500000×5.9%/12×（1＋5.9%/12）^240/[（1＋5.9%/12）^240－1]
         =3553.37元
         */
        List<LoanBean> list = new ArrayList<>();
        //月利率
        double rateM = rate / 12;
        double d1 = Math.pow(1 + rateM, year * 12);
        int yuegong = (int) (((total * rateM * d1) / (d1 - 1)) + 0.5d);
        //已还利息总和
        int sumLixi = 0;
        //已还本息总和
        int sumBenxi = 0;
        //已还本金总额
        int sumBenjin = 0;
        //期数
        int size = year * 12;
        for (int i = 0; i < size; i++) {
            int lixi = (int) ((total - sumBenjin) * rateM + 0.5d);
            int benjin = yuegong - lixi;
            sumBenjin += benjin;
            sumLixi += lixi;
            sumBenxi += yuegong;
//            Log.i("TAG", "month(" + (i + 1) + ") = 月供=" + yuegong + ", 本金=" + benjin + ", 利息=" + lixi + ", 剩余本金=" + (total - sumBenjin) + "，已还利息=" + sumLixi + ",已还=" + sumBenxi);
            LoanBean loan = new LoanBean();
            loan.setStage(i + 1);
            loan.setYuegong(yuegong);
            loan.setYuegongbenjin(benjin);
            loan.setYuegonglixi(lixi);
            loan.setTotalRepaid(sumBenxi);
            loan.setBenjinRepaid(sumBenjin);
            loan.setLixiRepaid(sumLixi);
            loan.setBenjinLeft(total - sumBenjin);
            list.add(loan);
        }
        mSumBenxi = sumLixi;
        return list;
    }

    private List<LoanBean> computeBenjin(int total, int year, double rate) {
        List<LoanBean> list = new ArrayList<>();
        //月利率
        double rateM = rate / 12;
        //月还本金
        int dx = total / year / 12;
        //已还利息总和
        int sumRate = 0;
        //已还本息总和
        int sumCost = 0;
        //期数
        int size = year * 12;
        for (int i = 0; i < size; i++) {
            int r = (int) ((total - dx * i) * rateM + 0.5d);
            sumRate += r;
            sumCost += (dx + r);
//            Log.i("TAG", "month(" + (i + 1) + ") = 月供=" + (dx + r) + ", 本金=" + dx + ", 利息=" + r + ", 剩余本金=" + (total - dx * (i + 1)) + "，已还利息=" + sumRate + ",已还=" + sumCost);
            LoanBean loan = new LoanBean();
            loan.setStage(i + 1);
            loan.setYuegong(dx + r);
            loan.setYuegongbenjin(dx);
            loan.setYuegonglixi(r);
            loan.setTotalRepaid(sumCost);
            loan.setBenjinRepaid(dx * (i + 1));
            loan.setLixiRepaid(sumRate);
            loan.setBenjinLeft(total - loan.getBenjinRepaid());
            list.add(loan);
        }
        mSumBenjin = sumRate;
        return list;
    }

    class ComputeTask extends AsyncTask<Void, Void, List<LoanBean>> {

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
        protected List<LoanBean> doInBackground(Void... params) {
            if (mType == 0) {
                return computeBenxi(mTotal, mYear, mRate);
            } else {
                return computeBenjin(mTotal, mYear, mRate);
            }

        }

        @Override
        protected void onPostExecute(List<LoanBean> list) {
            super.onPostExecute(list);
            if (mType == 0) {
                mBenxiDatas = list;
                mAdapter.setData(mBenxiDatas);
                setLixiTextColor(mSumBenxi);
            } else {
                mBenjinDatas = list;
                mAdapter.setData(mBenjinDatas);
                setLixiTextColor(mSumBenjin);
            }
            mAdapter.notifyDataSetChanged();

        }
    }

    private void setLixiTextColor(int sum) {
        String s = String.valueOf(sum);
        SpannableString ss = new SpannableString("总利息：" + s + "元");
        ss.setSpan(new ForegroundColorSpan(Color.RED), 4, 4 + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSumLixi.setText(ss);
    }

}
