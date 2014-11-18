package com.lepin.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.adapter.CarBrandAdapter;
import com.lepin.adapter.CarTypelAdapter;
import com.lepin.entity.CarBrand;
import com.lepin.entity.CarType;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

@Contextview(R.layout.car_brand)
public class SelectCarBrandActivity extends BaseActivity implements OnClickListener {

	private List<CarBrand> carTypes = null;// 数据源
	private List<CarBrand> newCarTypes = new ArrayList<CarBrand>();// 排序后的数据信息
	private List<CarBrand> selCarTypes = null;// 搜索时符合条件的数据信息
	private List<CarType> carModels = null;// 车辆型号
	private CarBrandAdapter adapter = null;
	private CarTypelAdapter modelAdapter = null;

	@ViewInject(id = R.id.layout)
	private LinearLayout layoutIndex;

	private HashMap<String, Integer> selector;// 存放含有索引字母的位置

	@ViewInject(id = R.id.search_brand_listview)
	private ListView listView;

	@ViewInject(id = R.id.common_title_back)
	private ImageView backTv;// 返回

	@ViewInject(id = R.id.common_title_title)
	private TextView titleTv;// 标题

	@ViewInject(id = R.id.showTv)
	private TextView showTv;// 索引字母提示

	@ViewInject(id = R.id.search_input)
	private EditText search;// 搜索框

