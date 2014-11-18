package com.lepin.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;

@Contextview(R.layout.my_pc_money)
public class MyPinCheMoneyActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView pmBack;// 返回
	@ViewInject(id = R.id.common_title_title)
	private TextView pmTitle;// 标题

	@ViewInject(id = R.id.mp_pc_money_value)
	private TextView pcbNum;// 余额

	@ViewInject(id = R.id.mp_pc_money_get_pcb_layout)
	private LinearLayout mGetPcbLayout;

	@ViewInject(id = R.id.mp_pc_money_click_join_btn)
	private Button mClickJoinButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();

	}

	public void initView() {
		pmTitle.setText(this.getString(R.string.my_pc_mongy_title));
		pmBack.setOnClickListener(this);
		mGetPcbLayout.setOnClickListener(this);
		mClickJoinButton.setOnClickListener(this);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	public void onClick(View v) {
		if (v == pmBack) {
			this.finish();
		} else if (v == mGetPcbLayout) {
			Util.getInstance().go2StaticHtmlPage(MyPinCheMoneyActivity.this, Constant.GET_PCB_WAYS,
					getString(R.string.how_to_get_pcb));
		} else if (v == mClickJoinButton) {// 点击参与
			int pcb = Integer.parseInt(pcbNum.getText().toString());
			if (pcb < 100) {
              Util.showToast(this, "抱歉,拼车币不足无法抽奖!");
			} else {
				Util.getInstance().go2Activity(this, LotteryActivity.class);
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Util.getInstance().getUserCoins(MyPinCheMoneyActivity.this, pcbNum);
	}
}
