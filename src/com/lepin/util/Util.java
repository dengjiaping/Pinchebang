package com.lepin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.alipay.android.app.sdk.AliPay;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lepin.CarSharingApplication;
import com.lepin.activity.AddNewCarActivity;
import com.lepin.activity.LoginActivity;
import com.lepin.activity.MyOrderDetailActivity;
import com.lepin.activity.PayActivity;
import com.lepin.activity.R;
import com.lepin.activity.SearchResultActivity;
import com.lepin.activity.SearchWithMapActivity;
import com.lepin.activity.ShareActivity;
import com.lepin.activity.UseClauseActivity;
import com.lepin.entity.City;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Key;
import com.lepin.entity.LoadPage;
import com.lepin.entity.Pinche;
import com.lepin.entity.PushMsg.PUSH_MSG_TYPE;
import com.lepin.entity.UpdateInfo;
import com.lepin.entity.User;
import com.lepin.loadimage.ImageFileCache;
import com.lepin.loadimage.ImageGetFromHttp;
import com.lepin.loadimage.ImageMemoryCache;
import com.lepin.pay.PayKeys;
import com.lepin.pay.Rsa;
import com.lepin.widget.LoadingDataDialog;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;
import com.umeng.socialize.controller.UMSocialService;

/**
 * app工具类。，此类包括check用户是否处于登录状态、用户对象是否为空、保存cookie、获取cookie、清空cookie、保存用户信息、清空用户信息
 * 、保存刷新时间、 获取刷新时间、处理异常、时间对话框、网络判断、判断SD卡以及N多数据解析处理方法
 * 
 * @author zhiqing
 * 
 */
