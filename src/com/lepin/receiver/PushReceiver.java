package com.lepin.receiver;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.lepin.activity.CarServiceActivity;
import com.lepin.activity.MessageCenterActivity;
import com.lepin.activity.MyOrderDetailActivity;
import com.lepin.activity.PincheTrailActivity;
import com.lepin.activity.RecommendActivity;
import com.lepin.entity.PushMsg;
import com.lepin.util.Constant;
import com.lepin.util.Util;

public class PushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Bundle bundle = intent.getExtras();

		if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Util.printLog("用户点击打开了通知");

			final String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
			// if(!TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_MSG_ID)))
			JPushInterface.reportNotificationOpened(context, msgId);
			operateWithBundle(bundle, context);
		}

	}

	// 打印所有的 intent extra 数据
	private void operateWithBundle(Bundle bundle, Context mContext) {
		String result = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Util.printLog("推送信息:" + result);
		JSONObject jsonObject;
		Bundle goBundle = new Bundle();
		Intent i = new Intent();
		try {
			jsonObject = new JSONObject(result);
			String type = jsonObject.getString("type");
			String expand = jsonObject.getString("expand");
			String orderString = PushMsg.PUSH_MSG_TYPE.ORDER.name();
			if (type.endsWith(orderString) || type.contains(orderString)) {// 订单
				i.setClass(mContext, MyOrderDetailActivity.class);
				goBundle.putString(Constant.BOOK_ID, expand);
			} else if (type.equals(PushMsg.PUSH_MSG_TYPE.ACTIVITY.name())) {// 活动
				i.setClass(mContext, CarServiceActivity.class);
				goBundle.putString(Constant.JCHDOrQCFW, Constant.JCHD);
				goBundle.putString("url", expand);
			} else if (type.equals(PushMsg.PUSH_MSG_TYPE.RECOMMEND_SINGLE.name())) {// 跳转到信息详情页
				i.setClass(mContext, PincheTrailActivity.class);
				goBundle.putInt("PincheId", Integer.parseInt(expand));
			} else if (type.equals(PushMsg.PUSH_MSG_TYPE.RECOMMEND_MULTI.name())) {// 跳转到推荐页面
				i.setClass(mContext, RecommendActivity.class);
				goBundle.putString("recommendType", expand);
			} else {// 跳转到消息中心
				i.setClass(mContext, MessageCenterActivity.class);
				goBundle.putBoolean(MessageCenterActivity.S_IS_PUSH, true);
			}
			i.putExtras(goBundle);
			if (!isAppRunning(mContext)) {
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			} else {
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			mContext.startActivity(i);
		} catch (JSONException e) {
			e.printStackTrace();
			i.setClass(mContext, MessageCenterActivity.class);
			goBundle.putBoolean(MessageCenterActivity.S_IS_PUSH, true);
			mContext.startActivity(i);
		}
	}

	protected boolean isAppRunning(Context mContext) {

		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(mContext.getPackageName())
					&& info.baseActivity.getPackageName().equals(mContext.getPackageName())) {
				return true;
			}
		}
		return false;
	}
}
