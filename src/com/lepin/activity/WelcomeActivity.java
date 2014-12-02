package com.lepin.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.lepin.CarSharingApplication;
import com.lepin.entity.LoadPage;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpUtil;
import com.lepin.util.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 欢迎页，定位
 * 
 * @author zhiqiang
 * 
 */
@Contextview(R.layout.welcome)
public class WelcomeActivity extends InstrumentedActivity {
	Util mUtil = Util.getInstance();

	@ViewInject(id = R.id.welcome_normal_loading_view)
	private RelativeLayout mNormalLoadingLayout;

	@ViewInject(id = R.id.welcome_loading)
	private ImageView mDynamicLoadingImageView;
	String url = null;
	final Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		CarSharingApplication.Instance().addAcrivity(WelcomeActivity.this);
		Util.getInstance().getLocalVersionInfo(WelcomeActivity.this);
		Bitmap bitmap = Util.getInstance().getLoadingBitmap();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				mHandler.obtainMessage(1).sendToTarget();
			}
		};
		timer.schedule(task, 2000);
		if (bitmap == null) {// 没有动态loading页使用默认
			mNormalLoadingLayout.setVisibility(View.VISIBLE);
		} else {
			mDynamicLoadingImageView.setImageBitmap(bitmap);
			mDynamicLoadingImageView.setVisibility(View.VISIBLE);
			LoadPage loadPage = Util.getInstance().getLoadingInfo(this);
			if (loadPage != null) {
				if (!TextUtils.isEmpty(loadPage.getUrl())) {
					url = loadPage.getUrl();
				}
			}
			mDynamicLoadingImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					timer.cancel();
					Bundle goBundle = new Bundle();
					goBundle.putString(Constant.JCHDOrQCFW, Constant.JCHD);
					goBundle.putString("url", url);
					Util.getInstance().go2ActivityWithBundle(WelcomeActivity.this,
							CarServiceActivity.class, goBundle);
				}
			});
		}
	}

	private void start() {
		if (Util.getInstance().isNetworkAvailable(this)) {// 网络是连接
			// 查看用户是否登录
			loginWithCooies();
		} else {
			toMainOrSelectCity();
		}
	}

	private void loginWithCooies() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				User user = HttpUtil.checkUserIsLogin(Constant.URL_LOGINED, WelcomeActivity.this);
				if (user != null) {
					mUtil.setUser(WelcomeActivity.this, user);// 查看用户是否是登录
					mUtil.setPushEnable(WelcomeActivity.this, user);
				}
				mHandler.obtainMessage(2).sendToTarget();
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				start();
				break;
			case 2:
				toMainOrSelectCity();
				break;
			}
		};
	};

	/**
	 * 欢迎页停留2秒
	 */

	private void toMainOrSelectCity() {
		if (Constant.isGetLocation || Util.getInstance().isCityInfoExist(WelcomeActivity.this)) {// 如果没有城市信息则选择城市
			Util.getInstance().go2Activity(WelcomeActivity.this, CarSharingActivity.class);
		} else {
			go2SelectCity();
		}
		WelcomeActivity.this.finish();
	}

	private void go2SelectCity() {
		Bundle bundle = new Bundle();
		bundle.putBoolean(Constant.SGETUSERLOCATION, true);
		Util.getInstance().go2ActivityWithBundle(WelcomeActivity.this, SelectCityActivity.class,
				bundle);

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
		MobclickAgent.onPause(WelcomeActivity.this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		timer.cancel();
		CarSharingApplication.Instance().exit(this);
	}

}
