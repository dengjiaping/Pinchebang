package com.lepin.activity;

import java.text.BreakIterator;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.google.gson.reflect.TypeToken;
import com.lepin.CarSharingApplication;
import com.lepin.adapter.FragmentTabAdapter;
import com.lepin.entity.City;
import com.lepin.entity.JsonResult;
import com.lepin.fragment.HomeOrderFragment;
import com.lepin.fragment.HomeSearchFragment;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.Interfaces.SetCurrentAddrress;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;
import com.lepin.widget.PcbHomeBottomMenu;
import com.lepin.widget.PcbHomeTobMenu;

@Contextview(R.layout.carsharing_activity)
public class CarSharingActivity extends BaseFragmentActivity implements OnClickListener {

	// 选择城市
	@ViewInject(id = R.id.home_choice_city)
	private CheckedTextView mChoiceCity;

	@ViewInject(id = R.id.home_menu)
	private ImageButton mTopMenu;

	@ViewInject(id = R.id.home_bottom_home)
	private CheckedTextView mBottomHome;

	@ViewInject(id = R.id.home_bottom_order)
	private CheckedTextView mBottomOrder;

	@ViewInject(id = R.id.home_bottom_publish_layout)
	private LinearLayout mPublishLayout;

	@ViewInject(id = R.id.home_bottom_publish)
	private ImageButton mBottomPublish;

	@ViewInject(id = R.id.home_title_layout)
	private View titleLayout;

	private FragmentTabAdapter mFragmentTabAdapter;
	private ArrayList<Fragment> fragments;

