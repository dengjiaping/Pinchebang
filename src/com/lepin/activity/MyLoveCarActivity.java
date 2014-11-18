package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import com.lepin.util.HttpUtil;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;

@Contextview(R.layout.car_info)
public class MyLoveCarActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mlTitleBack;// 返回

	@ViewInject(id = R.id.common_title_title)
	private TextView mlTitleText;// 标题

	@ViewInject(id = R.id.common_title_operater)
	private TextView mlTitleEdit;// 编辑

	@ViewInject(id = R.id.my_car_edit_show)
	private TextView mlEditShow;// 编辑提示信息

	@ViewInject(id = R.id.my_car_edit_type)
	private TextView mlType;// 车辆品牌

	@ViewInject(id = R.id.type_layout)
	private View mlLayout;

	@ViewInject(id = R.id.my_car_people_number)
	private TextView mlPeoNum;// 可搭载人数

	@ViewInject(id = R.id.my_car_edit_card)
	private EditText mlCard;// 牌照

	@ViewInject(id = R.id.my_car_edit_driving_year)
	private TextView mlDrivYear;// 驾龄

	@ViewInject(id = R.id.my_car_safe_layout)
	private View mlSafeLayout;// 车辆验证框

	@ViewInject(id = R.id.my_car_safe)
	private TextView mlSafe;// 车辆验证

	@ViewInject(id = R.id.my_car_save)
	private TextView mlSave;// 保存按钮

	private Util util = Util.getInstance();
	private boolean isEidting = false;// 是否处于编辑状态
	private boolean isPublishAddCar = false;// 是否第一次添加车辆
	private String[] drYearsArry;// 驾龄数组
	private String[] pepNumArry;// 搭载人数
	private String cType;
	private String cLicence;
	private String cInfo;
	private String carTypeId;
	private String from_info;
	private int carId;
	private Car car;

	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		isEidting = this.getIntent().getBooleanExtra("isEditting", false);
		from_info = this.getIntent().getStringExtra("from_perinfo");
		initView();
		loadCarInfo();
		drYearsArry = getResources().getStringArray(R.array.year);// 得到驾龄数据
		pepNumArry = getResources().getStringArray(R.array.peopnum);// 得到搭载人数数据
		flag = false;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (flag) {
			updateCarState();
		}
		flag = true;
	}

	public void initView() {
		mlTitleEdit.setVisibility(View.VISIBLE);
		mlTitleEdit.setText(this.getString(R.string.my_car_edit));
		this.mlTitleEdit.setOnClickListener(this);
		this.mlTitleBack.setOnClickListener(this);
		this.mlLayout.setEnabled(false);
		this.mlLayout.setOnClickListener(this);
		this.mlPeoNum.setOnClickListener(this);
		this.mlDrivYear.setOnClickListener(this);
		this.mlSave.setOnClickListener(this);
		this.mlSafeLayout.setOnClickListener(this);

		mlTitleText.setText(this.getString(R.string.my_car_title));
		if (isEidting) {
			setEnable(isEidting);
			setVisible(isEidting);
			isEidting = true;
			isPublishAddCar = true;
		}

	}

	/**
	 * 判断用户是否有车，有返回true,否则返回false
	 * 
	 * @return
	 */
	public boolean userCarIsExits() {
		User user = util.getLoginUser(MyLoveCarActivity.this);
		if (user != null) {
			Car car = user.getCar();
			if (car != null) {
				if (car.getCarId() >= 0) {
					return true;
				}
			} else {
			}
		}
		return false;
	}

	/**
	 * 获取车辆信息
	 */
	public void loadCarInfo() {
		if (userCarIsExits()) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			util.doPostRequest(MyLoveCarActivity.this, new OnHttpRequestDataCallback() {

				public void onSuccess(String result) {
					setCarInfo(result);
				}
			}, params, Constant.URL_GETUSERCARINFO,
					getString(R.string.car_verifying_get_carifo_tip), false);

			// util.doPostRequest(MyLoveCarActivity.this, new
			// OnDataLoadingCallBack() {
			// @Override
			// public void onLoadingBack(String result) {
			// // TODO Auto-generated method stub
			// setCarInfo(result);
			// }
			// }, params, Constant.URL_GETUSERCARINFO,
			// getString(R.string.car_verifying_get_carifo_tip));

		} else {
			Util.showToast(MyLoveCarActivity.this, getString(R.string.my_car_please_add));
			setEnable(true);
			setVisible(true);
			isEidting = true;
		}
	}

	private void updateCarState() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = "";// 响应结果
				try {
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					result = HttpUtil.post((List<NameValuePair>) params,
							Constant.URL_GETUSERCARINFO, MyLoveCarActivity.this);
				} catch (Exception e) {
					Util.getInstance().log(e.getMessage());
					e.printStackTrace();
				}
				JsonResult<Car> jresult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<Car>>() {
						});
				Looper.prepare();
				if (jresult != null) {
					if (jresult.isSuccess()) {
						car = jresult.getData();
						if (car.getState() != null) {
							if (car.getState().equals(Car.STATE_AUDIT_UNPASS)) {// 验证失败
								mlSafe.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										mlSafe.setText(getResources().getString(
												R.string.my_car_vefic_faile));
									}
								});
							}
							if (car.getState().equals(Car.STATE_WAIT_AUDIT)) {// 未验证
								mlSafe.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										mlSafe.setText(getResources().getString(
												R.string.my_car_vefic_notdo));
									}
								});
							}
							if (car.getState().equals(Car.STATE_AUDITING)) {
								mlSafe.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										mlSafe.setText(getResources().getString(
												R.string.my_car_vefic_doing));
										isEidting = false;
									}
								});
							}
							if (car.getState().equals(Car.STATE_AUDITED)) {
								mlSafe.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										mlSafe.setText(getResources().getString(
												R.string.my_car_vefic_okdo));
										isEidting = false;
									}
								});
							}
							Looper.loop();
						}
					}
				}
			}
		}).start();
	}

	protected void setCarInfo(String result) {
		// TODO Auto-generated method stub

		JsonResult<Car> jresult = util.getObjFromJsonResult(result,
				new TypeToken<JsonResult<Car>>() {
				});
		if (jresult != null) {
			if (jresult.isSuccess()) {
				car = jresult.getData();
				if (null == car) {
					car = util.getLoginUser(MyLoveCarActivity.this).getCar();
				}
				carTypeId = car.getCarTypeId();
				carId = car.getCarId();

				if (car.getCarType() != null) {
					// 此处还未处理
					mlType.setText(car.getCarType().getCarTypeName().toString());
				} else {
					mlType.setText(getString(R.string.my_car_null_data));
				}
				mlPeoNum.setText(car.getNum() + getString(R.string.human));
				if (car.getLicence() != null) {
					mlCard.setText(car.getLicence());
				} else {
					mlCard.setText(getString(R.string.my_car_null_data));
				}
				if (car.getInfo() != null) {
					mlDrivYear.setText(car.getInfo());
				} else {
					mlDrivYear.setText(getString(R.string.my_car_null_data));
				}
				if (car.getState() != null) {
					if (car.getState().equals(Car.STATE_AUDIT_UNPASS)) {// 验证失败
						mlSafe.setText(this.getString(R.string.my_car_vefic_faile));
					}
					if (car.getState().equals(Car.STATE_WAIT_AUDIT)) {// 未验证
						mlSafe.setText(this.getString(R.string.my_car_vefic_notdo));
					}
					if (car.getState().equals(Car.STATE_AUDITING)) {
						mlSafe.setText(this.getString(R.string.my_car_vefic_doing));
					}
					if (car.getState().equals(Car.STATE_AUDITED)) {
						mlSafe.setText(this.getString(R.string.my_car_vefic_okdo));
					}
				}

			}
		}

	}

	/**
	 * 事件响应
	 */
	@Override
	public void onClick(View v) {
		if (v == mlTitleBack) {
			if (from_info.equals("from_perinfo")) {
				this.finish();

			} else {
				if (isEidting && !isPublishAddCar) {
					isEidting = false;
					setEnable(isEidting);
					setVisible(isEidting);
					loadCarInfo();
				} else {
					this.finish();
				}
			}
		} else if (v == mlTitleEdit) {
			if (null != car.getState()
					&& (car.getState().equals(Car.STATE_AUDITING) || car.getState().endsWith(
							Car.STATE_AUDITED))) {
				Util.showToast(MyLoveCarActivity.this,
						getResources().getString(R.string.car_verifying_tip));
			} else {
				from_info = "";
				isEidting = true;
				setEnable(isEidting);
				setVisible(isEidting);
			}

		} else if (v == mlPeoNum) {
			if (isEidting) {
				choicePeoPleNumber();
			}
		} else if (v == mlDrivYear) {
			if (isEidting) {
				choiceDriveYear();
			}
		} else if (v == mlSave) {
			boolean dataOk = checkData();
			if (dataOk) {
				updateCarInfo();
			}
		} else if (v == mlSafeLayout) {
			if (userCarIsExits()) {
				// 如果正在验证，不能修改，TOAST提示
				// if (null != car.getState()
				// && (car.getState().equals(Car.STATE_AUDITING) ||
				// car.getState().equals(
				// Car.STATE_AUDITED))) {
				// Util.getInstance().showToast(MyLoveCarActivity.this,
				// getResources().getString(R.string.car_verifying_tip));
				// } else {
				// 只有未审核和审核失败才能进入
				Intent intent = new Intent();
				intent.setClass(MyLoveCarActivity.this, CarDriverVerify.class);
				intent.putExtra("CarId", car.getCarId());
				intent.putExtra("state", car.getState());
				startActivityForResult(intent, 10);
				// }
			} else {
				Util.showToast(this, getString(R.string.my_car_not_add));
			}
		} else if (v == mlLayout) {
			Intent intent = new Intent();
			intent.setClass(MyLoveCarActivity.this, SelectCarBrandActivity.class);
			startActivityForResult(intent, 5);
		}

	}

	/**
	 * 检验用户输入数据是否正确
	 */
	public boolean checkData() {
		cType = mlType.getText().toString().trim();
		cLicence = mlCard.getText().toString().trim().replaceAll(" ", "");
		cInfo = mlDrivYear.getText().toString().trim();
		if (TextUtils.isEmpty(cType)) {
			Util.showToast(this, getString(R.string.my_car_choose_car_type));
			return false;
		} else if (!ValidateTool.validateLicense(cLicence.toUpperCase(Locale.getDefault()))) {
			Util.showToast(this, getString(R.string.my_car_card_toast));
			mlCard.requestFocus();
			return false;
		}
		return true;
	}

	/**
	 * 设置是否可编辑
	 * 
	 * @param b
	 */
	public void setEnable(boolean b) {
		mlLayout.setEnabled(b);
		mlCard.setEnabled(b);
		mlCard.setSelection(mlCard.length());
	}

	/**
	 * 编辑页面和显示页面的切换
	 */
	public void setVisible(boolean b) {
		if (b == true) {
			mlTitleEdit.setVisibility(View.GONE);
			Drawable drawable = getResources().getDrawable(R.drawable.arrow);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			mlType.setCompoundDrawables(null, null, drawable, null);
			mlSave.setVisibility(View.VISIBLE);
			mlEditShow.setVisibility(View.VISIBLE);
			mlSafeLayout.setVisibility(View.GONE);
		} else {
			mlTitleEdit.setVisibility(View.VISIBLE);
			mlType.setCompoundDrawables(null, null, null, null);
			mlSave.setVisibility(View.INVISIBLE);
			mlEditShow.setVisibility(View.GONE);
			mlSafeLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (from_info.equals("from_perinfo")) {
				this.finish();
			} else {
				if (isEidting && !isPublishAddCar) {
					isEidting = false;
					setEnable(isEidting);
					setVisible(isEidting);
					loadCarInfo();
				} else {
					this.finish();
				}
			}
		}
		return false;
	}

	/**
	 * 得到可载人数下拉框
	 */
	protected void choicePeoPleNumber() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyLoveCarActivity.this);
		builder.setTitle(R.string.pick_details_total_people).setItems(pepNumArry,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mlPeoNum.setText(pepNumArry[which]);
					}
				});
		builder.create().show();
	}

	/**
	 * 得到驾龄下拉框
	 */
	protected void choiceDriveYear() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyLoveCarActivity.this);
		builder.setTitle(R.string.my_car_choice_dryear).setItems(drYearsArry,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mlDrivYear.setText(drYearsArry[which]);
					}
				});
		builder.create().show();
	}

	/**
	 * 修改车辆信息
	 */
	public void updateCarInfo() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("carId", String.valueOf(carId)));
		params.add(new BasicNameValuePair("carTypeId", carTypeId));

		params.add(new BasicNameValuePair("num", String.valueOf(mlPeoNum.getText().toString()
				.substring(0, 1))));

		params.add(new BasicNameValuePair("licence", cLicence.toUpperCase(Locale.getDefault())));
		params.add(new BasicNameValuePair("info", cInfo));

		util.doPostRequest(MyLoveCarActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {

				// TODO Auto-generated method stub
				JsonResult<Car> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<Car>>() {
						});
				if (jsonResult.isSuccess()) {
					Car car = jsonResult.getData();
					if (car != null) {
						User user = util.getLoginUser(MyLoveCarActivity.this);
						user.setCar(car);
						util.setUser(MyLoveCarActivity.this, user);
						isEidting = false;
						setEnable(isEidting);
						setVisible(isEidting);
						loadCarInfo();
						mlCard.setText(cLicence.toUpperCase(Locale.getDefault()));
						Util.showToast(MyLoveCarActivity.this,
								getString(R.string.my_car_data_submit_success));
					}
				} else {
					Util.showToast(MyLoveCarActivity.this, jsonResult.getErrorMsg().toString());
				}
			}
		}, params, Constant.URL_MODIFYCAR, getString(R.string.my_car_info_update), false);

		// util.doPostRequest(MyLoveCarActivity.this, new
		// OnDataLoadingCallBack() {
		//
		// @Override
		// public void onLoadingBack(String result) {
		// // TODO Auto-generated method stub
		// JsonResult<Car> jsonResult = util.getObjFromJsonResult(result,
		// new TypeToken<JsonResult<Car>>() {
		// });
		// if (jsonResult.isSuccess()) {
		// Car car = jsonResult.getData();
		// if (car != null) {
		// User user = util.getLoginUser(MyLoveCarActivity.this);
		// user.setCar(car);
		// util.setUser(MyLoveCarActivity.this, user);
		// isEidting = false;
		// setEnable(isEidting);
		// setVisible(isEidting);
		// loadCarInfo();
		// mlCard.setText(cLicence.toUpperCase(Locale.getDefault()));
		// Util.showToast(MyLoveCarActivity.this,
		// getString(R.string.my_car_data_submit_success));
		// }
		// } else {
		// Util.showToast(MyLoveCarActivity.this,
		// jsonResult.getErrorMsg().toString());
		// }
		//
		// }
		// }, params, Constant.URL_MODIFYCAR,
		// getString(R.string.my_car_info_update));

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK && requestCode == 5) {
			String cartype = data.getStringExtra("cartype");
			carTypeId = String.valueOf(data.getIntExtra("typeId", 0));
			mlType.setText(cartype);
		}
		if (resultCode == 10 && requestCode == 10) {
			loadCarInfo();
		}
	};
}
