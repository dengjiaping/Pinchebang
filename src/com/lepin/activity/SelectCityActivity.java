package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.lepin.entity.City;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;

@Contextview(R.layout.select_city)
public class SelectCityActivity extends BaseActivity implements OnItemClickListener,
		OnChildClickListener {
	private String key;// 查询key
	@ViewInject(id = R.id.common_title_title)
	private TextView title;

	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;

	@ViewInject(id = R.id.select_city_curr)
	private TextView mCurrCitytView;

	@ViewInject(id = R.id.position_city_title)
	private TextView currCityTitle;

	@ViewInject(id = R.id.select_city_search)
	private EditText searchEditText;

	@ViewInject(id = R.id.search_city_listview)
	private ListView mSearchListView;

	@ViewInject(id = R.id.select_city_listview)
	private ExpandableListView mExpandableListView;

	@ViewInject(id = R.id.select_city_layout)
	private LinearLayout mSelectCityLayout;

	private MyExpandAdapter mAdapter;
	private List<City> mCitiesList;

	private List<City> searchCityList = new ArrayList<City>();
	private City mCurrentCity;
	private boolean isFirstSelectCity = false;
	private Bundle bundle;
	private Util util = Util.getInstance();
	private LocationClient mLocationClient = null;

	private BaseAdapter searchAdapter = new BaseAdapter() {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			City city = searchCityList.get(position);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(SelectCityActivity.this).inflate(
						R.layout.item_city, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.pro = (TextView) convertView.findViewById(R.id.item_city);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.pro.setText(city.getArea_name());
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return searchCityList.get(position);
		}

		@Override
		public int getCount() {
			return searchCityList.size();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		bundle = getIntent().getExtras();
		mCurrentCity = new City();
		if (bundle != null) isFirstSelectCity = bundle.getBoolean(Constant.SGETUSERLOCATION);
		initView();
		startLocating();

	}

	private void startLocating() {
		mLocationClient = Util.getInstance().getLocationClient(SelectCityActivity.this,
				new BDLocationListener() {

					@Override
					public void onReceiveLocation(BDLocation arg0) {
						if (arg0.getCityCode() != null && arg0.getCity() != null) {
							mCurrentCity.setArea_id(Integer.parseInt(arg0.getCityCode()));
							mCurrentCity.setArea_name(arg0.getCity());
							// 设置当前显示当前定位城市
							mCurrCitytView.setText(arg0.getCity());
							mCurrCitytView.setOnClickListener(listener);
						}
					}

				});
		mLocationClient.start();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		this.title.setText(getResources().getString(R.string.select_city));// 初始化title
		if (isFirstSelectCity) {
			btnBack.setVisibility(View.INVISIBLE);
		} else {
			this.btnBack.setOnClickListener(listener);
		}
		loadArea();
		this.searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				key = s.toString().trim();
				if (key.length() > 0) {
					toggle(true);
					searchArea(key);
				} else {
					toggle(false);
				}
			}
		});
		this.mSearchListView.setAdapter(searchAdapter);
		this.mSearchListView.setOnItemClickListener(this);
		mExpandableListView.setOnChildClickListener(this);
	}

	protected void saveLocation(City city) {
		util.saveCity(SelectCityActivity.this, city);
	}

	/**
	 * 按钮事件监听
	 */
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == btnBack) {
				SelectCityActivity.this.finish();
			} else if (v == mCurrCitytView) {
				finish(mCurrentCity);
			}
		}
	};

	private class MyExpandAdapter extends BaseExpandableListAdapter {
		@Override
		public int getGroupCount() {
			return mCitiesList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mCitiesList.get(groupPosition).getSubCitys().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mCitiesList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mCitiesList.get(groupPosition).getSubCitys().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return mCitiesList.get(groupPosition).getArea_id();
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return mCitiesList.get(groupPosition).getSubCitys().get(childPosition).getArea_id();
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
				ViewGroup parent) {
			City city = mCitiesList.get(groupPosition);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(SelectCityActivity.this).inflate(
						R.layout.item_province, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.pro = (TextView) convertView.findViewById(R.id.item_pro);
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_pro_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (city.getSubCitys().size() <= 0) {
				viewHolder.icon.setImageDrawable(null);
			} else {
				if (isExpanded) {
					viewHolder.icon.setImageResource(R.drawable.list_tittle_arrow2);
				} else {
					viewHolder.icon.setImageResource(R.drawable.list_tittle_arrow1);
				}
			}
			viewHolder.pro.setText(city.getArea_name());
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
				View convertView, ViewGroup parent) {
			City city = mCitiesList.get(groupPosition).getSubCitys().get(childPosition);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(SelectCityActivity.this).inflate(
						R.layout.item_city, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.pro = (TextView) convertView.findViewById(R.id.item_city);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.pro.setText(city.getArea_name());
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	public static class ViewHolder {
		public TextView pro;
		public ImageView icon;
	}

	private void loadArea() {

		mCitiesList = Util.getInstance().loadArea(this);
		// 省
		setExpandAdapter();
	}

	private void setExpandAdapter() {
		this.mAdapter = new MyExpandAdapter();
		this.mExpandableListView.setAdapter(this.mAdapter);
		mExpandableListView.setGroupIndicator(null);
		mExpandableListView.setFooterDividersEnabled(false);
		mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
					long id) {
				final City city = mCitiesList.get(groupPosition);
				if (city.getSubCitys() == null || city.getSubCitys().size() < 1) {
					mCurrentCity = city;
					finish(mCurrentCity);
					return true;
				}
				return false;
			}
		});
	}

	private void searchArea(String cityName) {

		if (searchCityList.size() > 0) searchCityList.clear();
		for (City city : mCitiesList) {
			if (city.getArea_name().equals(cityName) || city.getArea_name().contains(cityName)) {
				searchCityList.add(city);
			} else {
				ArrayList<City> sCities = city.getSubCitys();
				if (sCities != null && sCities.size() > 0) {
					for (City sCity : sCities) {
						if (sCity.getArea_name().equals(cityName)
								|| sCity.getArea_name().contains(cityName))
							searchCityList.add(sCity);
					}
				}
			}

		}
		searchAdapter.notifyDataSetChanged();

	}

	// 统一退出函数
	private void finish(City city) {
		if (!Constant.isGetLocation) {
			Util.getInstance().saveCity(SelectCityActivity.this, city);
			util.go2Activity(SelectCityActivity.this, CarSharingActivity.class);
		} else {
			Intent intent = getIntent();
			intent.putExtra("city", city);
			setResult(Constant.SELECT_CITY_RESULT_CODE, intent);
		}
		super.finish();
	}

	private void toggle(boolean showSearchListView) {
		if (showSearchListView) {
			mSearchListView.setVisibility(View.VISIBLE);
			mSelectCityLayout.setVisibility(View.INVISIBLE);
			// 这样才能实现第一次的时候隐藏这些view
		} else {
			mSearchListView.setVisibility(View.INVISIBLE);
			mSelectCityLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		finish(searchCityList.get(position));
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
			int childPosition, long id) {
		finish(mCitiesList.get(groupPosition).getSubCitys().get(childPosition));
		return true;
	}

	@Override
	protected void onStop() {
		mLocationClient.stop();
		super.onStop();
	}
}
