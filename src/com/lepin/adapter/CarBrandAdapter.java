package com.lepin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.CarBrand;

public class CarBrandAdapter extends BaseAdapter {
	private Context context;
	private List<CarBrand> list;
	private LayoutInflater layoutInflater;
	private boolean input = false;// 用于判断是索引还是输入框

	public CarBrandAdapter(Context context, List<CarBrand> list, boolean input) {
		this.context = context;
		this.list = list;
		this.input = input;
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		CarBrand carBrand = list.get(position);

		if (!input) {
			if (carBrand.getCarBrandName().length() == 1)// 标题
			{
				convertView = this.layoutInflater.inflate(R.layout.new_cartype_title_item, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.cartype_item = (TextView) convertView.findViewById(R.id.cartype_title);
				convertView.setTag(viewHolder);
			} else {
				convertView = this.layoutInflater.inflate(R.layout.new_cartype_data_item, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.cartype_item = (TextView) convertView.findViewById(R.id.cartype_data);
				convertView.setTag(viewHolder);
			}

		} else {
			convertView = this.layoutInflater.inflate(R.layout.new_cartype_data_item, parent,false);
			viewHolder = new ViewHolder();
			viewHolder.cartype_item = (TextView) convertView.findViewById(R.id.cartype_data);
			convertView.setTag(viewHolder);
		}
		viewHolder.cartype_item.setText(carBrand.getCarBrandName());
		return convertView;
	}

	private class ViewHolder {
		private TextView cartype_item;
//		private TextView cartype_index;

		public ViewHolder() {
		}
	}

}
