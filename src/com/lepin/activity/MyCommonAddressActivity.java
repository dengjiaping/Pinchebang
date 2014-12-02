package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.entity.MyAddr;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

/**
 * 我的常用拼车地址
 * 
 * @author zhiqiang
 * 
 */
@Contextview(R.layout.my_common_address_activity)
public class MyCommonAddressActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	@ViewInject(id = R.id.my_common_address_home)
	private TextView mHomeAddress;

	@ViewInject(id = R.id.my_common_address_company)
	private TextView mCompanyAddress;

	@ViewInject(id = R.id.my_common_address_submit)
	private Button mSubmit;

	private int home = 1;
	private int company = 2;

	private int clickType;
	private ArrayList<MyAddr> myCommonAddrs = new ArrayList<MyAddr>();
	private MyAddr[] oldAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		mTitle.setText(getString(R.string.common_address_title));
		mBack.setOnClickListener(this);
		mHomeAddress.setOnClickListener(this);
		mCompanyAddress.setOnClickListener(this);
		mSubmit.setOnClickListener(this);
		checkCommonAddress();

	}

	private void checkCommonAddress() {
		User user = Util.getInstance().getUser(this);
		if (user != null) {
			oldAdd = user.getMyAddrs();
			if (oldAdd != null && oldAdd.length > 0) {
				for (MyAddr m : oldAdd) {
					myCommonAddrs.add(m);
					if (TextUtils.isDigitsOnly(m.getAddrType()) || TextUtils.isEmpty(m.getName()))
						continue;
					if (m.getAddrType().equals(MyAddr.ADDRTYPE_COMPANY)) {
						mCompanyAddress.setText(m.getName());
					} else if (m.getAddrType().equals(MyAddr.ADDRTYPE_FAMILY)) {
						mHomeAddress.setText(m.getName());
					}
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v == mBack) {
			this.finish();
		} else if (v == mHomeAddress) {
			clickType = home;
			choice();
		} else if (v == mCompanyAddress) {
			clickType = company;
			choice();
		} else if (v == mSubmit) {
			addOrUpdateCommonAddress();
		}
	}

	private void addOrUpdateCommonAddress() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < myCommonAddrs.size(); i++) {
			MyAddr m = myCommonAddrs.get(i);
			Util.printLog("常用地址:" + myCommonAddrs.get(i).toString());
			params.add(new BasicNameValuePair("myAddrs[" + i + "].name", m.getName()));
			params.add(new BasicNameValuePair("myAddrs[" + i + "].cityId", m.getCityId()));
			String type = m.getAddrType();
			params.add(new BasicNameValuePair("myAddrs[" + i + "].addrType", type));
			params.add(new BasicNameValuePair("myAddrs[" + i + "].lat", String.valueOf(m.getLat())));
			params.add(new BasicNameValuePair("myAddrs[" + i + "].lon", String.valueOf(m.getLon())));
			if (oldAdd != null && oldAdd.length > 0) {
				for (MyAddr mm : oldAdd) {
					if (type.equals(mm.getAddrType())) {
						params.add(new BasicNameValuePair("myAddrs[" + i + "].myAddrId", String
								.valueOf(mm.getMyAddrId())));
						continue;
					}
				}
			}
		}
		Util.printLog("添加常用地址参数:" + params);
		Util.getInstance().doPostRequest(this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<User> jsonResult = Util.getInstance().getObjFromJsonResult(result,
						new TypeToken<JsonResult<User>>() {
						});
				if (jsonResult != null && jsonResult.getData() != null) {
					User newUser = jsonResult.getData();
					Util.printLog("更新后的user:" + newUser.toString());
					Util.getInstance().setUser(MyCommonAddressActivity.this, newUser);
					Util.showToast(MyCommonAddressActivity.this,
							getString(R.string.edit_common_address_su));
					finish();
				} else {

				}
			}
		}, params, Constant.EDIT_COMMON_ADDRESS, getString(R.string.edit_common_address), false);
	}

	private void choice() {
		Intent intentWork = new Intent(this, ChoiceAdrrActivity.class);
		startActivityForResult(intentWork, Constant.REQUESTCODE_COMMON_ADDRESS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null || data.getExtras() == null) return;
		final Bundle dBundle = data.getExtras();
		if (requestCode == Constant.REQUESTCODE_COMMON_ADDRESS) {
			if (TextUtils.isEmpty(dBundle.getString(Constant.S_START))) return;

			MyAddr myAddr = new MyAddr();
			myAddr.setLat(dBundle.getLong(Constant.SLAT));
			myAddr.setLon(dBundle.getLong(Constant.SLON));
			myAddr.setCityId(dBundle.getString(Constant.CITY_CODE));
			myAddr.setName(dBundle.getString(Constant.S_START));
			if (clickType == home) {
				mHomeAddress.setText(myAddr.getName());
				myAddr.setAddrType(MyAddr.ADDRTYPE_FAMILY);
			} else if (clickType == company) {
				myAddr.setAddrType(MyAddr.ADDRTYPE_COMPANY);
				mCompanyAddress.setText(myAddr.getName());
			}
			if (myCommonAddrs == null) myCommonAddrs = new ArrayList<MyAddr>();
			myCommonAddrs.add(myAddr);
		}
	}
}
