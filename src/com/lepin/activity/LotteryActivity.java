package com.lepin.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

/**
 * 抽奖界面
 * 
 * @author zhiqiang
 * 
 */
@Contextview(R.layout.lottery_activity)
public class LotteryActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	@ViewInject(id = R.id.lottery_btn_layout)
	private LinearLayout mLotteryBtnLayout;// 按钮view

	@ViewInject(id = R.id.lottery_btn_100_layout)
	private LinearLayout m100Btn;

	@ViewInject(id = R.id.lottery_btn_200_layout)
	private LinearLayout m200Btn;

	// 转盘布局
	@ViewInject(id = R.id.lottery_draw_plate_layout)
	private LinearLayout mLotteryDrawPlateLayout;

	@ViewInject(id = R.id.lottery_draw_plate)
	private ImageView mDrawPlateImageView;

	@ViewInject(id = R.id.lottery_start)
	private ImageButton mStart;// 开始抽奖按钮

	// 抽奖信息
	@ViewInject(id = R.id.lottery_info)
	private TextView mLotteryInfoTextView;

	private int lottery100 = 100;
	private int lottery200 = 200;
	private int type;
	Animation rotateAnimation;

	LinkedHashMap<Integer, String> lotteryInfoHashMapA = new LinkedHashMap<Integer, String>();
	LinkedHashMap<Integer, String> lotteryInfoHashMapB = new LinkedHashMap<Integer, String>();

	List<String> circulationList = new ArrayList<String>();
	private int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		mTitle.setText(getString(R.string.pcb_lottery));
		mBack.setOnClickListener(this);
		m100Btn.setOnClickListener(this);
		m200Btn.setOnClickListener(this);
		mStart.setOnClickListener(this);
		rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.roate_animation);
		lotteryInfoHashMapA.put(0, "50拼车币");
		lotteryInfoHashMapA.put(1, "10元话费");
		lotteryInfoHashMapA.put(2, "100拼车币");
		lotteryInfoHashMapA.put(3, "20元话费");
		lotteryInfoHashMapA.put(4, "200拼车币");
		lotteryInfoHashMapA.put(5, "50元话费");
		lotteryInfoHashMapA.put(6, "500拼车币");
		lotteryInfoHashMapA.put(7, "1000拼车币");

		lotteryInfoHashMapB.put(0, "100拼车币");
		lotteryInfoHashMapB.put(1, "50元话费");
		lotteryInfoHashMapB.put(2, "200拼车币");
		lotteryInfoHashMapB.put(3, "蓝牙耳机");
		lotteryInfoHashMapB.put(4, "500拼车币");
		lotteryInfoHashMapB.put(5, "ipad 4");
		getCirculationInfo();
		final Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					if (circulationList.size() > 0) {
						if (mLotteryInfoTextView.getVisibility() != View.VISIBLE)
							mLotteryInfoTextView.setVisibility(View.VISIBLE);
						mLotteryInfoTextView.setText(circulationList.get(index));
						Util.printLog("轮播信息:" + circulationList.get(index));
						if ((index + 1) == circulationList.size()) {
							index = 0;
						} else {
							index += 1;
						}
					}
				}
			}
		};
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mHandler.obtainMessage(1).sendToTarget();
				mHandler.postDelayed(this, 1000);
			}
		}, 1000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBack) {
			if (mLotteryDrawPlateLayout.getVisibility() == View.VISIBLE) {
				mLotteryDrawPlateLayout.setVisibility(View.GONE);
				mLotteryBtnLayout.setVisibility(View.VISIBLE);
			} else {
				finish();
			}
		} else if (v == m100Btn) {
			type = lottery100;
			setDrawPlate(lottery100);
		} else if (v == m200Btn) {
			type = lottery200;
			setDrawPlate(lottery200);
		} else if (v == mStart) {
			roateDrawPlate();
			doLottery(type);
		}
	}

	// 旋转转盘
	private void roateDrawPlate() {
		mDrawPlateImageView.startAnimation(rotateAnimation);
	}

	private void stopRoate() {
		mDrawPlateImageView.clearAnimation();
	}

	private void doLottery(final int t) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("type", t == lottery100 ? "A" : "B"));
		HttpRequestOnBackgrount lotteryBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.POST, new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						stopRoate();
						JsonResult<Integer> jsonResult = Util.getInstance().getObjFromJsonResult(
								result, new TypeToken<JsonResult<Integer>>() {
								});
						if (jsonResult != null && jsonResult.isSuccess()) {
							int num = jsonResult.getData();
							String info = getInfo(num);
							String title = "恭喜您抽中了:" + info;
							showNoteDialog(title);
						}
					}

					@Override
					public void onFail(String errorType, String errorMsg) {
						stopRoate();
						Util.showToast(LotteryActivity.this, "哎呀，抽不了奖了，出了点问题");
					}
				}, params, LotteryActivity.this, true);
		lotteryBackgrount.execute(Constant.LOTTERY);
	}

	private String getInfo(int key) {
		if (type == lottery100) {
			return lotteryInfoHashMapA.get(key);
		} else if (type == lottery200) {
			return lotteryInfoHashMapB.get(key);
		}
		return null;
	}

	private void showNoteDialog(String title) {
		Util.getInstance().showDialog(this, title, "再来一把", "算了", new OnOkOrCancelClickListener() {

			@Override
			public void onOkClick(int type) {
				if (type == PcbConfirmDialog.OK) {

				} else {
					mLotteryDrawPlateLayout.setVisibility(View.GONE);
					mLotteryBtnLayout.setVisibility(View.VISIBLE);
				}

			}
		});
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	private void setDrawPlate(int type) {
		if (type == lottery100) {
			mDrawPlateImageView.setBackgroundResource(R.drawable.pcb_draw_plate_100);
		} else if (type == lottery200) {
			mDrawPlateImageView.setBackgroundResource(R.drawable.pcb_draw_plate_200);
		}
		mLotteryBtnLayout.setVisibility(View.GONE);
		mLotteryDrawPlateLayout.setVisibility(View.VISIBLE);
	}

	private void getCirculationInfo() {
		HttpRequestOnBackgrount getCirculationBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.GET, new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						Util.printLog("轮播信息:" + result);
						if (!TextUtils.isEmpty(result)) {
							JsonResult<List<String>> jsonResult = Util.getInstance()
									.getObjFromJsonResult(result,
											new TypeToken<JsonResult<List<String>>>() {
											});
							if (jsonResult != null && jsonResult.isSuccess()) {
								circulationList = jsonResult.getData();
							}
						}

					}
				}, null, LotteryActivity.this, false);
		getCirculationBackgrount.execute(Constant.GET_CIRCULATION);
	}
}
