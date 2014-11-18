package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.adapter.MyBalanceRecordAdapter;
import com.lepin.entity.GoldLog;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Page;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbListView;
import com.lepin.widget.PcbListView.PcbListViewListener;

/**
 * 我的余额界面
 * 
 * @author zhiqiang
 * 
 */
@Contextview(R.layout.my_balance_activity)
public class MyBalanceActivity extends BaseActivity implements OnClickListener, PcbListViewListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	@ViewInject(id = R.id.my_balance_value)
	private TextView mBalanceTextView;// 余额

	@ViewInject(id = R.id.my_balance_recharge)
	private Button mRechargeButton;// 充值按钮

	@ViewInject(id = R.id.my_balance_apply_to_cash)
	private Button mApplyToCashButton;// 申请提现按钮

	@ViewInject(id = R.id.my_balance_details_title)
	private TextView mRecordTitle;

	@ViewInject(id = R.id.my_balance_listview)
	private PcbListView mDetailsListView;

	private MyBalanceRecordAdapter adapter;// 适配器
	private List<GoldLog> goldLogs = new ArrayList<GoldLog>();

	private int currentPage = 1;
	private int totalPage = 0;

	private boolean isOnRefresh = false;
	private boolean isOnLoadMore = false;

	private String currentCash = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		mTitle.setText(getString(R.string.my_balance));
		mBack.setOnClickListener(this);
		mRechargeButton.setOnClickListener(this);
		mApplyToCashButton.setOnClickListener(this);
		mDetailsListView.setPullRefreshEnable(false);
		mDetailsListView.setPullLoadEnable(false);
		mDetailsListView.setPcbListViewListener(this);
		getAccountRecord(currentPage);
	}

	private void getAccountRecord(int page) {
		List<NameValuePair> paramsList = getParams(page);
		Util.getInstance().doPostRequest(this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				Util.printLog("消费记录:" + result);
				JsonResult<Page<GoldLog>> jsonResult = Util.getInstance().getObjFromJsonResult(
						result, new TypeToken<JsonResult<Page<GoldLog>>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					Page<GoldLog> page = jsonResult.getData();
					totalPage = page.getPageCount();
					if (currentPage >= totalPage) {
						mDetailsListView.setPullLoadEnable(false);
					} else {
						mDetailsListView.setPullLoadEnable(true);
					}

					List<GoldLog> tempGoldLogs = page.getRows();
					if (tempGoldLogs != null && tempGoldLogs.size() > 0) {
						mDetailsListView.setPullRefreshEnable(true);
						goldLogs.addAll(tempGoldLogs);
						setAdapter();
					} else {
						mRecordTitle.setVisibility(View.VISIBLE);
					}

				} else {

				}
			}

		}, paramsList, Constant.GET_CONSUMER_RECORDS, "获取帐户记录...", false);
	}

	private List<NameValuePair> getParams(int page) {
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("page", String.valueOf(page)));
		paramsList.add(new BasicNameValuePair("rows", String.valueOf(5)));
		return paramsList;
	}

	private void setAdapter() {
		adapter = new MyBalanceRecordAdapter(MyBalanceActivity.this, goldLogs);
		mDetailsListView.setAdapter(adapter);
		mDetailsListView.setVisibility(View.VISIBLE);
	}

	private void refreshOrLoadMore(int page) {
		HttpRequestOnBackgrount refreshOrLoadMoRequestOnBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.POST, new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						Util.printLog("消费记录刷新,加载更多:" + result);
						JsonResult<Page<GoldLog>> jsonResult = Util.getInstance()
								.getObjFromJsonResult(result,
										new TypeToken<JsonResult<Page<GoldLog>>>() {
										});
						if (jsonResult != null && jsonResult.isSuccess()) {
							Page<GoldLog> page = jsonResult.getData();
							if (page != null) {
								totalPage = page.getPageCount();
								if (currentPage >= totalPage) {
									mDetailsListView.setPullLoadEnable(false);
								} else {
									mDetailsListView.setPullLoadEnable(true);
								}
								List<GoldLog> tempGoldLogs = page.getRows();
								if (tempGoldLogs != null && tempGoldLogs.size() > 0) {
									if (isOnRefresh) {
										goldLogs.clear();
										Util.printLog("消费记录清除");
									}
									goldLogs.addAll(tempGoldLogs);
									adapter.notifyDataSetChanged();
									setStopRefreshOrLoadMore();
								}

							} else {
								setStopRefreshOrLoadMore();
							}

						} else {
							setStopRefreshOrLoadMore();

						}
					}

				}, getParams(page), this, false);
		refreshOrLoadMoRequestOnBackgrount.execute(Constant.GET_CONSUMER_RECORDS);
	}

	private void setStopRefreshOrLoadMore() {
		if (isOnRefresh) {
			isOnRefresh = false;
			mDetailsListView.stopRefresh();
		} else if (isOnLoadMore) {
			isOnLoadMore = false;
			mDetailsListView.stopLoadMore();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mBack) {
			this.finish();
		} else if (v == mRechargeButton) {
			Util.getInstance().go2Activity(this, RechargeMoneyActivity.class);
		} else if (v == mApplyToCashButton) {
			if (!TextUtils.isEmpty(currentCash) && !currentCash.equals("0.00")) {
				Util.getInstance().go2Activity(this, ApplyToCashActivity.class);
			} else {
				Util.showToast(this, "余额不足无法提现");
			}
		}

	}

	@Override
	public void onRefresh() {
		refreshRecord();
	}

	private void refreshRecord() {
		if (isOnRefresh) return;
		isOnRefresh = true;
		currentPage = 1;
		refreshOrLoadMore(currentPage);
	}

	@Override
	public void onLoadMore() {
		if (isOnLoadMore) return;
		isOnLoadMore = true;
		if ((currentPage + 1) > totalPage) return;
		currentPage += 1;
		refreshOrLoadMore(currentPage);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getCurrentCash();
		refreshRecord();
	}

	/**
	 * 获取账户余额
	 */
	private void getCurrentCash() {
		HttpRequestOnBackgrount getCurrentCashBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.GET, new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						JsonResult<String> currentCashResult = Util.getInstance()
								.getObjFromJsonResult(result, new TypeToken<JsonResult<String>>() {
								});
						if (currentCashResult != null && currentCashResult.isSuccess()) {
							String cash = currentCashResult.getData();
							if (!TextUtils.isEmpty(cash)) {
								currentCash = cash;
								mBalanceTextView.setText(Html.fromHtml("RMB:"
										+ "<font color=\"#ff0000\">" + cash + "</font>"));

							}
						}

					}
				}, null, MyBalanceActivity.this, false);
		getCurrentCashBackgrount.execute(Constant.GET_CURRENT_CASH);
	}
}
