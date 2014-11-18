package com.lepin.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.Pinche;
import com.lepin.entity.Point;

/**
 * 广场拼车信息ListView适配器
 * 
 * 
 */
public class PincheListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<Pinche> mPincheDataList;

	public PincheListAdapter(Context paramContext, ArrayList<Pinche> paramArrryList) {
		this.context = paramContext;
		this.layoutInflater = LayoutInflater.from(this.context);
		this.mPincheDataList = paramArrryList;
	}

	@Override
	public int getCount() {
		if (this.mPincheDataList != null) {
			return this.mPincheDataList.size();
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
		// 根据item id获取拼车实体对象
		Pinche pinche = mPincheDataList.get(position);
		if (convertView == null) {
			convertView = this.layoutInflater.inflate(R.layout.new_item_plazz, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.fromTv = (TextView) convertView.findViewById(R.id.item_start_addr);
			viewHolder.toTv = (TextView) convertView.findViewById(R.id.item_end_addr);
			viewHolder.dateTv = (TextView) convertView.findViewById(R.id.item_text_date);
			viewHolder.driverVerified = (ImageView) convertView
					.findViewById(R.id.new_item_driver_verified);
			viewHolder.passengers = (TextView) convertView
					.findViewById(R.id.new_item_driver_passenger);
			viewHolder.layout = (RelativeLayout) convertView
					.findViewById(R.id.pathway_point_layout);
			viewHolder.point1 = (TextView) convertView.findViewById(R.id.point1);
			viewHolder.point2 = (TextView) convertView.findViewById(R.id.point2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 根据item id获取拼车实体对象
		viewHolder.fromTv.setText(pinche.getStart_name());// 起点
		viewHolder.toTv.setText(pinche.getEnd_name());// 终点
		if (pinche.getCarpoolType().equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {
			viewHolder.dateTv.setText(context.getString(R.string.work_day) + "(早:"
					+ pinche.getDepartureTime() + " 晚:" + pinche.getBackTime() + ")");
		} else {
			viewHolder.dateTv.setText(context.getString(R.string.publish_start_date) + " :"
					+ pinche.getDepartureTime());
		}

		if (pinche.getInfoType().equals(Pinche.DRIVER)) {// 司机所发
			int num = pinche.getNum();
			if (num > 0) {
				viewHolder.passengers.setText(context.getString(R.string.people_num, num));
			}
			Point[] points = pinche.getPoints();
			if (null != points) {
				viewHolder.layout.setVisibility(View.VISIBLE);
				TextView[] textViews = new TextView[2];
				textViews[0] = viewHolder.point1;
				textViews[1] = viewHolder.point2;
				for (int i = 0; i < points.length; i++) {
					textViews[i].setVisibility(View.VISIBLE);
					textViews[i].setText(points.length == 1 ? "途经：" + points[i].getName() : "途径"
							+ (i + 1) + ":" + points[i].getName());
				}
			}
		}
		if (pinche.getUser() != null) {
			if (pinche.getUser().isUserStateVerify()) {
				viewHolder.driverVerified.setVisibility(View.VISIBLE);// 显示验证图标
			}
		}
		return convertView;
	}

	/**
	 * item子项缓存
	 */
	public static class ViewHolder {
		public TextView dateTv;// 出发时间
		public TextView fromTv;// 起点
		public TextView toTv;// 终点
		public TextView passengers;// 可载人数
		public ImageView driverVerified;

		public RelativeLayout layout; // 途经点布局
		public TextView point1; // 途经点1
		public TextView point2; // 途经点2
	}

}
