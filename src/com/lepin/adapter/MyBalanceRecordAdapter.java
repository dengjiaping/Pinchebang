package com.lepin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.GoldLog;

public class MyBalanceRecordAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<GoldLog> recordLists;

	public MyBalanceRecordAdapter(Context context, List<GoldLog> coinRecords) {
		this.context = context;
		this.recordLists = coinRecords;
		this.inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		if (recordLists != null) {
			return recordLists.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.new_item_pc_record, parent, false);
			viewHolder = new ViewHolder();

			viewHolder.typeTv = (TextView) convertView.findViewById(R.id.record_type);// 类型
			viewHolder.timeTv = (TextView) convertView.findViewById(R.id.record_time);// 时间
			viewHolder.amountTv = (TextView) convertView.findViewById(R.id.record_amount);// 金额

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		GoldLog record = recordLists.get(position);
		if (record != null) {
			viewHolder.typeTv.setText(record.getBrief());
			viewHolder.timeTv.setText(record.getLogTime());
			viewHolder.amountTv.setText(record.getAmount());

		}
		return convertView;
	}

	public final class ViewHolder {
		private TextView typeTv;// 消费类型
		private TextView amountTv;// 消费金额
		private TextView timeTv;// 消费时间

		public ViewHolder() {
		}
	}

}
