package com.lepin.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpUtil;
import com.lepin.util.Util;
import com.lepin.widget.CircleImageView;

@Contextview(R.layout.my_personal_centre_activity)
public class MyPersonalCentreActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitleTextView;

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBackBtn;

	// 头像
	@ViewInject(id = R.id.my_picture)
	private CircleImageView mypicture;

	// 昵称
	@ViewInject(id = R.id.personal_info_nick_name)
	private TextView mNickName;

	// 认证消息
	@ViewInject(id = R.id.my_personal_centre_indentification)
	private ImageView mIndentificationMark;

	// 我的信息
	@ViewInject(id = R.id.my_info_layout)
	private RelativeLayout mMyInfoLayout;

	@ViewInject(id = R.id.image, parentId = R.id.my_info_layout)
	private ImageView mMyInfoImageView;

	@ViewInject(id = R.id.text, parentId = R.id.personal_info_nick_sex_layout)
	private TextView mMyInfoTextView;

	// 我的余额
	@ViewInject(id = R.id.my_balance_layout)
	private RelativeLayout mMyBalanceLayout;

	@ViewInject(id = R.id.image, parentId = R.id.my_balance_layout)
	private ImageView mMyBalanceImageView;

	@ViewInject(id = R.id.text, parentId = R.id.my_balance_layout)
	private TextView mMyBalanceTextView;

	// 我的拼车币
	@ViewInject(id = R.id.my_pinche_coin_layout)
	private RelativeLayout mMyPincheCoinLayout;

	@ViewInject(id = R.id.image, parentId = R.id.my_pinche_coin_layout)
	private ImageView mMyPincheCoinImageView;

	@ViewInject(id = R.id.text, parentId = R.id.my_pinche_coin_layout)
	private TextView mMyPincheCoinTextView;

	// 常用拼车地点
	@ViewInject(id = R.id.my_commonly_used_address_layout)
	private RelativeLayout mMyCommonlyUsedAddressLayout;

	@ViewInject(id = R.id.image, parentId = R.id.my_commonly_used_address_layout)
	private ImageView mMyCommonlyUsedAddressImageView;

	@ViewInject(id = R.id.text, parentId = R.id.my_commonly_used_address_layout)
	private TextView mMyCommonlyUsedAddressTextView;

	// 我的爱车
	@ViewInject(id = R.id.my_love_car_layout)
	private RelativeLayout mMyLoveCarLayout;

	@ViewInject(id = R.id.image, parentId = R.id.my_love_car_layout)
	private ImageView mMyLoveCarImageView;

	@ViewInject(id = R.id.text, parentId = R.id.my_love_car_layout)
	private TextView mMyLoveCarTextView;

	private String[] image;
	public final static String S_PINCHEMONEY = "pinchemoney";
	private User user = null;

	private static final int CAMERA_RESULT = 1;
	private static final int IMAGE_RESULT = 2;

	private String url = null;
	/**
	 * path:TODO 图片文件路径
	 */
	String path;
	private Uri imageUri;
	private String mPhotoName = "image_portrait.jpg";
	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		user = Util.getInstance().getUser(MyPersonalCentreActivity.this);
		setOnClick();
		image = getResources().getStringArray(R.array.get_image_way);
		url = util.getPhotoURL(user.getUserId());
		Util.printLog("头像:" + url);
		mypicture.displayWithUrl(url, false, false);
	}

	private void setOnClick() {
		mypicture.setOnClickListener(this);
		mNickName.setOnClickListener(this);
		mMyInfoLayout.setOnClickListener(this);
		mMyBalanceLayout.setOnClickListener(this);
		mMyPincheCoinLayout.setOnClickListener(this);
		mMyCommonlyUsedAddressLayout.setOnClickListener(this);
		mMyLoveCarLayout.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mBackBtn) {// 返回
			MyPersonalCentreActivity.this.finish();
		} else if (v == mypicture) {
			selectImage();
		} else if (v == mMyInfoLayout) {
			util.go2Activity(MyPersonalCentreActivity.this, PersonalInfoActivity.class);
		} else if (v == mMyBalanceLayout) {// 我的余额
			Util.getInstance().go2Activity(this, MyBalanceActivity.class);
		} else if (v == mMyPincheCoinLayout) {
			util.go2Activity(MyPersonalCentreActivity.this, MyPinCheMoneyActivity.class);
		} else if (v == mMyCommonlyUsedAddressLayout) {// 常用拼车地点
			Util.getInstance().go2Activity(this, MyCommonAddressActivity.class);
		} else if (v == mMyLoveCarLayout) {
			if (user.getCar() != null) {
				Intent intent = new Intent(MyPersonalCentreActivity.this, MyLoveCarActivity.class);
				intent.putExtra("from_perinfo", "from_perinfo");
				startActivity(intent);
			} else {
				util.go2Activity(MyPersonalCentreActivity.this, AddNewCarActivity.class);
			}

		}
	}

	/**
	 * TODO 通过拍照或者图册获得照片
	 * 
	 * @author {author wangxiaohong}
	 */
	private void selectImage() {
		// TODO Auto-generated method stub
		if (!Util.getInstance().isSDCardMounted()) {
			Util.showToast(this, getResources().getString(R.string.check_sd));
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(MyPersonalCentreActivity.this);
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

	private void getImageByGallery() {
		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/jpeg");
		startActivityForResult(intent, IMAGE_RESULT);
	}

	private void getImageByPhoto() {
		path = util.getPath(user.getUserId() + "", mPhotoName, MyPersonalCentreActivity.this);
		imageUri = Uri.fromFile(new File(path));
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, CAMERA_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) return;
		Bitmap bitmap = null;
		if (requestCode == CAMERA_RESULT) {
			bitmap = util.getSmallBitmap(MyPersonalCentreActivity.this, path);
		    util.save(MyPersonalCentreActivity.this, path, bitmap);
		} else if (requestCode == IMAGE_RESULT) {
			Uri selectedImage = data.getData();
			path = util.getImagePath(MyPersonalCentreActivity.this, selectedImage);
			bitmap = util.getSmallBitmap(MyPersonalCentreActivity.this, path);
			String pcbPathString = util.getPath(user.getUserId() + "", mPhotoName,
					MyPersonalCentreActivity.this);
			;
			util.save(MyPersonalCentreActivity.this, pcbPathString, bitmap);
			path = pcbPathString;
		}
		if (null != bitmap) {
			mypicture.setImageBitmap(bitmap);
			HttpUtil.uploadFile(MyPersonalCentreActivity.this, path, "userImg",
					Constant.URL_VERIFY_DRIVER);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		user = Util.getInstance().getLoginUser(MyPersonalCentreActivity.this);
		setData2View();
	}

	private void setData2View() {
		// TODO Auto-generated method stub
		mTitleTextView.setText(getString(R.string.my_personal_centre_title));
		mNickName.setText(user.getUsername(MyPersonalCentreActivity.this));
		// 已经验证才显示Ｖ图标
		if (user != null && user.isUserStateVerify()) {
			mIndentificationMark.setVisibility(View.VISIBLE);
		}

		mMyInfoImageView.setImageResource(R.drawable.my_info);
		mMyInfoTextView.setText(R.string.my_info);

		mMyBalanceImageView.setImageResource(R.drawable.my_balance);
		mMyBalanceTextView.setText(R.string.my_balance);

		mMyPincheCoinImageView.setImageResource(R.drawable.my_pinche_coin);
		mMyPincheCoinTextView.setText(R.string.my_pinche_coin);

		mMyCommonlyUsedAddressImageView.setImageResource(R.drawable.my_commonly_used_address);
		mMyCommonlyUsedAddressTextView.setText(R.string.my_commonly_used_address);

		mMyLoveCarImageView.setImageResource(R.drawable.my_love_car);
		mMyLoveCarTextView.setText(R.string.my_love_car);
	}

	protected void updateUser(User uUser) {
		Util.getInstance().setUser(this, uUser);
		Util.getInstance().setPushEnable(this, uUser);

	}

	class MyDatePickerDialog extends DatePickerDialog {

		public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year,
				int monthOfYear, int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);
		}

		@Override
		protected void onStop() {
			// super.onStop();
		}
	}
}
