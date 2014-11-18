package com.lepin.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.lepin.activity.R;
import com.lepin.entity.GoldBank;
import com.lepin.entity.JsonResult;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

public class MyCashAccountAdapter extends BaseAdapter {
	private Context context;
	private List<GoldBank> list;
	private LayoutInflater layoutInflater;
	private ListView mls;
	private Util util = Util.getInstance();

	public MyCashAccountAdapter(Context context, List<GoldBank> list, ListView mls) {
		this.context = context;
		this.list = list;
		this.layoutInflater = LayoutInflater.from(this.context);
		this.mls = mls;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final GoldBank goldBank = list.get(position);
		if (convertView == null) {
			convertView = this.layoutInflater.inflate(R.layout.item_add_cash_account_divider,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.checkedTextView = (CheckedTextView) convertView
					.findViewById(R.id.alipay_cash_account_name);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.add_alipay_cash_account_delete_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.checkedTextView.setText(goldBank.getAccount().toString());
		viewHolder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(goldBank, position);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private CheckedTextView checkedTextView;
		private ImageView imageView;
	}

	/**
	 * 输入支付密码对话框
	 */
	public void showDialog(final GoldBank goldBank, final int position) {
		String payPsw;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.entry_pay_psw_title));
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
		builder.setView(view);
		final EditText password = (EditText) view.findViewById(R.id.dialog_context);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String payPsw = password.getText().toString().trim();
				if (!(null == payPsw || "" == payPsw)) {
					DelectCashAccount(String.valueOf(goldBank.getGoldBankId()), payPsw, position);
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	private void DelectCashAccount(String id, String psw, final int position) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("state", "DELETE"));
		params.add(new BasicNameValuePair("goldBankId", id));
		params.add(new BasicNameValuePair("payPwd", psw));
		util.doPostRequest(
				context,
				new OnHttpRequestDataCallback() {
					@Override
					public void onSuccess(String result) {
						JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
								new TypeToken<JsonResult<String>>() {
								});
						if (jsonResult != null && jsonResult.isSuccess()) {
							Util.showToast(
									context,
									context.getResources().getString(
											R.string.add_or_modify_cash_account_success_toast));
							list.remove(position);
							MyCashAccountAdapter.this.notifyDataSetChanged();
							setListViewHeightBasedOnChildren(mls);
						}
					}

					public void onFail(String errorType, String errorMsg) {
						Util.showToast(context, errorMsg);
					};
				}, params, Constant.ADD_DELETE_ACCOUNT,
				context.getString(R.string.delect_cash_account_tip), true);
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {

		// 获取listview的适配器
		ListAdapter listAdapter = listView.getAdapter();
		// item的高度
		int itemHeight = 45;
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {
			totalHeight += Dp2Px(context, itemHeight) + listView.getDividerHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
		listView.setLayoutParams(params);
	}

	public int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

}
