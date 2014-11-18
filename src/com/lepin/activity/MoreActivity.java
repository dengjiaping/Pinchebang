package com.lepin.activity;

import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

@Contextview(R.layout.more)
public class MoreActivity extends BaseActivity implements OnClickListener {
	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;// 标题

	@ViewInject(id = R.id.common_title_back)
	private ImageView mback;// 回退按钮

	@ViewInject(id = R.id.more_version_code)
	private TextView tvVersionCode;// 版本号

	/*
	 * @ViewInject(id = R.id.more_check_update) private View mCheckUpdate;//
	 * 检查新版本
	 */
	@ViewInject(id = R.id.more_idea_feedback)
	private View mFeedback;// 意见反馈
	@ViewInject(id = R.id.more_about)
	private View mAbout;// 关于我们
	@ViewInject(id = R.id.more_use_clause)
	private View mUseClause;// 使用条款
	@ViewInject(id = R.id.more_share)
	private View mShare;
	private Util util = Util.getInstance();

	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
			RequestType.SOCIAL);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		this.mUseClause.setOnClickListener(this);
		this.mAbout.setOnClickListener(this);
		this.mFeedback.setOnClickListener(this);
		this.mback.setOnClickListener(this);
		// this.mCheckUpdate.setOnClickListener(this);
		this.mShare.setOnClickListener(this);
		this.mTitle.setText(getResources().getString(R.string.moreTitle));// 初始化title
		if ("http://api.52pcb.com".equals(Constant.URL_LOCAL)) {
			this.tvVersionCode.setText(Constant.sLocalVersionName);
		} else {
			this.tvVersionCode.setText(Constant.sLocalVersionName + " 本地 ");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		 * case R.id.more_user_grade:// 评价 appMarketGrade();// 市场评价 break;
		 */
		/*
		 * case R.id.more_check_update:// 版本检测 // 检查是否有版本更新
		 * util.showLongToast(getActivity(), getString(R.string.check_version));
		 * util.checkUpdate(getActivity(), true); break;
		 */
		case R.id.common_title_back:
			MoreActivity.this.finish();
			break;
		case R.id.more_idea_feedback:// 意见反馈
			util.go2Activity(MoreActivity.this, FeedbackActivity.class);
			break;
		case R.id.more_about:// 关于我们
			util.go2Activity(MoreActivity.this, AboutActivity.class);
			break;
		case R.id.more_use_clause:// 使用条款
			Util.getInstance().go2StaticHtmlPage(MoreActivity.this, Constant.ARGEMENT, getString(R.string.use_clause));
			break;
		case R.id.more_share:
			util.share(MoreActivity.this, mController);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		mController.getConfig().setSinaCallbackUrl("http://api.52pcb.com/share.jsp");
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

	}
}