package com.lepin.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.entity.Key;
import com.lepin.entity.Pinche;
import com.lepin.fragment.LongFragment;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.widget.TopTabIndicator;
import com.lepin.widget.TopTabIndicator.TabClick;

@Contextview(R.layout.long_fragment_activity)
public class LongFragmentActivity extends BaseFragmentActivity implements OnClickListener {
	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	@ViewInject(id = R.id.long_fragment_pager)
	private ViewPager mViewPager;

	@ViewInject(id = R.id.long_fragment_indicator)
	private TopTabIndicator mIndicator;

	ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private int currentPage = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		ViewInjectUtil.inject(this);
		mTitle.setText(getString(R.string.publishTitle) + "(" + getString(R.string.square_long)
				+ ")");
		mBack.setOnClickListener(this);
		addFragments();
		PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (currentPage != arg0) {
					if (currentPage == 0) {
						mIndicator.setTab2Click(TopTabIndicator.RIGHT);
					} else {
						mIndicator.setTab2Click(TopTabIndicator.LEFT);
					}
					currentPage = arg0;
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		mIndicator.setTabClick(new TabClick() {

			@Override
			public void onTabClick(int index) {
				mViewPager.setCurrentItem(index);
				currentPage = index;
			}
		});
	}

	private void addFragments() {

		Bundle bundle = getIntent().getExtras();
		Key key = null;
		if (bundle != null) {
			key = (Key) bundle.getSerializable("key");
		}
		fragments.add(LongFragment.newInstance(Pinche.PASSENGER, key));
		fragments.add(LongFragment.newInstance(Pinche.DRIVER, key));
	}

	private class PageAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> fragments = null;

		public PageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			return super.instantiateItem(container, position);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.common_title_back:
			this.finish();
			break;
		}
	}
}