	private int height;
	private boolean flag = false;
	private boolean isModel = false;
	private String brand_name;
	private String[] indexStr = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		init();
		getCarTypes();// 获取数据

	}

	public void init() {
		titleTv.setText("品牌");
		backTv.setOnClickListener(this);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String searchStr = search.getText().toString().trim();
				if (searchStr.length() > 0) {
					selCarTypes = selCarTypesList(searchStr);
					adapter = new CarBrandAdapter(SelectCarBrandActivity.this, selCarTypes, true);
					listView.setAdapter(adapter);
					layoutIndex.setVisibility(View.GONE);
				} else {
					adapter = new CarBrandAdapter(SelectCarBrandActivity.this, newCarTypes, false);
					listView.setAdapter(adapter);
					layoutIndex.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (!isModel) {
					CarBrand carBrand = (CarBrand) adapter.getItem(position);
					if (carBrand.getCarBrandName().length() != 1 && carBrand.getInitials() != null
							&& carBrand.getCarBrandId() != null) {
						search.setEnabled(false);
						isModel = true;
						brand_name = carBrand.getCarBrandName();
						String car_brand_id = String.valueOf(carBrand.getCarBrandId());
						getCarModels(car_brand_id);

					}
				} else {
					CarType carType = (CarType) modelAdapter.getItem(position);
					String modelName = carType.getCarTypeName();
					int modelId = carType.getCarTypeId();
					Intent intent = new Intent(SelectCarBrandActivity.this, MyLoveCarActivity.class);
					intent.putExtra("cartype", modelName);
					intent.putExtra("typeId", modelId);
					setResult(RESULT_OK, intent);
					SelectCarBrandActivity.this.finish();
				}
			}
		});
	}

	/**
	 * 从服务器获取车辆品牌
	 */
	public void getCarTypes() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		util.doPostRequest(SelectCarBrandActivity.this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				layoutIndex.setVisibility(View.VISIBLE);
				carTypes = new ArrayList<CarBrand>();
				JsonResult<ArrayList<CarBrand>> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<ArrayList<CarBrand>>>() {
						});
				if (jsonResult.isSuccess()) {
					carTypes = jsonResult.getData();// 得到cartype数据
					if (carTypes.size() != 0) {
						if (carTypes != null) {
							String[] allNames = sortIndex(carTypes);// 按首字母排序
							sortList(allNames);

							adapter = new CarBrandAdapter(SelectCarBrandActivity.this, newCarTypes,
									false);
							listView.setAdapter(adapter);
							selector = new HashMap<String, Integer>();
							for (int i = 0; i < indexStr.length; i++) {
								for (int j = 0; j < newCarTypes.size(); j++) {
									if (indexStr[i].equals(newCarTypes.get(j).getCarBrandName())) {
										selector.put(indexStr[i], j);
									}
								}
							}
						}
					} else {
						Util.showToast(SelectCarBrandActivity.this,
								getString(R.string.common_no_data));
					}
				} else {
					Util.showToast(SelectCarBrandActivity.this, jsonResult.getErrorMsg());
				}
			}
		}, params, Constant.URL_GETALLCARBRANDS, "", false);

	}

	/**
	 * 把数据排序，并把A-Z顺序加进去
	 * 
	 * @param carTypes
	 * @return
	 */
	public String[] sortIndex(List<CarBrand> carTypes) {
		TreeSet<String> set = new TreeSet<String>();
		for (CarBrand carBrand : carTypes) {
			char ch = carBrand.getInitials().charAt(0);
			set.add(String.valueOf(ch).toUpperCase(Locale.getDefault()));// 获取首字母
		}
		String[] names = new String[carTypes.size() + set.size()];// 新数组，用于保存首字母和车辆类型
		int i = 0;
		for (String string : set) { // 把set中的字母添加到新数组中（前面）
			names[i] = string;
			i++;
		}

		String[] pyheader = new String[carTypes.size()];
		for (int j = 0; j < carTypes.size(); j++) {
			pyheader[j] = carTypes.get(j).getInitials();
		}

		System.arraycopy(pyheader, 0, names, set.size(), pyheader.length);// 将转换为拼音的数组加到新数组后面
		// 自动按照首字母排序
		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);// 严格按照字母顺序排序，忽略字母大小写，结果为按拼音排序的数组返回
		return names;

	}

	public void sortList(String[] names) {
		for (int i = 0; i < names.length; i++) {
			if (names[i].length() != 1) {
				for (int j = 0; j < carTypes.size(); j++) {
					if (names[i] == carTypes.get(j).getInitials()) {
						CarBrand carBrand = new CarBrand();
						carBrand.setCarBrandName(carTypes.get(j).getCarBrandName());
						carBrand.setInitials(carTypes.get(j).getInitials());
						carBrand.setCarBrandId(carTypes.get(j).getCarBrandId());
						newCarTypes.add(carBrand);
					}
				}
			} else {
				CarBrand carBrand = new CarBrand();
				carBrand.setInitials(names[i]);
				carBrand.setCarBrandName(names[i]);
				carBrand.setCarBrandId(001);
				newCarTypes.add(carBrand);
			}
		}
	}

	public void drawIndexView() {
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
		for (int i = 0; i < indexStr.length; i++) {
			TextView tv = new TextView(this);
			tv.setTextColor(getResources().getColor(R.color.btn_blue_normal));
			tv.setLayoutParams(params);
			tv.setText(indexStr[i]);
			tv.setGravity(Gravity.CENTER);

			layoutIndex.addView(tv);
			layoutIndex.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					float y = event.getY();
					int index = (int) y / height;// 得到点击字母位置的索引
					if (index < indexStr.length && index > -1) {
						String key = indexStr[index];
						if (selector.containsKey(key)) {
							int position = selector.get(key);
							if (listView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏。
								listView.setSelectionFromTop(
										position + listView.getHeaderViewsCount(), 0);
							} else {
								listView.setSelectionFromTop(position, 0);// 滑动到第一项
							}
							showTv.setText(key);
							showTv.setVisibility(View.VISIBLE);
						}
					}
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						showTv.setVisibility(View.GONE);
						break;
					case MotionEvent.ACTION_DOWN:
						// layoutIndex.setBackground();
						break;
					}

					return true;
				}
			});
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (!flag) {
			height = layoutIndex.getMeasuredHeight() / indexStr.length;
			drawIndexView();
			flag = true;
		}
	}

	/**
	 * 获取与输入内容首字母相同的车辆信息
	 * 
	 * @param name
	 * @return
	 */
	public ArrayList<CarBrand> selCarTypesList(String name) {
		ArrayList<CarBrand> list = new ArrayList<CarBrand>();

		if (newCarTypes != null) {
			final int size = newCarTypes.size();
			for (int i = 0; i < size; i++) {
				if (newCarTypes.get(i).getInitials() != null
						&& newCarTypes.get(i).getCarBrandId() != null) {
					if (newCarTypes.get(i).getInitials().startsWith(name)
							|| newCarTypes.get(i).getCarBrandName().startsWith(name)) {
						CarBrand carBrand = newCarTypes.get(i);
						list.add(carBrand);
					}
				}
			}
		}
		return list;

	}

	/**
	 * 获取某一品牌车型的各种型号
	 * 
	 * @param modelId
	 */
	public void getCarModels(String modelId) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("carBrandId", modelId));

		util.doPostRequest(SelectCarBrandActivity.this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<ArrayList<CarType>> jsonResultModel = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<ArrayList<CarType>>>() {
						});
				if (jsonResultModel != null && jsonResultModel.isSuccess()) {
					carModels = jsonResultModel.getData();// 得到cartype数据
					if (carModels != null) {
						layoutIndex.setVisibility(View.INVISIBLE);
						titleTv.setText(brand_name);
						// search.setHint("请选择汽车型号");
						modelAdapter = new CarTypelAdapter(SelectCarBrandActivity.this, carModels);
						listView.setAdapter(modelAdapter);
					}
				} else {
					Util.showToast(SelectCarBrandActivity.this, jsonResultModel.getErrorMsg());
					isModel = false;
					search.setEnabled(true);
				}

			}
		}, params, Constant.URL_GETCARTYPE, "", false);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isModel) {
				isModel = false;
				search.setEnabled(true);
				this.layoutIndex.setVisibility(View.VISIBLE);
				titleTv.setText("品牌");
				search.setHint("请输入车名或首字母");
				adapter = new CarBrandAdapter(SelectCarBrandActivity.this, newCarTypes, false);
				listView.setAdapter(adapter);
			} else {
				this.finish();
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {

		if (v == backTv) {
			if (isModel) {
				isModel = false;
				search.setEnabled(true);
				this.layoutIndex.setVisibility(View.VISIBLE);
				titleTv.setText("品牌");
				search.setHint("请输入车名或首字母");
				adapter = new CarBrandAdapter(SelectCarBrandActivity.this, newCarTypes, false);
				listView.setAdapter(adapter);
			} else {
				this.finish();
			}
		}
	}

}
