package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.adapter.PincheListAdapter;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Page;
import com.lepin.entity.Pinche;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

/**
 * 她发布的线路
 * 
 * @author {author wangxiaohong}
 * 
 */
@Contextview(R.layout.pinche_trail_her_publish_lines_results)
public class PincheTrailHerPublishLinesActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;// 标题
	@ViewInject(id = R.id.common_title_back)
	private ImageView mBackBtn;// 返回
	@ViewInject(id = R.id.listview)
	private ListView mListview;//
	private PincheListAdapter mAdapter;
	private ArrayList<Pinche> mPinchesList = new ArrayList<Pinche>();

	private int userId;
	private String role;
	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		final Bundle bundle = getIntent().getExtras();
		userId = bundle.getInt("userId");
		role = bundle.getString("role");
		mTitle.setText(getResources().getString(R.string.pinche_trail_publish_trail));

		loadData();
		mBackBtn.setOnClickListener(this);
	}

	private void loadData() {
		// TODO Auto-generated method stub
		final String mUrl = "userId=" + userId;// 请求拼车详情参数
		final String userType = "infoType="
				+ (role.equals("Driver") ? Pinche.DRIVER : Pinche.PASSENGER);

		util.doGetRequest(
				PincheTrailHerPublishLinesActivity.this,
				new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						JsonResult<Page<Pinche>> jsonResult = util.getObjFromJsonResult(result,
								new TypeToken<JsonResult<Page<Pinche>>>() {
								});
						if (jsonResult != null && jsonResult.isSuccess()) {
							final List<Pinche> pinches = jsonResult.getData().getRows();
							mPinchesList.addAll(pinches);
							setListView(mPinchesList);
						} else {
							Util.showToast(PincheTrailHerPublishLinesActivity.this,
									getString(R.string.request_error));
						}
					}
				}, Constant.URL_GET_TRAIL_PERSONAL_LINES + mUrl + "&" + userType,
				getString(R.string.lepin_dialog_loading), false);
	}

	protected void setListView(ArrayList<Pinche> data) {
		// TODO Auto-generated method stub
		this.mAdapter = new PincheListAdapter(PincheTrailHerPublishLinesActivity.this, data);
		mListview.setAdapter(mAdapter);
		mListview.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view == mBackBtn) {
			PincheTrailHerPublishLinesActivity.this.finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Bundle mBundle = new Bundle();
		mBundle.putInt("PincheId", mPinchesList.get((int) id).getInfo_id());
		util.go2ActivityWithBundle(PincheTrailHerPublishLinesActivity.this,
				PincheTrailActivity.class, mBundle);
	}

}
