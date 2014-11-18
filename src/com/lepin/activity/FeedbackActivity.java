package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

/**
 * 反馈意见[反馈内容、联系方式]
 * 
 * 
 */
@Contextview(R.layout.feedback)
public class FeedbackActivity extends BaseActivity {
	@ViewInject(id = R.id.common_title_title)
	private TextView tvTitle;// 标题
	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;// 返回按钮
	@ViewInject(id = R.id.feedback_et)
	private EditText etContent;// 反馈内容
	@ViewInject(id = R.id.feedback_contact)
	private EditText etContact;// 联系方式
	@ViewInject(id = R.id.feedback_btn_submit)
	private TextView btnSubmit;// 提交按钮
	@ViewInject(id = R.id.textLength)
	private TextView textLenth;
	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		this.tvTitle.setText(getResources().getString(R.string.more_idea_feedback_txt));// 初始化title
		// 返回
		this.btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FeedbackActivity.this.finish();
			}
		});

		etContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				textLenth.setText("您可以输入" + (200 - s.length()) + "字");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		// 提交
		this.btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (util.isNetworkAvailable(FeedbackActivity.this)) {
					String content = etContent.getText().toString();
					if (!util.isNullOrEmpty(content)) {
						if (content.length() <= 200) {// 文本内容不得大于200字符
							btnSubmit.setEnabled(false);
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							String contact = etContact.getText().toString();
							params.add(new BasicNameValuePair("content", content));
							params.add(new BasicNameValuePair("contact", contact));// 联系方式

							util.doPostRequest(FeedbackActivity.this,
									new OnHttpRequestDataCallback() {

										public void onSuccess(String result) {
											TypeToken<JsonResult<String>> token = new TypeToken<JsonResult<String>>() {
											};
											Gson gson = new GsonBuilder().create();
											JsonResult<String> json = gson.fromJson(result,
													token.getType());
											if (json.isSuccess()) {
												etContent.setText("");
												etContact.setText("");
												Util.showToast(FeedbackActivity.this, json
														.getData().toString());
											} else {
												Util.showToast(FeedbackActivity.this,
														"很抱歉反馈内容提交失败，请稍后再试！");
											}
										}

									}, params, Constant.URL_FEDDBACK, "反馈信息提交中...", false);

							// Util.getInstance().doPostRequest(FeedbackActivity.this,
							// new OnDataLoadingCallBack() {
							//
							// @Override
							// public void onLoadingBack(String result) {
							// // TODO Auto-generated method stub
							// if (!util.isNullOrEmpty(result)) {
							// TypeToken<JsonResult<String>> token = new
							// TypeToken<JsonResult<String>>() {
							// };
							// Gson gson = new GsonBuilder().create();
							// JsonResult<String> json = gson.fromJson(result,
							// token.getType());
							// if (json.isSuccess()) {
							// etContent.setText("");
							// etContact.setText("");
							// Util.showToast(FeedbackActivity.this, json
							// .getData().toString());
							// } else {
							// Util.showToast(FeedbackActivity.this,
							// "很抱歉反馈内容提交失败，请稍后再试！");
							// }
							// }
							// }
							// }, paramsList, Constant.URL_FEDDBACK,
							// "反馈信息提交中...");

							// LoadingDataDialog loadFeedback = util
							// .getLoadingDataDialog(FeedbackActivity.this);
							// // loadFeedback.setTitle();
							// loadFeedback.executePost(new
							// OnDataLoadingCallBack() {
							//
							// @Override
							// public void onLoadingBack(String result) {
							// // TODO Auto-generated method stub
							// if (!util.isNullOrEmpty(result)) {
							// TypeToken<JsonResult<String>> token = new
							// TypeToken<JsonResult<String>>() {
							// };
							// Gson gson = new GsonBuilder().create();
							// JsonResult<String> json = gson.fromJson(result,
							// token.getType());
							// if (json.isSuccess()) {
							// etContent.setText("");
							// etContact.setText("");
							// Util.showToast(FeedbackActivity.this,
							// json.getData()
							// .toString());
							// } else {
							// Util.showToast(FeedbackActivity.this,
							// "很抱歉反馈内容提交失败，请稍后再试！");
							// }
							// }
							// }
							// }, paramsList, Constant.URL_FEDDBACK);
							// loadFeedback.setTitleInfo("反馈信息提交中...");
							btnSubmit.setEnabled(true);
						} else {
							Util.showToast(FeedbackActivity.this, "反馈内容应少于200字符");
						}
					} else {
						Util.showToast(FeedbackActivity.this, "请先填写反馈内容");
					}
				} else {
					util.showTip(FeedbackActivity.this);
				}
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
