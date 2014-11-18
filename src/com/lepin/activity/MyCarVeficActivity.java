package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
import com.lepin.util.ValidateTool;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

@Contextview(R.layout.car_vefic)
public class MyCarVeficActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView vfBack;// 返回

	@ViewInject(id = R.id.common_title_title)
	private TextView vfTitle;// 标题

	@ViewInject(id = R.id.car_vefic_type)
	private TextView vfType;// 车型

	@ViewInject(id = R.id.my_car_more)
	private TextView vfSelect;// 图标

	@ViewInject(id = R.id.car_vefic_card)
	private EditText vfCard;// 车牌号

	@ViewInject(id = R.id.car_vefic_num)
	private EditText vfNum;// 车架号

	@ViewInject(id = R.id.car_vefic_submit)
	private TextView vfSubmit;// 提交

	@ViewInject(id = R.id.my_car_vefic_type)
	private View vfTypeLayout;

	private String[] carTypes;
	private String vType = Car.CARSIZE_SMALL_CAR;
	private String vCard;
	private String vNum;
	private String vId;

	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		loadCard();
		carTypes = getResources().getStringArray(R.array.cartype);// 得到驾龄数据
	}

	public void initView() {

		this.vfBack.setOnClickListener(this);
		this.vfSubmit.setOnClickListener(this);
		this.vfTypeLayout.setOnClickListener(this);
		this.vfTitle.setText(this.getString(R.string.my_car_vefic_title));
	}

	/**
	 * 加载车牌号和车辆检验结果
	 */
	public void loadCard() {
		final User user = util.getUser(MyCarVeficActivity.this);
		Car car = null;
		if (user != null) {
			car = user.getCar();
		}
		if (car != null) {
			if (car.getLicence() != null)// 车牌
			{
				vfCard.setText(car.getLicence());
			}

			if (car.getState() != null)// 验证
			{
				if (car.getState().equals(Car.STATE_AUDIT_UNPASS))// 审核失败
				{
					vfNum.setEnabled(true);
					vfTypeLayout.setEnabled(true);
					vfNum.requestFocus();
				} else if (car.getState().equals(Car.STATE_WAIT_AUDIT))// 未验证
				{
					vfNum.setEnabled(true);
					vfTypeLayout.setEnabled(true);
					vfNum.requestFocus();
				} else if (car.getState().equals(Car.STATE_AUDITING))// 审核中
				{
					vfNum.setEnabled(false);
					vfTypeLayout.setEnabled(false);
					vfSelect.setVisibility(View.GONE);
					vfNum.setText(car.getCarNum());
					vfSubmit.setVisibility(View.INVISIBLE);
				} else if (car.getState().equals(Car.STATE_AUDITED))// 已验证
				{
					vfNum.setEnabled(false);
					vfTypeLayout.setEnabled(false);
					vfSelect.setVisibility(View.GONE);
					vfNum.setText(car.getCarNum());
					vfSubmit.setVisibility(View.INVISIBLE);
				}
			}
			if (car.getCarSize() != null) {
				if (car.getCarSize().equals(Car.CARSIZE_SMALL_CAR)) {
					vfType.setText(getString(R.string.car_vefic_small));
				} else if (car.getCarSize().equals(Car.CARSIZE_MIDSIZE_CAR)) {
					vfType.setText(getString(R.string.car_vefic_medium));
				} else if (car.getCarSize().equals(Car.CARSIZE_LARGE_CAR)) {
					vfType.setText(getString(R.string.car_vefic_large));
				} else {
					vfType.setText(getString(R.string.car_vefic_other));
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == vfBack) {
			this.finish();
		}
		if (v == vfTypeLayout) {
			choiceCareType();
		}
		if (v == vfSubmit) {
			if (checkData()) {

				Util.getInstance().showDialog(MyCarVeficActivity.this,
						getString(R.string.car_vefic_submit_info), getString(R.string.confirm),
						getString(R.string.msg_btn_text2), new OnOkOrCancelClickListener() {

							@Override
							public void onOkClick(int type) {
								if (type == PcbConfirmDialog.OK) {
									submitCarCard();// 提交验证信息
								}
							}
						});
			}
		}

	}

	private void submitCarCard() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("carId", vId));
		params.add(new BasicNameValuePair("licence", vCard));
		params.add(new BasicNameValuePair("carSize", vType));
		params.add(new BasicNameValuePair("carNum", vNum));

		util.doPostRequest(MyCarVeficActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult.isSuccess()) {
					User user = util.getLoginUser(MyCarVeficActivity.this);
					if (user != null) {
						Car car = user.getCar();
						car.setCarNum(vNum);
						car.setState(Car.STATE_AUDITING);
						util.updateUser(MyCarVeficActivity.this,
								util.getUser(MyCarVeficActivity.this));
					}

					Util.showToast(MyCarVeficActivity.this,
							getString(R.string.car_vefic_submit_success));

					Intent intent = new Intent(MyCarVeficActivity.this, MyLoveCarActivity.class);
					setResult(10, intent);

					MyCarVeficActivity.this.finish();
				} else {
					Util.showToast(MyCarVeficActivity.this,
							getString(R.string.car_vefic_submit_fail));
				}

			}
		}, params, Constant.URL_CARVERIFICATION, getString(R.string.car_vefic_submit_ing), false);

		// util.doPostRequest(MyCarVeficActivity.this, new
		// OnDataLoadingCallBack() {
		// @Override
		// public void onLoadingBack(String result) {
		// // TODO Auto-generated method stub
		// JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
		// new TypeToken<JsonResult<String>>() {
		// });
		// if (jsonResult.isSuccess()) {
		// User user = util.getLoginUser(MyCarVeficActivity.this);
		// if (user != null) {
		// Car car = user.getCar();
		// car.setCarNum(vNum);
		// car.setState(Car.STATE_AUDITING);
		// util.updateUser(MyCarVeficActivity.this,
		// util.getUser(MyCarVeficActivity.this));
		// }
		//
		// Util.showToast(MyCarVeficActivity.this,
		// getString(R.string.car_vefic_submit_success));
		//
		// Intent intent = new Intent(MyCarVeficActivity.this,
		// MyLoveCarActivity.class);
		// setResult(10, intent);
		//
		// MyCarVeficActivity.this.finish();
		// } else {
		// Util.showToast(MyCarVeficActivity.this,
		// getString(R.string.car_vefic_submit_fail));
		// }
		//
		// }
		// }, params, Constant.URL_CARVERIFICATION,
		// getString(R.string.car_vefic_submit_ing));
	}

	/**
	 * 获取车型
	 */
	protected void choiceCareType() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyCarVeficActivity.this);
		builder.setTitle(R.string.my_car_vefic_chtype).setItems(carTypes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						vfType.setText(carTypes[which]);
					}
				});
		builder.create().show();
	}

	/**
	 * 检查输入数据是否符合规则
	 * 
	 * @return
	 */
	public boolean checkData() {
		Car car = util.getLoginUser(MyCarVeficActivity.this).getCar();
		vId = String.valueOf(car.getCarId());
		vCard = vfCard.getText().toString().trim();
		vNum = vfNum.getText().toString().trim();

		if (!ValidateTool.validateLicense(vCard)) {
			Util.showToast(this, getString(R.string.car_vefic_check_card_info));
			vfCard.requestFocus();
			return false;
		}
		if (vNum.equals("") || vNum == null || !isNumberOrLetter(vNum) || vNum.length() < 17) {
			Util.showToast(this, getString(R.string.car_vefic_check_num_info));
			vfNum.requestFocus();
			return false;
		}

		String tempType = vfType.getText().toString().trim();
		if (tempType.equals(getString(R.string.car_vefic_small))) {
			vType = Car.CARSIZE_SMALL_CAR;
		} else if (tempType.equals(getString(R.string.car_vefic_medium))) {
			vType = Car.CARSIZE_MIDSIZE_CAR;
		} else if (tempType.equals(getString(R.string.car_vefic_large))) {
			vType = Car.CARSIZE_LARGE_CAR;
		} else if (tempType.equals(getString(R.string.car_vefic_other))) {
			vType = Car.CARSIZE_OTHER;
		}

		return true;
	}

	/**
	 * 验证输入车架号是否为数字和字母
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNumberOrLetter(String string) {
		return string.matches("^[A-Za-z0-9]+$");
	}
}
