package com.lepin.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lepin.activity.R;
import com.lepin.entity.JsonResult;
import com.lepin.entity.User;
import com.lepin.loadimage.MemoryCache;

/**
 * Http请求工具类
 * 
 */
/** 请求URL */
public class HttpUtil {
	public static Map<String, String> cookieStore = new HashMap<String, String>();
	public static DefaultHttpClient httpClient = new DefaultHttpClient();
	public static BasicHttpContext httpContext = new BasicHttpContext();
	public static MBasicCookieStore bcs = null;
	public final static int TIME_OUT_DELAY = 10000;// 请求超时时间

	/**
	 * 清空本地Cookies
	 */
	public static void clearCacheCookies() {
		cookieStore.clear();
	}

	public static class MBasicCookieStore extends BasicCookieStore {
		private Context mContext;

		public MBasicCookieStore(Context mContext) {
			super();
			this.mContext = mContext;
		}

		public synchronized void addCookies(Cookie[] cookies) {
			super.addCookies(cookies);
			if (cookies == null) return;
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				String val = cookie.getValue();
				if (name.equals("signCiphertext") || name.equals("scti")) {
					cookieStore.put(name, val);
				}
				Util.getInstance().saveCookies(cookieStore, mContext);// 保存cookie
			}
		};

		public synchronized void addCookie(Cookie cookie) {
			super.addCookie(cookie);
			if (cookie == null) return;
			String name = cookie.getName();
			String val = cookie.getValue();
			if (name.equals("signCiphertext") || name.equals("scti")) {
				cookieStore.put(name, val);
			}
			Util.getInstance().saveCookies(cookieStore, mContext);// 保存cookie
		};
	}

	// 初始化 cookie 支持机制
	public static void init(Context mContext) {
		bcs = new MBasicCookieStore(mContext);
		// 接口版本
		Constant.deviceKey = Util.getInstance().getDeviceID(mContext);
		BasicClientCookie pcbVersion = new BasicClientCookie("pcbVersion",
				String.valueOf(Constant.PCB_VERSION));
		pcbVersion.setDomain(Constant.HOST);
		// 设备码
		BasicClientCookie deviceKey = new BasicClientCookie("deviceKey", Constant.deviceKey);
		deviceKey.setDomain(Constant.HOST);

		// 设备类型
		BasicClientCookie type = new BasicClientCookie("type", Constant.REQUEST_TYPE);
		type.setDomain(Constant.HOST);

		initCookieStore(mContext);
		bcs.addCookie(pcbVersion);
		bcs.addCookie(deviceKey);

		if (!TextUtils.isEmpty(cookieStore.get("signCiphertext"))) {
			// signCiphertext 和 scti 从 SharedPreferences 获取密文
			BasicClientCookie signCiphertext = new BasicClientCookie("signCiphertext",
					cookieStore.get("signCiphertext"));
			signCiphertext.setDomain(Constant.HOST);
			bcs.addCookie(signCiphertext);
		}

		if (!TextUtils.isEmpty(cookieStore.get("scti"))) {
			// 密文编号
			BasicClientCookie scti = new BasicClientCookie("scti", cookieStore.get("scti"));
			scti.setDomain(Constant.HOST);
			bcs.addCookie(scti);
		}

		bcs.addCookie(type);
		httpContext.setAttribute(ClientContext.COOKIE_STORE, bcs);
		setTimeout();
	}

	/**
	 * HttpGet请求
	 * 
	 * @param params
	 *            请求参数
	 * @return
	 */
	public static String get(String url, Context mContext) {
		Util.printLog("get url:" + url);
		HttpGet httpGet = new HttpGet(url);
		return getResponseStr(httpGet, mContext);
	}

	/**
	 * HttpPost请求
	 * 
	 * @param params
	 *            请求参数
	 * @return
	 */
	public static String post(List<NameValuePair> params, String url, Context mContext) {
		Util.printLog("post url:" + url);
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			return getResponseStr(httpPost, mContext);
		} catch (UnsupportedEncodingException e) {
			// return Constant.E_404;
			Util.printLog("HttpUtil:不支持编码");
			return null;
		}
	}

	/**
	 * 用于文件上传
	 * 
	 * @param url
	 * @param mContext
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String uploadFileWithpost(String url, Context mContext, HttpEntity entity) {
		Util.printLog("开始上传");
		try {
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			// 设置连接超时时间
			// httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			// 5000);
			HttpPost upPost = new HttpPost(url);
			upPost.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(upPost, httpContext);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return mContext.getString(R.string.upload_success);
			}
		} catch (Exception e) {
			Util.printLog("上传报错");
			e.printStackTrace();
		}
		Util.printLog("上传成功");
		return mContext.getString(R.string.upload_fail);
	}

	/**
	 * 获取请求响应结果
	 * 
	 * @param request
	 * @return
	 */

	private static String getResponseStr(HttpUriRequest request, Context mContext) {
		String result = "";
		try {
			HttpResponse httpResponse = httpClient.execute(request, httpContext);
			// 得到响应的字符串
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			} else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_GATEWAY_TIMEOUT) {// 超时
				Util.printLog("HttpUtil 123:request time out");
				// result = Constant.E_504;
				return null;
			}

		} catch (ConnectTimeoutException e) {// 链接超时
			e.printStackTrace();
			Util.printLog("HttpUtil:链接超时");
			// result = Constant.E_504;
		} catch (ParseException e) {// 解析返回数据出错
			// TODO Auto-generated catch block
			e.printStackTrace();
			Util.printLog("HttpUtil:解析返回数据出错");
		} catch (IOException e) {// 读取数据出错
			// TODO Auto-generated catch block
			e.printStackTrace();
			Util.printLog("HttpUtil:读取请求数据出错");
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private static void setTimeout() {
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, TIME_OUT_DELAY);// 请求超时
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				TIME_OUT_DELAY);// 连接超时
	}

	/**
	 * 初始化Cookie,从文件中读取cookie
	 */
	private static void initCookieStore(Context mContext) {
		if (cookieStore.isEmpty()) {// 第一次，从缓存文件中获取
			cookieStore = new HashMap<String, String>();
			Map<String, ?> map = Util.getInstance().getCookiesFromFile(mContext);
			if (map != null) {
				Set<String> keys = map.keySet();
				for (String key : keys) {
					Object val = map.get(key);
					if (("signCiphertext".equals(key)) || ("scti".equals(key))) {
						// Util.printLog("cookies1:key:" + key + "--val:" +
						// val);
						cookieStore.put(key, (String) val);
					}
				}
			}
		}
	}

	public static <T> JsonResult<T> getJsonBean(String json, TypeToken<T> token) {
		Gson gson = new GsonBuilder().create();
		JsonResult<T> result = gson.fromJson(json, token.getType());
		return result;
	}

	/**
	 * 网络检测用户是否登录
	 * 
	 * @return 如果登录了返回登录用户，否则返回null
	 */
	public static User checkUserIsLogin(String url, Context mContext) {
		User user = null;
		String response = get(url, mContext);
		if (!TextUtils.isEmpty(response)) {
			TypeToken<JsonResult<User>> token = new TypeToken<JsonResult<User>>() {
			};
			JsonResult<User> jsonResult = Util.getInstance().getObjFromJsonResult(response, token);
			if (jsonResult != null && jsonResult.isSuccess()) {
				user = jsonResult.getData();
			} else {
				Util.getInstance().clearUser(mContext);// 清空本地数据
			}
		}
		return user;
	}

	/**
	 * 主要用于WebView 的 cookie 与接口的cookie同步，
	 * 
	 * @param url
	 */
	public static void syncCookie(String url) {
		// Gets the singleton CookieManager instance.
		// If this method is used before the application instantiates a WebView
		// instance,
		// CookieSyncManager.createInstance(Context) must be called first.
		// CookieSyncManager.createInstance(getApplication());//
		CookieManager cm = CookieManager.getInstance();
		List<Cookie> cookies = bcs.getCookies();
		String domain = null;
		String path = null;
		if (cookies != null && cookies.size() > 0) {
			for (Cookie cookie : cookies) {
				if (domain == null) {
					domain = cookie.getDomain();
				}
				if (path == null || path.length() == 0) {
					path = cookie.getPath();
				}
				if (domain != null && path != null && path.length() > 0) {
					break;
				}
			}
			if ((domain != null && path != null) || (path != null && path.length() > 0)) {
				for (Cookie cookie : cookies) {
					cm.setCookie(url, cookie.getName() + "=" + cookie.getValue() + ";domain="
							+ domain + ";path=" + path);
				}
				CookieSyncManager.getInstance().sync();
			} else {
				Log.w("syncCookie", "domain='" + domain + "' or path='" + path + "' is empty");
			}

		}
		CookieSyncManager.getInstance().sync();
	}

	/**
	 * 下载图片
	 * 
	 * @param imgURL
	 * @return
	 * @throws Exception
	 */
	public static Bitmap downloadImage(String imgURL) throws Exception {
		Util.printLog("图片下载路径:" + imgURL);
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			HttpPost downloadPost = new HttpPost(imgURL);
			HttpResponse httpResponse = httpClient.execute(downloadPost, httpContext);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得相关信息 取得HttpEntiy
				HttpEntity httpEntity = httpResponse.getEntity();
				// 获得一个输入流
				is = httpEntity.getContent();
			}
			if (is != null) {
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			}
		} catch (Exception e) {
			throw e;
		}
		return bitmap;
	}

	/**
	 * 下载图片 conn方式
	 * 
	 * @param imgURL
	 * @return
	 * @throws Exception
	 */
	public static Bitmap downloadImage_conn(String imgURL) throws Exception {
		Bitmap bitmap = null;
		ByteArrayOutputStream bos = null; // 内存操作流
		try {
			URL url = new URL(imgURL);
			bos = new ByteArrayOutputStream();
			byte buffer[] = new byte[1024];
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			List<Cookie> cookies = HttpUtil.bcs.getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.size(); i++) {
					String key = String.valueOf(cookies.get(i).getName());
					String value = String.valueOf(cookies.get(i).getValue());
					Util.printLog(key + "=" + value);
					conn.addRequestProperty("Cookie", key + "=" + value);
				}
			}
			conn.connect();
			OutputStream osOutputStream = conn.getOutputStream();
			DataOutputStream dos = new DataOutputStream(osOutputStream);
			dos.close();
			InputStream input = conn.getInputStream();
			int len = -1;
			while ((len = input.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			byte[] data = bos.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			// bitmap = HttpUtil.downloadImage(imgURL);
		} catch (Exception e) {
			throw e;
		} finally {
			if (bos != null) {
				bos.close();
			}
		}
		return bitmap;
	}

	/**
	 * @param mContext
	 * @param path
	 *            上传图片所在的本地路径
	 * @param modelValue
	 *            model 字段
	 * @param url
	 *            要上传的到url
	 */
	public static void uploadFile(Context mContext, String path, String modelValue, String url) {
		new PhotoUploadAsyncTask(mContext).execute(path, modelValue, url);
	}

	/**
	 * 从ＳＤ卡取图片，没有再从网络下载
	 * 
	 * @param url
	 * @param imageView
	 * @return
	 */
	public static Bitmap getBitmap(String url, File f, ImageView imageView, boolean isSave,
			MemoryCache memoryCache) {
		// from SD cache
		if (isSave) {
			Bitmap b = Util.getInstance().decodeFile(f, imageView);
			if (b != null) return b;
		}
		if (!Util.getInstance().isNetworkAvailable(imageView.getContext())) return null;
		// download image
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Util.getInstance().copyStream(is, os);// 是否存到sd卡
			os.close();
			bitmap = Util.getInstance().decodeFile(f, imageView);
			is.close();
			if (!isSave) f.delete();// 删除图片
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError) memoryCache.clear();
			return null;
		}
	}

}

class PhotoUploadAsyncTask extends AsyncTask<String, Integer, String> {
	// private String url = "http://192.168.83.213/receive_file.php";
	private Context context;

	public PhotoUploadAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		// 保存需上传文件信息
		MultipartEntityBuilder entitys = MultipartEntityBuilder.create();
		entitys.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		entitys.setCharset(Charset.forName(HTTP.UTF_8));
		File file = new File(params[0]);
		entitys.addPart("image", new FileBody(file));
		entitys.addTextBody("model", params[1]);
		HttpEntity httpEntity = entitys.build();
		return HttpUtil.uploadFileWithpost(params[2], context, httpEntity);
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
	}

	@Override
	protected void onPostExecute(String result) {
		Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
	}

}
