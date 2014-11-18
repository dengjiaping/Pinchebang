package com.lepin.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.entity.Key;
import com.lepin.fragment.SearchResultFragment;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.widget.TopTabIndicator;
import com.lepin.widget.TopTabIndicator.TabClick;

/**
 * 搜索结果展示，搜索条件（上下班、长途、起点、终点进行模糊匹配）。搜索结果以列表展示点击单条信息可以预约。
 * key为由搜索页条件组成的搜索条件，该页面实现了下拉刷新和上拉刷新（点击加载更多）。
 * 
 * 
 */
@Contextview(R.layout.search_results)
public class SearchResultActivity extends BaseFragmentActivity implements OnClickListener {
	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;// 标题
	@ViewInject(id = R.id.common_title_back)
	private ImageView mBackBtn;// 返回

	@ViewInject(id = R.id.indicator_passager_driver)
	private TopTabIndicator indicator;

	private ArrayList<Fragment> mFragments;

	private final static int SDRIVER = 0;
	private final static int SPASSERGER = 1;
	private int currentIndex = SDRIVER;
	@ViewInject(id = R.id.searcher_viewpager)
	private ViewPager mSearcherPager;
	private ScreenAdapter mScreenAdapter;

	public final static String DRIVER_OR_PASSERGER = "driverOrpasserger";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
	}

	private void initView() {
		mTitle.setText(getString(R.string.search_result));
		mBackBtn.setOnClickListener(this);
		indicator.setTabClick(new TabClick() {

			@Override
			public void onTabClick(int index) {
				mSearcherPager.setCurrentItem(index);
				currentIndex = index;
			}
		});
		mFragments = new ArrayList<Fragment>();
		addFragment();
		mScreenAdapter = new ScreenAdapter(getSupportFragmentManager(), mFragments);
		mSearcherPager.setAdapter(mScreenAdapter);
		mSearcherPager.setOnPageChangeListener(mPageChangeListener);
		mSearcherPager.setCurrentItem(0);
	}

	protected void addFragment() {
		final Bundle mBundle = getIntent().getExtras();
		Key key = null;
		if (mBundle != null) key = (Key) mBundle.getSerializable("search_key");
		mFragments.add(SearchResultFragment.newInstance(SDRIVER, key));
		mFragments.add(SearchResultFragment.newInstance(SPASSERGER, key));
	}

	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {

			if (currentIndex != arg0) {
				if (currentIndex == 0) {
					indicator.setTab2Click(TopTabIndicator.RIGHT);
				} else {
					indicator.setTab2Click(TopTabIndicator.LEFT);
				}
				currentIndex = arg0;
			}
			mSearcherPager.setCurrentItem(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	private class ScreenAdapter extends FragmentPagerAdapter {
		private ArrayList<Fragment> mFragments;

		public ScreenAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
			super(fm);
			this.mFragments = fragments;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return mFragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mFragments.size();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SearchResultActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		if (v == mBackBtn) {
			finish();
		}
		// else
		// if (v == mDriverTab) {
		// if (currentIndex != SDRIVER) {
		// setLeftTab();
		// mSearcherPager.setCurrentItem(SDRIVER);
		// }
		// } else if (v == mPassergerTab) {
		// if (currentIndex != SPASSERGER) {
		// setRightTab();
		// mSearcherPager.setCurrentItem(SPASSERGER);
		// }
		// }

	}

}
