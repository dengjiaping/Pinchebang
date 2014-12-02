package com.lepin.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.fragment.SearchResultFragment;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;

/**
 * 推荐线路界面
 * 
 * @author zhiqiang
 * 
 */
@Contextview(R.layout.recommend_activity)
public class RecommendActivity extends BaseFragmentActivity implements OnClickListener {
	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		mBack.setOnClickListener(this);
		mTitle.setText(getString(R.string.recommender_title));

		String recommendType = getIntent().getExtras().getString("recommendType");
		Fragment fragment = SearchResultFragment.newInstance(recommendType, null);
		FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
		beginTransaction.add(R.id.recommend_content, fragment);
		beginTransaction.commit();
	}

	@Override
	public void onClick(View v) {
		if (v == mBack) {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
