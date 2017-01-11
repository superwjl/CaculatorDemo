package com.tik.caculatordemo.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tik.caculatordemo.R;
import com.tik.caculatordemo.widget.wheel.OnWheelChangedListener;
import com.tik.caculatordemo.widget.wheel.OnWheelClickedListener;
import com.tik.caculatordemo.widget.wheel.WheelView;
import com.tik.caculatordemo.widget.wheel.adapters.ArrayWheelAdapter;

import java.util.List;

/**
 * 弹起式对话框
 */
public class CustomSelectDialog extends Dialog implements OnWheelChangedListener,OnWheelClickedListener {

	private Context mContext;
	private TextView mTvTitle;
	private TextView mTvCancel;
	private TextView mTvOk;
	private WheelView mWheelView;
	private List<String> mList;
	private String[] mDatas;
	private int mCurrIndex;
	private String mTitle;
	private OnBtnClickListener mOnBtnClickListener;

	public CustomSelectDialog(Context context, String title, int position, List<String> list, OnBtnClickListener listener) {
		super(context, R.style.ui_grid_numberplate_theme);
		this.mContext = context;
		this.mTitle = title;
		mCurrIndex = position < 0 ? 0 : position;
		this.mList = list;
		this.mOnBtnClickListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置对话框使用的布局文件
		this.setContentView(R.layout.ui_dialog_select);
		initView();
		setListener();
		initDatas();
	}



	private void initDatas() {
		windowDeploy();
		mTvTitle.setText(mTitle);
		if(mList != null){
			mDatas = new String[mList.size()];
			mList.toArray(mDatas);
		}else{
			mDatas = new String[0];
		}
		mWheelView.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mDatas));
		// 设置可见条目数量
		mWheelView.setVisibleItems(7);
		mWheelView.setCurrentItem(mCurrIndex);
	}

	private void setListener() {
		mWheelView.addChangingListener(this);
		mWheelView.addClickingListener(this);
		mTvOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnBtnClickListener != null){
					mOnBtnClickListener.select(mList.get(mCurrIndex), mCurrIndex);

				}
				dismiss();
			}
		});
		mTvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private void initView() {
		mWheelView = (WheelView) findViewById(R.id.uiwv_select1);
		mTvTitle = (TextView) findViewById(R.id.uitv_bank_select_title);
		mTvCancel = (TextView) findViewById(R.id.uitv_cancel);
		mTvOk = (TextView) findViewById(R.id.uitv_ok);
	}

	// 设置窗口显示
	public void windowDeploy() {
		Window window = getWindow(); // 得到对话框
		window.setWindowAnimations(R.style.ui_dialogWindowAnim); // 设置窗口弹出动画
		window.setBackgroundDrawableResource(R.color.black); // 设置对话框背景为透明
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.height = LayoutParams.WRAP_CONTENT;
		wl.width = LayoutParams.MATCH_PARENT;
		wl.gravity = Gravity.BOTTOM; // 设置布局位置
		window.setAttributes(wl);
	}


	@Override
	public void onItemClicked(WheelView wheel, int itemIndex) {
		// TODO Auto-generated method stub
		if (wheel == mWheelView) {
			mWheelView.setCurrentItem(itemIndex, true);
			mCurrIndex = itemIndex;
		}
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mWheelView) {
			mCurrIndex = newValue;
		}
	}
	public interface OnBtnClickListener{
		void select(String s, int position);
	}

}
