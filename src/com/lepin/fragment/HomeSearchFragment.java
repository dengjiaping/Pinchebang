package com.lepin.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.activity.CarpoolWithCalendarActivity;
import com.lepin.activity.R;
import com.lepin.activity.SearchWithMapActivity;
import com.lepin.activity.SelectCityActivity;
import com.lepin.entity.CarpoolProgram;
import com.lepin.entity.CarpoolProgramJsonResult;
import com.lepin.entity.CarpoolProgramPassenger;
import com.lepin.entity.City;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Key;
import com.lepin.entity.Pinche;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.Interfaces.SetCurrentAddrress;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;
import com.lepin.widget.TopTabIndicator;
import com.lepin.widget.TopTabIndicator.TabClick;

public class HomeSearchFragment extends BaseFragment implements OnClickListener, SetCurrentAddrress {

	private View mRootView;

	/**
	 * 上下班
	 */
	@ViewInject(id = R.id.home_search_work_layout)
	private LinearLayout mWorkLayout;

	@ViewInject(id = R.id.home_search_work_start)
	private TextView mWorkStartTextView;

	@ViewInject(id = R.id.home_search_work_end)
	private TextView mWorkEndTextView;
	/**
	 * 长途
	 */
	@ViewInject(id = R.id.home_search_long_layout)
	private LinearLayout mLongLayout;
	// 起点
	@ViewInject(id = R.id.item_text_edirottext_image_title, parentId = R.id.home_search_start_layout)
	private TextView mLongStartTitle;

	@ViewInject(id = R.id.item_text_edirottext_image_value, parentId = R.id.home_search_start_layout)
	private EditText mlongStartEditText;

	@ViewInject(id = R.id.item_text_edirottext_image_mapview, parentId = R.id.home_search_start_layout)
	private ImageView mLongStartImView;

	// 终点
	@ViewInject(id = R.id.item_text_edirottext_image_title, parentId = R.id.home_search_end_layout)
	private TextView mLongEndTitle;

	@ViewInject(id = R.id.item_text_edirottext_image_value, parentId = R.id.home_search_end_layout)
	private EditText mLongEndEditText;

	@ViewInject(id = R.id.item_text_edirottext_image_mapview, parentId = R.id.home_search_end_layout)
	private ImageView mLongEndImView;

	@ViewInject(id = R.id.home_search_indicator)
	private TopTabIndicator mIndicator;

	@ViewInject(id = R.id.home_search_btn)
	private Button mSearchButton;

	@ViewInject(id = R.id.home_search_root)
	private LinearLayout mRootLayout;

	@ViewInject(id = R.id.home_search_get_plan_layout)
	private LinearLayout mGetPlanLayout;

	private static final int WORK = 0;
	private int mCurrentIdentity = WORK;

	private static final int PASSENGER = 1;
	private static final int DRIVER = 2;