	private PcbHomeTobMenu mHomeTobMenu;
	private PcbHomeBottomMenu mHomeBottomMenu;
	private int unReadMsgNum = 0;
	private DisplayMetrics dm;
	private LocationClient mLocationClient = null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		ViewInjectUtil.inject(this);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 城市信息
		Util.getInstance().isCityInfoExist(CarSharingActivity.this);
		mChoiceCity.setText(Constant.currCity);
		addFragments();
		mFragmentTabAdapter = new FragmentTabAdapter(this, fragments, R.id.home_content);
		mBottomHome.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.pcb_footerbar_home_pressed), null, null);
		mBottomHome.setTextColor(getResources().getColor(R.color.btn_blue_normal));
		setListener();
		Util.getInstance().checkUpdate(this, false);// 检查是否有升级
		Util.getInstance().checkLoadingPage(this);// 检查loading页是否更新
		initTopMenu();
		mLocationClient = Util.getInstance().getLocationClient(CarSharingActivity.this,
				mBdLocationListener);
		mLocationClient.start();
	}

	// 定位，如果当前定位的城市和已经选择的城市不一样则提醒是否切换
	private BDLocationListener mBdLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				final String locationCity = location.getCity();
				final String templeCityCode = location.getCityCode();
				Constant.CURRENT_ADDRESS = Util.getInstance().getEasyAddr(location.getAddrStr());
				Constant.CURRENT_ADDRESS_LAT = location.getLatitude();
				Constant.CURRENT_ADDRESS_LON = location.getLongitude();
				if (!TextUtils.isEmpty(Constant.CURRENT_ADDRESS)) {

					((SetCurrentAddrress) fragments.get(0)).setAddress(Constant.CURRENT_ADDRESS);
				}
				if (TextUtils.isEmpty(locationCity) || TextUtils.isEmpty(templeCityCode)) return;
				if (!(Constant.currCity.equals(locationCity) || locationCity
						.contains(Constant.currCity))) {
					Util.getInstance().showDialog(CarSharingActivity.this,
							getString(R.string.is_switch_to_locting_city, locationCity),
							getString(R.string.switch_to), getString(R.string.my_info_btn_cancel),
							new OnOkOrCancelClickListener() {

								@Override
								public void onOkClick(int type) {
									if (type == PcbConfirmDialog.OK) {
										saveCity(locationCity, Integer.parseInt(templeCityCode));
									}

								}
							});
				}
			}
		}
	};

	@Override
	protected void onStop() {
		mLocationClient.stop();
		super.onStop();
	}

	private void initTopMenu() {
		if (mHomeTobMenu == null) {
			mHomeTobMenu = new PcbHomeTobMenu(this, topMenuListener);
			mHomeTobMenu.getContentView().setFocusableInTouchMode(true);
			mHomeTobMenu.getContentView().setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_MENU && mHomeTobMenu.isShowing()) {
						mHomeTobMenu.dismiss();// 这里写明模拟menu的PopupWindow退出就行
						return true;
					}
					return false;
				}
			});
			// getMsg();
		}
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO
		if (!Util.getInstance().isUserLoging(CarSharingActivity.this)) {
			go2Login();
		} else {
			if (!mHomeTobMenu.isShowing()) showTopMenu();
		}
		return false;
	}

	private void addFragments() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new HomeSearchFragment());
		fragments.add(new HomeOrderFragment());
	}

	private void setListener() {
		mChoiceCity.setOnClickListener(this);
		mTopMenu.setOnClickListener(this);
		mBottomHome.setOnClickListener(this);
		mBottomOrder.setOnClickListener(this);
		mBottomPublish.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Util.printLog("CarSharingActivity　onpause");
		if (Util.getInstance().isUserLoging(CarSharingActivity.this)
				&& Util.getInstance().isNetworkAvailable(CarSharingActivity.this)) {// 如果登录就去查看是否有未读消息
			getMsg();
		}
		if (Constant.logout_swtch_to_home
				&& mFragmentTabAdapter.getCurrentTab() == FragmentTabAdapter.RIGHT_FRAGMENT) {
			Constant.logout_swtch_to_home = false;
			set2Home();
		}
	}

	private void getMsg() {
		if (!Util.getInstance().isUserLoging(this)) return;

		HttpRequestOnBackgrount getMsgBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.GET, new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						Util.printLog("获取未读信息:" + result);
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty((result))) {
							JsonResult<String> messageResult = Util.getInstance()
									.getObjFromJsonResult(result,
											new TypeToken<JsonResult<String>>() {
											});
							if (messageResult != null && messageResult.isSuccess()) {
								if (Integer.parseInt(messageResult.getData()) > 0) {// 有未读消息
									unReadMsgNum = Integer.parseInt(messageResult.getData());
									if (mHomeTobMenu != null)
										mHomeTobMenu.setmMsgNum(unReadMsgNum);
								} else {
									if (mHomeTobMenu != null) mHomeTobMenu.hiddenMsg();
								}
							}
						}
					}

					@Override
					public void onFail(String errorType, String errorMsg) {
						// TODO Auto-generated method stub
						super.onFail(errorType, errorMsg);
					}
				}, null, CarSharingActivity.this, false);
		getMsgBackgrount.execute(Constant.URL_GET_UNREAD_MSG_COUNT);

	}

	@Override
	public void onClick(View v) {
		if (v == mChoiceCity) {
			Intent intent0 = new Intent(CarSharingActivity.this, SelectCityActivity.class);
			startActivityForResult(intent0, Constant.SELECT_CITY_REQUEST_CODE);
		} else if (v == mTopMenu) {
			showTopMenu();
		} else if (v == mBottomHome) {// 首页
			if (mFragmentTabAdapter.getCurrentTab() != FragmentTabAdapter.LEFT_FRAGMENT) {
				set2Home();
			}
		} else if (v == mBottomOrder) {// 订单
			if (!Util.getInstance().isUserLoging(CarSharingActivity.this)) {// 未登录
				go2Login();
			} else {
				if (mFragmentTabAdapter.getCurrentTab() != FragmentTabAdapter.RIGHT_FRAGMENT) {
					setHomeBtnChecked(false);
					setOrderBtnIsChecked(true);
					mFragmentTabAdapter.showTab(FragmentTabAdapter.RIGHT_FRAGMENT);
				}
			}
		} else if (v == mBottomPublish) {
			if (!Util.getInstance().isUserLoging(CarSharingActivity.this)) {// 如果未登录
				go2Login();
				return;
			}
			mBottomPublish.setBackgroundResource(R.drawable.pcb_footerbar_publish_pressed);
			showBottomMenu();
		}
	}

	private void set2Home() {
		setHomeBtnChecked(true);
		setOrderBtnIsChecked(false);
		mFragmentTabAdapter.showTab(FragmentTabAdapter.LEFT_FRAGMENT);
	}

	private void showTopMenu() {
//		if (!Util.getInstance().isUserLoging(CarSharingActivity.this)) {// 如果未登录
//			go2Login();
//		} else {
			mHomeTobMenu.isUserLogin(CarSharingActivity.this);
			mHomeTobMenu.showAsDropDown(titleLayout, dm.widthPixels
					- (mHomeTobMenu.getWidth() + 30), 0);
//		}

	}

	private void go2Login() {
		Util.getInstance().go2Activity(CarSharingActivity.this, LoginActivity.class);
	}

	private void showBottomMenu() {

		if (mHomeBottomMenu == null) {
			mHomeBottomMenu = new PcbHomeBottomMenu(this, bottomMenuListener);
			mHomeBottomMenu.setFocusable(true);
			mHomeBottomMenu.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					mBottomPublish.setBackgroundResource(R.drawable.pcb_footerbar_publish_normal);
				}
			});

		}
		int y = mPublishLayout.getHeight() + 16;
		mHomeBottomMenu.showAtLocation(mBottomPublish, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
				0, y);
	}

	private void setHomeBtnChecked(boolean isChecked) {
		if (isChecked) {
			mBottomHome.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.pcb_footerbar_home_pressed), null, null);
			mBottomHome.setTextColor(getResources().getColor(R.color.btn_blue_normal));

		} else {
			mBottomHome.setTextColor(getResources().getColor(R.color.text_color));
			mBottomHome.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.pcb_footerbar_home_selector), null, null);
		}
	}

	private void setOrderBtnIsChecked(boolean isChecked) {
		if (isChecked) {
			mBottomOrder.setTextColor(getResources().getColor(R.color.btn_blue_normal));
			mBottomOrder.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.pcb_footerbar_order_pressed), null, null);
		} else {
			mBottomOrder.setTextColor(getResources().getColor(R.color.text_color));
			mBottomOrder
					.setCompoundDrawablesWithIntrinsicBounds(null,
							getResources().getDrawable(R.drawable.pcb_footerbar_order_selector),
							null, null);

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null || data.getExtras() == null) return;

		if (requestCode == Constant.SELECT_CITY_REQUEST_CODE
				&& resultCode == Constant.SELECT_CITY_RESULT_CODE) {
			if (data != null) {
				City city = (City) data.getExtras().get("city");
				Util.printLog("city:" + city.toString());
				if (city != null && !Util.getInstance().isNullOrEmpty(Constant.currCity)) {
					if (!(Constant.currCity.equals(city.getArea_name()) || Constant.currCityCode == city
							.getArea_id())) {// 还是原来的城市
						Util.getInstance().saveCity(CarSharingActivity.this, city);
						mChoiceCity.setText(Constant.currCity);
					}
				}
			}
		}
	}

	private long exitTime = 0;
	private long waitTime = 2000;// 等待的时间差

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if ((System.currentTimeMillis() - exitTime) > waitTime) {
			Util.showToast(this, getString(R.string.exit));
			exitTime = System.currentTimeMillis();
		} else {
			CarSharingApplication.Instance().exit(this);
		}
	}

	private void saveCity(final String locationCity, final int cityCode) {
		Constant.currCity = locationCity;
		Constant.currCityCode = cityCode;
		mChoiceCity.setText(Constant.currCity);
		City city = new City();
		city.setArea_name(Constant.currCity);
		city.setArea_id(Constant.currCityCode);
		Util.getInstance().saveCity(CarSharingActivity.this, city);
	}

	private OnClickListener topMenuListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mHomeTobMenu.dismiss();
			Bundle bundle = new Bundle();
			switch (v.getId()) {
			case R.id.home_top_menu_personal_layout:// 个人中心
				Util.getInstance().go2Activity(CarSharingActivity.this,
						MyPersonalCentreActivity.class);
				break;
			case R.id.home_top_menu_msg_layout:// 消息中心
				Bundle mBundle = new Bundle();
				mBundle.putBoolean(MessageCenterActivity.S_IS_PUSH, false);
				Util.getInstance().go2ActivityWithBundle(CarSharingActivity.this,
						MessageCenterActivity.class, mBundle);
				break;

			case R.id.home_top_menu_publish_layout:// 我的发布
				Util.getInstance().go2Activity(CarSharingActivity.this, MyPublishActivity.class);
				break;
			case R.id.home_top_menu_carservice_layout:// // 汽车服务
				bundle.putString(Constant.JCHDOrQCFW, Constant.QCFW);
				Util.getInstance().go2ActivityWithBundle(CarSharingActivity.this,
						CarServiceActivity.class, bundle);
				break;
			case R.id.home_top_menu_activities_layout:// 精彩活动
				bundle.putString(Constant.JCHDOrQCFW, Constant.JCHD);
				Util.getInstance().go2ActivityWithBundle(CarSharingActivity.this,
						CarServiceActivity.class, bundle);
				break;
			case R.id.home_top_menu_more_layout:// 更多
				Util.getInstance().go2Activity(CarSharingActivity.this, MoreActivity.class);
				break;
			case R.id.home_top_menu_setting_layout:// 设置
				Util.getInstance().go2Activity(CarSharingActivity.this, SettingActivity.class);
				break;
			case R.id.home_top_menu_no_login_title:// 去登录
				go2Login();
				break;
			}
		}
	};

	private OnClickListener bottomMenuListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mHomeBottomMenu.dismiss();
			switch (v.getId()) {
			case R.id.home_bottom_publish_menu_long:// 发布－长途
				Util.getInstance().go2Activity(CarSharingActivity.this, LongFragmentActivity.class);
				break;
			case R.id.home_bottom_publish_menu_work:// 发布－上下班
				Util.getInstance().go2Activity(CarSharingActivity.this, WorkFragmentActivity.class);
				break;
			}

		}
	};

}
