package com.lepin.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.Car;
import com.lepin.entity.JsonResult;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.CircleImageView;

/**
 * 用于展现从线路详情 头像跳转过来的 个人主页界面
 * 
 * @author {author wangxiaohong}
 * 
 */
@SuppressLint("NewApi")
@Contextview(R.layout.personal_info_trail_activity)
public class PersonalInfoTrailActivity extends BaseActivity implements OnClickListener {

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
	@ViewInject(id = R.id.indentification)
	private ImageView mIndentificationMark;

	// 第一栏
	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.personal_info_trail_one_layout)
	private TextView mOneTitle;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.personal_info_trail_one_layout)
	private TextView mOneValue;

	// 第二栏
	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.personal_info_trail_two_layout)
	private TextView mTwoTitle;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.personal_info_trail_two_layout)
	private TextView mTwoValue;

	// 第三栏
	@ViewInject(id = R.id.personal_info_trail_three_layout)
	private View mThreeLayout;

	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.personal_info_trail_three_layout)
	private TextView mThreeTitle;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.personal_info_trail_three_layout)
	private TextView mThreeValue;

	// 线路数
	@ViewInject(id = R.id.personal_info_trail_line_number)
	private View mLineLayout;

	@ViewInject(id = R.id.item_text_image_title, parentId = R.id.personal_info_trail_line_number)
	private TextView mLineTitle;

	@ViewInject(id = R.id.item_text_image_value, parentId = R.id.personal_info_trail_line_number)
	private TextView mLineValue;

	@ViewInject(id = R.id.item_text_image_mapview, parentId = R.id.personal_info_trail_line_number)
	private ImageView mLineRaw;

	int userId;
	private Util util = Util.getInstance();
	User user = null;
	private String role;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		// 获得数据
		final Bundle bundle = getIntent().getExtras();
		userId = bundle.getInt("userId");
		role = bundle.getString("role");
		mTitleTextView.setText(getResources().getString(R.string.pinche_trail_personal_page));
		loadDate();
		mBackBtn.setOnClickListener(this);
		mLineLayout.setOnClickListener(this);
	}

	/**
	 * 填充数据
	 */
	private void loadDate() {
		// TODO Auto-generated method stub
		final String mUrl = "userId=" + userId;

		util.doGetRequest(
				PersonalInfoTrailActivity.this,
				new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						JsonResult<User> jsonResult = util.getObjFromJsonResult(result,
								new TypeToken<JsonResult<User>>() {
								});
						if (jsonResult != null && jsonResult.isSuccess()) {
							user = jsonResult.getData();
							setUserDetails();// 设置界面初始值
						} else {
							Util.showToast(PersonalInfoTrailActivity.this,
									getString(R.string.request_error));
						}
					}
				}, Constant.URL_GET_TRAIL_PERSONAL_INFO + mUrl,
				getString(R.string.lepin_dialog_loading),
				false);

	}

	protected void setUserDetails() {
		// TODO Auto-generated method stub
		String url = Util.getInstance().getPhotoURL(user.getUserId());
		mypicture.displayWithUrl(url, true, true);
		mNickName.setText(user.getUsername());
		mLineRaw.setBackground(getResources().getDrawable(R.drawable.pcb_home_arrow));
		mLineTitle.setText(getResources().getString(R.string.pick_publish_trail));
		if (role.equals(Constant.DRIVER)) {
			// 显示司机
			Car car = user.getCar();
			if (Car.STATE_AUDITED.equals(car.getState())) {
				mIndentificationMark.setVisibility(View.VISIBLE);
			}
			mThreeLayout.setVisibility(View.VISIBLE);
			mOneTitle.setText(getResources().getString(R.string.add_car_type));
			mTwoTitle.setText(getResources().getString(R.string.my_car_driving_years));
			mThreeTitle.setText(getResources().getString(R.string.my_car_vefic_card));
			mOneValue.setText(car.getCarType().getCarTypeName() + "");
			mTwoValue.setText(user.getDriveAge());
			mThreeValue.setText(car.getLicence());
			mLineValue.setText(user.getInfoCountsAsDriver() + "");
		} else {
			// 乘客
			mOneTitle.setText(getResources().getString(R.string.my_data_sex));
			mTwoTitle.setText(getResources().getString(R.string.my_data_birthday));
			mOneValue.setText(user.getGender(this));
			mTwoValue.setText(user.getBirthday());
			mThreeLayout.setVisibility(View.GONE);
			mLineValue.setText(user.getInfoCountsAsPassenger() + "");
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (mBackBtn == view) {
			PersonalInfoTrailActivity.this.finish();
		} else if (mLineLayout == view) {
			Bundle dataBundle = new Bundle();
			dataBundle.putInt("userId", userId);
			dataBundle.putString("role", role);
			util.go2ActivityWithBundle(PersonalInfoTrailActivity.this,
					PincheTrailHerPublishLinesActivity.class, dataBundle);
		}
	}

}
