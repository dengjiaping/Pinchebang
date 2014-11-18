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
import com.lepin.entity.PushMsg;
import com.lepin.util.TimeUtils;
import com.lepin.util.Util;

public class MessageCenterAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<PushMsg> mPushMsgDataList;

	public MessageCenterAdapter(Context context, List<PushMsg> mPushMsgDataList) {
		this.context = context;
		this.mPushMsgDataList = (ArrayList<PushMsg>) mPushMsgDataList;
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPushMsgDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPushMsgDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		final PushMsg pushMsg = mPushMsgDataList.get(position);
		if (convertView == null) {
			convertView = this.layoutInflater.inflate(R.layout.new_item_message_center, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.message = (TextView) convertView.findViewById(R.id.message_info);
			viewHolder.time = (TextView) convertView.findViewById(R.id.message_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.message.setText(pushMsg.getContent());
		if (!pushMsg.getState().equals("DELETE")) {
			if (pushMsg.getState().equals("READED")) {
				viewHolder.message.setTextColor(context.getResources().getColor(
						R.color.message_center_readed_tx));
			} else {
				if (pushMsg.getType().equals(PushMsg.PUSH_MSG_TYPE.RECHARGE_ORDER)) {
					Util.printLog(pushMsg.toString());
				}
				viewHolder.message.setTextColor(context.getResources().getColor(R.color.black));

			}
			viewHolder.time.setText(TimeUtils.formatDate(pushMsg.getCreateTime(),
					TimeUtils.TIME_FORMART_HM));
		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView message;// 起点
		public TextView time;// 终点

	}

}
