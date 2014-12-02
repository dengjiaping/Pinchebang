package com.lepin.fragment;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.lepin.activity.LoginActivity;
import com.lepin.activity.MyOrderDetailActivity;
import com.lepin.activity.R;
import com.lepin.adapter.FragmentTabAdapter;
import com.lepin.adapter.MyOrderFragmentListviewAdapter;
import com.lepin.entity.Book;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Page;
import com.lepin.entity.Pinche;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.Util;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

public class MyOrderFragment extends BaseFragment implements OnItemClickListener, OnClickListener {
	private View mRootView;
	public final static String PASSENGER = "I_AM_PASSENGER";
	@ViewInject(id = R.id.my_order_listview)
	private ListView mListView;

	// 全部
	@ViewInject(id = R.id.my_order_top_item_all, parentId = R.id.top_item_layout)
	private CheckedTextView mAllItem;
	// 待付款
	@ViewInject(id = R.id.my_order_top_item_wait_pay, parentId = R.id.top_item_layout)
	private CheckedTextView mWaitPayItem;

	@ViewInject(id = R.id.my_order_top_item_wait_pay_divider, parentId = R.id.top_item_layout)
	private View mWaitPayDividerView;
	// 待上车
	@ViewInject(id = R.id.my_order_top_item_wait_confirm, parentId = R.id.top_item_layout)
	private CheckedTextView mWaitToGetInCar;

	// 完成
	@ViewInject(id = R.id.my_order_top_item_ok, parentId = R.id.top_item_layout)
	private CheckedTextView mOkItem;

	@ViewInject(id = R.id.no_data_layout)
	private LinearLayout mNoDataLayout;

	private String all = "all";
	private String mIdentityType;// 司机/乘客
	private ArrayList<Book> mBooks = new ArrayList<Book>();
	private MyOrderFragmentListviewAdapter myOrderFragmentListviewAdapter;

	// 当前显示的订单类型　
	private String mOrderCurrentState = all;
	private Util util = Util.getInstance();

	private int page = 1;
	// 最后操作的view
	private CheckedTextView mLastOperaView;

	private boolean isHint = false;// 是否加载数据

