package com.lepin.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.Book;
import com.lepin.entity.Pinche;
import com.lepin.util.Interfaces.ViewHolder;
import com.lepin.util.Util;

public class MyOrderFragmentListviewAdapter extends ArrayAdapter<Book> {
	private ArrayList<Book> mBooks;
	public Context mContext;
	private int mItemView;
	private LayoutInflater inflater;
	private String mCurrentOrderType;// 订单状态
	private String mIdentityType;// 身份
	private OnClickListener mBtnClickListener;
	private final static String PASSENGER = "I_AM_PASSENGER";

	public MyOrderFragmentListviewAdapter(Context context, int resource, ArrayList<Book> books,
			String currentOrderType, String identityType,
			OnClickListener listViewChildOnClickInterface) {
		super(context, resource, books);
		this.mBooks = books;
		this.mContext = context;
		this.mItemView = resource;
		this.mCurrentOrderType = currentOrderType;
		this.mIdentityType = identityType;
		this.mBtnClickListener = listViewChildOnClickInterface;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mBooks.size();
	}

	@Override
	public Book getItem(int position) {
		return mBooks.get(position);
	}

	@Override
	public int getPosition(Book item) {
		return mBooks.indexOf(item);
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		Book dataBook = mBooks.get(position);
		Pinche infoSnapshot = Util.getInstance().string2Bean(dataBook.getSnapshot(), Pinche.class);

		if (convertView == null) convertView = inflater.inflate(mItemView, parent, false);

		// ------------------------------起点-----------------------------------
		TextView mStarTextView = ViewHolder.get(convertView, R.id.my_order_listview_item_start);
		mStarTextView.setText(infoSnapshot.getStart_name(getContext()));
		// ------------------------------终点-----------------------------------
		TextView mEndTextView = ViewHolder.get(convertView, R.id.my_order_listview_item_end);
		mEndTextView.setText(infoSnapshot.getEnd_name(getContext()));

		// ------------------------------------------时间------------------------------------
		CheckedTextView timeTextView = ViewHolder
				.get(convertView, R.id.my_order_listview_item_time);
		timeTextView.setText(dataBook.getCreateTime());

		// -------------------------------操作按钮-------------------------------------------------
		Button mConfirmButton = ViewHolder
				.get(convertView, R.id.my_order_listview_item_operate_btn);
		mConfirmButton.setTag(dataBook);
		String templeState = mCurrentOrderType;
		if (mCurrentOrderType.equals("all") || mCurrentOrderType.equals(Book.STATE_WAITING_PROCESS))
			templeState = dataBook.getState();
		if (mIdentityType.equals(PASSENGER)) {// 当前身份为乘客

			if (templeState.equals(Book.STATE_NEW)) {// 这时可以付款了
				setOperateBtnStyle(mConfirmButton, R.string.pay_now, R.drawable.btn_red_selector,
						android.R.color.white);
				mConfirmButton.setOnClickListener(mBtnClickListener);
			} else if (templeState.equals(Book.STATE_PAYMENT)) {// 可以上车了
				setOperateBtnStyle(mConfirmButton, R.string.comfirm_get_in_car,
						R.drawable.btn_blue_selector, android.R.color.white);
				mConfirmButton.setOnClickListener(mBtnClickListener);
			} else if (templeState.equals(Book.STATE_COMPLETE)) {
				setOperateBtnStyle(mConfirmButton, R.string.has_ben_confirm, R.drawable.blue_sroke,
						R.color.blue_light);
			}

		} else {// 当前身份为司机
			if (templeState.equals(Book.STATE_NEW)) {// 这时可以取消
				setOperateBtnStyle(mConfirmButton, R.string.my_info_btn_cancel,
						R.drawable.btn_red_selector, android.R.color.white);
				mConfirmButton.setOnClickListener(mBtnClickListener);
			} else if (templeState.equals(Book.STATE_PAYMENT)) {// 乘客已经付款，也可以取消
				setOperateBtnStyle(mConfirmButton, R.string.cancel_with_pan,
						R.drawable.btn_red_selector, android.R.color.white);
				mConfirmButton.setOnClickListener(mBtnClickListener);
			} else if (templeState.equals(Book.STATE_COMPLETE)) {
				setOperateBtnStyle(mConfirmButton, R.string.has_ben_confirm, R.drawable.blue_sroke,
						R.color.blue_light);
			}

		}

		return convertView;
	}

	private void setOperateBtnStyle(Button mConfirmButton, int text, int backgroundResource,
			int textColor) {
		mConfirmButton.setText(mContext.getString(text));
		mConfirmButton.setBackgroundResource(backgroundResource);
		mConfirmButton.setTextColor(mContext.getResources().getColor(textColor));
	}

}
