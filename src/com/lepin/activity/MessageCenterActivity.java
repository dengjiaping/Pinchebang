package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import u.aly.bu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.adapter.MessageCenterAdapter;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Page;
import com.lepin.entity.PushMsg;
import com.lepin.entity.PushMsg.PUSH_MSG_TYPE;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;
import com.lepin.widget.PcbListView;
import com.lepin.widget.PcbListView.PcbListViewListener;

@Contextview(R.layout.message_center)
public class MessageCenterActivity extends BaseActivity implements OnClickListener,
		PcbListViewListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mcBack;// 返回
	@ViewInject(id = R.id.common_title_title)
	private TextView mcTitle;// 标题

	@ViewInject(id = R.id.message_center_data_listview)
	private PcbListView mListView;
	@ViewInject(id = R.id.message_center_data_is_null)
	private TextView mcNoData;
	@ViewInject(id = R.id.message_center_error)
	private TextView mcError;

	private Util util = Util.getInstance();
	private List<PushMsg> messages = new ArrayList<PushMsg>();// 获取的数据
	private MessageCenterAdapter messageCenterAdapter;// 适配器
	public final static String S_IS_PUSH = "isPush";
	private int mRow = 50;
	private PushMsg pMsg;
	private boolean isPush = false;
	private int mitemPosition;

	private boolean isRefresh = false;
	private boolean isLoadMore = false;
	private int mCurrentPageNum = 1;// 当前页数
	private int totalPageNum = 0;// 总共页数
	private HttpRequestOnBackgrount mRefreshOrLoadMoreTask;
	private static final String READ = "READED";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		isPush = getIntent().getExtras().getBoolean(S_IS_PUSH);
		init();
		getMessages();
	}

	public void init() {
		mcBack.setOnClickListener(this);
		mcTitle.setText(getString(R.string.message_center_title));
		mcError.setOnClickListener(this);
		mListView.setPcbListViewListener(this);
		mListView.setPullLoadEnable(false);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mitemPosition = position;
				pMsg = (PushMsg) mListView.getItemAtPosition(position);
				if (pMsg.getState().equals(READ)) {
					doReadResult(pMsg);
				} else {
					doForMessage(READ, pMsg);
				}
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				pMsg = (PushMsg) mListView.getItemAtPosition(position);
				Util.getInstance().showDialog(MessageCenterActivity.this,
						getString(R.string.is_comfirm_delete_msg),
						getString(R.string.dialog_confirm), getString(R.string.my_info_btn_cancel),
						new OnOkOrCancelClickListener() {

							@Override
							public void onOkClick(int type) {
								if (type == PcbConfirmDialog.OK) {
									doForMessage("DELETE", pMsg);
								}

							}
						});

				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == mcBack) {
			if (isPush) {
				util.isCityInfoExist(MessageCenterActivity.this);
				util.go2Activity(MessageCenterActivity.this, CarSharingActivity.class);
			} else {
				this.finish();
			}
		} else if (v == mcError) {
			getMessages();
		}
	}

	/**
	 * 获取消息
	 */
	public void getMessages() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("rows", String.valueOf(mRow)));
		pairs.add(new BasicNameValuePair("page", String.valueOf(mCurrentPageNum)));

		util.doPostRequest(MessageCenterActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				JsonResult<Page<PushMsg>> jsonResult = util.getObjFromJsonResultWithClass(result,
						new TypeToken<JsonResult<Page<PushMsg>>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {// 获取成功
					final Page<PushMsg> page = jsonResult.getData();
					messages = messageSort(page.getRows());// 排序
					totalPageNum = page.getPageCount();
					mCurrentPageNum = page.getPageNumber();
					mListView.setPullLoadEnable(mCurrentPageNum == totalPageNum ? false : true);
					if (messages.size() == 0) {// 无数据
						setNodataDisplay();
						mListView.setPullRefreshEnable(false);
					} else {
						setNomalDisplay();
						setAdapter(messages);
					}
				} else {// 获取失败
					setErrorDisplay();
					Util.showToast(MessageCenterActivity.this,
							getString(R.string.message_center_get_fault));
				}

			}
		}, pairs, Constant.URL_GETMESSAGE, getString(R.string.message_center_get_data), false);

	}

	/**
	 * 处理消息
	 * 
	 * @param operation
	 *            对消息的处理
	 */
	public void doForMessage(final String operation, final PushMsg pushMsg) {
		String loadText = "";
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("pushMsgId", String.valueOf(pushMsg.getPushMsgId())));
		if (operation.equals(READ)) {// 阅读
			pairs.add(new BasicNameValuePair("operation", READ));
			loadText = getString(R.string.message_center_read_data);
		} else {// 删除
			pairs.add(new BasicNameValuePair("operation", "DELETE"));
			loadText = getString(R.string.message_center_delete_data);
		}

		util.doPostRequest(MessageCenterActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				TypeToken<JsonResult<String>> token = new TypeToken<JsonResult<String>>() {
				};
				JsonResult<String> jsonResult = util.getObjFromJsonResultWithClass(result, token);
				if (jsonResult != null && jsonResult.isSuccess()) {
					if (operation.equals(READ))// 阅读
					{

						// if
						// ((pushMsg.getType().equals(PushMsg.PUSH_MSG_TYPE.NEW_CARPOOL_ORDER)
						// || pushMsg.getType().equals(
						// PushMsg.PUSH_MSG_TYPE.COMPLETE_CARPOOL_ORDER)
						// || pushMsg.getType().equals(
						// PushMsg.PUSH_MSG_TYPE.CONFIRM_CARPOOL_ORDER) ||
						// pushMsg
						// .getType().equals(PushMsg.PUSH_MSG_TYPE.CANCEL_CARPOOL_ORDER)))
						// {
						if (pushMsg.getType().name().endsWith(PushMsg.PUSH_MSG_TYPE.ORDER.name())) {
							Bundle bundle = new Bundle();
							bundle.putString(Constant.BOOK_ID, pushMsg.getExpand());
							Util.getInstance().go2OrderDetail(MessageCenterActivity.this, bundle);
						}
						setAdapter(readResult());
					} else {// 删除
						messages.remove(pMsg);
						messageCenterAdapter.notifyDataSetChanged();
						Util.showToast(MessageCenterActivity.this,
								getString(R.string.message_center_delete_data_success));
						if (messages.size() <= 0) {
							setNodataDisplay();
						}
					}
				} else {
					setNodataDisplay();
				}
			}
		}, pairs, Constant.URL_UPDATEMSGSTATE, loadText, false);

	}

	/**
	 * 跳转到订单页面
	 * 
	 * @param pushMsg
	 */
	public void doReadResult(PushMsg pushMsg) {
		PUSH_MSG_TYPE msgType = pushMsg.getType();
		// 订单相关

		if (msgType.name().endsWith(PushMsg.PUSH_MSG_TYPE.ORDER.name())) {
			Intent intent = new Intent(MessageCenterActivity.this, MyOrderDetailActivity.class);
			intent.putExtra("book_id", pushMsg.getExpand());
			startActivity(intent);
		} else if (msgType.equals(PushMsg.PUSH_MSG_TYPE.RECHARGE_COIN)) {
			Intent intent = new Intent(MessageCenterActivity.this, MyPinCheRecordActivity.class);
			intent.putExtra("book_id", pushMsg.getExpand());
			startActivity(intent);
		} else if (msgType.equals(PushMsg.PUSH_MSG_TYPE.RECHARGE_GOLD)) {
			Util.getInstance().go2Activity(this, MyBalanceActivity.class);
		} else if (msgType.equals(PushMsg.PUSH_MSG_TYPE.RECOMMEND_SINGLE)) {// 跳转到线路详情
			Util.getInstance().go2PincheTrailActivity(this, Integer.parseInt(pushMsg.getExpand()));
		} else if (msgType.equals(PushMsg.PUSH_MSG_TYPE.RECOMMEND_MULTI)) {// 推荐信息界面
			Bundle bundle = new Bundle();
			bundle.putString("recommendType", pushMsg.getExpand());
			Util.getInstance().go2ActivityWithBundle(this, RecommendActivity.class, bundle);
		}

		else if (pushMsg.getType().equals(PushMsg.PUSH_MSG_TYPE.OTHER)) {
		}
	}

	/**
	 * 设置适配器
	 * 
	 * @param pushMsgs
	 */
	public void setAdapter(List<PushMsg> pushMsgs) {
		messageCenterAdapter = new MessageCenterAdapter(this, pushMsgs);
		mListView.setAdapter(messageCenterAdapter);
	}

	/**
	 * 信息阅读成功后，将此条信息标记为已阅读
	 * 
	 * @return
	 */
	public List<PushMsg> readResult() {
		List<PushMsg> lists = new ArrayList<PushMsg>();
		for (PushMsg pushMsg : messages) {
			if (!pushMsg.getState().equals("DELETE")) {
				if (pushMsg.getPushMsgId() == pMsg.getPushMsgId()) {
					pushMsg.setState(READ);
				}
				lists.add(pushMsg);
			}

		}

		return messageSort(lists);
	}

	/**
	 * 没有记录
	 */
	public void setNodataDisplay() {
		mListView.setVisibility(View.GONE);
		mcError.setVisibility(View.GONE);
		mcNoData.setVisibility(View.VISIBLE);
	}

	/**
	 * 获取记录失败
	 */
	public void setErrorDisplay() {
		mListView.setVisibility(View.GONE);
		mcError.setVisibility(View.VISIBLE);
		mcNoData.setVisibility(View.GONE);
	}

	/**
	 * 有数据，正常显示
	 */
	public void setNomalDisplay() {
		mListView.setVisibility(View.VISIBLE);
		mcError.setVisibility(View.GONE);
		mcNoData.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mListView.setSelection(mitemPosition);
	}

	/**
	 * 排序
	 * 
	 * @param messages
	 */
	public List<PushMsg> messageSort(List<PushMsg> messages) {
		List<PushMsg> readedList = new ArrayList<PushMsg>();
		List<PushMsg> canReadList = new ArrayList<PushMsg>();

		for (PushMsg pushMsg : messages) {
			if (pushMsg.getState().equals(READ))// 已读
			{
				readedList.add(pushMsg);
			} else {// 未读
				canReadList.add(pushMsg);
			}
		}

		canReadList.addAll(readedList);

		return canReadList;
	}

	@Override
	public void onRefresh() {
		if (isRefresh) return;
		isRefresh = true;
		mCurrentPageNum = 1;
		totalPageNum = 1;
		doRefreshOrLoadMore();
	}

	private void doRefreshOrLoadMore() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("rows", String.valueOf(mRow)));
		pairs.add(new BasicNameValuePair("page", String.valueOf(mCurrentPageNum)));

		mRefreshOrLoadMoreTask = new HttpRequestOnBackgrount(HttpRequestOnBackgrount.POST,
				new OnHttpRequestDataCallback() {

					public void onSuccess(String result) {
						Util.printLog("消息中心刷新:" + result);
						if (isRefresh) {
							isRefresh = false;
							mListView.stopRefresh();
						} else if (isLoadMore) {
							isLoadMore = false;
							mListView.stopLoadMore();
						}

						JsonResult<Page<PushMsg>> jsonResult = util.getObjFromJsonResultWithClass(
								result, new TypeToken<JsonResult<Page<PushMsg>>>() {
								});
						Util.printLog("消息中心刷新:" + jsonResult.toString());
						if (jsonResult != null && jsonResult.isSuccess()) {
							totalPageNum = jsonResult.getData().getPageCount();
							// List<PushMsg> tempMessages =
							// messageSort(jsonResult.getData().getRows());
							List<PushMsg> tempMessages = jsonResult.getData().getRows();
							if (tempMessages != null && tempMessages.size() > 0) {
								messages.clear();
								messages.addAll(tempMessages);
								messageCenterAdapter.notifyDataSetChanged();
							}
						}
					}
				}, pairs, MessageCenterActivity.this, false);

		mRefreshOrLoadMoreTask.execute(Constant.URL_GETMESSAGE);
	}

	@Override
	public void onLoadMore() {
		isLoadMore = true;
		if ((mCurrentPageNum + 1) <= totalPageNum) {
			mCurrentPageNum += 1;
			doRefreshOrLoadMore();
		}
	}

}
