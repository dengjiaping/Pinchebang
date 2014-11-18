package com.lepin.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.fragment.MyPublishFragment;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.widget.TopTabIndicator;
import com.lepin.widget.TopTabIndicator.TabClick;

@Contextview(R.layout.publish_activity)
public class MyPublishActivity extends BaseFragmentActivity {

	@ViewInject(id = R.id.publish_viewpager)
	private ViewPager viewPager;

	@ViewInject(id = R.id.publish_indicator)
	private TopTabIndicator indicator;

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;// 返回按钮

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;// 标题

	@ViewInject(id = R.id.common_title_operater)
	private TextView mPublish;// 发布
    /*0 发布长途，1 发布上下班*/
	private int currentPage = 0;
	// 订单状态
	public static final int SOPEN = 0;
	public static final int SCLOSE = -1;
	public static final int SDELETE = -2;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ViewInjectUtil.inject(this);
		setTitleLayout();
		indicator.setTabClick(new TabClick() {

			@Override
			public void onTabClick(int index) {
				viewPager.setCurrentItem(index);
				currentPage = index;

			}
		});
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(MyPublishFragment.newInstance(Constant.SWORK));
		fragments.add(MyPublishFragment.newInstance(Constant.SLONG));
		ScreenAdapter screenAdapter = new ScreenAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(screenAdapter);
		viewPager.setOnPageChangeListener(new PageChangeListener());
	}

	private void setTitleLayout() {
		mTitle.setText(getString(R.string.my_pinche_title));
		mBack.setOnClickListener(onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mBack) {
				finish();
			} /*else if (v == mPublish) {
				Intent intent = new Intent(MyPublishActivity.this, PublishMainActivity.class);// 发布
				intent.putExtra("mypub", true);
				startActivity(intent);
				finish();
			}*/
		}
	};

	private class PageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			if (currentPage != arg0) {
				if (currentPage == 0) {
					indicator.setTab2Click(TopTabIndicator.RIGHT);
				} else {
					indicator.setTab2Click(TopTabIndicator.LEFT);
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
	}

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

}
