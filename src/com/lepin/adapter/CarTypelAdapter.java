package com.lepin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.CarType;

public class CarTypelAdapter extends BaseAdapter {
	private Context context;
	private List<CarType> list;
	private LayoutInflater layoutInflater;

	public CarTypelAdapter(Context context, List<CarType> list) {
		this.context = context;
		this.list = list;
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
		CarType carType = list.get(position);
		if (convertView == null) {
			convertView = this.layoutInflater
					.inflate(R.layout.new_cartype_data_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.carmodel_item = (TextView) convertView.findViewById(R.id.cartype_data);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.carmodel_item.setText(carType.getCarTypeName());
		return convertView;
	}

	static class ViewHolder {
		private TextView carmodel_item;
	}
}
