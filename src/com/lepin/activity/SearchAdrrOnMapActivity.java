package com.lepin.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.widget.MapIcon;

/**
 * 地图选点
 * 
 * @author zhiqiang
 * 
 */
@Contextview(R.layout.search_adrr_from_map)
public class SearchAdrrOnMapActivity extends BaseActivity {
	@ViewInject(id = R.id.search_adrr)
	private TextView mCurrAdrr;// 定位到的地址

	@ViewInject(id = R.id.search_mapview)
	private MapView mMapView;// 基本地图

	@ViewInject(id = R.id.search_ok)
	private TextView mSearchOk;

	private BaiduMap mBaiduMap = null;
	private LocationClient mLocationClient = null;
	private GeoCoder mGeoCoder = null;

	private long mLon = 0;// 定位地址经度
	private long mLat = 0;// 定位地址纬度
	private boolean isFirst = true;
	private String mCityCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		initBaiduMap();
	}

	private void initView() {
		mSearchOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLat <= 0 || mLon <= 0) {
					Util.showToast(SearchAdrrOnMapActivity.this, "未选择地点");
				} else {
					locationResult();
				}

			}
		});

	}

	/**
	 * 定位结果处理
	 */
	private void locationResult() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putLong(Constant.SLON, mLon);
		bundle.putLong(Constant.SLAT, mLat);
		bundle.putString(Constant.CITY_CODE, mCityCode);
		if (Constant.I_START == getIntent().getIntExtra(Constant.S_ICON, Constant.I_START)) {// 起点
			bundle.putString(Constant.S_START, mCurrAdrr.getText().toString());
			intent.putExtras(bundle);
			setResult(Constant.S_SEARCH_START, intent);
		} else {
			bundle.putString(Constant.S_END, mCurrAdrr.getText().toString());
			intent.putExtras(bundle);
			setResult(Constant.S_SEARCH_END, intent);
		}
		finish();

	}

	// * 初始化百度地图相关信息以及定位监听
	private void initBaiduMap() {
		mBaiduMap = mMapView.getMap();
		// 地图中心点变化
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setOnMapStatusChangeListener(mapStatusChangeListener);
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				// 设置地图缩放级别
				MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(17f);
				mBaiduMap.animateMapStatus(update);
			}
		});
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(geoCoderResultListener);

		mLocationClient = Util.getInstance().getLocationClient(this, getBdLocationListener);
		mLocationClient.start();

		Util.showLongToast(SearchAdrrOnMapActivity.this, getString(R.string.positioning));
		// 添加icon到地图表面
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		SearchAdrrOnMapActivity.this.getWindow().addContentView(
				new MapIcon(SearchAdrrOnMapActivity.this, Constant.I_START == getIntent()
						.getIntExtra(Constant.S_ICON, Constant.I_START) ? 0 : 1, new Point(
						dm.widthPixels, dm.heightPixels)), params);
	}

	// 坐标－－地址
	private OnGetGeoCoderResultListener geoCoderResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (isFirst) {
				isFirst = false;
				return;
			}
			mCurrAdrr.setText(Util.getInstance().getEasyAddr(result.getAddress()));
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {

		}
	};
	// 地图中心点变化
	private OnMapStatusChangeListener mapStatusChangeListener = new OnMapStatusChangeListener() {

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus mapStatus) {

			LatLng latLng = mapStatus.target;
			mLon = (long) (latLng.longitude * 1e6);
			mLat = (long) (latLng.latitude * 1e6);
			mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));

		}

		@Override
		public void onMapStatusChange(MapStatus arg0) {
			// TODO Auto-generated method stub

		}
	};
	// 定位
	private BDLocationListener getBdLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null) return;
			mLat = (long) (location.getLatitude() * 10e6);
			mLon = (long) (location.getLongitude() * 10e6);
			Util.printLog("定位纬度:" + location.getLatitude());
			Util.printLog("定位经度:" + location.getLongitude());
			mCityCode = location.getCityCode();
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			mCurrAdrr.setText(Util.getInstance().getEasyAddr(location.getAddrStr()));
		}
	};

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

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocationClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}
