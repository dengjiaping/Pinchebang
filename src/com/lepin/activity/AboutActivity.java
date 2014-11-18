package com.lepin.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;

/**
 * 关于我们,乐拼描述，
 * 
 */
@Contextview(R.layout.about)
public class AboutActivity extends BaseActivity {
	@ViewInject(id = R.id.common_title_title)
	private TextView tvTitle;// 关于我们
	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;// 返回
	@ViewInject(id = R.id.about_version_code)
	private TextView tvVersionCode;// 版本号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
	}

	private void initView() {
		this.tvTitle.setText(getResources().getString(R.string.about));
		this.tvVersionCode.setText(Constant.sLocalVersionName);
		this.btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AboutActivity.this.finish();
			}
		});
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
