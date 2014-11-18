package com.lepin.widget;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.lepin.util.HttpUtil;

/**
 * ListView数据加载
 * 
 * 
 */
public class Loading {
	private static Loading instance;

	public static Loading getInstance() {
		if (instance == null) {
			instance = new Loading();
		}
		return instance;
	}

	private Loading() {

	}

	/**
	 * POST请求
	 * 
	 * @param callBack
	 * @param params
	 * @param url
	 */

	public void executePost(CallBack callBack, Object params, String url, Context mContext) {
		ListPostAsyncTask listAsyncTask = new ListPostAsyncTask(callBack, params, url, mContext);
		listAsyncTask.execute(params);
	}

	public void executeGet(CallBack callBack, String url, Context mContext) {
		ListgetAsyncTask mListgetAsyncTask = new ListgetAsyncTask(callBack, mContext);
		mListgetAsyncTask.execute(url);
	}

	private class ListPostAsyncTask extends AsyncTask<Object, Void, String> {
		private CallBack callBack;// 回调方法
		private String mUrl;
		private Context mContext;

		public ListPostAsyncTask(CallBack callBack, Object params, String url, Context mContext) {
			this.callBack = callBack;
			this.mUrl = url;
			this.mContext = mContext;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(Object... params) {
			String response = "";
			try {
				if (params[0] instanceof List) {// List调用post请求
					response = HttpUtil.post((List<NameValuePair>) params[0], mUrl, mContext);
				} /*
				 * else { response = HttpUtil.get(params[0].toString()); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				this.callBack.getResult(result);// 获取返回结果
			}
			super.onPostExecute(result);
		}
	}

	private class ListgetAsyncTask extends AsyncTask<String, Void, String> {
		private CallBack callBack;// 回调方法
		private Context mContext;

		public ListgetAsyncTask(CallBack callBack, Context mContext) {
			this.callBack = callBack;
			this.mContext = mContext;
		}

		@Override
		protected String doInBackground(String... params) {
			String response = "";
			try {
				response = HttpUtil.get(params[0].toString(), mContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				this.callBack.getResult(result);// 获取返回结果
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * 回调方法的接口
	 */
	public interface CallBack {
		public void getResult(String result);
	}

}
