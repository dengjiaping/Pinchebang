package com.lepin.fragment;

import java.util.ArrayList;

import com.lepin.activity.R;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.widget.TopTabIndicator;
import com.lepin.widget.TopTabIndicator.TabClick;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeOrderFragment extends BaseFragment {

	private View mRootView;

	@ViewInject(id = R.id.home_order_pager)
	private ViewPager mViewPager;

	@ViewInject(id = R.id.home_order_indicator)
	private TopTabIndicator mIndicator;

	private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
	private int currentIndex = 0;
	private PageAdapter mPageAdapter;

	public final static String IDENTITY = "Identity";// 身份
	public final static String DIRVER = "I_AM_DRIVER";
	public final static String PASSENGER = "I_AM_PASSENGER";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mRootView = inflater.inflate(R.layout.home_order_fragment, container, false);
		ViewInjectUtil.inject(this, mRootView);
		return mRootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable("fragments", mFragments);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mIndicator.setTabClick(new TabClick() {

			@Override
			public void onTabClick(int index) {
				mViewPager.setCurrentItem(index);
				currentIndex = index;
			}
		});
		if (savedInstanceState == null) {
			addFragment();
		} else {
			mFragments = (ArrayList<Fragment>) savedInstanceState.getSerializable("fragments");
		}
		initView();
	}

	private void initView() {
		// addFragment();
		mPageAdapter = new PageAdapter(getFragmentManager(), mFragments);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(0);
	}

	protected void addFragment() {
		mFragments.add(MyOrderFragment.newInstance(PASSENGER));
		mFragments.add(MyOrderFragment.newInstance(DIRVER));
	}

	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 != currentIndex) {
				if (arg0 == TopTabIndicator.LEFT) {
					mIndicator.setTab2Click(TopTabIndicator.LEFT);
				} else if (arg0 == TopTabIndicator.RIGHT) {
					mIndicator.setTab2Click(TopTabIndicator.RIGHT);
				}
				mViewPager.setCurrentItem(arg0);
				currentIndex = arg0;
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	private class PageAdapter extends FragmentPagerAdapter {
		private ArrayList<Fragment> mFragments;

		public PageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
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

	public HomeOrderFragment() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
