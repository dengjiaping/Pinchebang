package com.lepin;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.lepin.util.HttpUtil;
import com.umeng.analytics.MobclickAgent;

/**
 *
 */
public class CarSharingApplication extends Application {
	private static ArrayList<Activity> mActivitiesStack = null;
	private static CarSharingApplication mInstance = null;

	public static CarSharingApplication Instance() {
		if (mInstance == null) {
			mInstance = new CarSharingApplication();
		}
		return mInstance;
	}

	/**
	 * 添加已经打开的Activity到堆栈
	 * 
	 * @param paramActivity
	 */
	public void addAcrivity(Activity paramActivity) {
		if (mActivitiesStack == null) {
			mActivitiesStack = new ArrayList<Activity>();
		}
		if (!mActivitiesStack.contains(paramActivity)) {
			mActivitiesStack.add(paramActivity);
		}
	}

	/**
	 * 关闭指定的Activity
	 * 
	 * @param paramActivity
	 */
	public void finishActivity(Activity paramActivity) {
		if (paramActivity != null && mActivitiesStack.contains(paramActivity)) {
			mActivitiesStack.remove(paramActivity);
		}
		paramActivity.finish();
	}

	/**
	 * 退出App
	 * 
	 * @param paramContext
	 */
	public void exit(Context paramContext) {
		final int size = mActivitiesStack.size();
		for (int i = 0; i < size; i++) {
			if (mActivitiesStack.get(i) != null) mActivitiesStack.get(i).finish();
		}
		mActivitiesStack.clear();
		System.exit(0);
		// 结束整个App包括Service，Activity
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
		mInstance = this;
		MobclickAgent.openActivityDurationTrack(false);
		JPushInterface.init(getApplicationContext());
		HttpUtil.init(getApplicationContext());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

}
