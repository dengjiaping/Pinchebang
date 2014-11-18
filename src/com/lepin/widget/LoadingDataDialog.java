package com.lepin.widget;

import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.util.HttpUtil;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

public class LoadingDataDialog extends ProgressDialog {
	LoadPostAsynTask mLoadPostAsynTask;
	LoadGetAsynTask mLoadGetAsynTask;
	private TextView mTitleInfo;
	private Context mContext;
	private boolean isHandleErrorBySelf = false;// 是否自己处理错误

	private OnHttpRequestDataCallback onDataLoadingCallBack2;

	public LoadingDataDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	public LoadingDataDialog(Context context, int theme, boolean isHandleErrorBySelf) {
		super(context, theme);
		this.mContext = context;
		this.isHandleErrorBySelf = isHandleErrorBySelf;
	}

	public LoadingDataDialog(Context context, int theme,
			OnHttpRequestDataCallback onDataLoadingCallBack2, boolean isHandleErrorBySelf) {
		super(context, theme);
		this.mContext = context;
		this.isHandleErrorBySelf = isHandleErrorBySelf;
		this.onDataLoadingCallBack2 = onDataLoadingCallBack2;
	}

	public boolean isHandleErrorBySelf() {
		return isHandleErrorBySelf;
	}

	public void setHandleErrorBySelf(boolean isHandleErrorBySelf) {
		this.isHandleErrorBySelf = isHandleErrorBySelf;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.pcb_dialog);
		mTitleInfo = (TextView) this.findViewById(R.id.leping_dialog_tv);
	}

	public void executePost(Object params, String url) {
		show();
		if (!Util.getInstance().isNetworkAvailable(mContext)) {
			LoadingDataDialog.this.dismiss();
			Util.showToast(mContext, mContext.getString(R.string.network_unavaiable));
			return;
		}
		mLoadPostAsynTask = new LoadPostAsynTask(url);
		mLoadPostAsynTask.execute(params);
	}

	public void executeGet(String url) {
		show();
		mLoadGetAsynTask = new LoadGetAsynTask();
		mLoadGetAsynTask.execute(url);
	}

	private class LoadPostAsynTask extends AsyncTask<Object, Void, String> {
		private String mUrl;

		public LoadPostAsynTask(String url) {
			this.mUrl = url;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(Object... params) {

			String response = "";// 响应结果
			try {
				if (params[0] instanceof List) {// List调用post请求
					response = HttpUtil.post((List<NameValuePair>) params[0], mUrl, mContext);
				}
			} catch (Exception e) {
				Util.getInstance().log(e.getMessage());
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
//			Util.printLog("LoadingDataDialog.post返回:"+result);
			LoadingDataDialog.this.dismiss();// 关闭对话框
			doReturnJadge(result);
			doResult(result, onDataLoadingCallBack2);
		}
	}

	private class LoadGetAsynTask extends AsyncTask<String, Void, String> {

		public LoadGetAsynTask() {
		}

		@Override
		protected String doInBackground(String... params) {
			String response = "";// 响应结果
			try {
				response = HttpUtil.get(params[0].toString(), mContext);
			} catch (Exception e) {
				Util.getInstance().log(e.getMessage());
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			LoadingDataDialog.this.dismiss();// 关闭对话框
			doReturnJadge(result);
			doResult(result, onDataLoadingCallBack2);
		}
	}

	public void cancel() {
		this.dismiss();
		if (mLoadPostAsynTask != null) {
			mLoadPostAsynTask.cancel(true);
		}
		if (mLoadGetAsynTask != null) {
			mLoadGetAsynTask.cancel(true);
		}
	}

	public void setTitleInfo(String info) {
		mTitleInfo.setText(info);
	}

	private void doResult(String result, OnHttpRequestDataCallback callBack) {
		Util.getInstance().doHttpResult(mContext, result, isHandleErrorBySelf,
				onDataLoadingCallBack2);
	}

	private void doReturnJadge(String result) {
		if (TextUtils.isEmpty(result)) {
			Util.showToast(mContext, "请求失败");
			return;
		}
	}
}
