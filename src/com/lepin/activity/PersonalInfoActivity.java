package com.lepin.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.TimeUtils;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

@Contextview(R.layout.personal_info_activity)
public class PersonalInfoActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitleTextView;

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBackBtn;

	// 昵称
	@ViewInject(id = R.id.personal_info_nickname_layout)
	private View mNickNameLayout;

	@ViewInject(id = R.id.text_pic_item_pic, parentId = R.id.personal_info_nickname_layout)
	private ImageView mNickNamePic;

	@ViewInject(id = R.id.text_pic_item_left_text, parentId = R.id.personal_info_nickname_layout)
	private TextView mNickNameTitle;

	@ViewInject(id = R.id.text_pic_item_right_text, parentId = R.id.personal_info_nickname_layout)
	private TextView mNickName;

	// 性别
	@ViewInject(id = R.id.personal_info_nick_sex_layout)
	private View mSexLayout;

	@ViewInject(id = R.id.text_pic_item_pic, parentId = R.id.personal_info_nick_sex_layout)
	private ImageView mSexPic;

	@ViewInject(id = R.id.text_pic_item_left_text, parentId = R.id.personal_info_nick_sex_layout)
	private TextView mSexTitle;

	@ViewInject(id = R.id.text_pic_item_right_text, parentId = R.id.personal_info_nick_sex_layout)
	private TextView mSex;

	// 电话
	@ViewInject(id = R.id.text_pic_item_left_text, parentId = R.id.personal_info_nick_phone_layout)
	private TextView mPhoneTitle;

	@ViewInject(id = R.id.text_pic_item_pic, parentId = R.id.personal_info_nick_phone_layout)
	private ImageView mPhonePic;

	@ViewInject(id = R.id.text_pic_item_right_text, parentId = R.id.personal_info_nick_phone_layout)
	private TextView mPhone;
	// 生日
	@ViewInject(id = R.id.personal_info_nick_birthday_layout)
	private View mBirthLayout;

	@ViewInject(id = R.id.text_pic_item_pic, parentId = R.id.personal_info_nick_birthday_layout)
	private ImageView mBirthdayPic;

	@ViewInject(id = R.id.text_pic_item_left_text, parentId = R.id.personal_info_nick_birthday_layout)
	private TextView mBirthdayTitle;

	@ViewInject(id = R.id.text_pic_item_right_text, parentId = R.id.personal_info_nick_birthday_layout)
	private TextView mBirthday;

	// 驾龄

	@ViewInject(id = R.id.personal_info_nick_drivaege_layout)
	private View mDrivaeAgeLayout;

	@ViewInject(id = R.id.text_pic_item_pic, parentId = R.id.personal_info_nick_drivaege_layout)
	private ImageView mDrivaeAgePic;

	@ViewInject(id = R.id.text_pic_item_left_text, parentId = R.id.personal_info_nick_drivaege_layout)
	private TextView mDrivaeAgeTitle;

	@ViewInject(id = R.id.text_pic_item_right_text, parentId = R.id.personal_info_nick_drivaege_layout)
	private TextView mDrivaeAge;

	// 我的登录密码
	@ViewInject(id = R.id.my_login_password_layout)
	private View mPassWordLayout;

	@ViewInject(id = R.id.text_pic_item_pic, parentId = R.id.my_login_password_layout)
	private ImageView mPassWordPic;

	@ViewInject(id = R.id.text_pic_item_left_text, parentId = R.id.my_login_password_layout)
	private TextView mPassWordTitle;

	@ViewInject(id = R.id.text_pic_item_right_text, parentId = R.id.my_login_password_layout)
	private TextView mPassWordCoTextView;

	// 我的支付密码
	@ViewInject(id = R.id.my_pay_password_layout)
	private View mPayWordLayout;

	@ViewInject(id = R.id.text_pic_item_pic, parentId = R.id.my_pay_password_layout)
	private ImageView mPayWordPic;

	@ViewInject(id = R.id.text_pic_item_left_text, parentId = R.id.my_pay_password_layout)
	private TextView mPayWordTitle;

	@ViewInject(id = R.id.text_pic_item_right_text, parentId = R.id.my_pay_password_layout)
	private TextView mPayWordContentTextView;

	// 我的提现账户
	@ViewInject(id = R.id.my_cash_account_layout)
	private View mMoneyLayout;

	@ViewInject(id = R.id.text_pic_item_pic, parentId = R.id.my_cash_account_layout)
	private ImageView mMoneyPic;

	@ViewInject(id = R.id.text_pic_item_left_text, parentId = R.id.my_cash_account_layout)
	private TextView mMoneyTitle;

	@ViewInject(id = R.id.text_pic_item_right_text, parentId = R.id.my_cash_account_layout)
	private TextView mMoneyContentTextView;
	// 修改
	@ViewInject(id = R.id.personal_info_nick_logout)
	private Button mLogOutBtn;

	private String[] genDer;
