package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.adapter.MyCashAccountAdapter;
import com.lepin.entity.GoldBank;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

@Contextview(R.layout.my_cash_account_setting)
public class MyCashAccountSettingActivity extends Activity implements OnClickListener {

	private static final int SUCCEED = 1;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitleTextView;

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBackBtn;

	@ViewInject(id = R.id.setting_cash_account_layout)
	private LinearLayout mAddCashAccountLayout; // 增加提现账户layout

	@ViewInject(id = R.id.add_alipay_cash_account_edit)
	private EditText mAddAlipayCashAccountEditText; // 增加支付宝账户的编辑框

	@ViewInject(id = R.id.my_cash_account_name)
	private EditText mAddAlipayCashAccountNameEditText; // 增加支付宝名称编辑框

	@ViewInject(id = R.id.add_cash_account_sure)
	private Button mAddCashAccountBtn; // 确定添加

	@ViewInject(id = R.id.setting_cash_account_add_delete_layout)
	private LinearLayout mAddOrDeleteCashAccountLayout; // 增加支付宝账户layout

	@ViewInject(id = R.id.add_account_listview)
	private ListView mCashAccountListView; // 动态生成的listview

	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		mTitleTextView.setText(getResources().getString(R.string.cash_account_title));
		Loader();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	private void Loader() {
		// TODO Auto-generated method stub
		setdata();
		setOnclick();
	}

	private void setOnclick() {
		// TODO Auto-generated method stub
		mBackBtn.setOnClickListener(this);
		mAddCashAccountBtn.setOnClickListener(this);
		mAddOrDeleteCashAccountLayout.setOnClickListener(this);
	}

	private void setdata() {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		util.doPostRequest(MyCashAccountSettingActivity.this, new OnHttpRequestDataCallback() {
			@Override
			public void onSuccess(String result) {
				JsonResult<ArrayList<GoldBank>> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<ArrayList<GoldBank>>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					ArrayList<GoldBank> gbBanks = jsonResult.getData();
					if (gbBanks.size() == 0) {// 初次添加账户
						IsShowAddAccountLayout(true);
					} else {// 增删账户
						IsShowAddAccountLayout(false);
						createUI(gbBanks);
					}
				}
			}
		}, params, Constant.GET_CASH_ACCOUNT, getString(R.string.get_cash_account_tip), false);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackBtn) {
			MyCashAccountSettingActivity.this.finish();
		} else if (v == mAddOrDeleteCashAccountLayout) {
			startActivityForResult(new Intent(MyCashAccountSettingActivity.this,
					MyCashAccountAddOrModifyActivity.class), SUCCEED);
		} else if (v == mAddCashAccountBtn) {
			addCashAccount();
		}
	}

	private void addCashAccount() {
		// TODO Auto-generated method stub
		String alipayAccountString = mAddAlipayCashAccountEditText.getText().toString();
		String alipayTrueNameString = mAddAlipayCashAccountNameEditText.getText().toString();
		AddCashAccount(alipayAccountString, alipayTrueNameString);

	}

	private void AddCashAccount(String account, String trueName) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", account));
		params.add(new BasicNameValuePair("trueName", trueName));
		util.doPostRequest(MyCashAccountSettingActivity.this, new OnHttpRequestDataCallback() {
			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					Util.showToast(MyCashAccountSettingActivity.this,
							getResources().getString(R.string.add_cash_account_success_toast));
					// IsShowAddAccountLayout(false);
					Loader();
				}
			}

			public void onFail(String errorType, String errorMsg) {
				Util.showToast(MyCashAccountSettingActivity.this, errorMsg);
			};
		}, params, Constant.ADD_DELETE_ACCOUNT, getString(R.string.get_cash_account_tip), true);
	}

	private void IsShowAddAccountLayout(Boolean flag) {
		if (flag) {
			mAddCashAccountLayout.setVisibility(View.VISIBLE);
			mAddOrDeleteCashAccountLayout.setVisibility(View.GONE);
		} else {
			mAddCashAccountLayout.setVisibility(View.GONE);
			mAddOrDeleteCashAccountLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 动态创建新增的账户
	 */
	@SuppressLint("NewApi")
	private void createUI(final ArrayList<GoldBank> goBanks) {
		MyCashAccountAdapter adapter = new MyCashAccountAdapter(MyCashAccountSettingActivity.this,
				goBanks, mCashAccountListView);
		mCashAccountListView.setAdapter(adapter);
		mCashAccountListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				// Bundle bundle = new Bundle();
				// bundle.putString("account",
				// goBanks.get(arg2).getAccount().toString());
				// bundle.putString("name",
				// goBanks.get(arg2).getTrueName().toString());
				Intent intent = new Intent(MyCashAccountSettingActivity.this,
						MyCashAccountAddOrModifyActivity.class);
				intent.putExtra("account", goBanks.get(arg2).getAccount().toString());
				intent.putExtra("name", goBanks.get(arg2).getTrueName().toString());
				intent.putExtra("id", String.valueOf(goBanks.get(arg2).getGoldBankId()));
				startActivityForResult(intent, SUCCEED);
			}
		});
		setListViewHeightBasedOnChildren(mCashAccountListView);
		adapter.notifyDataSetChanged();
		mCashAccountListView.invalidate();
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {

		// 获取listview的适配器
		ListAdapter listAdapter = listView.getAdapter();
		// item的高度
		int itemHeight = 45;
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {
			totalHeight += Dp2Px(getApplicationContext(), itemHeight) + listView.getDividerHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
		listView.setLayoutParams(params);
	}

	public int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) return;
		if (requestCode == SUCCEED) {// 添加账户成功
			Loader();
		}
	}

}