	// 拼车计划
	private boolean hasGetPlan = false;
	private CarpoolProgram mDriverCarpoolProgram;// 车主拼车计划
	private CarpoolProgramPassenger mPassengCarpoolProgram;// 乘客拼车计划
	LayoutInflater inflater = null;
	private View mPlanDriverView = null;
	private View mPlanPassengerView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.home_search_fragment, container, false);
		ViewInjectUtil.inject(this, mRootView);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mIndicator.setTabClick(new TabClick() {

			@Override
			public void onTabClick(int index) {
				if (index == WORK) {// 切换到上下班
					// 将起点和终点设置成可点击
					setWorkLayoutVisiable(true);
				} else {// 切换到长途
					setWorkLayoutVisiable(false);
				}
				mCurrentIdentity = index;
			}
		});
		inflater = LayoutInflater.from(getActivity());
		init();
		setClickListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (TextUtils.isEmpty(mWorkStartTextView.getText().toString()))
			mWorkStartTextView.setText(Constant.CURRENT_ADDRESS);
		// 只有用户登录了，并且在还没有去获取拼车计划时才去获取
		if (Util.getInstance().isUserLoging(getActivity())) {
			if (!hasGetPlan) {// 没有获取到计划信息
				if (Util.getInstance().isNetworkAvailable(getActivity())) {
					if (mGetPlanLayout.getVisibility() != View.VISIBLE)
						mGetPlanLayout.setVisibility(View.VISIBLE);
					getPlanInfo();
				}
			} else {
				if (Constant.reload_plan) {// 重新注册的用户回到首页
					Constant.reload_plan = false;
					removePlanViews();
					getPlanInfo();
				}
			}
		} else {// 如果用户登录了，然后点击了退出登录后，移除已经加载的计划view
			if (hasGetPlan) {
				removePlanViews();
			}
		}

	}

	private void getPlanInfo() {
		HttpRequestOnBackgrount getPlanBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.GET, new OnHttpRequestDataCallback() {

					public void onSuccess(String result) {
						if (mGetPlanLayout.getVisibility() == View.VISIBLE)
							mGetPlanLayout.setVisibility(View.GONE);
						if (!TextUtils.isEmpty(result)) {
//							 Util.printLog("拼车计划:" + result);
							JsonResult<CarpoolProgramJsonResult> jsonResult = Util.getInstance()
									.getObjFromJsonResult(result,
											new TypeToken<JsonResult<CarpoolProgramJsonResult>>() {
											});
							if (jsonResult != null && jsonResult.isSuccess()) {
								CarpoolProgramJsonResult carpoolProgramJsonResult = jsonResult
										.getData();

								if (carpoolProgramJsonResult != null) {
									mDriverCarpoolProgram = carpoolProgramJsonResult.getDriver();
									mPassengCarpoolProgram = carpoolProgramJsonResult
											.getPassenger();
									if (mPassengCarpoolProgram != null) {
										if (mPlanPassengerView == null) {
											hasGetPlan = true;
											addPassengerPlanView(mPassengCarpoolProgram);
										}
									}

									if (mDriverCarpoolProgram != null) {
										if (mPlanDriverView == null) {
											hasGetPlan = true;
											addDriverPlanView(mDriverCarpoolProgram);
										}
									}
								}
							}
						}

					}
				}, null, getActivity(), false);
		getPlanBackgrount.execute(Constant.URL_GET_CARPOOL_PROGRAM);
	}

	private void removePlanViews() {
		if (mPlanDriverView != null) {
			hasGetPlan = false;
			mRootLayout.removeView(mPlanDriverView);
			mPlanDriverView = null;
		}
		if (mPlanPassengerView != null) {
			hasGetPlan = false;
			mRootLayout.removeView(mPlanPassengerView);
			mPlanPassengerView = null;
		}
	}

	private void setClickListener() {
		mlongStartEditText.setOnClickListener(this);
		mLongEndEditText.setOnClickListener(this);
		mLongStartImView.setOnClickListener(this);
		mLongEndImView.setOnClickListener(this);
		mWorkStartTextView.setOnClickListener(this);
		mWorkEndTextView.setOnClickListener(this);
		mSearchButton.setOnClickListener(this);
	}

	private void init() {
		mLongStartTitle.setText(getString(R.string.pick_details_start_point));
		mLongStartImView.setBackgroundResource(R.drawable.pcb_right_arrow);

		mLongEndTitle.setText(getString(R.string.pick_details_end_point));
		mLongEndImView.setBackgroundResource(R.drawable.pcb_right_arrow);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public HomeSearchFragment() {
		super();
	}

	@Override
	public void onClick(View v) {

		if (v == mWorkStartTextView) {// 上下班起点
			go2Search();
		} else if (v == mWorkEndTextView) {// 上下班终点
			go2Search();
		} else if (v == mLongStartImView) {
			choiceAddrOrCity(Constant.I_START);
		} else if (v == mLongEndImView) {
			choiceAddrOrCity(Constant.I_END);
		} else if (v == mSearchButton) {// 搜索

			if (!Util.getInstance().isNetworkAvailable(getActivity())) {// 网络不通
				Util.showToast(getActivity(), getString(R.string.network_unavaiable));
				return;
			}
			if (checkSearchParameters()) { // 检查通过
				Util.getInstance().go2Search(getActivity(), createSearchKey());
			}
		}

	}

	private void go2Search() {
		Util.getInstance().go2Activity(getActivity(), SearchWithMapActivity.class);
		getActivity().overridePendingTransition(R.anim.choice_addr_activity_in,
				R.anim.main_activity_out);
	}

	private boolean checkSearchParameters() {
		final String mStartPoint = mlongStartEditText.getText().toString();
		final String mEndPoint = mLongEndEditText.getText().toString();
		if (TextUtils.isEmpty(mStartPoint) && TextUtils.isEmpty(mEndPoint)) {// 起点和终点都为空
			Util.showToast(getActivity(), getString(R.string.input_start_end));
			return false;
		}

		if (TextUtils.isEmpty(mStartPoint) && !TextUtils.isEmpty(mEndPoint)) {// 起点为空和终点不为空
			Util.showToast(getActivity(), getString(R.string.publish_start_point_hint));
			return false;
		}
		if (!TextUtils.isEmpty(mStartPoint) && TextUtils.isEmpty(mEndPoint)) {// 起点不为空和终点为空
			Util.showToast(getActivity(), getString(R.string.publish_end_point_hint));
			return false;
		}

		if (!TextUtils.isEmpty(mStartPoint) && !TextUtils.isEmpty(mEndPoint)
				&& mStartPoint.equals(mEndPoint)) {// 起点和终点相同
			Util.showToast(getActivity(), getString(R.string.start_end_can_not_same));
			return false;
		}

		if (!ValidateTool.isSearchInputLegitimate(mStartPoint)
				|| !ValidateTool.isSearchInputLegitimate(mEndPoint)) {
			Util.showToast(getActivity(), getString(R.string.input_type_error));
			return false;

		}
		return true;

	}

	private OnClickListener addViewClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.plan_driver_cancel:// 司机取消按钮
				showOperateDialog(DRIVER, CarpoolProgram.STATE_PAUSE);
				break;

			case R.id.plan_passenger_cancel:// 乘客取消按钮
				showOperateDialog(PASSENGER, CarpoolProgram.STATE_PAUSE);
				break;
			}

		}
	};

	protected void showOperateDialog(final int identety, final String state) {
		String title = null;
		if (state.equals(CarpoolProgram.STATE_PAUSE)) {
			title = identety == DRIVER ? getString(R.string.driver_cancel)
					: getString(R.string.passenger_cancel);
		} else {
			title = getString(R.string.is_delete_plan);
		}
		Util.getInstance().showDialog(getActivity(), title, getString(R.string.confirm),
				getString(R.string.my_info_btn_cancel), new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							pauseOrCancelPlan(identety, state);
						}
					}
				});
	};

	/**
	 * 暂停或者取消计划
	 * 
	 * @param identety
	 * @param state
	 */
	protected void pauseOrCancelPlan(final int identety, final String state) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("state", state));
		if (identety == DRIVER) {
			params.add(new BasicNameValuePair("carpoolProgramId", String
					.valueOf(mDriverCarpoolProgram.getCarpoolProgramId())));
		} else {
			params.add(new BasicNameValuePair("carpoolProgramPassengerId", String
					.valueOf(mPassengCarpoolProgram.getCarpoolProgramPassengerId())));
		}

		Util.getInstance().doPostRequest(
				getActivity(),
				new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						Util.printLog("删除或者暂停计划:" + result);
						TypeToken<JsonResult<String>> token = new TypeToken<JsonResult<String>>() {
						};
						JsonResult<String> jsonResult = Util.getInstance().getObjFromJsonResult(
								result, token);
						if (jsonResult != null && jsonResult.isSuccess()) {
							if (state.equals(CarpoolProgram.STATE_PAUSE)) {
								updateDriverOrPassengerPlanText(identety, jsonResult.getData());
							} else {// 删除计划
								removeViewWithAnimation(identety == DRIVER ? mPlanDriverView
										: mPlanPassengerView);
							}
							Util.showToast(
									getActivity(),
									state.equals(CarpoolProgram.STATE_PAUSE) ? getString(R.string.cancel_su)
											: getString(R.string.delete_success));
						} else {
							Util.showToast(getActivity(), getString(R.string.operate_fail));
						}
					}
				},
				params,
				identety == DRIVER ? Constant.URL_DRIVER_PAUSE_OR_CANCEL_PLAN
						: Constant.URL_PASSENGER_PAUSE_OR_CANCEL_PLAN,
				getString(R.string.order_operate, getString(R.string.my_info_btn_cancel)), false);
	}

	// 更新计划上面的显示信息
	protected void updateDriverOrPassengerPlanText(int identety, String data) {
		if (identety == DRIVER) {
			((Button) mPlanDriverView.findViewById(R.id.plan_driver_cancel))
					.setVisibility(View.GONE);
			((TextView) mPlanDriverView.findViewById(R.id.plan_driver_info_title)).setText(data);
		} else {
			((Button) mPlanPassengerView.findViewById(R.id.plan_passenger_cancel))
					.setVisibility(View.GONE);
			((TextView) mPlanPassengerView.findViewById(R.id.plan_passenger_info_title))
					.setText(data);
		}
	}

	private void addPassengerPlanView(final CarpoolProgramPassenger mPassengerPlan) {

		Pinche infoPinche = mPassengerPlan.getInfo();
		if (infoPinche == null) {
			Util.printLog("乘客拼车计划为空");
			return;
		}
		mPlanPassengerView = inflater.inflate(R.layout.plan_passenger, null);
		// 描述
		TextView descrip = (TextView) mPlanPassengerView
				.findViewById(R.id.plan_passenger_info_title);
		descrip.setText(mPassengerPlan.getDescrip());
		descrip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				go2PincheDetail(String.valueOf(mPassengerPlan.getCarpoolProgramPassengerId()),
						false);
			}
		});
		descrip.setOnLongClickListener(new OnLongClickListener() {// 长按删除拼车机会

			@Override
			public boolean onLongClick(View v) {
				showOperateDialog(PASSENGER, CarpoolProgram.STATE_DELETED);
				return false;
			}
		});
		// 取消按钮
		final String state = mPassengerPlan.getState();
		Button mPassengerCancelButton = ((Button) mPlanPassengerView
				.findViewById(R.id.plan_passenger_cancel));
		if (state.equals(CarpoolProgram.STATE_NORMAL)) {
			mPassengerCancelButton.setVisibility(View.VISIBLE);
			mPassengerCancelButton.setOnClickListener(addViewClickListener);
		}

		LinearLayout vLayout1 = (LinearLayout) mPlanPassengerView
				.findViewById(R.id.plan_passenger_point_layout);
		// 起点
		((TextView) vLayout1.findViewById(R.id.home_plan_item_left)).setText(getString(
				R.string.start_or_end_point_with, getString(R.string.pick_details_start_point),
				infoPinche.getStart_name(getActivity())));
		// 终点
		((TextView) vLayout1.findViewById(R.id.home_plan_item_right)).setText(getString(
				R.string.start_or_end_point_with, getString(R.string.pick_details_end_point),
				infoPinche.getEnd_name(getActivity())));
		LinearLayout vLayout２ = (LinearLayout) mPlanPassengerView
				.findViewById(R.id.plan_passenger_time_layout);
		// 出发时间
		((TextView) vLayout２.findViewById(R.id.home_plan_item_left)).setText(getString(
				R.string.start_or_end_point_with, getString(R.string.pick_details_start_date),
				infoPinche.getDepartureTime()));
		// 返程时间
		((TextView) vLayout２.findViewById(R.id.home_plan_item_right)).setText(getString(
				R.string.start_or_end_point_with, getString(R.string.pick_details_back_date),
				infoPinche.getBackTime()));
		LinearLayout vLayout3 = (LinearLayout) mPlanPassengerView
				.findViewById(R.id.plan_passenger_num_money_layout);
		// 乘客人数
		((TextView) vLayout3.findViewById(R.id.home_plan_item_left)).setText(getString(
				R.string.start_or_end_point_with, getString(R.string.passenger_number),
				mPassengerPlan.getInfo().getNum()));
		// 费用
		String chargeString = getString(R.string.start_or_end_point_with,
				getString(R.string.publish_cost), mPassengerPlan.getInfo().getCharge())
				+ getString(R.string.publish_cost_unit);
		((TextView) vLayout3.findViewById(R.id.home_plan_item_right)).setText(chargeString);
		addViewWithAnimation(mPlanPassengerView);

	}

	private void addDriverPlanView(CarpoolProgram mDrivierPlan) {
		List<CarpoolProgramPassenger> passengers = mDrivierPlan.getCarpoolProgramPassengers();
		if (passengers.size() < 1) {
			Util.printLog("司机拼车计划为空");
			return;
		}
		mPlanDriverView = inflater.inflate(R.layout.plan_driver, null);
		// 描述
		TextView mPlanDriverDescrip = (TextView) mPlanDriverView
				.findViewById(R.id.plan_driver_info_title);
		mPlanDriverDescrip.setText(mDrivierPlan.getDescrip());
		mPlanDriverDescrip.setOnLongClickListener(new OnLongClickListener() {// 长按描述删除拼车计划

					@Override
					public boolean onLongClick(View v) {
						showOperateDialog(DRIVER, CarpoolProgram.STATE_DELETED);
						return false;
					}
				});
		// 取消按钮
		final String state = mDrivierPlan.getState();
		Button mDriverCancelButton = ((Button) mPlanDriverView
				.findViewById(R.id.plan_driver_cancel));
		if (state.equals(CarpoolProgram.STATE_NORMAL)) {
			mDriverCancelButton.setVisibility(View.VISIBLE);
			mDriverCancelButton.setOnClickListener(addViewClickListener);
		}

		LinearLayout contentLayout = (LinearLayout) mPlanDriverView
				.findViewById(R.id.plan_driver_content);
		LayoutParams itemLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 添加item
		for (int i = 0; i < passengers.size(); i++) {
			LinearLayout itemLayout = (LinearLayout) inflater
					.inflate(R.layout.home_plan_item, null);
			itemLayout.setTag(String.valueOf(passengers.get(i).getCarpoolProgramPassengerId()));
			itemLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String carpoolProgramPassengerId = (String) v.getTag();

					go2PincheDetail(carpoolProgramPassengerId, true);

				}
			});
			TextView leftTextView = (TextView) itemLayout.findViewById(R.id.home_plan_item_left);
			leftTextView.setText(getString(R.string.passenger_with, passengers.get(i)
					.getPassenger().getUsername(getActivity())));

			TextView rightTextView = (TextView) itemLayout.findViewById(R.id.home_plan_item_right);
			rightTextView
					.setText(passengers.get(i).getState().equals(CarpoolProgram.STATE_NORMAL) ? getString(R.string.has_confirm_carpool)
							: getString(R.string.has_confirm_cancel));
			rightTextView.setTextColor(passengers.get(i).getState()
					.equals(CarpoolProgram.STATE_NORMAL) ? getResources().getColor(
					R.color.btn_blue_normal) : getResources().getColor(
					R.color.order_pay_btn_pressed));
			contentLayout.addView(itemLayout, itemLayoutParams);
		}
		addViewWithAnimation(mPlanDriverView);
	}

	/**
	 * 添加view，加上动画
	 * 
	 * @param willBeAddView
	 */
	private void addViewWithAnimation(View willBeAddView) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = 30;
		mRootLayout.addView(willBeAddView, params);
		TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		translateAnimation.setDuration(400);
		willBeAddView.startAnimation(translateAnimation);
	}

	private void removeViewWithAnimation(View willRemoveView) {
		mRootLayout.removeView(willRemoveView);
		TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		translateAnimation.setDuration(400);
	}

	// 选择城市或者地图上选点
	private void choiceAddrOrCity(int type) {

		// 选择城市
		Intent intent = new Intent(getActivity(), SelectCityActivity.class);
		intent.putExtra(Constant.S_ICON, type);
		startActivityForResult(intent, Constant.SICON_LONG_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null || data.getExtras() == null) return;
		Bundle mBundle = data.getExtras();

		if (requestCode == Constant.SICON_LONG_REQUEST) {
			if (Constant.SELECT_CITY_RESULT_CODE == resultCode) {
				City city = (City) mBundle.get("city");
				if (Constant.I_START == data.getIntExtra(Constant.S_ICON, Constant.I_START)) {
					mlongStartEditText.setText(city.getArea_name());
				} else {
					mLongEndEditText.setText(city.getArea_name());
				}
			}
		}

	}

	private void setWorkLayoutVisiable(boolean isShow) {
		mWorkLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
		mLongLayout.setVisibility(isShow ? View.GONE : View.VISIBLE);
	}

	private Key createSearchKey() {
		Key key = new Key();
		key.setCarpoolType(mCurrentIdentity == WORK ? Pinche.CARPOOLTYPE_ON_OFF_WORK
				: Pinche.CARPOOLTYPE_LONG_TRIP);
		final String startPoint = mlongStartEditText.getText().toString().trim();
		final String endPoint = mLongEndEditText.getText().toString().trim();
		key.setStart_name(startPoint);
		key.setEnd_name(endPoint);
		Util.printLog("搜索:" + key.toString());
		return key;
	}

	@Override
	public void setAddress(String address) {
		mWorkStartTextView.setText(address);
	}

	private void go2PincheDetail(String carpoolProgramPassengerId, boolean isDriver) {
		Bundle bundle = new Bundle();
		bundle.putString(Constant.CARPOOLPROGRAMPASSENGERID, carpoolProgramPassengerId);
		bundle.putBoolean(Constant.IS_DRIVER, isDriver);
		Util.getInstance().go2ActivityWithBundle(getActivity(), CarpoolWithCalendarActivity.class,
				bundle);
	}
}