//	private String[] image;
	private String selectedGender;
	private String genderTemp;
	private MyDatePickerDialog datePickerDialog;
	private String birthdayDate;
	private String[] drYearsArry;// 驾龄数组
	public final static String S_PINCHEMONEY = "pinchemoney";
	private User user = null;

	private static final int MODIFY_GENDER = 0;// 修改性别
	private static final int MODIFY_BIRTHDAY = 1;// 修改生日
	private static final int MODIFY_DRIVE_AGE = 2;// 修改生日

	/**
	 * path:TODO 图片文件路径
	 */
	String path;

	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		user = Util.getInstance().getUser(PersonalInfoActivity.this);
		setOnClick();
		drYearsArry = getResources().getStringArray(R.array.year);// 得到驾龄数据
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		genDer = getResources().getStringArray(R.array.gender);
		// image = getResources().getStringArray(R.array.get_image_way);
		if (user != null) {
			birthdayDate = user.getBirthday(PersonalInfoActivity.this);
			genderTemp = user.getGender(this);
		}
	}

	private void setData2View() {
		mTitleTextView.setText(getString(R.string.my_data_title));

		mNickNameTitle.setText(R.string.pick_details_name);
		mNickName.setText(user.getUsername(PersonalInfoActivity.this));
		mNickNamePic.setImageResource(R.drawable.pcb_nick_name);

		mSexTitle.setText(getString(R.string.my_data_sex));
		mSexPic.setImageResource(R.drawable.pcb_sex);
		mSex.setText(user.getGender(PersonalInfoActivity.this));

		mPhoneTitle.setText(getString(R.string.phone));
		mPhone.setText(user.getTel());
		mPhone.setTextColor(getResources().getColor(R.color.phone_color));
		mPhonePic.setImageResource(R.drawable.pcb_phone);

		mBirthdayPic.setImageResource(R.drawable.pcb_birthday);
		mBirthdayTitle.setText(getString(R.string.my_data_birthday));
		mBirthday.setText(user.getBirthday(PersonalInfoActivity.this));

		mDrivaeAgePic.setImageResource(R.drawable.pcb_driveage);
		mDrivaeAgeTitle.setText(getString(R.string.pick_details_car_year));
		mDrivaeAge.setText(user.getDriveAge());

		mPassWordPic.setImageResource(R.drawable.pcb_login_password);
		mPassWordTitle.setText(getString(R.string.myself_login_psw));
		mPassWordCoTextView.setText(getString(R.string.my_change_password));

		mPayWordPic.setImageResource(R.drawable.pcb_password);
		mPayWordTitle.setText(getString(R.string.myself_pay_psw));
		mPayWordContentTextView.setText(R.string.setting);

		mMoneyPic.setImageResource(R.drawable.pcb_money_new);
		mMoneyTitle.setText(getString(R.string.my_cash_account_title));
		mMoneyContentTextView.setText(getString(R.string.setting));
	}

	private void setOnClick() {
		mNickName.setOnClickListener(this);
		mSexLayout.setOnClickListener(this);
		mBirthLayout.setOnClickListener(this);
		if (user != null && user.getCar() != null) mDrivaeAgeLayout.setOnClickListener(this);
		mPassWordLayout.setOnClickListener(this);
		mPayWordLayout.setOnClickListener(this);
		mMoneyLayout.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
		mLogOutBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mBackBtn) {// 返回
			PersonalInfoActivity.this.finish();
		} else if (v == mMoneyLayout) { // 提现账户
			if (!user.isPayPwdSet()) {
				showDialog();
			} else {
				Util.getInstance().go2Activity(PersonalInfoActivity.this,
						MyCashAccountSettingActivity.class);
			}
		} else if (v == mNickName) {// 修改昵称
			Intent intent = new Intent(PersonalInfoActivity.this, UpdatePersonalInfoActivity.class);
			intent.putExtra("updateType", "name");
			startActivity(intent);
		} else if (v == mSexLayout) {// 修改性别
			choiceGender();
		} else if (v == mBirthLayout) {// 修改生日
			choiceBirthDate();
		} else if (v == mDrivaeAgeLayout) {// 修改驾龄
			choiceDriveYear();
		} else if (v == mPassWordLayout) {// 我的登录密码
			Intent intent = new Intent(PersonalInfoActivity.this, UpdatePersonalInfoActivity.class);
			intent.putExtra("updateType", "pass");
			startActivity(intent);
		} else if (v == mPayWordLayout) { // 我的支付密码
			util.go2Activity(PersonalInfoActivity.this, MyPayPswSettingActivity.class);
		} else if (v == mLogOutBtn) {// 退出登录
			logout();
		}
	}

	/**
	 * 未设置支付密码情况 弹出框选择
	 */
	private void showDialog() {
		util.showDialog(PersonalInfoActivity.this, "为了保障您的账户资金安全，您需要先设置支付密码才可以添加账户，是否立即添加？",
				"立即添加", "稍后添加", new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							util.go2Activity(PersonalInfoActivity.this,
									MyPayPswSettingActivity.class);
						}
					}
				});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		user = Util.getInstance().getLoginUser(PersonalInfoActivity.this);
		setData2View();
	}

	/**
	 * 注销登录
	 */

	private void logout() {
		if (Util.getInstance().isNetworkAvailable(this)) {
			Util.getInstance().doGetRequest(PersonalInfoActivity.this,
					new OnHttpRequestDataCallback() {

						@Override
						public void onSuccess(String result) {
							Util.getInstance().logout(PersonalInfoActivity.this);
							Util.getInstance().go2Activity(PersonalInfoActivity.this,
									CarSharingActivity.class);
							PersonalInfoActivity.this.finish();
						}
					}, Constant.URL_LOGOUT, getString(R.string.logout_int), false);

		} else {
			Util.showToast(PersonalInfoActivity.this, getString(R.string.network_unavaiable));
		}
	}

	/**
	 * 得到性别下拉框
	 */
	protected void choiceGender() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
		builder.setTitle(R.string.pick_gender).setItems(genDer,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (genDer[which].equals(getString(R.string.my_data_gender_m))) {
							genderTemp = User.GENDER_MALE;
						} else {
							genderTemp = User.GENDER_FEMALE;
						}
						selectedGender = genDer[which];
						updateGender(genderTemp);
					}
				});
		builder.create().show();
	}

	/**
	 * 修改性别
	 * 
	 * @param gender
	 */

	public void updateGender(String gender) {
		modifyInfo(MODIFY_GENDER, "gender", gender, Constant.URL_UPDATE_USER_INFO,
				getString(R.string.update_gender_ing));
	}

	private void modifyInfo(final int modifyType, String modifyKey, final String modifyValue,
			String url, String title) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(modifyKey, modifyValue));
		HttpRequestOnBackgrount modifyInfoBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.POST, new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						JsonResult<String> jsonResult = Util.getInstance().getObjFromJsonResult(
								result, new TypeToken<JsonResult<String>>() {
								});
						if (jsonResult != null && jsonResult.isSuccess()) {
							if (modifyType == MODIFY_GENDER) {// 修改性别
								user.setGender(genderTemp);
								Util.showToast(PersonalInfoActivity.this,
										getString(R.string.update_gender_success));
								mSex.setText(selectedGender);
							} else if (modifyType == MODIFY_BIRTHDAY) {// 修改生日
								user.setBirthday(birthdayDate);
								Util.showToast(PersonalInfoActivity.this,
										getString(R.string.update_birthdate_success));
								mBirthday.setText(birthdayDate);
							} else if (modifyType == MODIFY_DRIVE_AGE) {// 修改驾龄
								mDrivaeAge.setText(modifyValue);
								user.setDriveAge(modifyValue);
								Util.showToast(PersonalInfoActivity.this,
										getString(R.string.update_driveage_success));
							}
							updateUser(user);
						}
					}
				}, params, PersonalInfoActivity.this, false);

		modifyInfoBackgrount.execute(url);

	}

	/**
	 * 更新生日
	 * 
	 * @param birth
	 */
	public void updateBirth(String birthDate) {
		modifyInfo(MODIFY_BIRTHDAY, "birthday", birthDate, Constant.URL_UPDATE_USER_INFO,
				getString(R.string.update_birthdate_ing));

	}

	protected void updateUser(User uUser) {
		Util.getInstance().setUser(this, uUser);
		Util.getInstance().setPushEnable(this, uUser);

	}

	/**
	 * 选择出生日期
	 */
	public void choiceBirthDate() {
		String birthString = Util.getInstance().getLoginUser(PersonalInfoActivity.this)
				.getBirthday();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Calendar calendar = Calendar.getInstance(Locale.CHINA);// 获取一个日历对象
		Date date = new Date();
		try {
			date = sdf.parse(TextUtils.isEmpty(birthString) ? "" : birthString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(date);
		datePickerDialog = new MyDatePickerDialog(this, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				String strDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
				birthdayDate = TimeUtils.parseStr2Date(strDate, "yyyy-MM-dd").toString();// 生日自动补零
				updateBirth(birthdayDate);
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setCancelable(true);
		datePickerDialog.setCanceledOnTouchOutside(true);
		datePickerDialog.show();
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

	/**
	 * 得到驾龄下拉框
	 */
	protected void choiceDriveYear() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
		builder.setTitle(R.string.my_car_choice_dryear).setItems(drYearsArry,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// driveAge = drYearsArry[which];
						updateDriveAge(drYearsArry[which]);
					}
				});
		builder.create().show();
	}

	public void updateDriveAge(final String driveAge) {
		modifyInfo(MODIFY_DRIVE_AGE, "driveAge", driveAge, Constant.URL_UPDATE_USER_INFO,
				getString(R.string.update_driveage_ing));
	}

}
