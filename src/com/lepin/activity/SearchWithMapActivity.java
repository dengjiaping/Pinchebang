package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.lepin.adapter.AddressAdapter;
import com.lepin.entity.Address;
import com.lepin.entity.Key;
import com.lepin.entity.Pinche;
import com.lepin.entity.Point;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;

@Contextview(R.layout.search_with_map_activity)
public class SearchWithMapActivity extends BaseActivity implements OnClickListener {

	// -----------------------title--------------------
	@ViewInject(id = R.id.common_title_title)
	private TextView mTitleView;

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBackView;

	@ViewInject(id = R.id.common_title_operater)
	private TextView mSearch;
	// ----------------------------搜索布局－－－－－－－－－－－－－－－－－－－－－

	@ViewInject(id = R.id.search_with_map_search_layout)
	private LinearLayout mSearchLayout;

	@ViewInject(id = R.id.search_with_map_start)
	private EditText mStartTextView;

	@ViewInject(id = R.id.search_with_map_end)
	private EditText mEndTextView;
	// --------------------------------只显示起点终点和途经点---------------------------

	@ViewInject(id = R.id.search_with_map_show_addr_layout)
	private RelativeLayout mOnlyShowAddRelativeLayout;

	@ViewInject(id = R.id.search_with_map_only_show_start)
	private TextView mOnlyShowStart;

	@ViewInject(id = R.id.search_with_map_only_show_end)
	private TextView mOnlyShowEnd;

	@ViewInject(id = R.id.search_with_map_only_show_divider)
	private View mOnylShowFirstDivider;

	@ViewInject(id = R.id.search_with_map_only_show_throuth_point_first)
	private TextView mThrouthPointFirst;

	@ViewInject(id = R.id.search_with_map_only_show_throuth_point_second)
	private TextView mThrouthPointSecond;

	@ViewInject(id = R.id.search_with_map_only_show_throuth_point_divider)
	private View mOnylShowSecondDivider;
	// --------------------poi搜索－－－－－－－－－－－－－－－－－－－－－
	@ViewInject(id = R.id.search_with_map_listview)
	private ListView mPoiListView;

	private AddressAdapter mAdapter = null;// 地址设配器

	private List<Address> adrrResult = new ArrayList<Address>();// 地址结果集

	// 地图相关
	@ViewInject(id = R.id.search_with_map_mapview)
	private MapView mMapView;
	private LocationClient mLocationClient = null;
	private BaiduMap mBaiduMap = null;
	// poi搜索
	private PoiSearch mPoiSearch = null;
	private String locationCity = "";
	private String locationgCityCode = "";

	private static final int START = 1;
	private static final int END = 2;
	private static int mCurrentIndex;

	private boolean isSelected = false;

	private long startLon;// 起点经度
	private long startLat;// 起点纬度

	private long endLon;// 终点经度
	private long endLat;// 终点纬度
	private Parcelable[] points = null;// 途经点

