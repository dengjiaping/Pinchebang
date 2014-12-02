package com.lepin.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lepin.activity.LongFragmentActivity;
import com.lepin.activity.MyPublishDetailsActivity;
import com.lepin.activity.R;
import com.lepin.activity.WorkFragmentActivity;
import com.lepin.adapter.MyPublishAdapter;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Page;
import com.lepin.entity.Pinche;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

public class MyPublishFragment extends BaseFragment implements OnItemLongClickListener,
		OnItemClickListener {

	private View mRootView;
	private int pType;// 类型

	@ViewInject(id = R.id.publish_istView)
	private ListView mPublishListView;

	@ViewInject(id = R.id.publish_info_no)
	private LinearLayout mNoInfoLayout;

	@ViewInject(id = R.id.publish_new_info)
	private Button mPublishButton;

	private static final String SINDEX = "index";
	private Page<Pinche> mPinchePage;// 拼车信息分页相关
	private ArrayList<Pinche> mPincheList = new ArrayList<Pinche>();// 拼车信息
	private MyPublishAdapter mAdapter;
	private Util util = Util.getInstance();
	private String operateUrl;// 开启，关闭，删除临时变量

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.publish_fragment, container, false);
		ViewInjectUtil.inject(this, mRootView);
		return mRootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		pType = getArguments().getInt(SINDEX);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mPublishButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断当前是上下班还是长途，直接去发布
				Util.getInstance().go2Activity(
						getActivity(),
						pType == Constant.SWORK ? WorkFragmentActivity.class
								: LongFragmentActivity.class);
			}
		});
		loadPublishInfo();
	}

	private void loadPublishInfo() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 暂时未做分页处理
		if (pType == Constant.SWORK)// 上下班
		{
			params.add(new BasicNameValuePair("carpoolType", Pinche.CARPOOLTYPE_ON_OFF_WORK));
		} else {
			params.add(new BasicNameValuePair("carpoolType", Pinche.CARPOOLTYPE_LONG_TRIP));
		}
		params.add(new BasicNameValuePair("rows", String.valueOf(50)));
		params.add(new BasicNameValuePair("page", String.valueOf(1)));

		util.doPostRequest(getActivity(), new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				TypeToken<JsonResult<Page<Pinche>>> token = new TypeToken<JsonResult<Page<Pinche>>>() {
				};
				Gson gson = new GsonBuilder().create();
				JsonResult<Page<Pinche>> jsonResult = gson.fromJson(result, token.getType());

				if (jsonResult != null && jsonResult.isSuccess())// 获取数据成功
				{
					if (mPincheList.size() > 0) {
						mPincheList.clear();
					}
					mPinchePage = jsonResult.getData();// 获取数据
					if (mPinchePage.getRows().size() > 0)// 有数据
					{
						mNoInfoLayout.setVisibility(View.GONE);
						mPublishListView.setVisibility(View.VISIBLE);
						mPincheList.addAll(mPinchePage.getRows());
						setAdapter(mPincheList);
					} else {// 没有数据
						mNoInfoLayout.setVisibility(View.VISIBLE);
						mPublishListView.setVisibility(View.GONE);
					}
				}
			}
		}, params, Constant.URL_GETUSERPUBINFOS, getString(R.string.publish_get_info_ing), false);
	}

	protected void setAdapter(ArrayList<Pinche> mPincheList) {
		mAdapter = new MyPublishAdapter(getActivity(), mPincheList, mOperateClickListener);
		mPublishListView.setAdapter(mAdapter);
		mPublishListView.setOnItemClickListener(this);
		mPublishListView.setOnItemLongClickListener(this);
	}

	private OnClickListener mOperateClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			Pinche pinche = mPincheList.get(position);
			Util.printLog("我的发布－点击按钮的对象:"+pinche.toString());
			String stateString = pinche.getState().equals(Pinche.STATE_COLSE) ? Pinche.STATE_NORMAL
					: Pinche.STATE_COLSE;
			showDialogMsg(pinche.getInfo_id(), stateString, position);
		}
	};

	private void showDialogMsg(final int info_id, final String state, final int objIndex) {

		String title = "";
		String ok = "";
		if (state.equals(Pinche.STATE_COLSE)) {
			title = getString(R.string.publish_info_close);
			ok = getString(R.string.msg_btn_text3);
		} else if (state.equals(Pinche.STATE_NORMAL)) {
			title = getString(R.string.publish_info_open);
			ok = getString(R.string.turn_on);
		}
		Util.getInstance().showDialog(getActivity(), title, ok,
				getString(R.string.my_info_btn_cancel), new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							changeState(info_id, state, objIndex);
						}

					}
				});
	}

	/**
	 * 关闭信息
	 * 
	 * @param infor_id
	 */
	public void changeState(int info_id, final String state, final int objIndex) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (state.equals(Pinche.STATE_NORMAL)) {
			operateUrl = Constant.URL_OPENINFO;
		} else if (state.equals(Pinche.STATE_COLSE)) {
			operateUrl = Constant.URL_CLOSEINFO;
		} else if (state.equals(Pinche.STATE_DELETE)) {
			operateUrl = Constant.URL_DELINFO;
		}

		params.add(new BasicNameValuePair("infoId", String.valueOf(info_id)));

		HttpRequestOnBackgrount changeStateBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.POST, new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						JsonResult<String> jsonResult = Util.getInstance().getObjFromJsonResult(
								result, new TypeToken<JsonResult<String>>() {
								});

						if (jsonResult.isSuccess()) {
							if (state.equals(Pinche.STATE_DELETE)) {
								mPincheList.remove(objIndex);
								Util.showToast(getActivity(), getString(R.string.delete_success));
								if (mPincheList.size() == 0) {
									mNoInfoLayout.setVisibility(View.VISIBLE);
									mPublishListView.setVisibility(View.GONE);
								} else {

								}
							} else {
								mPincheList.get(objIndex).setState(String.valueOf(state));
							}
							mAdapter.notifyDataSetChanged();
						} else {
							Util.showToast(getActivity(),
									getString(R.string.publish_date_change_fault));
						}
					}

					@Override
					public void onFail(String errorType, String errorMsg) {
						// TODO Auto-generated method stub
						super.onFail(errorType, errorMsg);
					}
				}, params, getActivity(), true);
		changeStateBackgrount.execute(operateUrl);

	}

	public MyPublishFragment() {
		super();
	}

	public static MyPublishFragment newInstance(int index) {
		MyPublishFragment testFragment = new MyPublishFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(SINDEX, index);
		testFragment.setArguments(bundle);
		return testFragment;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Pinche pinche = (Pinche) mPublishListView.getItemAtPosition(position);
		Intent intent = new Intent();
		intent.setClass(getActivity(), MyPublishDetailsActivity.class);
		intent.putExtra("infoId", pinche.getInfo_id());
		intent.putExtra("carpoolType", pinche.getCarpoolType());
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Pinche pinche = (Pinche) mPublishListView.getItemAtPosition(position);
		showDeleteDialogMsg(pinche.getInfo_id(), position);
		return true;
	}

	/**
	 * 刪除提示框
	 * 
	 * @param infor_id
	 * @param state
	 */
	private void showDeleteDialogMsg(final int info_id, final int objIndex) {

		Util.getInstance().showDialog(getActivity(), getString(R.string.publish_info_delete),
				getString(R.string.my_info_btn_delete), getString(R.string.my_info_btn_cancel),
				new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							changeState(info_id, Pinche.STATE_DELETE, objIndex);
						}

					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
