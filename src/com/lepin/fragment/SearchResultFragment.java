package com.lepin.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.lepin.activity.LongFragmentActivity;
import com.lepin.activity.R;
import com.lepin.activity.SearchResultActivity;
import com.lepin.activity.WorkFragmentActivity;
import com.lepin.adapter.PincheListAdapter;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Key;
import com.lepin.entity.Page;
import com.lepin.entity.Pinche;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbListView;
import com.lepin.widget.PcbListView.PcbListViewListener;

public class SearchResultFragment extends BaseFragment implements OnItemClickListener,
		OnClickListener, PcbListViewListener {
	private View mRootView;

	@ViewInject(id = R.id.search_no_publish)
	private Button mGo2PulishBtn;

	@ViewInject(id = R.id.search_result_no)
	private LinearLayout mNoDataLayout;

	@ViewInject(id = R.id.search_listview)
	private PcbListView mListview;

	private String mIdentity;// 如果时从推送进入，mIdentity：LONG_TRIP|ON_OFF_WORK
	private Key mSearchKey;
	private ArrayList<Pinche> mPinchesList;
	private PincheListAdapter mAdapter;
	private final static int SITEMSIZE = 10;// 每页条数
	private boolean isRefresh = false;
	private boolean isLoadMore = false;
	private int mCurrentPageNum = 1;// 当前页数
	private int totalPageNum = 0;// 总共页数
	private Util util = Util.getInstance();
	private HttpRequestOnBackgrount mRefreshOrLoadMoreTask;
	private boolean isHint = false;// 是否加载数据

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.search_result_fragment, container, false);
		isHint = true;
		ViewInjectUtil.inject(this, mRootView);
		return mRootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) searching();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		// searching();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		final Bundle mBundle = getArguments();
		mSearchKey = (Key) mBundle.getSerializable("search_key");
		mIdentity = mBundle.getString(SearchResultActivity.DRIVER_OR_PASSERGER);
	}

	private void init() {
		mPinchesList = new ArrayList<Pinche>();
		mListview.setPullLoadEnable(false);
		mGo2PulishBtn.setOnClickListener(this);
	}

	public static final SearchResultFragment newInstance(String mIdentity, Key key) {
		SearchResultFragment fragment = new SearchResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString(SearchResultActivity.DRIVER_OR_PASSERGER, mIdentity);
		bundle.putSerializable("search_key", key);
		fragment.setArguments(bundle);
		return fragment;
	}

	protected void searching() {
		if (!isHint) return;
		isHint = false;
		final List<NameValuePair> params = getParams(mCurrentPageNum);
		util.doPostRequest(
				getActivity(),
				new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						if (mSearchKey != null) {
							if (TextUtils.isEmpty(result)) {
								mNoDataLayout.setVisibility(View.GONE);
							} else {
								searchResult(result);
							}
						} else {
							JsonResult<ArrayList<Pinche>> jResult = Util.getInstance()
									.getObjFromJsonResult(result,
											new TypeToken<JsonResult<ArrayList<Pinche>>>() {
											});
							if (jResult != null && jResult.isSuccess()) {
								ArrayList<Pinche> data = jResult.getData();
								handleData(data);
							}

						}
					}
				}, params, mSearchKey == null ? Constant.GET_RECOMMEND_INFO
						: Constant.URL_GET_INFOS,
				getString(R.string.searchint), false);
	}

	/**
	 * 获取搜索参数
	 * 
	 * @return
	 */
	private List<NameValuePair> getParams(int page) {
		// 搜索条件
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		if (mSearchKey == null) {
			paramsList.add(new BasicNameValuePair("carpoolType", mIdentity));
			return paramsList;
		}
		paramsList.add(new BasicNameValuePair("startName", mSearchKey.getStart_name()));
		paramsList.add(new BasicNameValuePair("endName", mSearchKey.getEnd_name()));
		paramsList.add(new BasicNameValuePair("carpoolType", mSearchKey.getCarpoolType()));
		paramsList.add(new BasicNameValuePair("rows", String.valueOf(SITEMSIZE)));
		paramsList.add(new BasicNameValuePair("page", String.valueOf(mCurrentPageNum)));
		paramsList.add(new BasicNameValuePair("infoType", mIdentity));
		if (!mSearchKey.getCarpoolType().equals(Pinche.CARPOOLTYPE_LONG_TRIP)) {
			if (mSearchKey.getStart_lat() > 0) {
				paramsList.add(new BasicNameValuePair("startLat", String.valueOf(mSearchKey
						.getStart_lat())));
				paramsList.add(new BasicNameValuePair("startLon", String.valueOf(mSearchKey
						.getStart_lon())));
				paramsList.add(new BasicNameValuePair("startCityId", mSearchKey.getStartCityId()));

			}
			if (mSearchKey.getEnd_lat() > 0) {
				paramsList.add(new BasicNameValuePair("endLat", String.valueOf(mSearchKey
						.getEnd_lat())));
				paramsList.add(new BasicNameValuePair("endLon", String.valueOf(mSearchKey
						.getEnd_lon())));
				paramsList.add(new BasicNameValuePair("endCityId", mSearchKey.getEndCityId()));
			}
		}
		Util.printLog(mSearchKey.toString());
		return paramsList;
	}

	private void setListAdapter(ArrayList<Pinche> data) {
		this.mAdapter = new PincheListAdapter(getActivity(), data);
		mListview.setAdapter(mAdapter);
		mListview.setOnItemClickListener(this);
		mListview.setPcbListViewListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Util.getInstance().go2PincheTrailActivity(getActivity(),
				mPinchesList.get((int) id).getInfo_id());
	}

	@Override
	public void onClick(View v) {
		if (mSearchKey != null) {
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("key", mSearchKey);
			Util.getInstance()
					.go2ActivityWithBundle(
							getActivity(),
							mSearchKey.getCarpoolType().equals(Pinche.CARPOOLTYPE_ON_OFF_WORK) ? WorkFragmentActivity.class
									: LongFragmentActivity.class, mBundle);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			searching();
		}
	}

	@Override
	public void onRefresh() {
		isRefresh = true;
		mCurrentPageNum = 1;
		totalPageNum = 1;
		doRefreshOrLoadMore();
	}

	private void doRefreshOrLoadMore() {
		mRefreshOrLoadMoreTask = new HttpRequestOnBackgrount(HttpRequestOnBackgrount.POST,
				new OnHttpRequestDataCallback() {

					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						if (isRefresh) {
							isRefresh = false;
							mListview.stopRefresh();
							mPinchesList.clear();
						} else if (isLoadMore) {
							isLoadMore = false;
							mListview.stopLoadMore();
						}

						JsonResult<Page<Pinche>> jsonResult = util.getObjFromJsonResult(result,
								new TypeToken<JsonResult<Page<Pinche>>>() {
								});
						if (jsonResult != null && jsonResult.isSuccess()) {
							totalPageNum = jsonResult.getData().getPageCount();
							if (mCurrentPageNum == totalPageNum) {
								mListview.setPullLoadEnable(false);
							} else {
								mListview.setPullLoadEnable(true);
							}
							final List<Pinche> pinches = jsonResult.getData().getRows();
							if (pinches != null && pinches.size() > 0) {
								mPinchesList.addAll(pinches);
								mAdapter.notifyDataSetChanged();
							}
						}
					}
				}, getParams(mCurrentPageNum), getActivity(), false);
		mRefreshOrLoadMoreTask.execute(Constant.URL_GET_INFOS);
	}

	@Override
	public void onLoadMore() {
		isLoadMore = true;
		if ((mCurrentPageNum + 1) <= totalPageNum) {
			mCurrentPageNum += 1;
			doRefreshOrLoadMore();
		}
	}

	private void searchResult(String result) {
		JsonResult<Page<Pinche>> jsonResult = util.getObjFromJsonResult(result,
				new TypeToken<JsonResult<Page<Pinche>>>() {
				});
		if (jsonResult != null && jsonResult.isSuccess()) {
			totalPageNum = jsonResult.getData().getPageCount();
			mCurrentPageNum = jsonResult.getData().getPageNumber();
			mListview.setPullLoadEnable(mCurrentPageNum == totalPageNum ? false : true);
			final List<Pinche> pinches = jsonResult.getData().getRows();

			handleData(pinches);
		} else {
			Util.showToast(getActivity(), getString(R.string.request_data_error));
		}
	}

	private void handleData(final List<Pinche> pinches) {
		if (pinches != null && pinches.size() <= 0) {// 没有数据
			mListview.setVisibility(View.GONE);
			mNoDataLayout.setVisibility(View.VISIBLE);
			mListview.setPullRefreshEnable(false);
		} else {
			mNoDataLayout.setVisibility(View.GONE);
			mPinchesList.addAll(pinches);
			setListAdapter(mPinchesList);
		}
	}

}
