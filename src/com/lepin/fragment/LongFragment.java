package com.lepin.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.reflect.TypeToken;
import com.lepin.activity.LoginActivity;
import com.lepin.activity.R;
import com.lepin.activity.SelectCityActivity;
import com.lepin.entity.Car;
import com.lepin.entity.City;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Key;
import com.lepin.entity.Pinche;
import com.lepin.entity.Point;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Interfaces.ActivityResult;
import com.lepin.util.TimeUtils;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;

public class LongFragment extends BaseFragment implements OnClickListener, ActivityResult {
	private String mIndetity;
	private View mRootView;

	// 起点
	@ViewInject(id = R.id.item_text_editortext_arrow_title, parentId = R.id.long_fragment_start_layout)
	private TextView mStartTitle;

	@ViewInject(id = R.id.item_text_editortext_value, parentId = R.id.long_fragment_start_layout)
	private EditText mStartEditorText;

	@ViewInject(id = R.id.item_text_editortext_arrow_image, parentId = R.id.long_fragment_start_layout)
	private ImageView mStartImage;
	// 终点
	@ViewInject(id = R.id.item_text_editortext_arrow_title, parentId = R.id.long_fragment_end_layout)
	private TextView mEndTitle;

	@ViewInject(id = R.id.item_text_editortext_value, parentId = R.id.long_fragment_end_layout)
	private EditText mEndEdiText;

	@ViewInject(id = R.id.item_text_editortext_arrow_image, parentId = R.id.long_fragment_end_layout)
	private ImageView mEndImage;