	BitmapDescriptor startBitmapDescriptor = BitmapDescriptorFactory
			.fromResource(R.drawable.map_starting);
	BitmapDescriptor endBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_end);
	private boolean isOnlyShowPoints = false;// 搜索or只显示地图
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		bundle = getIntent().getExtras();
		if (bundle != null) isOnlyShowPoints = true;
		initView();
		initMap();
	}

	private void showPosition(Bundle bundle) {// 从订单详情中进来的
		String startString = bundle.getString(Constant.S_START);
		String endString = bundle.getString(Constant.S_END);

		mOnlyShowStart.setText(startString);
		mOnlyShowEnd.setText(endString);

		startLat = bundle.getInt(Constant.START_LAT);
		startLon = bundle.getInt(Constant.START_LON);
		endLat = bundle.getInt(Constant.END_LAT);
		endLon = bundle.getInt(Constant.END_LON);
		points = bundle.getParcelableArray("points");
		if (points != null && points.length > 0 && points[0] != null) {// 画途经点
			// 第一个途经点
			Point point1 = (Point) points[0];
			mThrouthPointFirst.setText(getString(R.string.through_point_num, 1, point1.getName()));
			// 第二个途经点
			if (points.length == 2 && points[1] != null) {
				Point point2 = (Point) points[1];
				mThrouthPointSecond.setText(getString(R.string.through_point_num, 2,
						point2.getName()));
				mOnylShowSecondDivider.setVisibility(View.VISIBLE);
				mThrouthPointSecond.setVisibility(View.VISIBLE);
			}
			mThrouthPointFirst.setVisibility(View.VISIBLE);
			mOnylShowFirstDivider.setVisibility(View.VISIBLE);
		}
	}

	private void initPoiSearch() {
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(mGetPoiSearchResultListener);
	}

	private OnGetPoiSearchResultListener mGetPoiSearchResultListener = new OnGetPoiSearchResultListener() {

		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			// 获取POI检索结果
			adrrResult.clear();
			List<PoiInfo> allPoi = poiResult.getAllPoi();
			if (allPoi == null || allPoi.size() < 1) {
				Util.showToast(SearchWithMapActivity.this, getString(R.string.research));
				return;
			}
			for (PoiInfo p : allPoi) {
				if (p.name.endsWith("市")) continue;
				if (p.location == null) continue;
				final long lat = (long) (p.location.latitude * 1e6);// 纬度
				final long lon = (long) (p.location.longitude * 1e6);// 经度
				if (lat <= 0 || lon <= 0) continue;// 过滤掉没有经纬度的
				Address address = new Address();
				address.setCity(p.city);
				address.setAddress(p.address);
				address.setName(p.name);
				address.setX(lon);
				address.setY(lat);
				adrrResult.add(address);
			}

			if (adrrResult.size() > 0) {
				setAdapter();
			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult arg0) {

		}
	};

	private void initView() {
		mBackView.setOnClickListener(this);
		mTitleView.setText(getString(R.string.app_name));

		if (!isOnlyShowPoints) {// 搜索
			mSearch.setVisibility(View.VISIBLE);
			mSearch.setText(getString(R.string.main_foot_search));
			mSearch.setOnClickListener(this);
			// 如果当前定位地址成功
			if (!TextUtils.isEmpty(Constant.CURRENT_ADDRESS)) {
				mStartTextView.setText(Constant.CURRENT_ADDRESS);
				mEndTextView.requestFocus();
			}
			mEndTextView.addTextChangedListener(new MyTextWatcher(END));
			mSearchLayout.setVisibility(View.VISIBLE);
		} else {// 只显示起点和终点
			mStartTextView.setFocusable(false);
			mStartTextView.setEnabled(false);
			mEndTextView.setFocusable(false);
			mEndTextView.setEnabled(false);
			mOnlyShowAddRelativeLayout.setVisibility(View.VISIBLE);
		}

	}

	private class MyTextWatcher implements TextWatcher {
		private int mIndex;

		public MyTextWatcher(int index) {
			mIndex = index;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (isSelected) {
				isSelected = false;
			} else {
				afterChanged(mIndex, s);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}

	}

	private void afterChanged(int index, CharSequence s) {
		mCurrentIndex = index;
		if (s.length() > 0) {
			if (!Util.getInstance().isNetworkAvailable(this)) {
				Util.showToast(this, getString(R.string.network_unavaiable));
				return;
			}
			mPoiSearch.searchInCity((new PoiCitySearchOption()).keyword(s.toString())
					.city(TextUtils.isEmpty(locationCity) ? Constant.currCity : locationCity)
					.pageCapacity(20).pageNum(0));
		}
	}

	private void initMap() {

		// 地图初始化
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocationClient = Util.getInstance().getLocationClient(this, mBdLocationListener);
		mLocationClient.start();
		if (!isOnlyShowPoints) {// 搜索
			initPoiSearch();
		} else {// 只显示起点,终点,途经点
			showPosition(bundle);
		}
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位

		mLocationClient.stop(); // 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	private BDLocationListener mBdLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			LatLng first = new LatLng(Constant.CURRENT_ADDRESS_LAT, Constant.CURRENT_ADDRESS_LON);

			if (location == null) {// 定位失败
				if (!isOnlyShowPoints) {
					if (Constant.CURRENT_ADDRESS_LAT > 0 && Constant.CURRENT_ADDRESS_LON > 0) {
						startLat = (long) (Constant.CURRENT_ADDRESS_LAT * 1e6);
						startLon = (long) (Constant.CURRENT_ADDRESS_LON * 1e6);
						addOverLay(first, startBitmapDescriptor);
						animat2Point(first, 13f);
					}
					mStartTextView.addTextChangedListener(new MyTextWatcher(START));
				}
				return;
			}

			// 定位数据
			locationCity = location.getCity();
			locationgCityCode = location.getCityCode();
			String currentAddress = Util.getInstance().getEasyAddr(location.getAddrStr());

			// 定位成功后，如果是搜索
			if (!TextUtils.isEmpty(currentAddress) && !isOnlyShowPoints) {
				Constant.CURRENT_ADDRESS = currentAddress;
				mStartTextView.setText(currentAddress);
				mStartTextView.addTextChangedListener(new MyTextWatcher(START));
			}
			// 定位成功
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			animat2Point(currentLatLng, 17f);// 地图移动到定位的点

			if (!isOnlyShowPoints) {// 搜索
				Constant.CURRENT_ADDRESS = currentAddress;
				Constant.CURRENT_ADDRESS_LAT = location.getLatitude();
				Constant.CURRENT_ADDRESS_LON = location.getLongitude();
				startLat = (long) (Constant.CURRENT_ADDRESS_LAT * 1e6);
				startLon = (long) (Constant.CURRENT_ADDRESS_LON * 1e6);
				// addOverLay(currentLatLng, startBitmapDescriptor);
				
			} else {
				// 起点图标
				LatLng startLatLng = new LatLng((double) startLat / 1e6, (double) startLon / 1e6);
				addOverLay(startLatLng, startBitmapDescriptor);
				// 终点图标
				LatLng endLatLng = new LatLng((double) endLat / 1e6, (double) endLon / 1e6);
				addOverLay(endLatLng, endBitmapDescriptor);

				// 起点和终点途经点连成线
				addLine(startLatLng, endLatLng, points);
				animat2Point(startLatLng, 13f);
			}
		}

	};

	@Override
	public void onClick(View v) {
		if (v == mBackView) {
			if (mPoiListView.getVisibility() == View.VISIBLE) {
				mPoiListView.setVisibility(View.GONE);
			} else {
				exit();
			}
		} else if (v == mSearch) {
			if (checkParameters()) {
				Util.getInstance().go2Search(this, createSearchKey());
			}
			
		}

	}

	private Key createSearchKey() {
		Key key = new Key();
		key.setCarpoolType(Pinche.CARPOOLTYPE_ON_OFF_WORK);
		final String startPoint = mStartTextView.getText().toString().trim();
		final String endPoint = mEndTextView.getText().toString().trim();
		if (!TextUtils.isEmpty(startPoint)) {
			key.setStart_name(startPoint);
			key.setStart_lat(startLat);
			key.setStart_lon(startLon);
		}
		if (!TextUtils.isEmpty(endPoint)) {
			key.setEnd_name(endPoint);
			key.setEnd_lat(endLat);
			key.setEnd_lon(endLon);
		}
		key.setStartCityId(TextUtils.isEmpty(locationgCityCode) ? String
				.valueOf(Constant.currCityCode) : locationgCityCode);
		key.setEndCityId(TextUtils.isEmpty(locationgCityCode) ? String
				.valueOf(Constant.currCityCode) : locationgCityCode);
		key.setCurrDistrict(Constant.currDistrict);
		return key;
	}

	protected boolean checkParameters() {
		final String startName = mStartTextView.getText().toString();
		
		if (TextUtils.isEmpty(startName) || startLat <= 0 || startLon <= 0) {
			Util.showToast(this, getString(R.string.choice_statr));
			return false;
		}
		final String endName = mEndTextView.getText().toString();

		if (TextUtils.isEmpty(endName) || endLat <= 0 || endLon <= 0) {
			Util.showToast(this, getString(R.string.choice_end));
			return false;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (mPoiListView.getVisibility() == View.VISIBLE) {
			mPoiListView.setVisibility(View.GONE);
		} else {
			exit();
		}
	}

	private void exit() {
		this.finish();
		overridePendingTransition(R.anim.main_activity_in, R.anim.choice_addr_activity_out);
	}

	private void setAdapter() {
		if (mAdapter == null) {
			mAdapter = new AddressAdapter(adrrResult, this);
			mPoiListView.setAdapter(mAdapter);
			mPoiListView.setOnItemClickListener(onItemClickListener);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		mPoiListView.setVisibility(View.VISIBLE);
	}
	

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			isSelected = true;
			mPoiListView.setVisibility(View.GONE);
			Address address = adrrResult.get(position);
			if (address != null) {
				if (mCurrentIndex == START) {

					mStartTextView.setText(address.getName());
					// 地图上画点
					drwaPointOnMap(address, START);

				} else {
					mEndTextView.setText(address.getName());
					drwaPointOnMap(address, END);
				}
			}
			adrrResult.clear();
			mAdapter.notifyDataSetChanged();
		}
	};

	private void drwaPointOnMap(Address address, int icon) {
		double y = (double) address.getY() / 1e6;
		double x = (double) address.getX() / 1e6;
		if (icon == START) {
			startLat = address.getY();
			startLon = address.getX();
		} else {
			endLat = address.getY();
			endLon = address.getX();
		}
		mBaiduMap.clear();
		LatLng llA = new LatLng(y, x);
		if (icon == START) {
			// 如果终点已经存在
			addOverLay(llA, startBitmapDescriptor);
			if (endLat > 0 && endLon > 0) {
				addOverLay(new LatLng((double) endLat / 1e6, (double) endLon / 1e6),
						endBitmapDescriptor);
				addLine();
			}
		} else {
			addOverLay(llA, endBitmapDescriptor);
			if (startLat > 0 && startLon > 0) {
				addOverLay(new LatLng((double) startLat / 1e6, (double) startLon / 1e6),
						startBitmapDescriptor);
				addLine();
			}
		}
		animat2Point(llA, 13f);
	}

	private void addLine() {

		LatLng p1 = new LatLng((double) startLat / 1e6, (double) startLon / 1e6);
		LatLng p2 = new LatLng((double) endLat / 1e6, (double) endLon / 1e6);

		List<LatLng> gPoints = new ArrayList<LatLng>();
		gPoints.add(p1);
		gPoints.add(p2);
		addLineWithPoints(gPoints);
	}

	private void addLine(LatLng startLng, LatLng enLng, Parcelable[] pPoints) {
		List<LatLng> gPoints = new ArrayList<LatLng>();
		gPoints.add(startLng);

		if (pPoints != null && pPoints.length > 0) {// 画途经点
			BitmapDescriptor throuthPointBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.throuth_point);
			// 第一个途经点
			Point point1 = (Point) pPoints[0];
			double pStlat1 = (double) point1.getLat() / 1e6;
			double pStlon1 = (double) point1.getLon() / 1e6;
			LatLng p3 = new LatLng(pStlat1, pStlon1);
			addOverLay(p3, throuthPointBitmapDescriptor);
			gPoints.add(p3);
			// 第二个途经点
			if (points.length == 2&& pPoints[1] != null) {
				Point point2 = (Point) pPoints[1];
				LatLng p4 = new LatLng((double) point2.getLat() / 1e6,
						(double) point2.getLon() / 1e6);
				addOverLay(p4, throuthPointBitmapDescriptor);
				gPoints.add(p4);

			}
		}
		gPoints.add(enLng);
		addLineWithPoints(gPoints);
	}

	private void addLineWithPoints(List<LatLng> points) {
		OverlayOptions ooPolyline = new PolylineOptions().width(5).color(0xAAFF0000).points(points);
		mBaiduMap.addOverlay(ooPolyline);
	}

	private void addOverLay(LatLng latLng, BitmapDescriptor iconId) {
		OverlayOptions ooA = new MarkerOptions().position(latLng).icon(iconId).zIndex(9);
		mBaiduMap.addOverlay(ooA);
	}

	private void animat2Point(LatLng llA, float zoom) {
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, zoom);
		mBaiduMap.animateMapStatus(u);
	}
}
