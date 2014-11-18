package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.Car;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;

@Contextview(R.layout.add_new_car)
public class AddNewCarActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView addBack;// 返回
	@ViewInject(id = R.id.common_title_title)
	private TextView addTitle;// 标题

	@ViewInject(id = R.id.add_car_type_layout)
	private LinearLayout typeLayout;// 车辆品牌

	@ViewInject(id = R.id.add_car_type)
	private TextView addCarType;

	@ViewInject(id = R.id.add_car_people_num_layout)
	private LinearLayout numLayout;// 搭载人数

	@ViewInject(id = R.id.add_car_people_number)
	private TextView addNum;
	@ViewInject(id = R.id.add_car_edit_card)
	private EditText addCard;// 车牌
	@ViewInject(id = R.id.add_car_save)
	private TextView addSave;// 添加

	private String carTypeId;
	private String[] pepNumArry;// 搭载人数
	private String cLicence;// 车牌
	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		init();
		pepNumArry = getResources().getStringArray(R.array.peopnum);// 得到搭载人数数据
	}

	public void init() {
		addBack.setOnClickListener(this);
		typeLayout.setOnClickListener(this);
		numLayout.setOnClickListener(this);
		addSave.setOnClickListener(this);

		addTitle.setText(getString(R.string.add_car_title));

	}

	@Override
	public void onClick(View v) {
		if (v == addBack) {
			AddNewCarActivity.this.finish();
		} else if (v == typeLayout) {
			Intent intent = new Intent();
			intent.setClass(AddNewCarActivity.this, SelectCarBrandActivity.class);
			startActivityForResult(intent, 5);
		} else if (v == numLayout) {
			choicePeoPleNumber();
		} else if (v == addSave) {
			if (checkData()) {
				addNewCarInfo();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 5) {
			String cartype = data.getStringExtra("cartype");
			carTypeId = String.valueOf(data.getIntExtra("typeId", 0));
			addCarType.setText(cartype);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 得到可载人数下拉框
	 */
	protected void choicePeoPleNumber() {
		AlertDialog.Builder builder = new AlertDialog.Builder(AddNewCarActivity.this);
		builder.setTitle(R.string.pick_details_total_people).setItems(pepNumArry,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						addNum.setText(pepNumArry[which]);
					}
				});
		builder.create().show();
	}

	/**
	 * 检验用户输入数据是否正确
	 */
	public boolean checkData() {
		cLicence = addCard.getText().toString().trim().replaceAll(" ", "");
		if (TextUtils.isEmpty(carTypeId)) {
			Util.showToast(this, getString(R.string.add_car_select_type));
			return false;
		} else if (!ValidateTool.validateLicense(cLicence.toUpperCase(Locale.getDefault()))) {
			Util.showToast(this, getString(R.string.my_car_card_toast));
			addNum.requestFocus();
			return false;
		}
		return true;
	}

	public void addNewCarInfo() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("carTypeId", carTypeId));
		if (addNum.getText().toString().length() > 0) {
			params.add(new BasicNameValuePair("num", addNum.getText().toString().substring(0, 1)));
		}
		params.add(new BasicNameValuePair("licence", cLicence.toUpperCase(Locale.getDefault())));

		util.doPostRequest(AddNewCarActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				JsonResult<Car> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<Car>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					Car car = jsonResult.getData();
					util.getLoginUser(AddNewCarActivity.this).setCar(car);
					util.setUser(AddNewCarActivity.this, util.getLoginUser(AddNewCarActivity.this));
					Util.showToast(AddNewCarActivity.this, getString(R.string.add_car_success));
					AddNewCarActivity.this.finish();
				} else {
					Util.showToast(AddNewCarActivity.this, getString(R.string.request_error));
				}

			}

		}, params, Constant.URL_ADDCAR, getString(R.string.add_car_info), false);

		// util.doPostRequest(AddNewCarActivity.this, new
		// OnDataLoadingCallBack() {
		//
		// @Override
		// public void onLoadingBack(String result) {
		// JsonResult<Car> jsonResult = util.getObjFromJsonResult(result,
		// new TypeToken<JsonResult<Car>>() {
		// });
		// if (jsonResult != null && jsonResult.isSuccess()) {
		// Car car = jsonResult.getData();
		// util.getLoginUser(AddNewCarActivity.this).setCar(car);
		// util.setUser(AddNewCarActivity.this,
		// util.getLoginUser(AddNewCarActivity.this));
		// Util.showToast(AddNewCarActivity.this,
		// getString(R.string.add_car_success));
		// AddNewCarActivity.this.finish();
		// } else {
		// Util.showToast(AddNewCarActivity.this,
		// getString(R.string.request_error));
		// }
		//
		// }
		// }, params, Constant.URL_ADDCAR, getString(R.string.add_car_info));

	}
}
