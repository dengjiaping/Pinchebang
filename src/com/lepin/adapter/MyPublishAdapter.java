package com.lepin.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.Pinche;
import com.lepin.entity.Point;
import com.lepin.util.TimeUtils;

public class MyPublishAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<Pinche> mPincheDataList;
	private StateCallBack callBack;// 回调函数，用户处理消息的开关和删除

	public MyPublishAdapter(Context context, ArrayList<Pinche> mPincheDataList,
			StateCallBack callBack) {
		this.context = context;
		this.mPincheDataList = mPincheDataList;
		this.layoutInflater = LayoutInflater.from(this.context);
		this.callBack = callBack;
	}

	public interface StateCallBack {
		public void execute(View v, int info_id, int objIndex, String state);

	}

	@Override
	public int getCount() {
		return mPincheDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mPincheDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final Pinche pinche = mPincheDataList.get(position);
		if (convertView == null) {
			convertView = this.layoutInflater.inflate(R.layout.new_item_mypublish, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.start = (TextView) convertView.findViewById(R.id.item_mypublish_start);
			viewHolder.end = (TextView) convertView.findViewById(R.id.item_mypublish_end);
			viewHolder.btnimg = (ImageView) convertView.findViewById(R.id.item_mypublish_btn);
			viewHolder.layout = (RelativeLayout) convertView
					.findViewById(R.id.pathway_point_layout);
			viewHolder.point1 = (TextView) convertView.findViewById(R.id.point1);
			viewHolder.point2 = (TextView) convertView.findViewById(R.id.point2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.layout.setVisibility(View.GONE);
		viewHolder.start.setText(pinche.getStart_name(context));// 起点
		viewHolder.end.setText(pinche.getEnd_name(context));// 终点
		viewHolder.point1.setVisibility(View.GONE);
		viewHolder.point2.setVisibility(View.GONE);
		Point[] points = pinche.getPoints();
		if (null != points) {
			int length = points.length;
			for (int i = 0; i < length; i++) {
				TextView throuthPoinTextView = null;
				if (i == 0) {
					throuthPoinTextView = viewHolder.point1;
				} else if (i == 1) {
					throuthPoinTextView = viewHolder.point2;
				}
				if (length == 1) {
					throuthPoinTextView.setText(context.getString(
							R.string.through_point_num_no_index, points[i].getName()));
				} else if (length == 2) {
					throuthPoinTextView.setText(context.getString(R.string.through_point_num,
							(i + 1), points[i].getName()));
				}
				throuthPoinTextView.setVisibility(View.VISIBLE);
			}
			viewHolder.layout.setVisibility(View.VISIBLE);
		}

		if (pinche.getCarpoolType().equals(Pinche.CARPOOLTYPE_ON_OFF_WORK))// 上下班
		{
			if (pinche.getState().equals(Pinche.STATE_COLSE)) {
				viewHolder.btnimg.setBackgroundResource(R.drawable.pcb_sitting_off);
			} else if (pinche.getState().equals(Pinche.STATE_NORMAL)) {
				viewHolder.btnimg.setBackgroundResource(R.drawable.pcb_sitting_on);
			}
			setOnClickListener(viewHolder.btnimg, pinche.getInfo_id(), pinche.getState(), position);
		} else {// 长途
			Calendar nowCalendar = Calendar.getInstance();
			nowCalendar.setTimeInMillis(System.currentTimeMillis());
			Calendar lCalendar = Calendar.getInstance();
			lCalendar
					.setTime(new Date(1000 * TimeUtils.dateStrToSecond(pinche.getDepartureTime())));
			if (nowCalendar.before(lCalendar)) {// 没过期
				if (pinche.getState().equals(Pinche.STATE_COLSE)) {// 关闭
					viewHolder.btnimg.setBackgroundResource(R.drawable.pcb_sitting_off);
				} else if (pinche.getState().equals(Pinche.STATE_NORMAL)) {
					viewHolder.btnimg.setBackgroundResource(R.drawable.pcb_sitting_on);
				}
				setOnClickListener(viewHolder.btnimg, pinche.getInfo_id(), pinche.getState(),
						position);
			} else {// 已过期
				viewHolder.btnimg.setBackgroundResource(R.drawable.ic_overdate);
				viewHolder.btnimg.setEnabled(false);
			}
		}
		viewHolder.btnimg.setTag(pinche);
		return convertView;
	}

	public void setOnClickListener(View v, final int info_id, final String state, final int objIndex) {
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				callBack.execute(v, info_id, objIndex,
						Pinche.STATE_COLSE.equals(state) ? Pinche.STATE_NORMAL : Pinche.STATE_COLSE);
			}
		});
	}

	public static class ViewHolder {
		public TextView start;// 起点
		public TextView end;// 终点
		public ImageView btnimg;

		public RelativeLayout layout; // 途经点布局
		public TextView point1; // 途经点1
		public TextView point2; // 途经点2
	}
}
