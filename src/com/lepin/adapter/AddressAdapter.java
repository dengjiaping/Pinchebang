package com.lepin.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.Address;

/**
 * 百度地图搜索的地址信息
 * 
 * 
 */
public class AddressAdapter extends BaseAdapter {
	private List<Address> mAddress;
	private Activity mActivity;

	public AddressAdapter(List<Address> mAddress, Activity mActivity) {
		this.mAddress = mAddress;
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		return this.mAddress.size();
	}

	@Override
	public Object getItem(int position) {
		return mAddress.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.mActivity).inflate(R.layout.item_addr, null);
			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.result_addr);
			viewHolder.tvCity = (TextView) convertView.findViewById(R.id.result_city);
			viewHolder.tvAddr = (TextView) convertView.findViewById(R.id.result_district);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvName.setText(this.mAddress.get(position).getName());
		viewHolder.tvCity.setText(this.mAddress.get(position).getCity());
		viewHolder.tvAddr.setText(this.mAddress.get(position).getAddress());
		return convertView;
	}

	public static class ViewHolder {
		public TextView tvName;
		public TextView tvCity;
		public TextView tvAddr;
	}

}
