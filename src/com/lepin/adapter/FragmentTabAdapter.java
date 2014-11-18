package com.lepin.adapter;

import java.util.List;

import com.lepin.activity.R;
import com.lepin.util.Constant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created with IntelliJ IDEA. Author: wangjie email:tiantian.china.2@gmail.com
 * Date: 13-10-10 Time: 上午9:25
 */
public class FragmentTabAdapter implements OnClickListener {
	private List<Fragment> fragments; // 一个tab页面对应一个Fragment
	private FragmentActivity fragmentActivity; // Fragment所属的Activity
	private int fragmentContentId; // Activity中所要被替换的区域的id
	public static final int LEFT_FRAGMENT = 0;
	public static final int RIGHT_FRAGMENT = 1;
	private int mCurrentFragment = LEFT_FRAGMENT; // 当前Tab页面索引

	// private OnRgsExtraCheckedChangedListener
	// onRgsExtraCheckedChangedListener; // 用于让调用者在切换tab时候增加新的功能

	public FragmentTabAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments,
			int fragmentContentId) {
		this.fragments = fragments;
		this.fragmentActivity = fragmentActivity;
		this.fragmentContentId = fragmentContentId;

		// 默认显示第一页
		FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
		ft.add(fragmentContentId, fragments.get(LEFT_FRAGMENT));
		ft.commitAllowingStateLoss();
	}

	/**
	 * 切换tab
	 * 
	 * @param index
	 */
	public void showTab(int index) {
		if (index != mCurrentFragment) {
			mCurrentFragment = index;
			Constant.home_current_fragment = index;
			Fragment tofragment = fragments.get(index);
			Fragment fromFragment = null;
			if (index == LEFT_FRAGMENT) {
				fromFragment = fragments.get(RIGHT_FRAGMENT);
			} else {
				fromFragment = fragments.get(LEFT_FRAGMENT);
			}
			FragmentTransaction ft = obtainFragmentTransaction(index);

			if (!tofragment.isAdded()) {
				ft.hide(fromFragment).add(fragmentContentId, tofragment).commitAllowingStateLoss();
			} else {
				ft.hide(fromFragment).show(tofragment).commitAllowingStateLoss();
			}

		}
	}

	/**
	 * 获取一个带动画的FragmentTransaction
	 * 
	 * @param index
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
		// 设置切换动画
		if (index > mCurrentFragment) {
			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
		} else {
			ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
		}
		return ft;
	}

	public int getCurrentTab() {
		return mCurrentFragment;
	}

	public Fragment getCurrentFragment() {
		return fragments.get(mCurrentFragment);
	}

	public void setOnRgsExtraCheckedChangedListener(
			OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
		// this.onRgsExtraCheckedChangedListener =
		// onRgsExtraCheckedChangedListener;
	}

	/**
	 * 切换tab额外功能功能接口
	 */
	public interface OnRgsExtraCheckedChangedListener {
		public void OnRgsExtraCheckedChanged(View rootView, int oldId, int newId);
	}

	@Override
	public void onClick(View v) {
		// final int clickViewId = (Integer) v.getTag();
		// final int oldViewId = currentTab;
		// if (currentTab != clickViewId) {
		// Fragment fragment = fragments.get(clickViewId);
		// FragmentTransaction ft = obtainFragmentTransaction(currentTab);
		// getCurrentFragment().onPause(); // 暂停当前tab
		//
		// if (fragment.isAdded()) {
		// // fragment.onStart(); // 启动目标tab的onStart()
		// fragment.onResume(); // 启动目标tab的onResume()
		// } else {
		// ft.add(fragmentContentId, fragment);
		// }
		// showTab(clickViewId); // 显示目标tab
		// currentTab = clickViewId; // 更新目标tab为当前tab
		// ft.commit();
		//
		//
		// }

	}

}
