package com.lepin.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Pinche;
import com.lepin.entity.Point;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.TimeUtils;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

@Contextview(R.layout.my_publish_details)
public class MyPublishDetailsActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mpBack;// 返回
	@ViewInject(id = R.id.common_title_title)
	private TextView mpTitle;// 标题
	@ViewInject(id = R.id.common_title_operater)
	private TextView mpEdit;// 修改

	@ViewInject(id = R.id.my_publish_details_start)
	private TextView mpStart;// 出发地
	@ViewInject(id = R.id.my_publish_details_destination)
	private TextView mpDestination;// 目的地
	@ViewInject(id = R.id.my_publish_details_edit_note)
	private TextView mpEditNote;// 编辑提示
	@ViewInject(id = R.id.my_publish_details_layout_time)
	private View mpTimeLayout;// 时间段布局
	@ViewInject(id = R.id.my_publish_details_time)
	private TextView mpTime;// 时间段

	@ViewInject(id = R.id.my_publish_details_start_date_layout)
	private TextView mpStartTimelayout;// 出发时间
	@ViewInject(id = R.id.my_publish_details_start_date)
	private TextView mpStartTime;// 出发时间
	@ViewInject(id = R.id.my_publish_stop_starttime)
	private CheckBox mStartTimeCheckBox;
	@ViewInject(id = R.id.my_publish_details_backtime_layout)
	private View mpBackTimeLayout;// 返程时间布局
	@ViewInject(id = R.id.my_publish_details_back_date)
	private TextView mpBackTime;// 返程时间
	@ViewInject(id = R.id.my_publish_stop_backtime)
	private CheckBox mBackTimeCheckBox;// 停止返回时间

	@ViewInject(id = R.id.point1_layout)
	private View pointLayout1; // 途经点布局1
	@ViewInject(id = R.id.point2_layout)
	private View pointLayout2; // 途经点布局2
	@ViewInject(id = R.id.my_publish_details_through_point1)
	private TextView point1;// 途经点1
	@ViewInject(id = R.id.my_publish_details_through_point2)
	private TextView point2;// 途经点2
	@ViewInject(id = R.id.my_publish_line)
	private View line;// 两个途经点之间的中间线
	// @ViewInject(id = R.id.line2)
	// private View line2;// 两个途经点之间的中间线

	/*
	 * @ViewInject(id = R.id.driver_or_passager_icon) private ImageView mpIoc;//
	 * 乘客司机图标
	 */
	@ViewInject(id = R.id.my_publish_details_cost)
	private EditText mpCost;// 费用

	@ViewInject(id = R.id.my_publish_details_peponum)
	private TextView mpPeopNum;// 搭载人数

	@ViewInject(id = R.id.my_publish_details_note)
	private EditText mpNoTe;// 备注

	private String info_id;
	private String carpool_type;//上下班，长途
	private int StartOrBack = 0;// (0:出发，1：返回）
	private String strDate;//
	private String mpSdate = "";// 出发
	private String mpEdate = "";// 返回
	private String[] peoNum;// 驾龄数组
	private Pinche pinche;
	private long days = 0;// 长途到期天数
	private long millisecond = 0;
	private long nowTime;// 当前时间
	private long endTime;// 长途出发时间
	private String num = "1";
	private boolean isEdit = false;
	private Util util = Util.getInstance();
	private String cycleString;

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		Intent intent = MyPublishDetailsActivity.this.getIntent();
		info_id = String.valueOf(intent.getIntExtra("infoId", -1));
		carpool_type = intent.getStringExtra("carpoolType");

		getInfoById(info_id);
		peoNum = getResources().getStringArray(R.array.peopnum);// 得到驾龄数据
		util.checkCost(mpCost);
	}

	public void initView() {
		mpEdit.setVisibility(View.VISIBLE);
		mpEditNote.setVisibility(View.GONE);
		mpTimeLayout.setVisibility(View.GONE);
		mpTitle.setText(getString(R.string.my_publish_detail));
		mpEdit.setText(getString(R.string.publish_btn_update_text));
		mpBack.setOnClickListener(this);
		mpEdit.setOnClickListener(this);
		mpTimeLayout.setOnClickListener(this);
		mpStartTime.setOnClickListener(this);
		mpBackTime.setOnClickListener(this);
		mpPeopNum.setOnClickListener(this);
		mStartTimeCheckBox.setChecked(true);
		mBackTimeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isEdit) {
					if (!isChecked) {
						mpBackTime.setText(getString(R.string.publish_back_date_no));
						mpBackTime.setEnabled(false);
						mpEdate = "";
					} else {
						mpBackTime.setText(getString(R.string.publish_back_date_hint));
						mpBackTime.setEnabled(true);
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == mpBack)// 返回
		{
			if (isEdit) {
				setEnable(false);
				setData(pinche);
				mpEdit.setText(getString(R.string.publish_btn_update_text));
				isEdit = false;
			} else {
				this.finish();
			}
		} else if (v == mpEdit)// 编辑
		{
			if (!isEdit) {
				mpEditNote.setVisibility(View.VISIBLE);
				mBackTimeCheckBox.setVisibility(View.VISIBLE);
				mStartTimeCheckBox.setVisibility(View.VISIBLE);
				mpEdit.setText(getString(R.string.publish_btn_save_text));
				isEdit = true;
				setEnable(true);
				if (carpool_type.equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {// 上下班
					mpStartTime.setText(mpSdate);
					if (!mpEdate.equals("")) {
						mpBackTime.setText(mpEdate);
					} else {
						mpBackTime.setText(getString(R.string.publish_back_date_no));
					}
				}
				nowTime = Calendar.getInstance().getTimeInMillis();// 当前时间
				endTime = 1000 * TimeUtils.dateStrToSecond(pinche.getDepartureTime());// 无法转换
				millisecond = endTime - nowTime;
				days = millisecond / (24 * 60 * 60 * 1000);
				mpCost.setSelection(mpCost.getText().length());// 光标移到末尾
			} else {
				if (carpool_type.equals(Pinche.CARPOOLTYPE_LONG_TRIP)) {// 长途检查时间是否合理
					if (millisecond <= 0 || days > 90) {
						Util.showToast(MyPublishDetailsActivity.this,
								getString(R.string.publish_date_wrong));
						util.showDateSelectorDialog(MyPublishDetailsActivity.this, new Date(),
								dateSetListener);
						return;
					}
				}

				if (mpCost.getText().toString().trim() != null
						&& !"".equals(mpCost.getText().toString().trim())) {
					int tempCost = Integer.parseInt(mpCost.getText().toString().trim());
					if (tempCost > 0) {
						String peopNum = mpPeopNum.getText().toString().trim();
						num = peopNum.substring(0, peopNum.length());
						updateInfoById();
					} else {
						Util.showToast(MyPublishDetailsActivity.this,
								getString(R.string.publish_money_error));
						mpCost.requestFocus();
					}
				} else {
					Util.showToast(MyPublishDetailsActivity.this,
							getString(R.string.publish_input_money));
					mpCost.requestFocus();
				}
			}
		} else if (v == mpTimeLayout) {
			Intent intent = new Intent(MyPublishDetailsActivity.this, WeekSelectActivity.class);
			startActivityForResult(intent, 11);
		} else if (v == mpStartTime) {
			StartOrBack = 0;
			if (carpool_type.equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {
				util.showDateSelectorDialog(this, new Date(), timeSetListener, "0", "m");
			} else {
				util.showDateSelectorDialog(this, new Date(), dateSetListener);
			}
		} else if (v == mpBackTime) {
			StartOrBack = 1;
			util.showDateSelectorDialog(this, new Date(), timeSetListener, "0", "m");
		} else if (v == mpPeopNum) {
			choicePeopNum();
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	public void setEnable(boolean b) {
		if (carpool_type.equals(Pinche.CARPOOLTYPE_ON_OFF_WORK))// 上下班
		{
			if (b == true) {
				mpTimeLayout.setVisibility(View.VISIBLE);
			} else {
				mpTimeLayout.setVisibility(View.GONE);
			}
			mpStartTime.setText(mpTime.getText().toString() + " " + mpSdate);
			if (!TextUtils.isEmpty(mpEdate)) {
				mpBackTime.setText(mpTime.getText().toString() + " " + mpEdate);
			} else {
				mpBackTime.setText(getString(R.string.publish_back_date_no));
			}
			mpBackTime.setEnabled(b);
		}
		if (b == true) {
			mpEditNote.setVisibility(View.VISIBLE);
			mBackTimeCheckBox.setVisibility(View.VISIBLE);
			mStartTimeCheckBox.setVisibility(View.VISIBLE);
		} else {
			mpEditNote.setVisibility(View.GONE);
			mBackTimeCheckBox.setVisibility(View.GONE);
			mStartTimeCheckBox.setVisibility(View.GONE);
		}
		mpStartTime.setEnabled(b);
		mpCost.setEnabled(b);
		mpPeopNum.setEnabled(b);
		mpNoTe.setEnabled(b);
		mBackTimeCheckBox.setEnabled(b);
	}

	/**
	 * 数据填写（初始化和返回时）
	 * 
	 * @param pinche
	 */
	public void setData(Pinche pinche) {
		mpStart.setText(pinche.getStart_name(this));
		mpDestination.setText(pinche.getEnd_name(this));

		// 途径点
		Point[] points = pinche.getPoints();
		if (null != points) {
			line.setVisibility(View.VISIBLE);
			View[] layout = { pointLayout1, pointLayout2 };
			TextView[] pointsTextViews = { point1, point2 };
			for (int i = 0; i < points.length; i++) {
				layout[i].setVisibility(View.VISIBLE);
				pointsTextViews[i].setText(points[i].getName());
			}
		}

		if (carpool_type.equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {
			cycleString = pinche.getCycle().getNums();
			mpTime.setText(pinche.getCycle().getTxt());// 时间段
			mpStartTime.setText(mpTime.getText() + " " + pinche.getDepartureTime());
			if (!pinche.getBackTime().equals("")) {
				mpBackTime.setText(mpTime.getText() + " " + pinche.getBackTime());
				mBackTimeCheckBox.setChecked(true);
			} else {
				mpBackTime.setText(getString(R.string.publish_back_date_no));
				mBackTimeCheckBox.setChecked(false);
			}
			mpSdate = pinche.getDepartureTime();
			mpEdate = pinche.getBackTime();
		} else {
			mpStartTime.setText(pinche.getDepartureTime());
			mpBackTimeLayout.setVisibility(View.GONE);
		}
		mpCost.setText(String.valueOf(pinche.getCharge()));
		mpPeopNum.setText(String.valueOf(pinche.getNum()));
		String note = pinche.getNote();
		if (TextUtils.isEmpty(note)) {
			mpNoTe.setHint(getString(R.string.publish_not_have_note));
		} else {
			mpNoTe.setText(pinche.getNote());
		}
		String title = mpTitle.getText().toString();
		if (mpTitle.getText().length() <= "我的发布详情".length()) {

			if (pinche.getInfoType().equals(Pinche.DRIVER))// 司机
			{
				mpTitle.setText(title + "(" + getString(R.string.driver) + ")");
			} else {
				((TextView) findViewById(R.id.my_publish_details_peponum_title))
						.setText(getString(R.string.my_car_total_people));
				mpTitle.setText(title + "(" + getString(R.string.passenger) + ")");
			}
		}
	}

	/**
	 * 获取时间段
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 11:
			String str = data.getStringExtra("result");
			cycleString = data.getStringExtra("numResult");
			this.mpTime.setText(str);// 获取周期
			break;
		}
	}

	/**
	 * 年月日选择监听
	 */
	private DatePickerDialog.OnDateSetListener dateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			strDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;// 出发或者返回时间的年月日
			if (carpool_type.equals(Pinche.CARPOOLTYPE_LONG_TRIP)) {
				setStrDate(TimeUtils.parseStr2Date(strDate, "yyyy-MM-dd").toString());
				util.showDateSelectorDialog(MyPublishDetailsActivity.this, new Date(),
						timeSetListener, "1", "");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			try {
				nowTime = Calendar.getInstance().getTimeInMillis();// 当前时间
				endTime = sdf.parse(strDate).getTime();
				millisecond = endTime - nowTime;
				days = millisecond / (24 * 60 * 60 * 1000);
			} catch (ParseException e) {
				days = -1;
				millisecond = 0;
			}
		}
	};

	/**
	 * 时分选择监听
	 */
	private TimePickerDialog.OnTimeSetListener timeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			String strHHss = "";
			if (carpool_type.equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {// 上下班
				if (StartOrBack == 0) {// 出发时间
					// strHHss = hourOfDay + ":" + minute;
					strHHss = (hourOfDay <= 9 ? "0" + hourOfDay : hourOfDay) + ":" + minute;
					mpStartTime.setText(strHHss);// 上下班出发时间
					mpSdate = TimeUtils.parseStr2Date(strHHss, "HH:mm").toString();
				} else {
					// strHHss = hourOfDay + ":" + minute;
					strHHss = (hourOfDay <= 9 ? "0" + hourOfDay : hourOfDay) + ":" + minute;
					mpBackTime.setText(strHHss);// 上下班返回时间
					mpEdate = TimeUtils.parseStr2Date(strHHss, "HH:mm").toString();
				}
			} else {
				// strHHss = hourOfDay + ":" + minute;
				strHHss = (hourOfDay <= 9 ? "0" + hourOfDay : hourOfDay) + ":"
						+ (String.valueOf(minute).length() == 2 ? minute : "0" + minute);
				if (millisecond <= 0 || days > 90) {
					Util.showToast(MyPublishDetailsActivity.this,
							getString(R.string.publish_date_wrong));
					util.showDateSelectorDialog(MyPublishDetailsActivity.this, new Date(),
							dateSetListener);
				} else {
					// mpStartTime.setText(getStrDate() + " "
					// + TimeUtils.parseStr2Date(strHHss, "HH:ss"));// 长途出发时间
					mpStartTime.setText(getStrDate() + " " + strHHss);// 长途出发时间
				}
			}
		}
	};

	/**
	 * 得到可载人数下拉框
	 */
	protected void choicePeopNum() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyPublishDetailsActivity.this);
		builder.setTitle(R.string.pick_details_total_people).setItems(peoNum,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mpPeopNum.setText(peoNum[which]);
					}
				});
		builder.create().show();
	}

	/**
	 * 通过info_id得到详细信息
	 * 
	 * @param id
	 */
	public void getInfoById(String id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("infoId", id));

		util.doPostRequest(MyPublishDetailsActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				Util.printLog("发布详情:"+result);
				JsonResult<Pinche> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<Pinche>>() {
						});
				if (jsonResult.isSuccess()) {
					pinche = jsonResult.getData();
					setData(pinche);
					if (carpool_type.equals(Pinche.CARPOOLTYPE_LONG_TRIP))// 长途过期
					{
						String currentTime = TimeUtils.getCurrentTime();
						long cTime = TimeUtils.dateStrToSecond(currentTime);
						String startTime = pinche.getDepartureTime();
						long eTime = TimeUtils.dateStrToSecond(startTime);
						if (eTime <= cTime) {
							mpEdit.setVisibility(View.INVISIBLE);
						}
					}
				} else {
					Util.showToast(MyPublishDetailsActivity.this,
							getString(R.string.publish_get_detail_fault));
					MyPublishDetailsActivity.this.finish();
				}
			}
		}, params, Constant.URL_GETINFOBYID, "", false);

	}

	/**
	 * 更新发布信息
	 * 
	 * @param pinche
	 */
	public void updateInfoById() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("infoId", info_id));
		params.add(new BasicNameValuePair("infoType", pinche.getInfoType()));
		params.add(new BasicNameValuePair("carpoolType", pinche.getCarpoolType()));
		params.add(new BasicNameValuePair("startName", String.valueOf(pinche.getStart_name(this))));
		params.add(new BasicNameValuePair("endName", String.valueOf(pinche.getEnd_name(this))));
		params.add(new BasicNameValuePair("carId", String.valueOf(pinche.getCar_id())));

		if (carpool_type.equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {// 上下班
			params.add(new BasicNameValuePair("startLat", String.valueOf(pinche.getStartLat())));
			params.add(new BasicNameValuePair("startLon", String.valueOf(pinche.getStartLon())));
			params.add(new BasicNameValuePair("endLat", String.valueOf(pinche.getEndLat())));
			params.add(new BasicNameValuePair("endLon", String.valueOf(pinche.getEndLon())));
			params.add(new BasicNameValuePair("cycle", cycleString));// 时间段
			params.add(new BasicNameValuePair("departureTime", String.valueOf(TimeUtils
					.dateToSecond(mpSdate))));// 出发
			if (mBackTimeCheckBox.isChecked() && !mpEdate.equals("")) {
				params.add(new BasicNameValuePair("backTime", String.valueOf(TimeUtils
						.dateToSecond(mpEdate))));// 返回
			}
		} else {
			String time = mpStartTime.getText().toString().trim();
			params.add(new BasicNameValuePair("departureTime", String.valueOf(TimeUtils
					.dateStrToSecond(time))));// 长途出发
		}
		params.add(new BasicNameValuePair("charge", mpCost.getText().toString()));// 费用
		params.add(new BasicNameValuePair("num", num));// 人数
		params.add(new BasicNameValuePair("note", mpNoTe.getText().toString().trim()));// 备注

		util.doPostRequest(MyPublishDetailsActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				TypeToken<JsonResult<String>> tokens = new TypeToken<JsonResult<String>>() {
				};
				Gson gsons = new GsonBuilder().create();
				JsonResult<String> jsonResults = gsons.fromJson(result, tokens.getType());
				if (jsonResults.isSuccess()) {
					Util.showToast(MyPublishDetailsActivity.this,
							getString(R.string.publish_data_update_success));
					setEnable(false);
					mpEdit.setText(getString(R.string.publish_btn_update_text));
					isEdit = false;
				} else {
					Util.showToast(MyPublishDetailsActivity.this,
							getString(R.string.publish_data_update_fault));
				}
			}

			@Override
			public void onFail(String errorType, String errorMsg) {
				// TODO Auto-generated method stub
				super.onFail(errorType, errorMsg);
				Util.showToast(MyPublishDetailsActivity.this, errorMsg);
			}
		}, params, Constant.URL_EDITINFO, getString(R.string.publish_data_update_ing), true);

	}

}
