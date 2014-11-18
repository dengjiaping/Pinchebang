package com.lepin.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.UMSharingMyOrder;
import com.lepin.util.Util;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

@Contextview(R.layout.share_activity)
public class ShareActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitleView;

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBackView;

	@ViewInject(id = R.id.share_activity_share)
	private Button mShareButton;
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
			RequestType.SOCIAL);
	private String mBookId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		mBookId = getIntent().getExtras().getString(Constant.BOOK_ID);
		mTitleView.setText(getString(R.string.share));
		mBackView.setOnClickListener(this);
		mShareButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mBackView) {// 返回
			this.finish();
		} else if (v == mShareButton) {
			if (mBookId.length() > 0 && !mBookId.equals("0"))
				Util.getInstance().share(ShareActivity.this, String.valueOf(mBookId), mController,
						UMSharingMyOrder.SHARE_TYPE_LINE, Constant.URL_SHARE_LINE,
						getString(R.string.share_line_content),
						getString(R.string.share_line_title));
		}
	}
}
