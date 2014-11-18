package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.lepin.adapter.AddressAdapter;
import com.lepin.entity.Address;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;

/**
 * 发布信息是基于LBS利用百度地图POI搜索功能进行起点和终点的搜索，用户输入的起点和终点必须是有效地址，在百度地图上能够找到相对应的点。
 * 如果用户输入的地址在地图无法找到将无法进行相关操作。
 * 
 */
@Contextview(R.layout.choice_adrr_from_map)
public class ChoiceAdrrActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;// 返回

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;// 标题

	@ViewInject(id = R.id.choice_adrr_satrt)
	private EditText mAddrEditText;// 起点

	@ViewInject(id = R.id.adrr_listview)
	private ListView mListview;// POI检索到的地址列表

	@ViewInject(id = R.id.search_refresh)
	private ImageView mRefreshImView;

	private int mExtraType;

	private List<Address> adrrResult = new ArrayList<Address>();// 地址结果集
	private AddressAdapter mAdapter = null;// 地址设配器
	private String hint;

	private PoiSearch mPoiSearch = null;
	private String mCityCode = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		mExtraType = getIntent().getIntExtra(Constant.S_ADDR, Constant.I_START);
		initView();
		initBaiduMap();
	}

	private OnGetPoiSearchResultListener mGetPoiSearchResultListener = new OnGetPoiSearchResultListener() {

		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			// 获取POI检索结果
			clearData();
			List<PoiInfo> allPoi = poiResult.getAllPoi();
			if (allPoi == null || allPoi.size() < 1) {
				Util.showToast(ChoiceAdrrActivity.this, getString(R.string.research));
				return;
			}

			for (PoiInfo p : allPoi) {
				if (p.name.endsWith("市")) continue;
				if (p.location == null) continue;
				final long lat = (long) (p.location.latitude * 1e6);// 纬度
				final long lon = (long) (p.location.longitude * 1e6);// 经度
				if (lat <= 0 || lon <= 0) continue;// 过滤掉没有经纬度的
				Address address = new Address();
				address.setCity(p.city);
				address.setAddress(p.address);
				address.setName(p.name);
				address.setX(lon);
				address.setY(lat);
				adrrResult.add(address);
			}
			stopAnimation();
			if (adrrResult.size() > 0) {
				mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult arg0) {
			// 获取Place详情页检索结果

		}
	};

	/**
	 * 初始化组件
	 */
	private void initView() {
		mTitle.setText(getString(R.string.choice_location));
		mBack.setOnClickListener(this);
		mListview.setOnItemClickListener(this);
		mAddrEditText.addTextChangedListener(new TextChangedListener());
		hint = Constant.I_START == mExtraType ? getString(R.string.search_start_point_hint)
				: getString(R.string.search_end_point_hint);
		mAddrEditText.setHint(hint);
		mAdapter = new AddressAdapter(adrrResult, ChoiceAdrrActivity.this);
		mListview.setAdapter(mAdapter);
	}

	protected void startAnimation() {
		Util.getInstance().startAnimation(ChoiceAdrrActivity.this, mRefreshImView);
	}

	protected void stopAnimation() {
		Util.getInstance().stopAnimation(mRefreshImView);
	}

	/**
	 * 初始化地图
	 */
	private void initBaiduMap() {
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(mGetPoiSearchResultListener);
	}

	protected void search(String keyWord, String city) {
		mPoiSearch.searchInCity((new PoiCitySearchOption()).keyword(keyWord)
				.city(city == null ? Constant.currCity : city).pageCapacity(20).pageNum(0));
	}

	public class TextChangedListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.length() > 0) {
				clearData();
				startAnimation();
				search(s.toString(), Constant.currCity);
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int postion, long arg3) {
		final Address address = adrrResult.get(postion);
		Util.printLog("onItemClick-address:" + address.toString());
		setReturnData(address);
		// if (address.getX() > 0 && address.getY() > 0) {
		// } else {
		// search(address.getName(), address.getCity());
		// }

	}

	@Override
	public void onClick(View view) {
		if (view == this.mBack) {
			exit();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		exit();
	}

	private void exit() {
		finish();
		overridePendingTransition(R.anim.main_activity_in, R.anim.choice_addr_activity_out);
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		super.onDestroy();
	}

	private void clearData() {
		adrrResult.clear();
		mAdapter.notifyDataSetChanged();
	}

	private void setReturnData(Address address) {
		mCityCode = Util.getInstance().getCityCode(ChoiceAdrrActivity.this, address.getCity());
		Util.printLog("得到的城市ID:" + mCityCode);
		Intent intent = new Intent();
		intent.putExtra(Constant.SLAT, address.getY());
		intent.putExtra(Constant.SLON, address.getX());
		intent.putExtra(Constant.CITY_CODE, mCityCode);
		Util.printLog("纬度：" + address.getY() + "---------经度：" + address.getX());
		intent.putExtra(Constant.CITY, address.getCity());
		if (Constant.I_START == mExtraType) {// 起点
			intent.putExtra(Constant.S_START, address.getName());
			setResult(Constant.S_CHOICE_ADRR_RESULT_START, intent);
		} else if (Constant.I_END == mExtraType) {
			intent.putExtra(Constant.S_END, address.getName());
			setResult(Constant.S_CHOICE_ADRR_RESULT_END, intent);
		} else if (Constant.I_THROUTH_POINT == mExtraType) {// 选择途径点
			intent.putExtra(Constant.S_THROUTH_POINT, address.getName());
			setResult(Constant.I_THROUTH_POINT, intent);
		}
		exit();
	}
}