	// ---------------------- 途经点----------------------------------
	@ViewInject(id = R.id.long_fragment_through_point_layout)
	private LinearLayout mThrouthPointLayout;

	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.long_fragment_through_point)
	private TextView mThrouthPointTitle;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.long_fragment_through_point)
	private ImageView mThrouthPointBtn;

	// 第一根分割线
	@ViewInject(id = R.id.long_fragment_through_point_one_divider)
	private View mTHrouthPointOneDivider;

	// 第二根分割线
	@ViewInject(id = R.id.long_fragment_through_point_bottom_divider)
	private View mThrouthPointBottomDivider;

	// 途经点１
	@ViewInject(id = R.id.long_fragment_through_point_one)
	private View mThrouthPointOneLayout;

	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.long_fragment_through_point_one)
	private TextView mThrouthPointOneTitle;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.long_fragment_through_point_one)
	private ImageView mThrouthPointOneBtn;

	// 途经点2
	@ViewInject(id = R.id.long_fragment_through_point_two)
	private View mThrouthPointTwoLayout;

	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.long_fragment_through_point_two)
	private TextView mThrouthPointTwoTitle;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.long_fragment_through_point_two)
	private ImageView mThrouthPointTwoBtn;

	// 出发时间
	@ViewInject(id = R.id.long_fragment_setout_layout)
	private View mSetOutLayout;

	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.long_fragment_setout_layout)
	private TextView mSetOutTitle;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.long_fragment_setout_layout)
	private TextView mSetOutTextView;

	// 费用
	@ViewInject(id = R.id.item_text_editortext_title, parentId = R.id.long_fragment_cost_layout)
	private TextView mCostTitle;

	@ViewInject(id = R.id.item_text_editortext_value, parentId = R.id.long_fragment_cost_layout)
	private EditText mCostTextView;

	// 可载人数

	@ViewInject(id = R.id.item_text_editortext_title, parentId = R.id.long_fragment_people_num_layout)
	private TextView mPeopleNumTitle;

	@ViewInject(id = R.id.item_text_editortext_value, parentId = R.id.long_fragment_people_num_layout)
	private EditText mPeopleNumTextView;

	// 联系电话

	@ViewInject(id = R.id.item_text_editortext_title, parentId = R.id.long_fragment_people_phone_layout)
	private TextView mPhoneTitle;

	@ViewInject(id = R.id.item_text_editortext_value, parentId = R.id.long_fragment_people_phone_layout)
	private EditText mPhoneNumTextView;

	// 备注
	@ViewInject(id = R.id.long_fragment_note)
	private EditText mNoTeEditText;

	// 发布按钮
	@ViewInject(id = R.id.long_fragment_publish_btn)
	private Button mPublishButton;

	private String strDate;// 暂时存储上下班时间
	long goDate = 0;
	private int days = 0;// 长途到期天数
	private Key mKey = null;

	private List<Point> pointsList = new ArrayList<Point>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parentGroup = (ViewGroup) mRootView.getParent();
			if (parentGroup != null) {
				parentGroup.removeView(mRootView);
			}
			return mRootView;
		}

		mRootView = inflater.inflate(R.layout.long_fragment, container, false);
		ViewInjectUtil.inject(this, mRootView);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setText();
		setListener();
	}

	private void setText() {
		mStartTitle.setText(getString(R.string.pick_details_start_point));

		mEndTitle.setText(getString(R.string.pick_details_end_point));

		mSetOutTitle.setText(getString(R.string.my_info_details_start_date));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		calendar.add(Calendar.DATE, 2);
		goDate = calendar.getTimeInMillis() / 1000;
		mSetOutTextView.setText(Util.getInstance().getNextCurrentTime());// 默认２天后

		mCostTitle.setText(getString(R.string.pick_details_cost_note));
		mCostTextView.setText(String.valueOf(5));
		Util.getInstance().setEditTextPoint2End(mCostTextView);

		if (mIndetity.equals(Pinche.PASSENGER)) {// 乘客
			((View) mRootView.findViewById(R.id.long_fragment_people_num_divider))
					.setVisibility(View.GONE);
			((View) mRootView.findViewById(R.id.long_fragment_people_num_layout))
					.setVisibility(View.GONE);
		} else {// 司机

			mThrouthPointOneBtn.setImageResource(R.drawable.pcb_ic_delete);
			mThrouthPointOneBtn.setOnClickListener(this);

			mThrouthPointTwoBtn.setImageResource(R.drawable.pcb_ic_delete);
			mThrouthPointTwoBtn.setOnClickListener(this);

			mThrouthPointTitle.setText(getString(R.string.through_point));
			mThrouthPointBtn.setImageResource(R.drawable.pcb_ic_add);
			mThrouthPointBtn.setOnClickListener(this);

			mThrouthPointLayout.setVisibility(View.VISIBLE);
			mPeopleNumTitle.setText(getString(R.string.pick_details_total_people));
			mPeopleNumTextView.setText(String.valueOf(3));
			Util.getInstance().setEditTextPoint2End(mPeopleNumTextView);
		}
		mPhoneTitle.setText(getString(R.string.pick_details_tel));
		mPhoneNumTextView.setEnabled(false);
		mPhoneNumTextView.setTextColor(getResources().getColor(R.color.btn_blue_normal));
		if (mKey != null) {
			if (mKey.getCarpoolType().equals(Pinche.CARPOOLTYPE_LONG_TRIP)) {
				mStartEditorText.setText(TextUtils.isEmpty(mKey.getStart_name()) ? "" : mKey
						.getStart_name());
				mEndEdiText
						.setText(TextUtils.isEmpty(mKey.getEnd_name()) ? "" : mKey.getEnd_name());

			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Util.getInstance().isUserLoging(getActivity())) {
			mPhoneNumTextView.setText(Util.getInstance().getUser(getActivity()).getTel());
		}
	}

	public LongFragment() {
		super();
	}

	public static LongFragment newInstance(String indentity, Key key) {

		LongFragment fragment = new LongFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.IDENTITY, indentity);
		bundle.putSerializable("key", key);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mIndetity = getArguments().getString(Constant.IDENTITY);
		mKey = (Key) getArguments().getSerializable("key");
	}

	private void setListener() {
		mStartImage.setOnClickListener(this);
		mEndImage.setOnClickListener(this);

		mSetOutLayout.setOnClickListener(this);
		mPublishButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mStartImage) {// 起点选择
			selectCity(Constant.I_START);
		} else if (v == mEndImage) {// 终点选择
			selectCity(Constant.I_END);
		} else if (v == mSetOutLayout) {// 出发时间
			Util.getInstance().showDateSelectorDialog(getActivity(), new Date(), dateSetListener);
		} else if (v == mPublishButton) {// 发布
			if (validateData()) {
				if (Util.getInstance().isUserLoging(getActivity())) {// 用户是否登录
					publishPinche();
				} else {
					Util.getInstance().go2Activity(getActivity(), LoginActivity.class);
				}
			}
		} else if (v == mThrouthPointBtn) {// 添加途经点
			if (pointsList.size() == 2) return;
			selectCity(Constant.I_THROUTH_POINT);
		} else if (v == mThrouthPointOneBtn) {// 删除第一个途经点
			String firstName = mThrouthPointOneTitle.getText().toString();
			if (pointsList.size() == 1) mThrouthPointBottomDivider.setVisibility(View.GONE);
			deletePointWithName(firstName);
			mThrouthPointOneLayout.setVisibility(View.GONE);
			mTHrouthPointOneDivider.setVisibility(View.GONE);

		} else if (v == mThrouthPointTwoBtn) {// 删除第二个途经点
			String secondName = mThrouthPointTwoTitle.getText().toString();
			if (pointsList.size() == 2) {
				// mThrouthPointBottomDivider.setVisibility(View.GONE);
				mTHrouthPointOneDivider.setVisibility(View.GONE);
			}
			deletePointWithName(secondName);
			mThrouthPointTwoLayout.setVisibility(View.GONE);

		}

	}

	private void deletePointWithName(String add) {
		if (pointsList == null && pointsList.size() == 0) return;
		for (int i = 0; i < pointsList.size(); i++) {
			if (pointsList.get(i).getName().equals(add)) {
				pointsList.remove(i);
				break;
			}
		}
	}

	int myear, mmonth, mday;
	/**
	 * 年月日选择监听
	 */
	private DatePickerDialog.OnDateSetListener dateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			strDate = year + "-" + (month + 1) + "-" + day;// 出发或者返回时间的年月日
			setStrDate(TimeUtils.parseStr2Date(strDate, "yyyy-MM-dd").toString());
			Util.getInstance().showDateSelectorDialog(getActivity(), new Date(), timeSetListener,
					"1", "m");
			myear = year;
			mmonth = month;
			mday = day;
			days = Util.getInstance().calculteDate(year, month, day);
		}

	};

	/**
	 * 时分选择监听
	 */
	private TimePickerDialog.OnTimeSetListener timeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			String strHHss = "";
			strHHss = (hourOfDay <= 9 ? "0" + hourOfDay : hourOfDay) + ":"
					+ (minute <= 9 ? "0" + minute : minute);

			if (days > 30) {
				Util.showToast(getActivity(), getString(R.string.start_time_must_1month));
				Util.getInstance().showDateSelectorDialog(getActivity(), new Date(),
						dateSetListener);
			} else if (!Util.getInstance()
					.theTimeIsAfterNow(myear, mmonth, mday, hourOfDay, minute)) {
				Util.showToast(getActivity(), getString(R.string.start_time_must_after_publish));
				Util.getInstance().showDateSelectorDialog(getActivity(), new Date(),
						dateSetListener);
			} else {
				goDate = Util.getInstance().getTimeInSconds(myear, mmonth, mday, hourOfDay, minute);
				mSetOutTextView.setText(getStrDate() + " " + strHHss);// 长途出发时间
			}
		}
	};

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrDate() {
		return strDate;
	}

	private void selectCity(int type) {
		Intent intent = new Intent(getActivity(), SelectCityActivity.class);
		startActivityForResult(intent, type);
	}

	private boolean validateData() {
		final String start = mStartEditorText.getText().toString().trim();
		if (start.length() <= 0) {
			Util.showToast(getActivity(), getString(R.string.start_is_null));
			return false;
		}
		final String end = mEndEdiText.getText().toString().trim();
		if (end.length() <= 0) {
			Util.showToast(getActivity(), getString(R.string.end_is_null));
			return false;
		}

		if (start.equals(end)) {
			Util.showToast(getActivity(), getString(R.string.start_end_can_not_same));
			return false;
		}
		// 费用
		final String charge = this.mCostTextView.getText().toString().trim();
		if (!Util.getInstance().checkMoneyIsRight(getActivity(), charge)) return false;

		if (mIndetity.equals(Pinche.DRIVER)) {
			final String peopleNum = mPeopleNumTextView.getText().toString();
			if (!Util.getInstance().checkPeopleNum(getActivity(), peopleNum)) return false;
		}
		final String noteString = mNoTeEditText.getText().toString();
		if (!ValidateTool.isInputLegitimate(noteString)) {
			Util.showToast(getActivity(), getString(R.string.input_type_limit));
			return false;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) return;
		City city = (City) data.getSerializableExtra("city");
		if (city == null) return;
		if (resultCode == Constant.SELECT_CITY_RESULT_CODE) {
			if (requestCode == Constant.I_START) {// 起点
				if (mEndEdiText.getText().toString().length() > 0
						&& mEndEdiText.getText().toString().equals(city.getArea_name())) {
					Util.showLongToast(getActivity(), getString(R.string.start_end_can_not_same));
				} else {
					this.mStartEditorText.setText(city.getArea_name());
				}
			} else if (requestCode == Constant.I_END) {// 终点
				if (mStartEditorText.getText().toString().length() > 0
						&& mStartEditorText.getText().toString().equals(city.getArea_name())) {
					Util.showLongToast(getActivity(), getString(R.string.end_start_can_not_same));
				} else {
					this.mEndEdiText.setText(city.getArea_name());
				}
			}

		}
		if (requestCode == Constant.I_THROUTH_POINT) {// 途经点
			String addr = city.getArea_name();
			if (TextUtils.isEmpty(addr)) return;
			if (pointsList.size() == 1) {
				if (!TextUtils.isEmpty(pointsList.get(0).getName())
						&& addr.equals(pointsList.get(0).getName())) {
					Util.showToast(getActivity(), getString(R.string.point_can_not_equls));
					return;
				}
				if (mThrouthPointOneLayout.getVisibility() == View.VISIBLE) {// 第一个点已经有了,添加第二个点
					mThrouthPointTwoTitle.setText(addr);
					mTHrouthPointOneDivider.setVisibility(View.VISIBLE);
					mThrouthPointTwoLayout.setVisibility(View.VISIBLE);
				} else {
					addPointFirst(addr);// 第一个途经点
				}
			} else if (pointsList.size() == 0) {// 第一个途经点
				addPointFirst(addr);
			}

			pointsList.add(new Point(addr));
		}
	}

	private void addPointFirst(String addr) {
		mThrouthPointOneTitle.setText(addr);
		mThrouthPointOneLayout.setVisibility(View.VISIBLE);
		mThrouthPointBottomDivider.setVisibility(View.VISIBLE);
		if (mThrouthPointTwoLayout.getVisibility() == View.VISIBLE) {
			mTHrouthPointOneDivider.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 发布拼车信息
	 */
	private void publishPinche() {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("infoType", mIndetity));// 司机
		paramList.add(new BasicNameValuePair("carpoolType", Pinche.CARPOOLTYPE_LONG_TRIP));// 长途
		if (mIndetity.equals(Pinche.DRIVER)) {// 司机发布
			paramList.add(new BasicNameValuePair("num", mPeopleNumTextView.getText().toString()));
			if (pointsList.size() > 0) {
				for (int i = 0; i < pointsList.size(); i++) {
					Point point = pointsList.get(i);
					paramList
							.add(new BasicNameValuePair("points[" + i + "].name", point.getName()));// 途经点
				}
			}
		} else {
			paramList.add(new BasicNameValuePair("num", String.valueOf(1)));
		}
		paramList.add(new BasicNameValuePair("startName", this.mStartEditorText.getText()
				.toString()));
		paramList.add(new BasicNameValuePair("endName", this.mEndEdiText.getText().toString()));

		paramList.add(new BasicNameValuePair("departureTime", String.valueOf(goDate)));// 出发时间
		paramList.add(new BasicNameValuePair("charge", mCostTextView.getText().toString()));
		if (mIndetity.equals(Pinche.DRIVER)) {// 如果是司机发布，而且没有车
			if (Util.getInstance().isUserLoging(getActivity())) {// 车辆Id没有就是新加
				final Car car = Util.getInstance().getLoginUser(getActivity()).getCar();
				if (car != null) {
					int carId = car.getCarId();
					if (carId > 0) {// 汽车Id
						paramList.add(new BasicNameValuePair("carId", String.valueOf(carId)));
					}
				} else {
					// 没有车，去添加车辆
					Util.getInstance().publish2AddCar(getActivity());
					return;
				}
			}
		}
		final String note = mNoTeEditText.getText().toString();
		if (note != null && note.length() > 0) paramList.add(new BasicNameValuePair("note", note));
		// Util.printLog("发布－长途：" + paramList);

		Util.getInstance().doPostRequest(getActivity(), new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = Util.getInstance().getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					Util.printLog("发布成功:" + jsonResult.getData().toString());

					Util.getInstance().go2Share(getActivity(), jsonResult.getData());
					getActivity().finish();
				} else {
					Util.showToast(getActivity(), getString(R.string.request_error));
				}
			}
		}, paramList, Constant.URL_PUBLISH, getString(R.string.publish_ing), false);

	}

}
