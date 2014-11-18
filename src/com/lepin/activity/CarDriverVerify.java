/**
 * Project Name:PinCheBang_2
 * File Name:CarDriverVerify.java
 * Package Name:com.lepin.activity
 * Date:2014年9月9日上午10:52:01
 * Copyright (c) 2014, chenzhou1025@126.com All Rights Reserved.
 *
 */
/**
 * Date:2014年9月9日上午10:52:01
 * Copyright (c) 2014, wxh All Rights Reserved.
 */

package com.lepin.activity;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.Car;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpUtil;
import com.lepin.util.Util;
import com.lepin.widget.MyProgress;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

/**
 * ClassName:CarDriverVerify <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2014年9月9日 上午10:52:01 <br/>
 * @author   {author wangxiaohong}
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
/**
 * @description TODO
 */
@Contextview(R.layout.driver_vefic)
public class CarDriverVerify extends Activity implements OnClickListener {
	@ViewInject(id = R.id.common_title_back)
	private ImageView vfBack;// 返回

	@ViewInject(id = R.id.common_title_title)
	private TextView vfTitle;// 标题

	@ViewInject(id = R.id.driving_licence_original)
	private ImageView mDrivingLicenceOriginal;// 驾驶证正本

	@ViewInject(id = R.id.driving_licence_copy)
	private ImageView mDrivingLicenceCopy;// 驾驶证副本

	@ViewInject(id = R.id.vehicle_licence_original)
	private ImageView mVehicleLicenceOriginal;// 行驶证正本

	@ViewInject(id = R.id.vehicle_licence_copy)
	private ImageView mVehicleLicenceCopy;// 行驶证副本

	@ViewInject(id = R.id.driving_licence_original_progressbar)
	private MyProgress mDrivingLicenceoriginalProgressbar;// 驾驶证正本上传进度条

	@ViewInject(id = R.id.driving_licence_copy_progressbar)
	private MyProgress mDrivingLicencecopyProgressbar;// 驾驶证副本上传进度条

	@ViewInject(id = R.id.vehicle_licence_original_progressbar)
	private MyProgress mVehicleLicenceoriginalProgressbar;// 行驶证正本上传进度条

	@ViewInject(id = R.id.vehicle_licence_copy_progressbar)
	private MyProgress mVehicleLicenceCopyProgressbar;// 行驶证副本上传进度条

	@ViewInject(id = R.id.driver_vefic_submit)
	private TextView mBtnDriverVerify;// 立即验证

	private static final int CAMERA_RESULT = 1;
	private static final int IMAGE_RESULT = 2;

	private Util util = Util.getInstance();
	private Uri imageUri;
	private int i;
	private String[] pathString = new String[4];
	ImageView[] imageViews = new ImageView[4];
	MyProgress[] progressBars = new MyProgress[4];
	private String[] PhoteName = { "driving_licence_original.jpg", "driving_licence_copy.jpg",
			"vehicle_licence_original.jpg", "vehicle_licence_copy.jpg" };
	private int[] Drawables = { R.drawable.driving_licence_original,
			R.drawable.driving_licence_copy, R.drawable.vehicle_license_original,
			R.drawable.vehicle_license_copy };
	private String[] Mode = { "driverLicenseOriginal", "driverLicenseCopy",
			"drivingLicenseOriginal", "drivingLicenseCopy" };

	/* 存放访问网络图片的url */
	private String[] urls = new String[4];
	private String[] image;
	/**
	 * path:TODO 图片路径
	 */
	private String path = null;

	private String carId;
	private String state = null;

	// 点击了提交图片后要等待 通知后台后才能 退出
	// private Boolean flag = true;

	Bitmap[] bitmaps = new Bitmap[4];

	private int count = 4;

