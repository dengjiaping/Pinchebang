package com.lepin.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.util.Util;

/**
 * 广场ListView支持下拉刷新，上拉加载更多
 * 
 */
public class PcbListView extends ListView implements OnScrollListener {
	private float mLastY = -1; // 保存触摸滑动的y坐标

	private Scroller mScroller; // 滚动返回
	private OnScrollListener mScrollListener; // 用户屏幕滚动事件监听

	// IXListViewListener引发刷新和加载更多
	private PcbListViewListener mListViewListener;

	private PcbXListViewHeader mHeaderView;// Listview头部布局

	// 整个头部布局视图，通过计算视图高度使其在没有正在刷新的状态时隐藏头部
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;// 显示加载时间
	private int mHeaderViewHeight; // 头部布局的高度

	private boolean mEnablePullRefresh = true;// 是否可以下拉刷新（默认可以）
	private boolean mPullRefreshing = false; // 是否正在刷新

	private PcbListViewFooter mFooterView;// ListView页脚布局

	private boolean mEnablePullLoad;// 是否能够上拉加载更多
	private boolean mPullLoading;// 是否正在下拉加载
	private boolean mIsFooterReady = false;// 页脚是否有

	private int mTotalItemCount;// 记录ListView每次加载的总子项数

	private int mScrollBack;// 滚动返回到头部或者底部
	private final static int SCROLLBACK_HEADER = 0;// 回滚到头部
	private final static int SCROLLBACK_FOOTER = 1;// 回滚到底部

	private final static int SCROLL_DURATION = 400; // 回滚持续时间
	private final static int PULL_LOAD_MORE_DELTA = 40; // 当上拉超过50px是引发加载更多

	private final static float OFFSET_RADIO = 1.8f; // 实现类似ios的效果

	public PcbListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public PcbListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public PcbListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);

		// 初始化ListView头部
		mHeaderView = new PcbXListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);// 显示刷新时间
		addHeaderView(mHeaderView);

		// 初始化ListView页脚
		mFooterView = new PcbListViewFooter(context);

		// 初始化头部高度
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mHeaderViewHeight = mHeaderViewContent.getHeight();// 获取头部组件的高度
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	/**
	 * 第一次加载时加载底部布局
	 */
	@Override
	public void setAdapter(ListAdapter adapter) {
		// 添加页脚，仅仅添加一次
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * 是否可以下拉刷新处理
	 * 
	 * @param enable
	 *            true可以下拉刷新，false可以下拉刷新
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // 下拉刷新不可
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 是否可以加载更多处理,上拉或者点击加载更多
	 * 
	 * @param enable
	 *            true可以上拉，或者点击加载更多，false页脚隐藏无数据可以加载
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();// 隐藏页脚
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();// 显示页脚
			mFooterView.setState(PcbListViewFooter.STATE_NORMAL);
			// 上拉或者点击加载更多
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();// 加载更多
				}
			});
		}
	}

	/**
	 * 停止下拉刷新并重置头部布局
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {// 如果正在刷新
			mPullRefreshing = false;
			resetHeaderHeight();// 重绘头部布局
		}
	}

	/**
	 * 停止加载更多并重置页脚
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {// 如果长在加载更多
			mPullLoading = false;
			mFooterView.setState(PcbListViewFooter.STATE_NORMAL);// 重置页脚（初始化为下拉刷新）
		}
	}

	/**
	 * 设置最后刷新时间
	 * 
	 * @param time
	 *            上次刷新的时间
	 */
	public void setRefreshTime(String time) {
		if (Util.getInstance().isNullOrEmpty(time)) {
			mHeaderTimeView.setText("刚刚");
		} else {
			mHeaderTimeView.setText(time);
		}
	}

	/**
	 * 调用ListView.OnScrollListener
	 */
	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	/**
	 * 通过设置高度改变头部布局的状态
	 * 
	 * @param delta
	 */
	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 可以下拉刷新并且没有处于正在刷新状态
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(PcbXListViewHeader.STATE_READY);// 松开加载更多
			} else {
				mHeaderView.setState(PcbXListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // 恢复ListView item的位置，0时头部布局隐藏
	}

	/**
	 * 重置头部布局的高度
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) { // 头部布局不显示
			return;
		}
		// 正在刷新时头部没有充分的显示，这时什么也不做
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // 默认头部回滚之后会把头部隐藏
		if (mPullRefreshing && height > mHeaderViewHeight) {// 正在刷新，仅仅回滚显示整个头部
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;// 回滚到头部
		/**
		 * 当startScroll执行过程中即在duration时间内，computeScrollOffset方法会一直返回false，
		 * 但当动画执行完成后会返回返加true。postInvalidate执行后，会去调computeScroll 方法
		 */
		mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		invalidate();// 重绘头部视图
	}

	/**
	 * 通过设置页脚Margin改变页脚布局状态
	 * 
	 * @param delta
	 */
	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {// 可以上拉加载更多并且没有处于加载状态
			if (height > PULL_LOAD_MORE_DELTA) {// 当上拉的高度超过50px时会触发加载更多
				mFooterView.setState(PcbListViewFooter.STATE_READY);
			} else {// 无法触发加载更多
				mFooterView.setState(PcbListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);// 设置页脚Margin
	}

	/**
	 * 重置页脚高度
	 */
	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;// 回滚到底部
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
			invalidate();
		}
	}

	/**
	 * 上拉或者点击加载更多操作
	 */
	private void startLoadMore() {
		mPullLoading = true;// 设置状态为正在加载状态
		mFooterView.setState(PcbListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();// IXListViewListener接口回到加载更多
		}
	}

	private float xDistance, yDistance, lastX, lastY;

	/**
	 * ACTION_DOWN时返回false，ACTION_MOVE时返回true
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			lastX = ev.getX();
			lastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += Math.abs(curX - lastX);
			yDistance += Math.abs(curY - lastY);
			lastX = curX;
			lastY = curY;
			if (xDistance > yDistance) {
				return false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();// 获取相对于屏幕左上角的y坐标
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {// 头部
				// 处于顶部并且头部是被释放掉了的时候，就可以下拉进行刷新
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();// 调用滚动监听
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {// 页脚
				// 在列表的最后项，并且已经上拉或者准备上拉
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {// 回滚到头部
				// 引发刷新
				if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(PcbXListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();// 重置头部高度
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {// 回滚到页脚
				// 加载更多
				if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();// 重置底部布局
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		// 滚动监听获取总的子项数
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public void setPcbListViewListener(PcbListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * ListView.OnScrollListener监听接口，将调用onXScrolling当回滚到头部或页脚时
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * 实现IXListViewListener将获取下拉刷新，加载更多事件
	 */
	public interface PcbListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}
}