	public static final MyOrderFragment newInstance(String identity) {
		MyOrderFragment fragment = new MyOrderFragment();
		Bundle bundle = new Bundle();
		bundle.putString("identity", identity);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.my_order_fragment, container, false);
		isHint = true;
		ViewInjectUtil.inject(this, mRootView);
		return mRootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mIdentityType = getArguments().getString("identity");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (getUserVisibleHint()) {

			if (Constant.home_current_fragment == FragmentTabAdapter.RIGHT_FRAGMENT
					|| Constant.is_refresh_orders) {

				if (Constant.is_refresh_orders && mIdentityType.equals("I_AM_PASSENGER"))
					Constant.is_refresh_orders = false;
				refresh();
			}

		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			refresh();
		}
	}

	private void refresh() {
		if (isHint && !Constant.is_comfirm_dialog_show) {
			isHint = false;
			loadOrders();
		}
	}

	protected void initView() {
		if (mIdentityType.equals(HomeOrderFragment.DIRVER)) {
			mWaitPayItem.setVisibility(View.GONE);
			mWaitPayDividerView.setVisibility(View.GONE);
			mWaitToGetInCar.setText(getString(R.string.to_be_processed));
		}
		setItem2CheckedState(mAllItem, true);
		mLastOperaView = mAllItem;

		mAllItem.setOnClickListener(this);
		mWaitToGetInCar.setOnClickListener(this);
		mWaitPayItem.setOnClickListener(this);
		mOkItem.setOnClickListener(this);
	}

	protected void setItem2CheckedState(CheckedTextView view, boolean isChecked) {
		if (isChecked) {
			view.setChecked(true);
			view.setBackgroundColor(getResources().getColor(R.color.layout_bg1));
			view.setTextColor(getResources().getColor(R.color.btn_blue_normal));
		} else {
			view.setChecked(false);
			view.setBackgroundColor(getResources().getColor(android.R.color.white));
			view.setTextColor(getResources().getColor(R.color.text_gray));
		}
	}

	/**
	 * 加载我的订单
	 */
	private void loadOrders() {
		if (util.isNetworkAvailable(getActivity())) {
			if (mBooks.size() > 0) mBooks.clear();
			if (!Util.getInstance().isUserLoging(getActivity())) return;
			if (mNoDataLayout.getVisibility() == View.VISIBLE) {
				mNoDataLayout.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.VISIBLE);
			}
			// 默认加载第一页
			// 这里暂时没做分页，加载更多，刷新
			String mUrl = Constant.URL_GET_ORDERS + "type=" + mIdentityType + "&rows=100"
					+ "&page=" + page;// 我的订单请求参数
			if (!mOrderCurrentState.equals(all)) {
				mUrl += "&state=" + mOrderCurrentState;
			}

			util.doGetRequest(getActivity(), new OnHttpRequestDataCallback() {
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					JsonResult<Page<Book>> jsonResult = util.getObjFromJsonResult(result,
							new TypeToken<JsonResult<Page<Book>>>() {
							});

					if (jsonResult != null && jsonResult.isSuccess()) {
						if (jsonResult.getData().getRows() == null
								|| jsonResult.getData().getRows().size() <= 0) {// 无数据时
							doNoData();
						} else {
							mBooks.addAll(jsonResult.getData().getRows());
							setListView();
						}
					} else {
						Util.showToast(getActivity(), getString(R.string.get_data_error));
						doNoData();
					}
				}
			}, mUrl, getString(R.string.orders_loading), false);

		} else {
			Util.showToast(getActivity(), getString(R.string.network_unavaiable));
		}
	}

	protected void doNoData() {
		mListView.setVisibility(View.INVISIBLE);
		mNoDataLayout.setVisibility(View.VISIBLE);
		mNoDataLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mNoDataLayout.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.VISIBLE);
				loadOrders();

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int arg2, long arg3) {
		Book book = (Book) (mListView.getItemAtPosition(arg2));
		if (!Util.getInstance().isUserLoging(getActivity())) {
			Util.getInstance().go2Activity(getActivity(), LoginActivity.class);
		} else {
			Bundle bundle = new Bundle();
			bundle.putString(Constant.BOOK_ID, String.valueOf(book.getInfoOrderId()));
			Util.getInstance().go2ActivityWithBundle(getActivity(), MyOrderDetailActivity.class,
					bundle);
		}
	}

	private OnClickListener itemBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Book dataBook = (Book) v.getTag();
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("infoOrderId", String.valueOf(dataBook
					.getInfoOrderId())));
			String templeState = dataBook.getState();
			int position = mBooks.indexOf(dataBook);
			if (mIdentityType.equals(PASSENGER)) {// 当前身份为乘客
				if (templeState.equals(Book.STATE_NEW)) {// 这时可以付款了
					go2Pay(position, dataBook, params);
				} else if (templeState.equals(Book.STATE_PAYMENT)) {// 可以上车了
					getInCar(String.valueOf(dataBook.getInfoOrderId()));
					doBtnOperation(position, dataBook, "确认上车中...", params, Constant.URL_ORDER_IN);
				}
			} else {// 当前身份为司机
				if (templeState.equals(Book.STATE_NEW) || templeState.equals(Book.STATE_PAYMENT)) {// 这时可以取消
					doBtnOperation(position, dataBook, "取消中...", params, Constant.URL_CANCLEBOOK);
				}
			}
		}
	};

	protected void go2Pay(final int position, final Book book, ArrayList<NameValuePair> p) {
		p.add(new BasicNameValuePair("payType", Constant.PAY_OFFLINE));
		final ArrayList<NameValuePair> pairs = p;
		util.showDialog(getActivity(), getString(R.string.choice_pay_type),
				getString(R.string.complete_carpool_online),
				getString(R.string.complete_carpool_cash), new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {// 在线支付
							Pinche mPinche = util.string2Bean(book.getSnapshot(), Pinche.class);
							Util.getInstance().go2OnLinePay(getActivity(), book.getPrice(),
									String.valueOf(book.getInfoOrderId()), mPinche.getStart_name(),
									mPinche.getEnd_name(), mPinche.getStartLat(),
									mPinche.getStartLon(), mPinche.getEndLat(),
									mPinche.getEndLon(), mPinche.getCarpoolType());
						} else {// 线下支付
							doBtnOperation(position, book, "支付中...", pairs, Constant.URL_PAY);
						}
					}
				});
	}

	/**
	 * 乘客确认上车
	 * 
	 * @param valueOf
	 */
	protected void getInCar(String infoOrderId) {

	}

	// 点击按钮操作订单
	protected void doBtnOperation(final int itemPosition, final Book book, String title,
			ArrayList<NameValuePair> params, String url) {
		Util.printLog("操作参数:" + params);
		util.doPostRequest(getActivity(), new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					if (mOrderCurrentState.equals(all)) {// 在全部选项下
						if (mIdentityType.equals(PASSENGER)) {
							mBooks.get(itemPosition).setState(Book.STATE_COMPLETE);
						} else {
							mBooks.remove(itemPosition);
						}
					} else {// 在单个选项下
						mBooks.remove(itemPosition);
					}
					myOrderFragmentListviewAdapter.notifyDataSetChanged();
					Util.showToast(getActivity(), jsonResult.getData());
				} else {
					Util.showToast(getActivity(), getString(R.string.request_error));
				}
			}

		}, params, url, title, false);

	}

	private void setListView() {
		if (myOrderFragmentListviewAdapter == null) {
			myOrderFragmentListviewAdapter = new MyOrderFragmentListviewAdapter(getActivity(),
					R.layout.my_order_listview_item, mBooks, mOrderCurrentState, mIdentityType,
					itemBtnClickListener);
			mListView.setAdapter(myOrderFragmentListviewAdapter);
			mListView.setOnItemClickListener(MyOrderFragment.this);
		} else {
			myOrderFragmentListviewAdapter.notifyDataSetChanged();
		}
	}

	public void onClick(View v) {
		if (v == mAllItem) {
			setLastAndNowViewState(mAllItem, all);
		} else if (v == mWaitToGetInCar) {// 待上车
			if (mIdentityType.equals(HomeOrderFragment.PASSENGER)) {
				setLastAndNowViewState(mWaitToGetInCar, Book.STATE_PAYMENT);
			} else {// 司机端是待处理
				setLastAndNowViewState(mWaitToGetInCar, Book.STATE_WAITING_PROCESS);
			}
		} else if (v == mWaitPayItem) {// 待付款
			setLastAndNowViewState(mWaitPayItem, Book.STATE_NEW);
		} else if (v == mOkItem) {
			setLastAndNowViewState(mOkItem, Book.STATE_COMPLETE);
		}
		loadOrders();
	}

	protected void setLastAndNowViewState(CheckedTextView nowView, String orderType) {
		if (mLastOperaView != nowView) {
			setItem2CheckedState(mLastOperaView, false);
			setItem2CheckedState(nowView, true);
			mLastOperaView = nowView;
			mOrderCurrentState = orderType;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isHint = true;
	}
}