	/**
	 * TODO
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		// 得到车辆ID
		carId = String.valueOf(this.getIntent().getIntExtra("CarId", 0));
		state = this.getIntent().getStringExtra("state");
		this.vfBack.setOnClickListener(this);
		this.vfTitle.setText(this.getString(R.string.driver_vefic_title));
		// 四个图片选择框 和 立即提交按钮
		mDrivingLicenceOriginal.setOnClickListener(this);
		mDrivingLicenceCopy.setOnClickListener(this);
		mVehicleLicenceOriginal.setOnClickListener(this);
		mVehicleLicenceCopy.setOnClickListener(this);
		mBtnDriverVerify.setOnClickListener(this);
		image = getResources().getStringArray(R.array.get_image_way);
		// 控件放入到数组中
		imageViews[0] = mDrivingLicenceOriginal;
		imageViews[1] = mDrivingLicenceCopy;
		imageViews[2] = mVehicleLicenceOriginal;
		imageViews[3] = mVehicleLicenceCopy;
		progressBars[0] = mDrivingLicenceoriginalProgressbar;
		progressBars[1] = mDrivingLicencecopyProgressbar;
		progressBars[2] = mVehicleLicenceoriginalProgressbar;
		progressBars[3] = mVehicleLicenceCopyProgressbar;

		// /logged/driverLicenseOriginal/27.jpg
		for (int i = 0; i < urls.length; i++) {
			urls[i] = Constant.URL_LOCAL + "/logged/" + Mode[i] + "/" + carId + ".jpg";
		}
		// 如果SD可用，获得存放头像图片的SD卡路径 临时用
		pathString = new String[4];

		for (int i = 0; i < PhoteName.length; i++) {
			if (Util.getInstance().isSDCardMounted()) {
				// if (!Util.getInstance().existFile(PhoteName[i])) {
				// pathString[i] =
				// Util.getInstance().createOrGetFilePath(PhoteName[i],
				// CarDriverVerify.this);
				pathString[i] = util.getPath(carId, PhoteName[i], CarDriverVerify.this);
				// }
				bitmaps[i] = Util.getInstance().getBitmap(urls[i], pathString[i],
						CarDriverVerify.this, imageViews[i]);
				if (bitmaps[i] != null) imageViews[i].setImageBitmap(bitmaps[i]);
			}
		}
	}

	/**
	 * TODO
	 */
	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub
		if (v == vfBack) {
			// if (flag)
			CarDriverVerify.this.finish();
			// else Util.showToast(CarDriverVerify.this,
			// getString(R.string.driver_verify_nofity_is_finish_tips));

		} else {
			if (null != state
					&& (state.equals(Car.STATE_AUDITING) || state.equals(Car.STATE_AUDITED))) {
				Util.showToast(CarDriverVerify.this,
						getResources().getString(R.string.car_verifying_tip));
			} else {
				if (v == mBtnDriverVerify) {
					submitForVerify();
				} else {
					if (v == mDrivingLicenceOriginal) {
						i = 0;
					} else if (v == mDrivingLicenceCopy) {
						i = 1;
					} else if (v == mVehicleLicenceOriginal) {
						i = 2;
					} else if (v == mVehicleLicenceCopy) {
						i = 3;
					}
					selectImage();
				}
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @author {author wangxiaohong}
	 */
	private void submitForVerify() {
		// 首先监测网络状况，如果是在WIFI情况下直接上传，如果是在2G/3G情况下弹出其实框
		if (util.getNetType(CarDriverVerify.this) == Util.NET_TYPE_2G_OR_3G) {
			Util.getInstance().showDialog(CarDriverVerify.this,
					getString(R.string.car_verify_net_tips),
					getString(R.string.car_verify_net_tips_yes),
					getString(R.string.car_verify_net_tips_no), new OnOkOrCancelClickListener() {
						@Override
						public void onOkClick(int type) {
							if (type == PcbConfirmDialog.CANCEL) {
								return;
							} else {
								uploadImageAndNoity();
							}
						}
					});
		} else {
			uploadImageAndNoity();
		}
	}

	void uploadImageAndNoity() {

		// flag = false;
		// 是否四张验证图片都与示例图片不同
		for (int j = 0; j < imageViews.length; j++) {
			if (imageViews[j].getDrawable().getConstantState()
					.equals(getResources().getDrawable(Drawables[j]).getConstantState())) {
				String[] show_tip = getResources().getStringArray(R.array.show_licence_tip);
				Toast.makeText(CarDriverVerify.this, show_tip[j], Toast.LENGTH_SHORT).show();
				return;
			}
		}

		// 进度条可见 图片不可点击
		for (int i = 0; i < progressBars.length; i++) {
			progressBars[i].setClickable(false);
			imageViews[i].setClickable(false);
		}

		// 获得上传图片路径
		String[] path = new String[4];
		for (int i = 0; i < path.length; i++) {
			// path[i] = Util.getInstance().createOrGetFilePath(PhoteName[i],
			// CarDriverVerify.this);
			path[i] = util.getPath(carId, PhoteName[i], CarDriverVerify.this);
		}

		// 上传图片
		for (int i = 0; i < progressBars.length; i++) {
			new FileUploadAsyncTask(CarDriverVerify.this, progressBars[i])
					.execute(path[i], Mode[i]);
		}

		// 通知验证
		// new Timer().schedule(new TimerTask() {
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// notifyVerify();
		// }
		// }, 5000);

	}

	void notifyVerify() {
		if (progressBars[0].getProgress() == progressBars[0].getMax()
				&& progressBars[1].getProgress() == progressBars[1].getMax()
				&& progressBars[2].getProgress() == progressBars[2].getMax()
				&& progressBars[3].getProgress() == progressBars[3].getMax()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("carId", String.valueOf(carId)));
			Looper.prepare();
			// util.doPostRequest
			String result = HttpUtil.post(params, Constant.URL_CARVERIFICATION,
					CarDriverVerify.this);
			if (!util.isNullOrEmpty(result)) {
				JsonResult<String> Result = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (Result.isSuccess()) {
					Util.showToast(
							CarDriverVerify.this,
							getResources().getString(
									R.string.driver_verify_nofity_upload_success_tips));
				} else {
					Util.showToast(CarDriverVerify.this, Result.getErrorMsg().toString());
				}
			}
			Looper.loop();
		}

	}

	/**
	 * TODO 通过拍照或者图册获得照片
	 * 
	 * @author {author wangxiaohong}
	 */
	private void selectImage() {
		// TODO Auto-generated method stub
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			Util.showToast(this, getResources().getString(R.string.check_sd));
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(CarDriverVerify.this);
		builder.setTitle(R.string.pick_image).setItems(image,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (image[which].equals(getString(R.string.my_data_image_way_photo))) {
							getImageByPhoto();
						} else {
							getImageByGallery();
						}
					}
				});
		builder.create().show();
	}

	private void getImageByPhoto() {
		path = util.getPath(carId, PhoteName[i], CarDriverVerify.this);
		imageUri = Uri.fromFile(new File(path));
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, CAMERA_RESULT);
	}

	private void getImageByGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/jpeg");
		startActivityForResult(intent, IMAGE_RESULT);
	}

	/**
	 * TODO
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) return;
		Bitmap bitmap = null;
		if (requestCode == CAMERA_RESULT) {
			path = util.getPath(carId, PhoteName[i], CarDriverVerify.this);
			bitmap = Util.getInstance().getSmallBitmap(CarDriverVerify.this, path);
			boolean flag = Util.getInstance().save(CarDriverVerify.this, path, bitmap);
		} else if (requestCode == IMAGE_RESULT) {
			Uri selectedImage = data.getData();
			if (!String.valueOf(selectedImage).startsWith("content:")) {
				Util.showToast(CarDriverVerify.this, "请从图库中选择再试！");
			} else {
				path = Util.getInstance().getImagePath(CarDriverVerify.this, selectedImage);
				bitmap = Util.getInstance().getSmallBitmap(CarDriverVerify.this, path);
				Util.getInstance().save(CarDriverVerify.this, pathString[i], bitmap);
			}
		}
		if (null != bitmap) {
			imageViews[i].setImageBitmap(bitmap);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		for (int i = 0; i < bitmaps.length; i++) {
			if (null != bitmaps[i]) bitmaps[i].recycle();
		}
	}

	private class FileUploadAsyncTask extends AsyncTask<String, Integer, String> {
		private Context context;
		private MyProgress myProgressbar;
		private long totalSize;

		public FileUploadAsyncTask(Context context, MyProgress pb) {
			this.context = context;
			this.myProgressbar = pb;
		}

		@Override
		protected void onPreExecute() {
			myProgressbar.setVisibility(View.VISIBLE);
			myProgressbar.setText(0);
		}

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
			totalSize = httpEntity.getContentLength();
			ProgressOutHttpEntity progressHttpEntity = new ProgressOutHttpEntity(httpEntity,
					new ProgressListener() {
						@Override
						public void transferred(long transferedBytes) {
							publishProgress((int) (100 * transferedBytes / totalSize));
						}
					});
			return uploadFile(params[0], params[1], progressHttpEntity);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			myProgressbar.setProgress((int) (progress[0]));
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
		}

		/**
		 * 上传文件到服务器
		 * 
		 * @param url
		 *            服务器地址
		 * @param entity
		 *            文件
		 * @return
		 */
		public String uploadFile(String path, String mode, ProgressOutHttpEntity entity) {
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			// 设置连接超时时间
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			HttpPost httpPost = new HttpPost(Constant.URL_VERIFY_DRIVER);
			try {
				httpPost.setEntity(entity);
				// httpPost.setEntity(new UrlEncodedFormEntity(params,
				// "UTF-8"));
				HttpResponse httpResponse = httpClient.execute(httpPost, HttpUtil.httpContext);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					count--;
					if (count <= 0) {
						// 提交数据
						notifyVerify();
						count = 4;
					}
					return "图片上传成功";
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (httpClient != null && httpClient.getConnectionManager() != null) {
					httpClient.getConnectionManager().shutdown();
				}
			}
			return "图片上传失败";
		}
	}
}

class ProgressOutHttpEntity extends HttpEntityWrapper {

	private final ProgressListener listener;

	public ProgressOutHttpEntity(final HttpEntity entity, final ProgressListener listener) {
		super(entity);
		this.listener = listener;
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		CountingOutputStream(final OutputStream out, final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		@Override
		public void write(final byte[] b, final int off, final int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}

		@Override
		public void write(final int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}

	}

	@Override
	public void writeTo(final OutputStream out) throws IOException {
		this.wrappedEntity.writeTo(out instanceof CountingOutputStream ? out
				: new CountingOutputStream(out, this.listener));
	}
}

/**
 * 进度监听器接口
 */
interface ProgressListener {
	public void transferred(long transferedBytes);
}
