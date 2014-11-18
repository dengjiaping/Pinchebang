package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lepin.adapter.MyPincheCashRecordAdapter;
import com.lepin.entity.CoinRecord;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Page;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbListView.PcbListViewListener;

@Contextview(R.layout.my_pc_record)
public class MyPinCheRecordActivity extends BaseActivity implements PcbListViewListener,
		OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView reBack;// 返回
	@ViewInject(id = R.id.common_title_title)
	private TextView reTitle;// 标题
	@ViewInject(id = R.id.my_record_is_null)
	private TextView reNoRecord;// 没有记录信息

	@ViewInject(id = R.id.my_pc_record_listview)
	private ListView reListView;

	private Page<CoinRecord> records;
	private ArrayList<CoinRecord> coinRecords = new ArrayList<CoinRecord>();

	private int reTotalPage = 1;// 总页数
	private int reCurrentPage = 1;
	private int rows = 10;
	private MyPincheCashRecordAdapter adapter;// 适配器
	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		getCoinLog(reCurrentPage);
	}

	public void initView() {
		// reListView.setEnabled(false);
		reTitle.setText(R.string.my_pc_record);
		reBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == reBack) {
			this.finish();
		}
	}

	/**
	 * 获取消费记录
	 */

	public void getCoinLog(int currentPage) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("rows", String.valueOf(rows)));
		params.add(new BasicNameValuePair("page", String.valueOf(currentPage)));

		util.doPostRequest(MyPinCheRecordActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				TypeToken<JsonResult<Page<CoinRecord>>> token = new TypeToken<JsonResult<Page<CoinRecord>>>() {
				};
				Gson gson = new GsonBuilder().create();
				JsonResult<Page<CoinRecord>> jsonResult = gson.fromJson(result, token.getType());

				if (jsonResult.isSuccess()) {
					Util.printLog("record:" + result);
					records = jsonResult.getData();// 获取信息
					reTotalPage = records.getPageCount();// 总页数
					setRecordData();

					if (coinRecords.size() > 0) {
						setAdapter(coinRecords);
					} else {
						Util.showToast(MyPinCheRecordActivity.this, "你还没记录信息");
						reNoRecord.setVisibility(View.VISIBLE);
						reListView.setVisibility(View.GONE);
					}
				} else {
					Util.showToast(MyPinCheRecordActivity.this, jsonResult.getErrorMsg());
				}
			}
		}, params, Constant.URL_GETUSERCOINLOG, "", false);


	}

	/**
	 * 设置适配器
	 */

	public void setAdapter(ArrayList<CoinRecord> list) {
		this.adapter = new MyPincheCashRecordAdapter(this, list);
		this.adapter.notifyDataSetChanged();
		this.reListView.setAdapter(adapter);
	}

	/**
	 * 设置数据
	 */

	public void setRecordData() {

		if (reCurrentPage < reTotalPage) {
			coinRecords.addAll(records.getRows());
			reCurrentPage++;
		} else {
			coinRecords.addAll(records.getRows());
			if (reCurrentPage > 1) Util.showToast(this, "没有更多数据");
		}

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		getCoinLog(reCurrentPage);
	}

}
