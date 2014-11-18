package com.lepin.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpUtil;
import com.lepin.util.Util;

@Contextview(R.layout.car_service_main)
public class CarServiceActivity extends BaseActivity implements OnClickListener {

	// 返回
	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView title_text;

	@ViewInject(id = R.id.webview)
	private WebView mWebview;

	// 定位获取经纬度
	private LocationClient mLocationClient = null;

	String flag = null;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void init() {
		btnBack.setOnClickListener(this);
		// 定位当前位置，获取经纬度
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		flag = bundle.getString(Constant.JCHDOrQCFW);
		url = bundle.getString("url");
		mWebview.getSettings().setJavaScriptEnabled(true);

		mWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;

			}
		});
		if (flag.equals(Constant.JCHD)) {
			HttpUtil.syncCookie(Constant.URL_JCHD);
			// setCookie();
			title_text.setText(getResources().getString(R.string.activites));
			// 如果url不为空就是活动
			mWebview.loadUrl(TextUtils.isEmpty(url) ? Constant.URL_JCHD : url);
		} else if (flag.endsWith(Constant.QCFW)) {
			title_text.setText(getResources().getString(R.string.car_service));
			initBaiduMap();
		}
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		switch (i) {
		case R.id.common_title_back:
			if (mWebview.canGoBack()) {
				mWebview.goBack();
			} else {
				onBack();
				this.finish();
			}
			break;
		default:
			if (Constant.s_CURRENT_LAT == 0 || Constant.s_CURRENT_LON == 0) {
				Util.showToast(this, getString(R.string.car_server_getlocation));
			}
			break;
		}

	}

	@Override
	public void onBackPressed() {
		onBack();
		this.finish();
	}

	private void onBack() {
		if (!TextUtils.isEmpty(url)) {
			Util.getInstance().go2Activity(this, CarSharingActivity.class);
		}
	}

	private void initBaiduMap() {
		mLocationClient = Util.getInstance().getLocationClient(CarServiceActivity.this,
				mBdLocationListener);
		mLocationClient.start();
	}

	private BDLocationListener mBdLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {// 定位成功
				Constant.s_CURRENT_LAT = (int) (location.getLatitude() * 1E6);
				Constant.s_CURRENT_LON = (int) (location.getLongitude() * 1E6);
				mWebview.loadUrl(Constant.URL_QCFW + "?centerLat=" + Constant.s_CURRENT_LAT
						+ "&centerLon=" + Constant.s_CURRENT_LON);
			}
		}

	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onStop() {
		if (flag.endsWith(Constant.QCFW)) {
			mLocationClient.stop();
		}
		super.onStop();
	}

}
