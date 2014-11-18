package com.lepin.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.Pinche;
import com.lepin.util.TimeUtils;

/**
 * 我的拼车信息适配器
 * 
 */
public class MyInfoAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater layoutInflater;
	private ArrayList<Pinche> myInfoList;

	public MyInfoAdapter(Context context, ArrayList<Pinche> paramList) {
		this.mContext = context;
		this.myInfoList = paramList;
		this.layoutInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		if (this.myInfoList != null) {
			return this.myInfoList.size();
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
			convertView = this.layoutInflater.inflate(R.layout.new_item_my_info, parent,false);
			viewHolder = new ViewHolder();
			viewHolder.startTv = (TextView) convertView.findViewById(R.id.item_my_info_start);
			viewHolder.endTv = (TextView) convertView.findViewById(R.id.item_my_info_end);
			viewHolder.dateTv = (TextView) convertView.findViewById(R.id.item_my_info_date);
			viewHolder.expired = (TextView) convertView.findViewById(R.id.item_my_info_expired);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_my_info_icon);
			viewHolder.startIv = (ImageView) convertView.findViewById(R.id.icon_start);
			viewHolder.endIv = (ImageView) convertView.findViewById(R.id.icon_end);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Pinche info = this.myInfoList.get(position);
		viewHolder.startTv.setText(info.getStart_name(mContext));
		viewHolder.endTv.setText(info.getEnd_name(mContext));
		if (info.getInfoType().equals(Pinche.DRIVER)) {// 司机
			viewHolder.icon.setBackgroundResource(R.drawable.ic_tip2);
		} else {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_tip1);
		}
		if (info.getCarpoolType().equals(Pinche.CARPOOLTYPE_LONG_TRIP)) {// 长途
			if (info.isExpired()) {
				viewHolder.startIv.setImageResource(R.drawable.ic_starting2);
				viewHolder.endIv.setImageResource(R.drawable.ic_end2);
				viewHolder.dateTv.setText("出发：" + info.getDepartureTime());// 出发时间（长途）
				viewHolder.expired.setVisibility(View.VISIBLE);
			} else {
				viewHolder.startIv.setImageResource(R.drawable.ic_starting);
				viewHolder.endIv.setImageResource(R.drawable.ic_end);
				viewHolder.dateTv.setText("出发：" + info.getDepartureTime());// 出发时间（长途）
				viewHolder.expired.setVisibility(View.GONE);
			}

		} else {// 上下班
			viewHolder.startIv.setImageResource(R.drawable.ic_starting);
			viewHolder.endIv.setImageResource(R.drawable.ic_end);
			viewHolder.expired.setVisibility(View.GONE);
			if ("".equals(info.getBackTime())) {
				viewHolder.dateTv
						.setText(info.getCycle().getTxt() + "\n(早：" + info.getDepartureTime() + ")");// 出发时间单程（上下班）
			} else {
				viewHolder.dateTv.setText(info.getCycle().getTxt() + "\n(早：" + info.getDepartureTime()
						+ " 晚：" + TimeUtils.secondToDate(Long.parseLong(info.getBackTime())) + ")");// 出发时间（上下班）
			}
		}
		convertView.setId(info.getInfo_id());
		return convertView;
	}

	public final class ViewHolder {
		public TextView startTv;
		public TextView endTv;
		public TextView dateTv;
		public TextView expired;
		public ImageView icon;
		public ImageView startIv;
		public ImageView endIv;

		public ViewHolder() {
		}
	}

}
