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
import com.lepin.util.Interfaces.ViewHolder;
import com.lepin.util.TimeUtils;

public class MyPublishAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<Pinche> mPincheDataList;
	private OnClickListener mClickListener;

	public MyPublishAdapter(Context context, ArrayList<Pinche> mPincheDataList,
			OnClickListener onClickListener) {
		this.context = context;
		this.mPincheDataList = mPincheDataList;
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mClickListener = onClickListener;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Pinche pinche = mPincheDataList.get(position);
		final String stateString = pinche.getState();
		if (convertView == null)
			convertView = layoutInflater.inflate(R.layout.new_item_mypublish, parent, false);
		TextView start = ViewHolder.get(convertView, R.id.item_mypublish_start);// 起点
		TextView end = ViewHolder.get(convertView, R.id.item_mypublish_end);// 终点

		ImageView operateImageViewBtn = ViewHolder.get(convertView, R.id.item_mypublish_btn);// 操作按钮

		RelativeLayout pointsLayout = ViewHolder.get(convertView, R.id.pathway_point_layout);// 途经点布局
		pointsLayout.setVisibility(View.GONE);

		TextView point1 = ViewHolder.get(convertView, R.id.point1);// 途经点１
		point1.setVisibility(View.GONE);
		TextView point2 = ViewHolder.get(convertView, R.id.point2);// 途经点２
		point2.setVisibility(View.GONE);

		// 给view设置text
		start.setText(pinche.getStart_name(context));// 起点
		end.setText(pinche.getEnd_name(context));// 终点
		operateImageViewBtn.setTag(position);
		operateImageViewBtn.setEnabled(true);
		if (pinche.getCarpoolType().equals(Pinche.CARPOOLTYPE_LONG_TRIP)
				&& !isOverTime(new Date(1000 * TimeUtils.dateStrToSecond(pinche.getDepartureTime())))) {
			operateImageViewBtn.setBackgroundResource(R.drawable.ic_overdate);
			operateImageViewBtn.setEnabled(false);
		} else {
			operateImageViewBtn
					.setBackgroundResource(stateString.equals(Pinche.STATE_COLSE) ? R.drawable.pcb_sitting_off
							: R.drawable.pcb_sitting_on);
			operateImageViewBtn.setOnClickListener(mClickListener);
		}
		Point[] points = pinche.getPoints();
		if (null != points && points.length != 0) {

			int length = points.length;
			for (int i = 0; i < length; i++) {
				TextView throuthPoinTextView = null;
				if (i == 0) {
					throuthPoinTextView = point1;
				} else if (i == 1) {
					throuthPoinTextView = point2;
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
			pointsLayout.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	/**
	 * 产查当前路线是否过期
	 * 
	 * @param date
	 * @return true:未过期 false:已经过期
	 */
	private boolean isOverTime(Date date) {
		Calendar nowCalendar = Calendar.getInstance();
		Calendar lCalendar = Calendar.getInstance();
		lCalendar.setTime(date);
		return nowCalendar.before(lCalendar);

	}
}