public class Util implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User currentUser = null;
	public String infoString = "yes";
	public DatePickerDialog dateSelectorDialog;// 年月日
	public TimePickerDialog timeSelectorDialog;// 时分
	private final static String SP_SYS_LP = "sys_lp";
	private final static String SP_COOKIES = "cookies";
	private final static String SP_USER_OBJ = "user_obj";
	private final static String SP_USER_INFO = "user_info";
	private final static String SP_CITY = "city";
	private final static String SP_LOADING_PAG = "loading";
	private ArrayList<City> areas = null;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private String mImei = null;
	// 网络类型
	public static final int NET_TYPE_NO = -1;
	public static final int NET_TYPE_2G_OR_3G = 0;
	public static final int NET_TYPE_WIFI = 1;

	/**
	 * saveFile:保存的文件
	 */
	private File saveFile;

	private static class UtilHodel {
		public static final Util UTIL = new Util();
	}

	private Util() {
	}

	public static Util getInstance() {
		return UtilHodel.UTIL;
	}

	public User getLoginUser(Context mContext) {
		if (currentUser != null) {
			return currentUser;
		} else {
			currentUser = getUser(mContext);
			if (currentUser != null) {
				return currentUser;
			}
		}
		return currentUser;
	}

	public void setUser(Context mContext, User user) {
		this.currentUser = user;
		updateUser(mContext, user);
	}

	/**
	 * 保存标识区分是否是第一次使用app
	 * 
	 * @param flag
	 */
	public void saveFlag(String flag, Context mContext) {
		if (mContext != null) {
			SharedPreferences sp = getSharedPreferences(mContext, SP_SYS_LP);
			SharedPreferences.Editor ed = sp.edit();
			ed.putString("one", flag);
			ed.commit();
		}
	}

	public SharedPreferences getSharedPreferences(Context mContext, String name) {
		return mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	/**
	 * 得到标识,"1"表示是第一次使用，"2"表示不是第一次使用
	 * 
	 * @return
	 */
	public boolean isFirstLogin(Context mContext) {
		String flag = "1";
		if (mContext != null) {
			SharedPreferences sp = getSharedPreferences(mContext, SP_SYS_LP);
			flag = sp.getString("one", "1");
		}
		return flag.equals("1");
	}

	/**
	 * 获取cookie 根据KEY获取以下信息 type:android deviceKey:设备号 pcbVersion 版本号 JSESSIONID
	 * sessionID signCiphertext 密文 Path 路径 Domain 域名 Comment 、Ports、CommentURL
	 * 
	 * @return
	 */
	public Map<String, ?> getCookiesFromFile(Context mContext) {
		Map<String, ?> map = null;
		if (mContext != null) {
			// 读取cookie
			SharedPreferences sp = getSharedPreferences(mContext, SP_COOKIES);
			map = sp.getAll();
		}
		return map;
	}

	/**
	 * 持久化cookie
	 * 
	 * @param cookieStore
	 */
	public void saveCookies(Map<String, String> map, Context mContext) {
		SharedPreferences sp = getSharedPreferences(mContext, SP_COOKIES);
		Editor ed = sp.edit();
		if (map != null) {
			Set<String> keys = map.keySet();
			for (String key : keys) {
				ed.putString(key, map.get(key));
			}
		}
		ed.commit();
	}

	/**
	 * 清空本地保存的Cookie
	 */
	public void clearCookie(Context mContext) {
		if (mContext != null) {
			SharedPreferences sp = getSharedPreferences(mContext, SP_COOKIES);
			Editor ed = sp.edit();
			ed.clear();
			ed.commit();
		}
		HttpUtil.clearCacheCookies();
	}

	/**
	 * 使用base64保存user对象
	 */
	public void updateUser(Context mContext, User user) {
		SharedPreferences sp = getSharedPreferences(mContext, SP_USER_OBJ);
		SharedPreferences.Editor editor = sp.edit();
		if (user == null) {
			editor.clear();
			editor.commit();
		} else {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos);
				os.writeObject(user);
				// 编码是将字符数组编码为字符串
				String stringBase64 = new String(Base64.encodeToString(bos.toByteArray(),
						Base64.DEFAULT));
				editor.putString(SP_USER_INFO, stringBase64);
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 使用base64对取出的user对象反序列化
	 * 
	 * @param mContext
	 * @return
	 */
	public User getUser(Context mContext) {
		User user = null;
		try {
			SharedPreferences sp = getSharedPreferences(mContext, SP_USER_OBJ);
			String stringBase64 = sp.getString(SP_USER_INFO, null);
			if (stringBase64 == null) return user;
			// 进行对应的解码，
			byte[] bytesBase64 = Base64.decode(stringBase64.getBytes(), Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytesBase64);
			ObjectInputStream ois = new ObjectInputStream(bais);
			user = (User) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean isUserLoging(Context mContext) {
		if (currentUser != null) {
			return true;
		} else {
			final User user = getUser(mContext);
			if (user != null) {
				currentUser = user;
				return true;
			}
		}

		return false;
	}

	/**
	 * 保存下拉更新时间
	 * 
	 * @param time
	 */
	public void saveRefreshTime(Context mContext, String time) {
		SharedPreferences sp = mContext.getSharedPreferences("time", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("lastTime", time);
		editor.commit();
	}

	public String getRefreshTime(Context mContext) {
		String time = "";
		SharedPreferences sp = mContext.getSharedPreferences("time", Context.MODE_PRIVATE);
		time = sp.getString("lastTime", "");
		return time;
	}

	/**
	 * Toast消息提示网络未连接
	 * 
	 * @param paramContext
	 */
	public void showTip(Context paramContext) {
		showToast(paramContext, paramContext.getString(R.string.network_unavaiable));
	}

	/**
	 * Toast消息提示
	 * 
	 * @param paramContext
	 * @param paramString
	 */
	public static void showToast(Context mContext, String paramString) {
		Toast.makeText(mContext, paramString, Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(Context mContext, String paramString) {
		Toast.makeText(mContext, paramString, Toast.LENGTH_LONG).show();
	}

	/**
	 * 时间年月日选择
	 * 
	 * @param paramContext
	 *            上下文对象
	 * @param paramDate
	 *            当前系统时间
	 * @param paramOnDateSetListener
	 *            监听器
	 */
	public void showDateSelectorDialog(Context paramContext, Date paramDate,
			DatePickerDialog.OnDateSetListener paramOnDateSetListener) {
		Calendar c = Calendar.getInstance();
		if (paramDate != null) {
			c.setTime(paramDate);
			c.add(Calendar.DAY_OF_MONTH, 2);
		}
		if ((dateSelectorDialog == null) || (!dateSelectorDialog.isShowing())) {
			dateSelectorDialog = new DatePickerDialog(paramContext, paramOnDateSetListener,
					c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			dateSelectorDialog.show();
		}
	}

	/**
	 * 时间时分选择
	 * 
	 * @param paramContext
	 *            上下文对象
	 * @param paramDate
	 *            当前系统时间
	 * @param paramOnTimeSetListener
	 *            监听器
	 */
	public void showDateSelectorDialog(Context paramContext, Date paramDate,
			TimePickerDialog.OnTimeSetListener paramOnTimeSetListener, String str, String strM) {
		Calendar c = Calendar.getInstance();
		if (paramDate != null) {
			c.setTime(paramDate);
		}
		if ((timeSelectorDialog == null) || (!timeSelectorDialog.isShowing())) {
			if ("0".equals(str)) {// 上下班
				if ("m".equals(strM)) {// 上班出发时间默认 07:30，下班默认为 18:30
					timeSelectorDialog = new TimePickerDialog(paramContext, paramOnTimeSetListener,
							7, 30, true);
				} else {
					timeSelectorDialog = new TimePickerDialog(paramContext, paramOnTimeSetListener,
							18, 30, true);
				}
			} else {// 长途默认为HH:00
				timeSelectorDialog = new TimePickerDialog(paramContext, paramOnTimeSetListener,
						c.get(Calendar.HOUR_OF_DAY), 0, true);
			}
			timeSelectorDialog.show();
		}
	}

	/**
	 * 判断SD是否存在
	 * 
	 * @return
	 */
	public boolean hasSdcard() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param paramContext
	 * @return
	 */
	public boolean isNetworkAvailable(Context paramContext) {
		NetworkInfo networkInfo = ((ConnectivityManager) paramContext
				.getSystemService("connectivity")).getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取网络各种状态
	 * 
	 * @param paramContext
	 * @return
	 */
	/*
	 * public int getNetState(Context paramContext) { int netType =
	 * Constant.TYPE_NO; NetworkInfo networkInfo = ((ConnectivityManager)
	 * paramContext .getSystemService("connectivity")).getActiveNetworkInfo();
	 * if (networkInfo == null) { return netType; } else { if
	 * (Constant.TYPE_WIFI == networkInfo.getType()) { netType =
	 * Constant.TYPE_WIFI; } else if (Constant.TYPE_CMWAP ==
	 * networkInfo.getType()) { netType = Constant.TYPE_CMWAP; } } return
	 * netType; }
	 */

	/**
	 * 获取本机电话号码
	 * 
	 * @param context
	 * @return
	 */
	public String getPhoneNumber(Context context) {
		SharedPreferences mPreferences = getSharedPreferences(context, "user");
		return mPreferences.getString("userPhone", "");
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param paramActivity
	 */
	public void hideSoftKeyborad(Activity activity) {
		View view = activity.getWindow().peekDecorView();
		if (view != null && view.getWindowToken() != null) {
			((InputMethodManager) activity.getSystemService("input_method"))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 校验字符串是否为空或者null
	 * 
	 * @param paramString
	 *            被校验的字符串
	 * @return true 代表是空，否则false
	 */
	public boolean isNullOrEmpty(String paramString) {

		return TextUtils.isEmpty(paramString);
	}

	/**
	 * 判断是否整数
	 * 
	 * @param paramString
	 *            被校验的字符串
	 * @return 是整数返回true，否则返回false
	 */
	public boolean isNumeric(String paramString) {
		return paramString.matches("^[0-9]*$");
	}

	public static void printLog(String string) {
		Log.i("pinchebang", string);
	}

	/**
	 * 清除持久化的用户数据
	 */
	public void clearUser(Context mContext) {
		SharedPreferences sp = getSharedPreferences(mContext, SP_USER_OBJ);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 显示Dialog
	 * 
	 * @param mContext
	 * @param titleInfo
	 *            标题
	 * @param ok
	 *            确定
	 * @param cancel
	 *            取消
	 * @param listener
	 *            按钮监听
	 */
	public void showDialog(Context mContext, String title, String ok, String cancel,
			OnOkOrCancelClickListener listener) {
		if (Constant.is_comfirm_dialog_show) return;// 如果已经有一个在显示了就别再显示了
		PcbConfirmDialog mCustomerDialog = PcbConfirmDialog.getInstance(mContext,
				R.style.dialog_tran);
		mCustomerDialog.setText(title, ok, cancel);
		mCustomerDialog.setCustomerListener(listener);
		mCustomerDialog.show();
	}

	public void go2Activity(Context mContext, Class<?> cls) {
		Intent mIntent = new Intent(mContext, cls);
		mContext.startActivity(mIntent);
	}

	public void go2ActivityWithBundle(Context mContext, Class<?> cls, Bundle bundle) {
		Intent mIntent = new Intent(mContext, cls);
		mIntent.putExtras(bundle);
		mContext.startActivity(mIntent);
	}

	/**
	 * 计算地图上两点距离
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public Double get2PointsDistances(LatLng start, LatLng end) {
		Util.printLog("计算距离-起点:" + start.latitude);
		Util.printLog("计算距离-起点:" + start.longitude);
		Util.printLog("计算距离-终点:" + end.latitude);
		Util.printLog("计算距离-终点:" + end.longitude);
		return DistanceUtil.getDistance(start, end);
	}

	/**
	 * 获取LoadingDataDialog
	 * 
	 * @param mContext
	 * @return
	 */
	public LoadingDataDialog getLoadingDataDialog(Context mContext) {
		final LoadingDataDialog mLoadingDataDialog = new LoadingDataDialog(mContext,
				R.style.My_dialog);
		mLoadingDataDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				mLoadingDataDialog.cancel();
			}
		});
		return mLoadingDataDialog;
	}

	/**
	 * 获取LoadingDataDialog
	 * 
	 * @param mContext
	 * @param onDataLoadingCallBack2
	 * @param isHandleErrorBySelf
	 * @return
	 */

	public LoadingDataDialog getLoadingDataDialog(Context mContext,
			OnHttpRequestDataCallback onDataLoadingCallBack2, boolean isHandleErrorBySelf) {
		final LoadingDataDialog mLoadingDataDialog = new LoadingDataDialog(mContext,
				R.style.My_dialog, onDataLoadingCallBack2, isHandleErrorBySelf);
		mLoadingDataDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				mLoadingDataDialog.cancel();
			}
		});
		return mLoadingDataDialog;
	}

	/**
	 * 拿到一天后的时间
	 * 
	 * @return
	 */
	public String getNextCurrentTime() {
		Calendar cal = Calendar.getInstance();
		Date date = new Date(System.currentTimeMillis());
		cal.setTime(date);
		cal.add(Calendar.DATE, 2);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		return sdf.format(cal.getTime());
	}

	public <T> boolean isDataExist(JsonResult<T> jsonResult) {
		if (jsonResult.getData() == null) {
			return false;
		}
		return true;
	}

	/**
	 * 获取使用支付宝支付或者充值的参数
	 * 
	 * @param mContext
	 * @param rechargeNum
	 *            金额
	 * @param orderId
	 *            订单编号 rechargeNum
	 * @return
	 */
	public String getNewOrderInfo(Context mContext, String orderId, String money, String body,
			String title) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(PayKeys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		// out_trade_no 商户订单编号，如果是充值业务 那么此参数传入充值的会员编号，如果是拼车订单，此参数表示拼车订单编号
		sb.append(orderId);
		sb.append("\"&subject=\"");
		sb.append(title);// 商品名称
		sb.append("\"&body=\"");
		// body
		// 自定义扩展参数如：0,25;
		// 参数具体含义：支付业务,会员编号;
		// 支付业务:0表示充值业务（默认），1表示拼车订单
		sb.append(body);// 商品介绍
		sb.append("\"&total_fee=\"");
		sb.append(money);

		// 网址需要做URL编码
		sb.append("\"&notify_url=\"");
		try {
			sb.append(URLEncoder.encode(Constant.URL_LOCAL + "/pay/alipay.do", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// sb.append("\"&return_url=\"");
		// sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(PayKeys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");
		String string = new String(sb);
		String sign = Rsa.sign(string, PayKeys.RSA_PRIVATE);
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		string += "&sign=\"" + sign + "\"&" + getSignType();
		return string;
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * 调用支付宝支付
	 * 
	 * @param mContext
	 * @param rechargeOrPayMoney
	 *            金额
	 * @param orderId
	 *            订单ID
	 * @param payOrRecharge
	 *            操作类型
	 * @param mHandler
	 */
	public void go2RechargeOrPay(final Context mContext, String moneyNum, String orderId,
			String body, String title, final Handler mHandler) {
		try {
			final String info = getNewOrderInfo(mContext, orderId, moneyNum, body, title);
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay((Activity) mContext, mHandler); // 设置为沙箱模式，不设置默认为线上环境
					String result = alipay.pay(info);
					Message msg = new Message();
					msg.what = 1;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 获取充值拼车币支付时的订单编号
	 * 
	 * @param mContext
	 * @param money
	 *            充值金额
	 * @param TYPE
	 *            具体业务
	 * @param objId
	 * @param mDataLoadingCallBack
	 */
	public void getOperateOrderNum(Context mContext, String money, String type, String objId,
			OnHttpRequestDataCallback mDataLoadingCallBack) {
		final LoadingDataDialog mDataDialog = getLoadingDataDialog(mContext, mDataLoadingCallBack,
				false);
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("amount", money));
		params.add(new BasicNameValuePair("serviceType", type));
		params.add(new BasicNameValuePair("rechargeType", "ALIPAY"));
		if (objId != null) params.add(new BasicNameValuePair("objId", objId));
		mDataDialog.executePost(params, Constant.URL_GET_RECHARGE_ORDER_ID);
		mDataDialog.setTitleInfo(mContext.getString(R.string.init));
	}

	/**
	 * 获取统一订单，初始话支付
	 * 
	 * @param mContext
	 * @param money
	 * @param type
	 *            充值类型
	 * @param mPayId
	 *            　
	 * @param mHandler
	 */
	public void initRecharge(final Context mContext, final int money, String type, String mPayId,
			final Handler mHandler) {
		// 拼车币
		getOperateOrderNum(mContext, String.valueOf(money), type, mPayId,
				new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						Util.printLog("payactivity 支付初始化统一订单:" + result);
						JsonResult<String> mJsonResult = Util.getInstance().getObjFromJsonResult(
								result, new TypeToken<JsonResult<String>>() {
								});
						if (mJsonResult != null && mJsonResult.isSuccess()) {
							go2RechargeOrPay(mContext, String.valueOf((float) money / 100),
									mJsonResult.getData(), mContext.getString(R.string.pay_body),
									mContext.getString(R.string.pay_order), mHandler);

						} else {
							Util.showToast(mContext, mContext.getString(R.string.init_f) + ":"
									+ mJsonResult.getErrorMsg());
						}
					}
				});

	}

	/**
	 * 为edittext添加文本事件，价格不能以0开头
	 * 
	 * @param editText
	 */
	public void checkCost(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = editText.getText().toString().trim();
				if (str.length() > 1) {
					if ("0".equals(str.substring(0, 1))) {
						editText.setText(str.substring(1, str.length()));
						editText.setSelection(editText.getText().length());
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * 软件分享
	 * 
	 * @param mContext
	 */
	public void share(Context mContext, UMSocialService Controller) {
 
		UMSharing share = new UMSharing(mContext, Controller);
		share.init();
		share.startShare();
	}

	/**
	 * 订单分享,线路分享
	 * 
	 * @param mContext
	 */
	public void share(Context mContext, String book_id, UMSocialService Controller,
			String shareType, String shareUrl, String shareContent, String title) {
		UMSharingMyOrder sharingMyOrder = new UMSharingMyOrder(mContext, book_id, Controller,
				shareType, shareUrl, shareContent, title);
		// UMSharingMyOrder share = new UMSharingMyOrder(mContext, book_id,
		// Controller);
		sharingMyOrder.init();
		sharingMyOrder.startShare();
	}

	/**
	 * 拼车信息未发布点击返回按钮时提示
	 */
	public void showPublishDialogMsg(final Activity context) {

		showDialog(context, context.getString(R.string.msg_text2),
				context.getString(R.string.msg_btn_text1),
				context.getString(R.string.msg_btn_text2), new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							context.finish();
						}

					}
				});

	}

	/**
	 * 设置推送时的别名
	 * 
	 * @param context
	 * @param userId
	 */
	public void setPushEnable(Context context, User user) {
		String userId = "";
		if (!isUserLoging(context) || getUser(context).getPushSwitch().equals(User.PUSH_CLOSED))
			return;
		Set<String> set = new HashSet<String>();
		if (user != null) {
			// 设置注销推送 ""表示注销
			userId = String.valueOf(user.getUserId());
			set.add("ANDROID");
			set.add(user.getGender());
			// set.add("age_" + user.getBirthday());
			set.add("AV_" + Constant.PCB_VERSION);
		}
		printLog("设置推送:" + set.toString());
		JPushInterface.setAliasAndTags(context, userId, set, new TagAliasCallback() {

			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				Util.printLog("设置推送返回:" + arg0);
			}
		});
	}

	public DisplayMetrics getScreenWandH(Activity mActivity) {
		DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * 获取城市信息
	 * 
	 * @param mContext
	 * @return
	 */
	public String getAreaInfo(Context mContext) {
		InputStream inputStream = mContext.getResources().openRawResource(R.raw.area);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			inputStream.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	/**
	 * 计算传入时间和当前时间差
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public int calculteDate(int year, int month, int day) {
		Calendar nowCalendar = Calendar.getInstance();// 当前时间
		Calendar goCalendar = Calendar.getInstance();
		goCalendar.set(year, month, day);
		return goCalendar.get(Calendar.DAY_OF_MONTH) - nowCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public long getTimeInSconds(int year, int month, int day, int hourOfDay, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, hourOfDay, minute);
		return calendar.getTimeInMillis() / 1000;
	}

	public boolean theTimeIsAfterNow(int year, int month, int day, int hourOfDay, int minute) {
		Calendar nowCalendar = Calendar.getInstance();// 当前时间
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		return calendar.after(nowCalendar);
	}

	/**
	 * 计算两点距离
	 * 
	 * @param start_lat
	 * @param start_lon
	 * @param end_lat
	 * @param end_lon
	 * @return
	 */
	public boolean get2point2Distances(long start_lat, long start_lon, long end_lat, long end_lon) {
		boolean isTrue = true;
		LatLng start = new LatLng((double) start_lat / 1e6, (double) start_lon / 1e6);
		LatLng end = new LatLng((double) end_lat / 1e6, (double) end_lon / 1e6);
		final double distances = get2PointsDistances(start, end);
		Util.printLog("两点距离：" + distances);
		if (distances < Constant.TwoPoinstsDistances) {
			isTrue = false;
		}
		return isTrue;
	}

	public enum SERVICE_TYPE {
		/** 充值拼车币 */
		RECHARGE,
		/** 拼车订单 */
		CARPOOL_ORDER,
		/** 话费充值 */
		RECHARGE_TELEPHONE_CHARGES,
		/*
		 * 计划支付
		 */
		CARPOOL_PROGRAM,
		/**
		 * 充值余额
		 */
		GOLD_RECHARGE
	};

	public enum RECHARGE_TYPE {
		/** 支付宝充值 */
		ALIPAY
	};

	/**
	 * post请求
	 * 
	 * @param mContext
	 * @param callBack
	 * @param params
	 * @param loadingUrl
	 * @param loadingMsg
	 */
	public void doPostRequest(Context mContext, OnHttpRequestDataCallback onDataLoadingCallBack2,
			List<NameValuePair> params, String loadingUrl, String loadingMsg,
			boolean isHandleErrorBySelf) {
		if (!isNetworkAvailable(mContext)) {// 检查网络
			showTip(mContext);
			return;
		}
		LoadingDataDialog loadingDataDialog = getLoadingDataDialog(mContext,
				onDataLoadingCallBack2, isHandleErrorBySelf);
		loadingDataDialog.executePost(params, loadingUrl);
		loadingDataDialog.setTitleInfo(loadingMsg);
	}

	/**
	 * get请求
	 * 
	 * @param mContext
	 * @param callBack
	 * @param loadingUrl
	 * @param loadingMsg
	 */
	public void doGetRequest(Context mContext, OnHttpRequestDataCallback onDataLoadingCallBack2,
			String loadingUrl, String loadingMsg, boolean isHandleErrorBySelf) {
		if (!isNetworkAvailable(mContext)) {
			showTip(mContext);
			return;
		}
		LoadingDataDialog loadingDataDialog = getLoadingDataDialog(mContext,
				onDataLoadingCallBack2, isHandleErrorBySelf);
		loadingDataDialog.executeGet(loadingUrl);
		loadingDataDialog.setTitleInfo(loadingMsg);
	}

	/**
	 * 将jsonString转换成对象
	 * 
	 * @param jsonString
	 * @param t
	 *            需要转换成的对象
	 * @return
	 */
	public <T> JsonResult<T> getObjFromJsonResult(String jsonString, TypeToken<JsonResult<T>> token) {
		/*
		 * GsonBuilder gb = new
		 * GsonBuilder().registerTypeAdapter(PUSH_MSG_TYPE.class, new
		 * PushMsgTypeDeserualizer()); Gson gson = gb.create();
		 */
		JsonResult<T> resultT = null;
		try {
			resultT = gson.fromJson(jsonString, token.getType());
		} catch (Exception e) {
			printLog("解析json错误:" + e.getMessage());
			resultT = null;
		}
		return resultT;
	}

	public <T> T getObjFromJson(String jsonString, TypeToken<T> token) {
		/*
		 * GsonBuilder gb = new
		 * GsonBuilder().registerTypeAdapter(PUSH_MSG_TYPE.class, new
		 * PushMsgTypeDeserualizer()); Gson gson = gb.create();
		 */
		try {
			return gson.fromJson(jsonString, token.getType());
		} catch (JsonSyntaxException e) {
			printLog("Util.getObjFromJson解析json错误:" + e.getMessage());
		}
		return null;
	}

	public <T> JsonResult<T> getObjFromJsonResultWithClass(String jsonString,
			TypeToken<JsonResult<T>> token) {
		GsonBuilder gb = new GsonBuilder().registerTypeAdapter(PUSH_MSG_TYPE.class,
				new PushMsgTypeDeserualizer());
		Gson gson = gb.create();
		JsonResult<T> resultT = null;
		try {
			resultT = gson.fromJson(jsonString, token.getType());
		} catch (Exception e) {
			resultT = null;
		}
		return resultT;
	}

	/**
	 * 获取本地Apk版本
	 */
	public int getLocalVersionInfo(Context mContext) {
		PackageInfo packageInfo;
		try {
			packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			Constant.sLocalVersionCode = packageInfo.versionCode;
			Constant.sLocalVersionName = "V" + packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return Constant.sLocalVersionCode;
	}

	public void checkUpdate(Context mContext, boolean isShowTip) {
		if (!(Constant.sLocalVersionCode > 0)) getLocalVersionInfo(mContext);
		if (isNetworkAvailable(mContext)) {
			UpdateTask updateTase = new UpdateTask();
			updateTase.setContext(mContext);
			updateTase.setShowTip(isShowTip);
			updateTase.execute(Constant.URL_UPDATE);
		}
	}

	/**
	 * 更新TASK
	 * 
	 * @author zhiqiang
	 * 
	 */
	private class UpdateTask extends AsyncTask<String, Void, String> {

		private Context mContext;
		private boolean isShowTip = false;

		public void setShowTip(boolean isShowTip) {
			this.isShowTip = isShowTip;
		}

		public void setContext(Context context) {
			this.mContext = context;
		}

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			if (!isNullOrEmpty(url)) {
				return HttpUtil.get(url, mContext);
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(final String jResult) {

			if (jResult != null) {
				UpdateInfo updateInfo = null;
				if (isJsonResultRight(jResult)) {
					JsonResult<UpdateInfo> jsonResult = getObjFromJsonResult(jResult,
							new TypeToken<JsonResult<UpdateInfo>>() {
							});
					if (jsonResult.isSuccess()) {
						updateInfo = jsonResult.getData();
					}
					final UpdateInfo updateInfo2 = updateInfo;
					if (updateInfo2 != null) {
						if (updateInfo.getVersionCode() > Constant.sLocalVersionCode) {// 有更新
							showDialog(mContext, mContext.getString(R.string.can_update_version),
									mContext.getString(R.string.update),
									mContext.getString(R.string.my_info_btn_cancel),
									new OnOkOrCancelClickListener() {

										@Override
										public void onOkClick(int type) {
											if (type == PcbConfirmDialog.OK) {// 更新
												String urlString = updateInfo2.getPath();
												if (!urlString.startsWith("http:")) {
													urlString = Constant.URL_LOCAL + urlString;
												}
												DownloadUpdateTask downloadUpdateTask = new DownloadUpdateTask();
												downloadUpdateTask.setmContext(mContext);
												downloadUpdateTask.execute(urlString);
											}

										}
									});
						} else {
							if (isShowTip)
								showToast(mContext, mContext.getString(R.string.no_update_version));
						}
					}
				}
			}
		}
	}

	/**
	 * 更新APP的任务
	 * 
	 * @author zhiqiang
	 * 
	 */
	private class DownloadUpdateTask extends AsyncTask<String, Integer, Boolean> {
		// 通知栏
		private NotificationManager updateNotificationManager = null;
		private Notification updateNotification = null;
		// 通知栏跳转Intent
		private PendingIntent updatePendingIntent = null;

		private File updateDir = null;
		private File updateFile = null;
		private Context mContext;

		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			if (Util.getInstance().isSDCardMounted()) {
				updateDir = getCacheFile(mContext);
				// updateDir = new File(Constant.CACHE);
				if (!updateDir.exists() && updateDir.isDirectory()) updateDir.mkdirs();
				updateFile = new File(updateDir.getPath(), "pinchebang.apk");
			}
			if (updateFile.exists()) {
				updateFile.delete();
			}
			this.updateNotificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			this.updateNotification = new Notification();

			// 设置下载过程中，点击通知栏，回到主界面
			// 设置通知栏显示内容
			updateNotification.icon = R.drawable.icon;
			updateNotification.tickerText = mContext.getString(R.string.app_name)
					+ mContext.getString(R.string.downloading);
			updateNotification.setLatestEventInfo(mContext, mContext.getString(R.string.app_name),
					"0%", updatePendingIntent);
			// 发出通知
			updateNotificationManager.notify(0, updateNotification);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String... params) {
			int downloadCount = 0;
			// int currentSize = 0;
			long totalSize = 0;
			int updateTotalSize = 0;

			HttpURLConnection httpConnection = null;
			InputStream is = null;
			FileOutputStream fos = null;
			Util.printLog("开始下载:");
			try {
				URL url = new URL(params[0]);
				httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
				httpConnection.setConnectTimeout(10000);
				httpConnection.setReadTimeout(20000);
				updateTotalSize = httpConnection.getContentLength();
				if (httpConnection.getResponseCode() == 404) {
					Util.printLog("应用更新下载404");
					throw new Exception("fail!");
				}
				is = httpConnection.getInputStream();
				fos = new FileOutputStream(updateFile, false);
				byte buffer[] = new byte[4096];
				int readsize = 0;
				while ((readsize = is.read(buffer)) > 0) {
					fos.write(buffer, 0, readsize);
					totalSize += readsize;
					Util.printLog("应用更新下载:" + totalSize);
					// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
					int temp = (int) (totalSize * 100 / updateTotalSize);
					if ((downloadCount == 0) || temp - 10 > downloadCount) {
						downloadCount += 10;
						updateNotification.setLatestEventInfo(mContext,
								mContext.getString(R.string.downloading), temp + "%",
								updatePendingIntent);
						updateNotificationManager.notify(0, updateNotification);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (httpConnection != null) {
						httpConnection.disconnect();
					}
					if (is != null) is.close();
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return totalSize > 0 ? true : false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Util.printLog("是否下载成功:" + result);
				updateNotificationManager.cancelAll();
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				mContext.startActivity(installIntent);
			}
		}
	}

	/**
	 * 获取拼车币
	 * 
	 * @param mContext
	 * @param mPinCheMoneyView
	 *            需要显示的view
	 */
	public void getUserCoins(final Context mContext, final TextView mPinCheMoneyView) {

		doGetRequest(mContext, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				JsonResult<String> pinCheBiResult = getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				Constant.s_PinCheBi = Integer.parseInt(pinCheBiResult.getData());
				mPinCheMoneyView.setText(String.valueOf(Constant.s_PinCheBi));
			}
		}, Constant.URL_GET_PINCHE_BI, mContext.getString(R.string.get_pinche_money), false);
	}

	public void saveCity(Context mContext, City city) {
		Constant.currCityCode = city.getArea_id();// 城市id
		Constant.currCity = city.getArea_name();// 城市
		Constant.currDistrict = "";// 行政区划
		Constant.isGetLocation = true;
		saveCity(mContext);
	}

	public void saveCity(Context mContext) {

		SharedPreferences sp = getSharedPreferences(mContext, SP_CITY);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("currCityCode", Constant.currCityCode);
		editor.putString("currCity", Constant.currCity);
		editor.putString("currDistrict", Constant.currDistrict);
		editor.commit();
	}

	/**
	 * 是否保存了城市信息
	 * 
	 * @param mContext
	 * @return
	 */
	public boolean isCityInfoExist(Context mContext) {
		SharedPreferences sp = getSharedPreferences(mContext, SP_CITY);
		Constant.currCityCode = sp.getInt("currCityCode", -1);
		Constant.currCity = sp.getString("currCity", "");
		Constant.currDistrict = sp.getString("currDistrict", "");// 行政区划
		if (TextUtils.isEmpty(Constant.currCity) || Constant.currCityCode < 0) {
			return false;
		} else {
			Constant.isGetLocation = true;
			return true;
		}
	}

	public void startAnimation(Context mContext, View view) {
		Animation mRefreshaAnimation = AnimationUtils.loadAnimation(mContext,
				R.anim.refresh_animation);
		mRefreshaAnimation.setFillAfter(true);
		LinearInterpolator ln = new LinearInterpolator();
		mRefreshaAnimation.setInterpolator(ln);
		if (mRefreshaAnimation != null) {
			view.startAnimation(mRefreshaAnimation);
		}
	}

	public void stopAnimation(View view) {
		view.clearAnimation();
	}

	public void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1) break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 判断jsonresult字符串是否为空或者404,504
	 * 
	 * @param mContext
	 * @param jsonResult
	 * @return
	 */
	public boolean isJsonResultRight(String jsonResult) {
		if (isNullOrEmpty(jsonResult)) {
			return false;
		}
		return true;
	}

	public <T> T string2Bean(String json, Class<T> t) {
		Gson gson = new GsonBuilder().create();
		T bean = gson.fromJson(json, t);
		return bean;
	}

	/**
	 * 记录日志
	 * 
	 * @param log
	 */
	public void log(String log) {
		String logPathString = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/pinchebanglog";
		Util.printLog("log path:" + logPathString);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(logPathString);
			try {
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
					Util.printLog("file create successed:" + file.getAbsolutePath());
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(file, "pLog.txt")));
				writer.write(log + "\n");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取LocationClient
	 * 
	 * @param mContext
	 * @param listener
	 *            BDLocationListener
	 * @return
	 */
	public LocationClient getLocationClient(Context mContext, BDLocationListener listener) {
		LocationClient mClient = new LocationClient(mContext);
		mClient.registerLocationListener(listener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setIsNeedAddress(true);
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);
		mClient.setLocOption(option);
		return mClient;
	}

	/**
	 * 发布时，如果没有车辆信息，就添加
	 * 
	 * @param mContext
	 */
	public void publish2AddCar(final Context mContext) {
		showDialog(mContext, mContext.getString(R.string.no_carinfo_2_add),
				mContext.getString(R.string.add), mContext.getString(R.string.my_info_btn_cancel),
				new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							go2Activity(mContext, AddNewCarActivity.class);
						}
					}
				});
	}

	/**
	 * 发布时检查费用是否正确
	 * 
	 * @param mContext
	 * @param money
	 * @return
	 */
	public boolean checkMoneyIsRight(Context mContext, String money) {
		if (!money.matches("[1-9][0-9]{0,3}")) {
			Util.showToast(mContext, mContext.getString(R.string.publish_money_error));
			return false;
		}
		return true;
	}

	/**
	 * 检查发布时人数
	 * 
	 * @param mContext
	 * @param peopleNum
	 * @return
	 */
	public boolean checkPeopleNum(Context mContext, String peopleNum) {
		if (!peopleNum.matches("[1-6]")) {
			showToast(mContext, mContext.getString(R.string.people_num_toast));
			return false;
		}
		return true;
	}

	/**
	 * 加载城市和省份信息
	 * 
	 * @param mContext
	 * @return
	 */
	public ArrayList<City> loadArea(Context mContext) {
		if (areas == null) {
			areas = new ArrayList<City>();
			String areaInfoString = getAreaInfo(mContext);
			try {
				JSONObject jsonObject = new JSONObject(areaInfoString);
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				JSONObject jObject = null;
				for (int i = 0; i < jsonArray.length(); i++) {
					City city = new City();
					jObject = jsonArray.getJSONObject(i);
					city.setArea_name(jObject.getString("areaName"));
					city.setArea_id(jObject.getInt("areaId"));
					city.setPid(jObject.getInt("pid"));
					city.setOrderid(jObject.getInt("orderid"));
					JSONArray suArray = jObject.getJSONArray("subAreas");
					ArrayList<City> cities = new ArrayList<City>();
					for (int j = 0; j < suArray.length(); j++) {
						JSONObject suJsonObject = suArray.getJSONObject(j);
						City sCity = new City();
						sCity.setArea_name(suJsonObject.getString("areaName"));
						sCity.setArea_id(suJsonObject.getInt("areaId"));
						sCity.setPid(suJsonObject.getInt("pid"));
						sCity.setOrderid(suJsonObject.getInt("orderid"));
						sCity.setSubCitys(null);
						cities.add(sCity);
					}
					city.setSubCitys(cities);
					areas.add(city);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return areas;
	}

	/**
	 * 获取省份信息
	 * 
	 * @param areas
	 * @param mContext
	 * @return
	 */
	public Map<Integer, City> getProvince(Context mContext) {
		loadArea(mContext);
		/*
		 * // Map<Integer, City> mProvinceMap = new LinkedHashMap<Integer,
		 * City>(); // // 省 // for (City city : areas) { //
		 * mProvinceMap.put(city.getArea_id(), city); // }
		 */return null;
	}

	/**
	 * 获取某个城市的id
	 * 
	 * @param mContext
	 * @param cityName
	 * @return
	 */
	public String getCityCode(Context mContext, String cityName) {
		ArrayList<City> cities = loadArea(mContext);
		for (City city : cities) {
			if (cityName.contains(city.getArea_name())) {// 如果是直辖市
				return String.valueOf(city.getArea_id());
			} else {
				ArrayList<City> sCities = city.getSubCitys();
				if (sCities == null || sCities.size() < 1) continue;
				for (City sCity : sCities) {
					if (cityName.contains(sCity.getArea_name())) {//
						return String.valueOf(sCity.getArea_id());
					}

				}
			}
		}
		return null;
	}

	/**
	 * 设置EditText光标位置
	 * 
	 * @param editText
	 */
	public void setEditTextPoint2End(EditText editText) {
		int l = editText.getText().toString().length();
		editText.setSelection(l);
	}

	public String createOrGetFilePath(String fileName, Context mContext) {
		File saveCatalog = new File(Constant.CACHE);
		if (!saveCatalog.exists()) {
			saveCatalog.mkdirs();
		}
		saveFile = new File(saveCatalog, fileName);
		try {
			saveFile.createNewFile();
		} catch (IOException e) {
			showToast(mContext, "创建文件失败，请检查SD是否有足够空间");
			e.printStackTrace();
		}
		return saveFile.getAbsolutePath();
	}

	/**
	 * TODO filePath:图片路径
	 * 
	 * @author {author wangxiaohong}
	 */
	public Bitmap getSmallBitmap(Context mContext, String filePath) {
		DisplayMetrics dm;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, dm.widthPixels, dm.heightPixels);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * TODO 保存并压缩图片 将bitmap 保存 到 path 路径的文件里
	 * 
	 * @author {author wangxiaohong}
	 */
	public boolean save(Context mContext, String path, Bitmap bitmap) {
		DisplayMetrics dm;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (path != null) {
			try {
				// FileCache fileCache = new FileCache(mContext);
				// File f = fileCache.getFile(url);
				File f = new File(path);
				FileOutputStream fos = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
				saveMyBitmap(bitmap);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	private void saveMyBitmap(Bitmap bm) {
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(saveFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bm.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPath(String carId, String fileName, Context mContext) {
		File saveCatalog = new File(Constant.CACHE, carId);
		// saveCatalog = new File(path);
		if (!saveCatalog.exists()) {
			saveCatalog.mkdirs();
		}
		saveFile = new File(saveCatalog, fileName);
		try {
			saveFile.createNewFile();
		} catch (IOException e) {
			Toast.makeText(mContext, "创建文件失败，请检查SD是否有足够空间", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		return saveFile.getAbsolutePath();

	}

	/**
	 * TODO 获得相册选择图片的图片路径
	 * 
	 * @author {author wangxiaohong}
	 */
	public String getImagePath(Context mContext, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String ImagePath = cursor.getString(column_index);
		cursor.close();
		return ImagePath;
	}

	/**
	 * 跳转到分享路线界面
	 * 
	 * @param mContext
	 * @param bookId
	 */
	public void go2Share(Context mContext, String bookId) {
		Util.printLog("发布成功后分享线路id:" + bookId);
		Bundle mBundle = new Bundle();
		mBundle.putString(Constant.BOOK_ID, bookId);
		go2ActivityWithBundle(mContext, ShareActivity.class, mBundle);
	}

	/**
	 * 跳转到搜索路线界面
	 * 
	 * @param mContext
	 * @param searchKey
	 */
	public void go2Search(Context mContext, Key searchKey) {
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("search_key", searchKey);
		Util.getInstance().go2ActivityWithBundle(mContext, SearchResultActivity.class, mBundle);
	}

	/**
	 * TODO 获取手机唯一设备号
	 * 
	 * @author {author wangxiaohong}
	 */
	public String getDeviceID(Context mContext) {
		TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		return deviceId;
	}

	/**
	 * TODO 获取手机联网类型
	 * 
	 * 返回 -1 未联网 0 2G or 3G 1 wifi
	 * 
	 * @author {author wangxiaohong}
	 */
	public int getNetType(Context mContext) {
		ConnectivityManager connectMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info == null) {
			Toast.makeText(mContext, "您的手机未连接任何网络，请检查您的网络，稍后再试", Toast.LENGTH_SHORT).show();
			return NET_TYPE_NO;
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return NET_TYPE_WIFI;
		} else {
			return NET_TYPE_2G_OR_3G;
		}
	}

	/**
	 * @param url
	 *            缓存/网络获取图片URL
	 * @param path
	 *            本地磁盘图片 path
	 * @param mContext
	 * @return
	 */
	public String getImei(Context mContext) {
		if (mImei != null) return mImei;
		TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		mImei = tm.getDeviceId();
		return mImei;
	}

	/**
	 * @param url
	 *            缓存/网络获取图片URL
	 * @param path
	 *            本地磁盘图片 path
	 * @param mContext
	 * @return
	 */
	public Bitmap getBitmap(String url, String path, Context mContext, ImageView imageView) {
		ImageMemoryCache memoryCache = new ImageMemoryCache(mContext);
		ImageFileCache fileCache = new ImageFileCache();

		// 从内存缓存中获取图片
		Bitmap result = memoryCache.getBitmapFromCache(url);
		if (result == null) {
			// 从文件中获取
			if (null != path) {
				result = fileCache.getImage(path);
			}
			if (null == result) {
				// 从网络中获取
				ImageGetFromHttp httpImage = new ImageGetFromHttp();
				Util.printLog(url);

				result = httpImage.downloadBitmap(url, imageView, mContext);

				if (result != null) {
					fileCache.saveBitmap(result, path);
					memoryCache.addBitmapToCache(url, result);
				}
			} else {
				// 添加到内存缓存
				memoryCache.addBitmapToCache(url, result);
			}
		}
		return result;
	}

	/**
	 * 判断ＳＤ卡是否挂载　
	 * 
	 * @return
	 */
	public boolean isSDCardMounted() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) return true;
		return false;
	}

	/**
	 * 获取字符串的MD5值
	 * 
	 * @param val
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String getMD5String(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] b = md5.digest();// 加密
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString().replace("-", "");
	}

	/**
	 * 打电话
	 * 
	 * @param mContext
	 * @param tel
	 */
	public void call(final Context mContext, final String tel) {
		showDialog(mContext, mContext.getString(R.string.is_contact),
				mContext.getString(R.string.confirm), mContext.getString(R.string.call_later),
				new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {

							if (!TextUtils.isEmpty(tel)) {
								Intent phoneIntent = new Intent("android.intent.action.CALL", Uri
										.parse("tel:" + tel));
								mContext.startActivity(phoneIntent);
							} else {
								Util.showToast(mContext,
										mContext.getString(R.string.phone_num_error));
							}
						}
					}
				});
	}

	/**
	 * 在地图上显示起点和终点
	 * 
	 * @param mPinche
	 */
	public void showStartAndEndOnMap(Context mContext, Pinche mPinche) {
		if (mPinche.getStartLat() <= 0 || mPinche.getStartLon() <= 0 || mPinche.getEndLat() <= 0
				|| mPinche.getEndLon() <= 0) {
			showToast(mContext, mContext.getString(R.string.location_error));
			return;
		}

		Bundle mBundle = new Bundle();
		// 起点名称，经纬度
		mBundle.putString(Constant.S_START, mPinche.getStart_name());
		mBundle.putInt(Constant.START_LAT, mPinche.getStartLat());
		mBundle.putInt(Constant.START_LON, mPinche.getStartLon());
		// 终点名称，经纬度
		mBundle.putString(Constant.S_END, mPinche.getEnd_name());
		mBundle.putInt(Constant.END_LAT, mPinche.getEndLat());
		mBundle.putInt(Constant.END_LON, mPinche.getEndLon());

		if (mPinche.getPoints() != null && mPinche.getPoints().length > 0)
			mBundle.putParcelableArray("points", mPinche.getPoints());// 途经点
		go2ActivityWithBundle(mContext, SearchWithMapActivity.class, mBundle);
	}

	/**
	 * @param userId
	 *            用户ID
	 * @return 用户头像ID的 URL
	 */
	public String getPhotoURL(int userId) {
		return Constant.URL_RESOURCE + "/userImg/" + userId + ".png";
	}

	public void go2OnLinePay(Context mContext, int price, String idName, String bookId,
			String startName, String endName, String url, SERVICE_TYPE type) {
		if (price <= 0) {
			showToast(mContext, mContext.getString(R.string.order_money_error));
			return;
		}
		final Bundle mBundle = new Bundle();
		mBundle.putInt("cost", price);
		Util.printLog("订单详情支付-num：" + price);
		mBundle.putString(Constant.BOOK_ID, bookId);
		mBundle.putString("start_name", startName);
		mBundle.putString("end_name", endName);
		mBundle.putString("id_name", idName);
		mBundle.putString("url", url);
		mBundle.putString("type", type.name());
		go2ActivityWithBundle(mContext, PayActivity.class, mBundle);

	}

	/**
	 * 跳转到订单详情
	 * 
	 * @param mContext
	 * @param bundle
	 */
	public void go2OrderDetail(Context mContext, Bundle bundle) {
		go2ActivityWithBundle(mContext, MyOrderDetailActivity.class, bundle);
	}

	/**
	 * 获取请求返回的数据是否有错误类型
	 * 
	 * @param jsonString
	 * @return null：表示没有错误
	 */
	public String getErrorType(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("success")) {
				boolean isSuccess = jsonObject.getBoolean("success");
				if (isSuccess) return null;
			}

			if (jsonObject.has("errorType")) {
				String errorType = jsonObject.getString("errorType");
				if (!TextUtils.isEmpty(errorType)) {
					return errorType;
				} else {
					return null;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			printLog("Util.getErrorType错误");
			return null;
		}
		return null;
	}

	/**
	 * 获取请求返回的数据错误信息
	 * 
	 * @param jsonString
	 * @return null：表示没有错误
	 */
	public String getErrorMsg(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("errorMsg")) {
				String errorMsg = jsonObject.getString("errorMsg");
				if (!TextUtils.isEmpty(errorMsg)) {
					return errorMsg;
				} else {
					return null;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			printLog("Util.getErrorType错误");
			return null;
		}
		return null;
	}

	public void showLoginAgainDailog(final Context mContext) {
		showDialog(mContext, mContext.getString(R.string.login_again),
				mContext.getString(R.string.login_now), mContext.getString(R.string.logout),
				new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							Bundle bundle = new Bundle();
							User user = Util.getInstance().getUser(mContext);
							setUser(mContext, null);// 清楚本地用户数据
							if (user != null && !TextUtils.isEmpty(user.getTel())) {
								bundle.putString(LoginActivity.PHONE, user.getTel());
							}
							Util.getInstance().go2ActivityWithBundle(mContext, LoginActivity.class,
									bundle);

						} else {
							CarSharingApplication.Instance().exit(mContext);
						}
					}
				});
	}

	/**
	 * 去重新登录
	 * 
	 * @param mContext
	 */
	public void go2LoginAgain(Context mContext) {
		showLoginAgainDailog(mContext);
	}

	/**
	 * 去掉定位，搜索地址中包含的"市"
	 * 
	 * @param add
	 * @return
	 */
	public String getEasyAddr(String add) {
		if (TextUtils.isEmpty(add)) return "";
		if (add.contains("市")) {
			int i = add.indexOf("市");
			return add.substring(i + 1, add.length());
		}
		return add;
	}

	public void logout(Context mContext) {
		setUser(mContext, null);// 清除登录用户
		clearCookie(mContext);// 清除cookie文件
		HttpUtil.clearCacheCookies();
		clearUser(mContext);
		setPushEnable(mContext, null);
		Constant.s_PinCheBi = -1;
		Constant.logout_swtch_to_home = true;
		Constant.is_refresh_orders = true;
	}

	/**
	 * 获取应用的缓存目录
	 * 
	 * @param mContext
	 * @return
	 */
	public File getCacheFile(Context mContext) {
		File cacheDir = null;
		if (isSDCardMounted())
			cacheDir = new File(Constant.CACHE);
		else cacheDir = mContext.getCacheDir();
		if (!cacheDir.exists()) cacheDir.mkdirs();
		return cacheDir;
	}

	/**
	 * decodes image and scales it to reduce memory consumption
	 * 
	 * @param f
	 * @param imageView
	 * @return
	 */
	public Bitmap decodeFile(File f, final ImageView imageView) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;// 服务器图片尺寸

			int size_height = imageView.getHeight();
			int size_width = imageView.getWidth();
			if (size_height <= 0 || size_width <= 0) {// 计算imageView宽度和高度
				int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				imageView.measure(w, h);
				size_height = imageView.getMeasuredHeight();
				size_width = imageView.getMeasuredWidth();
			}
			int scale = 1;

			while (true) {
				if (width_tmp / 2 < size_width || height_tmp / 2 < size_height) break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	/**
	 * HTTP 请求返回数据类型
	 * 
	 * @author zhiqiang
	 * 
	 */
	public static abstract class OnHttpRequestDataCallback {
		/**
		 * 请求成功数据
		 * 
		 * @param restult
		 */
		public abstract void onSuccess(String result);

		/**
		 * 请求失败　
		 * 
		 * @param result
		 */
		public void onFail(String errorType, String errorMsg) {
		};
	}

	/**
	 * LoadingDataDialog ,HttpRequestOnBackgrount处理返回数据
	 * 
	 * @param mContext
	 * @param jsonString
	 *            　json串
	 * @param isHandleError
	 *            是否自己处理错误
	 * @param onDataLoadingCallBack
	 */
	public void doHttpResult(Context mContext, String jsonString, boolean isHandleError,
			OnHttpRequestDataCallback onDataLoadingCallBack) {
		String errorType = getErrorType(jsonString);
		if (errorType != null) {// 有错误
			printLog("Util.doHttpResult:errorType:" + errorType);
			if (errorType.equals(JsonResult.ERRORTYPE_OTHER_DEVICE_LOGGED)// 如果是没有登录或者被人挤下去直接处理
					|| errorType.equals(JsonResult.ERRORTYPE_UN_LOGIN)) {
				setPushEnable(mContext, null);
				go2LoginAgain(mContext);
				return;
			} else {
				if (isHandleError) {// 自己处理
					onDataLoadingCallBack.onFail(errorType,
							Util.getInstance().getErrorMsg(jsonString));
				} else {// 默认处理
					showToast(mContext, mContext.getString(R.string.request_error));
				}
			}
		} else {// 没有错误就是成功的情况
			onDataLoadingCallBack.onSuccess(jsonString);
		}
	}

	/**
	 * 获取启动页
	 * 
	 * @param mContext
	 * @return
	 */
	public Bitmap getLoadingBitmap() {
		if (!isSDCardMounted()) return null;
		File file = new File(Constant.CACHE, "loading.jpg");
		if (!file.exists()) return null;
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			return BitmapFactory.decodeStream(new FileInputStream(file), null, o);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 检查是否loading页有更新
	 * 
	 * @param mContext
	 */
	public void checkLoadingPage(final Context mContext) {
		LoadPage loadPage = getLoadingInfo(mContext);
		String url = Constant.GET_LOADING_PAGE;
		if (loadPage != null) {
			if (!(loadPage.getUpdateTime() <= 0)) {
				url += "?lastUpdateTime=" + loadPage.getUpdateTime();
			}
		}
		printLog("检查loading页url：" + url);
		HttpRequestOnBackgrount httpRequestOnBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.GET, new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						printLog("loadingpage:" + result);
						JsonResult<LoadPage> lJsonResult = getObjFromJsonResult(result,
								new TypeToken<JsonResult<LoadPage>>() {
								});
						if (lJsonResult != null && lJsonResult.isSuccess()) {
							if (lJsonResult.getData() != null) {// 有新的loading页
								final LoadPage loadPage = lJsonResult.getData();
								if (!isSDCardMounted()) return;
								final File file = new File(getCacheFile(mContext), "loading.jpg");
								if (file.exists()) {// 如果有老的就删除
									file.delete();
									clearLoadingPageInfo(mContext);
								}
								saveLoadPageInfo(mContext, loadPage);
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										downLoadFile(loadPage.getImgUrl(), file);
									}
								}).start();
							}
						}
					}

					@Override
					public void onFail(String errorType, String errorMsg) {
						super.onFail(errorType, errorMsg);
					}
				}, null, mContext, true);
		httpRequestOnBackgrount.execute(url);
	}

	/**
	 * 保存loading页信息
	 * 
	 * @param mContext
	 * @param loadPage
	 *            LoadPage对象
	 */
	private void saveLoadPageInfo(Context mContext, LoadPage loadPage) {
		printLog("loadingpage保存信息:" + loadPage.toString());
		SharedPreferences sharedPreferences = getSharedPreferences(mContext, SP_LOADING_PAG);
		Editor editor = sharedPreferences.edit();
		editor.putString("url", loadPage.getUrl());
		editor.putString("imgUrl", loadPage.getImgUrl());
		editor.putLong("updateTime", loadPage.getUpdateTime());
		editor.commit();
	}

	/**
	 * 获取存储loading页信息
	 * 
	 * @param mContext
	 * @return
	 */
	public LoadPage getLoadingInfo(Context mContext) {
		SharedPreferences sharedPreferences = getSharedPreferences(mContext, SP_LOADING_PAG);
		LoadPage loadPage = new LoadPage();
		loadPage.setUpdateTime(sharedPreferences.getLong("updateTime", -1));
		loadPage.setImgUrl(sharedPreferences.getString("imgUrl", null));
		loadPage.setUrl(sharedPreferences.getString("url", null));
		return loadPage;
	}

	/**
	 * 清楚存储的loading页信息
	 * 
	 * @param mContext
	 */
	private void clearLoadingPageInfo(Context mContext) {
		SharedPreferences sharedPreferences = getSharedPreferences(mContext, SP_LOADING_PAG);
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	private void downLoadFile(String url, File file) {
		int totalSize = 0;// 下载文件大小
		int readSize = 0;
		byte buffer[] = new byte[4096];
		InputStream is = null;
		FileOutputStream out = null;
		HttpURLConnection downConnection = null;
		try {
			downConnection = (HttpURLConnection) new URL(url).openConnection();
			downConnection.setConnectTimeout(2000);
			downConnection.setConnectTimeout(10000);
			totalSize = downConnection.getContentLength();
			if (downConnection.getResponseCode() == 404) {
				Util.printLog("应用更新下载404");
				throw new Exception("fail!");
			}
			is = downConnection.getInputStream();
			out = new FileOutputStream(file);
			while ((readSize = is.read(buffer)) != -1) {
				totalSize -= readSize;
				if (totalSize <= 0) printLog("Util文件下载完成");
				out.write(buffer);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			printLog("Util文件下载失败");
			file.delete();
		} finally {
			try {
				is.close();
				out.close();
				downConnection.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 跳转到用户使用条款，如何获取拼车币
	 * 
	 * @param mContext
	 */
	public void go2StaticHtmlPage(Context mContext, String path, String title) {
		Bundle bundle = new Bundle();
		bundle.putString("path", path);
		bundle.putString("title", title);
		go2ActivityWithBundle(mContext, UseClauseActivity.class, bundle);
	}

	/**
	 * 分享APP获取分享线路后获取奖励
	 * 
	 * @param mContext
	 * @param type
	 *            :SHARE_INFO|SHARE_APP
	 */
	public void getSharePrise(final Context mContext, String type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("type", type));
		doPostRequest(mContext, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					showToast(mContext, "成功获得分享奖励");
				}
			}
		}, params, Constant.GET_SHARE_PRISE, "获取分享奖励...", false);
	}
}