package com.lepin.util;

import java.util.List;

import org.apache.http.NameValuePair;

import com.lepin.util.Util.OnHttpRequestDataCallback;

import android.content.Context;
import android.os.AsyncTask;

public class HttpRequestOnBackgrount extends AsyncTask<String, Void, String> {
	public final static int GET = 0;
	public final static int POST = 1;
	private int requestType = GET;
	private OnHttpRequestDataCallback onDataLoadingCallBack;

	public OnHttpRequestDataCallback getOnDataLoadingCallBack() {
		return onDataLoadingCallBack;
	}

	public void setOnDataLoadingCallBack(OnHttpRequestDataCallback onDataLoadingCallBack) {
		this.onDataLoadingCallBack = onDataLoadingCallBack;
	}

	private List<NameValuePair> listParmas = null;
	private Context mContext;
	private boolean isHandleError = false;// 是否自己处理错误

	public boolean isHandleError() {
		return isHandleError;
	}

	public void setHandleError(boolean isHandleError) {
		this.isHandleError = isHandleError;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (Constant.is_comfirm_dialog_show) return;
	}

	public HttpRequestOnBackgrount(int requestType, OnHttpRequestDataCallback onLoadingCallBack2,
			List<NameValuePair> params, Context context, boolean isHandleBySelf) {
		super();
		this.requestType = requestType;
		this.onDataLoadingCallBack = onLoadingCallBack2;
		this.listParmas = params;
		this.mContext = context;
		this.isHandleError = isHandleBySelf;
	}

	@Override
	protected String doInBackground(String... params) {
		if (!Util.getInstance().isNetworkAvailable(mContext)) return null;
		String resultString = "";
		if (requestType == GET) {
			resultString = HttpUtil.get(params[0], mContext);
		} else {
			resultString = HttpUtil.post(listParmas, params[0], mContext);
		}
		return resultString;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result == null) return;
		Util.getInstance().doHttpResult(mContext, result, isHandleError, onDataLoadingCallBack);
	}
}
