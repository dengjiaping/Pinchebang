package com.lepin.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;

/**
 * 显示用户使用条款
 * 
 * 
 */
@Contextview(R.layout.use_clause)
public class UseClauseActivity extends BaseActivity {
	@ViewInject(id = R.id.common_title_title)
	private TextView tvTitle;// 标题
	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;// 返回
	@ViewInject(id = R.id.agreement)
	private WebView tvTest;// 显示协议内容

	private String path;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		Bundle bundle = getIntent().getExtras();
		path = bundle.getString("path");
		if (TextUtils.isEmpty(path)) path = Constant.ARGEMENT;
		title = bundle.getString("title");
		if (TextUtils.isEmpty(title)) title = getString(R.string.use_clause);
		initView();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		this.tvTitle.setText(title);
		this.tvTest.loadUrl(path);// 加载用户协议
		this.btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UseClauseActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
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

}
