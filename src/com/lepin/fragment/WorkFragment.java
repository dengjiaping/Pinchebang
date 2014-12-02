package com.lepin.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.reflect.TypeToken;
import com.lepin.activity.ChoiceAdrrActivity;
import com.lepin.activity.LoginActivity;
import com.lepin.activity.R;
import com.lepin.activity.SearchAdrrOnMapActivity;
import com.lepin.activity.WeekSelectActivity;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Key;
import com.lepin.entity.Pinche;
import com.lepin.entity.Point;
import com.lepin.entity.User;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Interfaces.ActivityResult;
import com.lepin.util.TimeUtils;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

public class WorkFragment extends BaseFragment implements OnClickListener, ActivityResult {
	private String mIndetity;
	private View mRootView;
	// 起点
	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.work_fragment_start_layout)
	private TextView mStartTitle;

	@ViewInject(id = R.id.item_text_image_value, parentId = R.id.work_fragment_start_layout)
	private TextView mStartTextView;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.work_fragment_start_layout)
	private ImageView mStartImView;

	// 终点
	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.work_fragment_end_layout)
	private TextView mEndTitle;

	@ViewInject(id = R.id.item_text_image_value, parentId = R.id.work_fragment_end_layout)
	private TextView mEndTextView;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.work_fragment_end_layout)
	private ImageView mEndImView;

	// ---------------------- 途经点----------------------------------
	@ViewInject(id = R.id.work_fragment_through_point_layout)
	private LinearLayout mThrouthPointLayout;

	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.work_fragment_through_point)
	private TextView mThrouthPointTitle;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.work_fragment_through_point)
	private ImageView mThrouthPointBtn;

	// 第一根分割线
	@ViewInject(id = R.id.work_fragment_through_point_one_divider)
	private View mTHrouthPointOneDivider;

	// 第二根分割线
	@ViewInject(id = R.id.work_fragment_through_point_bottom_divider)
	private View mThrouthPointBottomDivider;

	// 途经点１
	@ViewInject(id = R.id.work_fragment_through_point_one)
	private View mThrouthPointOneLayout;

	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.work_fragment_through_point_one)
	private TextView mThrouthPointOneTitle;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.work_fragment_through_point_one)
	private ImageView mThrouthPointOneBtn;

	// 途经点2
	@ViewInject(id = R.id.work_fragment_through_point_two)
	private View mThrouthPointTwoLayout;

	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.work_fragment_through_point_two)
	private TextView mThrouthPointTwoTitle;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.work_fragment_through_point_two)
	private ImageView mThrouthPointTwoBtn;

	// 时间段
	@ViewInject(id = R.id.work_fragment_time_layout)
	private View mTimeLayout;

	@ViewInject(id = R.id.item_text_text_arrow_title, parentId = R.id.work_fragment_time_layout)
	private TextView mTimeTitle;

	@ViewInject(id = R.id.item_text_text_arrow_value, parentId = R.id.work_fragment_time_layout)
	private TextView mTimeTextView;

	@ViewInject(id = R.id.item_text_text_arrow_image, parentId = R.id.work_fragment_time_layout)
	private ImageView mTimeImView;

	// 出发
	@ViewInject(id = R.id.work_fragment_set_out_layout)
	private View mSetOutLayout;

	@ViewInject(id = R.id.item_text_text_arrow_title, parentId = R.id.work_fragment_set_out_layout)
	private TextView mSetOutTitle;

	@ViewInject(id = R.id.item_text_text_arrow_value, parentId = R.id.work_fragment_set_out_layout)
	private TextView mSetOutTextView;

	// 返程
	@ViewInject(id = R.id.work_fragment_return_layout)
	private View mReturnLayout;

	@ViewInject(id = R.id.item_text_text_arrow_title, parentId = R.id.work_fragment_return_layout)
	private TextView mReturnTitle;

	@ViewInject(id = R.id.item_text_text_arrow_value, parentId = R.id.work_fragment_return_layout)
	private TextView mReturnTextView;

	// 费用
	@ViewInject(id = R.id.item_text_editortext_title, parentId = R.id.work_fragment_cost_layout)
	private TextView mCostTitle;

	@ViewInject(id = R.id.item_text_editortext_value, parentId = R.id.work_fragment_cost_layout)
	private EditText mCostTextView;

	// 可载人数
	@ViewInject(id = R.id.work_fragment_people_num_layout)
	private LinearLayout mPeopleLayout;

	@ViewInject(id = R.id.item_text_editortext_title, parentId = R.id.work_fragment_people_num)
	private TextView mPeopleNumTitle;

	@ViewInject(id = R.id.item_text_editortext_value, parentId = R.id.work_fragment_people_num)
	private EditText mPeopleNumTextView;

	// 联系电话

	@ViewInject(id = R.id.item_text_editortext_title, parentId = R.id.work_fragment_people_phone_layout)
	private TextView mPhoneTitle;

	@ViewInject(id = R.id.item_text_editortext_value, parentId = R.id.work_fragment_people_phone_layout)
	private EditText mPhoneNumTextView;

	// 备注
	@ViewInject(id = R.id.work_fragment_note)
	private EditText mNoTeEditText;

	// 发布按钮
	@ViewInject(id = R.id.work_fragment_publish_btn)
	private Button mPublishButton;

	private String startCity = "";
	private String endCity = "";

	// 起点经纬度
	private long start_lat = 0;
	private long start_lon = 0;
	// 终点经纬度
	private long end_lat = 0;
	private long end_lon = 0;
	// private boolean isNight = true;// 是否晚上同行（只有上下班才有）
	private boolean isStartTime = true;// 标识是否返程
	private String cycleString;// 循环周期
	private Key mKey = null;
	private String mStartCityCode = "";
	private String mEndCityCode = "";

	private List<Point> pointsList = new ArrayList<Point>();// 途经点数组

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parentGroup = (ViewGroup) mRootView.getParent();
			if (parentGroup != null) {
				parentGroup.removeView(mRootView);
			}
			return mRootView;
		}

		mRootView = inflater.inflate(R.layout.work_fragment, container, false);
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
		cycleString = getString(R.string.work_day_num_text);
		mStartTitle.setText(getString(R.string.pick_details_start_point));
		mStartImView.setBackgroundResource(R.drawable.mapview_blue_selector);

		mEndTitle.setText(getString(R.string.pick_details_end_point));
		mEndImView.setBackgroundResource(R.drawable.mapview_green_selector);

		mTimeTitle.setText(getString(R.string.publish_period));
		mTimeTextView.setText(getString(R.string.work_day));

		mSetOutTitle.setText(getString(R.string.publish_start_date));
		mSetOutTextView.setText(TimeUtils.getCurrentTime("07:30"));

		mReturnTitle.setText(getString(R.string.pick_details_back_date));
		// mReturnTextView.setText(TimeUtils.getCurrentTime("18:30"));

		mCostTitle.setText(getString(R.string.pick_details_cost_note));
		mCostTextView.setMaxLines(4);
		mCostTextView.setText(String.valueOf(5));
		Util.getInstance().setEditTextPoint2End(mCostTextView);

		if (mIndetity.equals(Pinche.PASSENGER)) {// 乘客
			((View) mRootView.findViewById(R.id.work_fragment_people_num_layout))
					.setVisibility(View.GONE);
		} else {// 司机
			mThrouthPointOneBtn.setImageResource(R.drawable.pcb_ic_delete);
			mThrouthPointOneBtn.setOnClickListener(this);

			mThrouthPointTwoBtn.setImageResource(R.drawable.pcb_ic_delete);
			mThrouthPointTwoBtn.setOnClickListener(this);

			mThrouthPointTitle.setText(getString(R.string.through_point));
			mThrouthPointBtn.setImageResource(R.drawable.pcb_ic_add);

			mThrouthPointLayout.setVisibility(View.VISIBLE);

			mPeopleNumTitle.setText(getString(R.string.pick_details_total_people));
			mPeopleNumTextView.setText(String.valueOf(3));
			mPeopleLayout.setVisibility(View.VISIBLE);
			Util.getInstance().setEditTextPoint2End(mPeopleNumTextView);
			mThrouthPointBtn.setOnClickListener(this);
		}

		mPhoneTitle.setText(getString(R.string.pick_details_tel));
		mPhoneNumTextView.setFocusable(false);
		mPhoneNumTextView.setEnabled(false);
		mPhoneNumTextView.setTextColor(getResources().getColor(R.color.btn_blue_normal));
		if (mKey != null) {
			if (mKey.getCarpoolType().equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {
				mStartTextView.setText(TextUtils.isEmpty(mKey.getStart_name()) ? "" : mKey
						.getStart_name());
				mEndTextView.setText(TextUtils.isEmpty(mKey.getEnd_name()) ? "" : mKey
						.getEnd_name());
				start_lat = mKey.getStart_lat();
				start_lon = mKey.getStart_lon();
				end_lat = mKey.getEnd_lat();
				end_lon = mKey.getEnd_lon();
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Util.getInstance().isUserLoging(getActivity())) {
			mPhoneNumTextView.setText(Util.getInstance().getUser(getActivity()).getTel());
		}
	}

	public WorkFragment() {
		super();
	}

	public static WorkFragment newInstance(String identity, Key key) {
		WorkFragment fragment = new WorkFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.IDENTITY, identity);
		bundle.putSerializable("key", key);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mIndetity = getArguments().getString(Constant.IDENTITY);
		Util.printLog("发布上下班:" + mIndetity);
		mKey = (Key) getArguments().getSerializable("key");
	}

	private void setListener() {
		mStartTextView.setOnClickListener(this);
		mStartImView.setOnClickListener(this);

		mEndTextView.setOnClickListener(this);
		mEndImView.setOnClickListener(this);

		mTimeLayout.setOnClickListener(this);
		mSetOutLayout.setOnClickListener(this);
		mReturnLayout.setOnClickListener(this);
		mPublishButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mStartTextView) {// 起点选择
			choiceAddrWithSearch(Constant.I_START, Constant.S_CHOICE_ADRRR_RQUEST);
		} else if (v == mStartImView) {// 起点地图选择
			choiceStartOrEndOnMap(Constant.I_START);
		} else if (v == mEndTextView) {// 终点选择
			choiceAddrWithSearch(Constant.I_END, Constant.S_CHOICE_ADRRR_RQUEST);
		} else if (v == mEndImView) {// 终点地图选择
			choiceStartOrEndOnMap(Constant.I_END);
		} else if (v == mTimeLayout) {// 时间段选择
			Intent intent = new Intent(getActivity(), WeekSelectActivity.class);
			startActivityForResult(intent, 11);
		} else if (v == mSetOutLayout) {// 出发时间选择
			this.isStartTime = true;
			Util.getInstance().showDateSelectorDialog(getActivity(), new Date(), timeSetListener,
					"0", "m");
		} else if (v == mReturnLayout) {// 返程时间选择
			this.isStartTime = false;
			Util.getInstance().showDateSelectorDialog(getActivity(), new Date(), timeSetListener,
					"0", "n");
		} else if (v == mPublishButton) {// 发布
			if (validateData()) {
				if (Util.getInstance().isUserLoging(getActivity())) {// 用户是否登录
					if (!TextUtils.isEmpty(startCity) && !TextUtils.isEmpty(endCity)
							&& !startCity.equals(endCity)) {
						showAlertDialogMsg();
					} else {
						publishPinche();
					}
				} else {
					Util.getInstance().go2Activity(getActivity(), LoginActivity.class);
				}
			}
		} else if (v == mThrouthPointBtn) {// 添加途经点
			if (pointsList.size() == 2) return;
			choiceAddrWithSearch(Constant.I_THROUTH_POINT, Constant.S_CHOICE_THROUTH_POINT);
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

	/**
	 * 时分选择监听
	 */
	private TimePickerDialog.OnTimeSetListener timeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Util.printLog("hourOfDay:" + hourOfDay);
			Util.printLog("minute:" + minute);
			String strHHss = (hourOfDay <= 9 ? "0" + hourOfDay : hourOfDay) + ":"
					+ (minute <= 9 ? "0" + minute : minute);
			Util.printLog("strHHss:" + strHHss);
			if (isStartTime) {
				mSetOutTextView.setText(strHHss);// 上下班出发时间
			} else {
				mReturnTextView.setText(strHHss);// 上下班返回时间
			}
		}
	};

	private void showAlertDialogMsg() {
		Util.getInstance().showDialog(getActivity(), getString(R.string.publish_city_note),
				getString(R.string.main_foot_publish), getString(R.string.my_info_btn_cancel),
				new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							publishPinche();
						}
					}
				});
	}

	private boolean validateData() {
		final String start = mStartTextView.getText().toString().trim();
		if (start.length() <= 0) {
			Util.showToast(getActivity(), getString(R.string.start_is_null));
			return false;
		}
		final String end = mEndTextView.getText().toString().trim();
		if (end.length() <= 0) {
			Util.showToast(getActivity(), getString(R.string.end_is_null));
			return false;
		}
		if (start.equals(end)) {
			Util.showToast(getActivity(), getString(R.string.start_end_can_not_same));
			return false;
		}
		// 费用
		final String money = mCostTextView.getText().toString();
		if (!Util.getInstance().checkMoneyIsRight(getActivity(), money)) return false;
		if (mIndetity.equals(Pinche.DRIVER)) {// 司机
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

	private void choiceStartOrEndOnMap(int type) {
		Intent intentWork = new Intent(getActivity(), SearchAdrrOnMapActivity.class);
		intentWork.putExtra(Constant.S_ICON, type);
		startActivityForResult(intentWork, Constant.S_SEARCH_REQUEST);

	}

	/**
	 * 通过搜索选择起点和终点
	 * 
	 * @param type
	 */
	private void choiceAddrWithSearch(int type, int requestCode) {
		Intent intentWork = new Intent(getActivity(), ChoiceAdrrActivity.class);
		intentWork.putExtra(Constant.S_ADDR, type);
		startActivityForResult(intentWork, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) return;
		final Bundle mBundle = data.getExtras();
		switch (requestCode) {
		case 11:// 时间周期
			String time = data.getStringExtra("result");
			if (!time.equals("星期")) {
				this.mTimeTextView.setText(time);// 获取周期
				cycleString = data.getStringExtra("numResult");
			}

			Util.printLog("time:" + mTimeTextView.getText().toString());
			Util.printLog("cycle:" + cycleString);
			break;
		case Constant.S_CHOICE_ADRRR_RQUEST:// 搜索选择地点
			if (resultCode == Constant.S_CHOICE_ADRR_RESULT_START) {// 起点
				startCity = mBundle.getString(Constant.CITY);
				setStartOrEnd(mBundle, Constant.I_START);
			} else if (resultCode == Constant.S_CHOICE_ADRR_RESULT_END) {// 终点
				endCity = mBundle.getString(Constant.CITY);
				setStartOrEnd(mBundle, Constant.I_END);
			}
			break;
		case Constant.S_SEARCH_REQUEST:// 地图上选择的点
			if (resultCode == Constant.S_SEARCH_START) {
				setStartOrEnd(mBundle, Constant.I_START);
			} else if (resultCode == Constant.S_SEARCH_END) {
				setStartOrEnd(mBundle, Constant.I_END);
			}
			break;
		case Constant.S_CHOICE_THROUTH_POINT:
			if (resultCode == Constant.I_THROUTH_POINT) {
				String addr = mBundle.getString(Constant.S_THROUTH_POINT);
				long lat = mBundle.getLong(Constant.SLAT);
				long lon = mBundle.getLong(Constant.SLON);
				String cityCode = mBundle.getString(Constant.CITY_CODE);
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
				pointsList.add(createPoint(addr, lat, lon, cityCode));
			}
			break;
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

	private Point createPoint(String addr, long lat, long lon, String cityCode) {
		Point first = new Point();
		first.setCityId(Integer.parseInt(cityCode));
		first.setLat(lat);
		first.setLon(lon);
		first.setName(addr);
		return first;
	}

	private void setStartOrEnd(final Bundle mBundle, int isStartOrEnd) {
		boolean isStart = isStartOrEnd == Constant.I_START ? true : false;
		String selectedName = mBundle.getString(isStart ? Constant.S_START : Constant.S_END);
		if (isStart) {
			mStartCityCode = mBundle.getString(Constant.CITY_CODE);
			Util.printLog("发布选择的起点城市ID:" + mStartCityCode);
		} else {
			mEndCityCode = mBundle.getString(Constant.CITY_CODE);
			Util.printLog("发布选择的终点城市ID:" + mEndCityCode);
		}
		if (Util.getInstance().isNullOrEmpty(selectedName)) {// 判断选择的地点是否为空
			Util.showToast(getActivity(), getString(isStart ? R.string.start_is_null
					: R.string.end_is_null));
			return;
		}
		String tempString = isStart ? mEndTextView.getText().toString() : mStartTextView.getText()
				.toString();
		if (!Util.getInstance().isNullOrEmpty(tempString)// 判断起点和终点是否相同
				&& tempString.equals(selectedName)) {
			Util.showToast(getActivity(), getString(isStart ? R.string.start_end_can_not_same
					: R.string.end_start_can_not_same));
			return;
		}
		if (isStart) {
			start_lon = mBundle.getLong(Constant.SLON, 0);
			start_lat = mBundle.getLong(Constant.SLAT, 0);
		} else {
			end_lon = mBundle.getLong(Constant.SLON, 0);
			end_lat = mBundle.getLong(Constant.SLAT, 0);
		}
		if (isStart) {
			if (end_lat > 0 && end_lon > 0) {
				if (Util.getInstance().get2point2Distances(start_lat, start_lon, end_lat, end_lon)) {
					mStartTextView.setText(selectedName);
				} else {// 距离太短
					start_lat = 0;
					start_lon = 0;
					this.mStartTextView.setText("");
					Util.showLongToast(getActivity(), getString(R.string.publish_note)
							+ Constant.TwoPoinstsDistances + getString(R.string.m));
				}
			} else {
				mStartTextView.setText(selectedName);
			}
		} else {
			if (start_lat > 0 && start_lon > 0) {
				if (Util.getInstance().get2point2Distances(start_lat, start_lon, end_lat, end_lon)) {
					mEndTextView.setText(selectedName);
				} else {
					end_lat = 0;
					end_lon = 0;
					this.mEndTextView.setText("");
					Util.showLongToast(getActivity(), getString(R.string.publish_note)
							+ Constant.TwoPoinstsDistances + getString(R.string.m));
				}
			} else {
				mEndTextView.setText(selectedName);
			}
		}
	}

	/**
	 * 发布拼车信息
	 */
	private void publishPinche() {
		if (Util.getInstance().isNetworkAvailable(getActivity())) {// 判断网络是否连接
			List<NameValuePair> paramList = getParamters();
			if (paramList == null) return;
			Util.printLog("发布参数:" + paramList.toString());

			Util.getInstance().doPostRequest(getActivity(), new OnHttpRequestDataCallback() {

				@Override
				public void onSuccess(String result) {
					JsonResult<String> jsonResult = Util.getInstance().getObjFromJsonResult(result,
							new TypeToken<JsonResult<String>>() {
							});
					if (jsonResult != null && jsonResult.isSuccess()) {
						// 跳转到分享
						Util.getInstance().go2Share(getActivity(), jsonResult.getData());
						getActivity().finish();
					} else {
						Util.showToast(getActivity(), jsonResult.getErrorMsg().toString());
					}
				}
			}, paramList, Constant.URL_PUBLISH, getString(R.string.publish_ing), false);
		} else {
			Util.showToast(getActivity(), getString(R.string.network_unavaiable));
		}
	}

	private List<NameValuePair> getParamters() {
		long goDate = 0;
		long backDate = 0;
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("infoType", mIndetity));// 身份
		paramList.add(new BasicNameValuePair("carpoolType", Pinche.CARPOOLTYPE_ON_OFF_WORK));// 上下班
		paramList
				.add(new BasicNameValuePair("startName", this.mStartTextView.getText().toString()));
		paramList.add(new BasicNameValuePair("endName", this.mEndTextView.getText().toString()));

		goDate = TimeUtils.dateToSecond(mSetOutTextView.getText().toString());
		paramList.add(new BasicNameValuePair("departureTime", String.valueOf(goDate)));// 出发时间
		String backTimeString = mReturnTextView.getText().toString();
		if (!TextUtils.isEmpty(backTimeString)) {// 返回时间
			backDate = TimeUtils.dateToSecond(backTimeString);
			paramList.add(new BasicNameValuePair("backTime", String.valueOf(backDate)));
		}
		paramList.add(new BasicNameValuePair("charge", mCostTextView.getText().toString()));
		if (mIndetity.equals(Pinche.DRIVER)) {// 司机发布
			paramList.add(new BasicNameValuePair("num", mPeopleNumTextView.getText().toString()));// 可载人数
			if (pointsList.size() > 0) {
				for (int i = 0; i < pointsList.size(); i++) {
					Point point = pointsList.get(i);
					paramList
							.add(new BasicNameValuePair("points[" + i + "].name", point.getName()));// 途经点
					paramList.add(new BasicNameValuePair("points[" + i + "].cityId", String
							.valueOf(point.getCityId())));// 途经点
					paramList.add(new BasicNameValuePair("points[" + i + "].lon", String
							.valueOf(point.getLon())));// 途经点
					paramList.add(new BasicNameValuePair("points[" + i + "].lat", String
							.valueOf(point.getLat())));// 途经点

				}
			}
		} else {// 乘客发布
			paramList.add(new BasicNameValuePair("num", String.valueOf(1)));
		}
		User user = Util.getInstance().getLoginUser(getActivity());
		if (mIndetity.equals(Pinche.DRIVER)) {
			if (user != null) {// 车辆Id没有就是新加
				if (user.getCar() != null) {
					int carId = user.getCar().getCarId();
					if (carId > 0) {// 汽车Id
						paramList.add(new BasicNameValuePair("carId", String.valueOf(carId)));
					}
				} else {
					// 没有车，去添加车辆
					Util.getInstance().publish2AddCar(getActivity());
					return null;
				}
			}
		}
		paramList.add(new BasicNameValuePair("startLat", String.valueOf(start_lat)));
		paramList.add(new BasicNameValuePair("startLon", String.valueOf(start_lon)));
		paramList.add(new BasicNameValuePair("endLat", String.valueOf(end_lat)));
		paramList.add(new BasicNameValuePair("endLon", String.valueOf(end_lon)));
		paramList.add(new BasicNameValuePair("cycle", cycleString));// 新加周期，只有上下班才有
		if (mKey != null) {
			mStartCityCode = mKey.getStartCityId();
			mEndCityCode = mKey.getEndCityId();
		}
		if (TextUtils.isEmpty(mStartCityCode))
			mStartCityCode = String.valueOf(Constant.currCityCode);
		if (TextUtils.isEmpty(mEndCityCode)) mEndCityCode = String.valueOf(Constant.currCityCode);
		Util.printLog("发布起点城市ID:" + mStartCityCode);
		Util.printLog("发布终点城市ID:" + mEndCityCode);
		paramList.add(new BasicNameValuePair("startCityId", mStartCityCode));// 城市id，只有上下班才有
		paramList.add(new BasicNameValuePair("endCityId", mEndCityCode));
		paramList.add(new BasicNameValuePair("note", mNoTeEditText.getText().toString()));
		return paramList;
	}

}
