package com.lepin.activity;

import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

/**
 * 周期选择默认为星期一到星期五，如果用户选择星期一到星期五则为每周，选择星期一到星期天则为每周，其他情况为星期一,二....
 * 
 * 
 */
@Contextview(R.layout.week)
public class WeekSelectActivity extends BaseActivity implements OnClickListener {
	@ViewInject(id = R.id.week_workday)
	private CheckBox check_workday;
	@ViewInject(id = R.id.week_all)
	private CheckBox check_all;
	@ViewInject(id = R.id.week_monday)
	private CheckBox check_monday;
	@ViewInject(id = R.id.week_tuesday)
	private CheckBox check_tuesday;
	@ViewInject(id = R.id.week_wednesday)
	private CheckBox check_wednesday;
	@ViewInject(id = R.id.week_thursday)
	private CheckBox check_thursday;
	@ViewInject(id = R.id.week_friday)
	private CheckBox check_friday;
	@ViewInject(id = R.id.week_saturday)
	private CheckBox check_saturday;
	@ViewInject(id = R.id.week_sunday)
	private CheckBox check_sunday;

	@ViewInject(id = R.id.week_sure_btn)
	private Button btnSure;// 确定

	private String result = "星期";
	private String numResult = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		result = getString(R.string.one_2_five);
		initView();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		this.check_workday.setOnClickListener(this);
		this.check_all.setOnClickListener(this);
		this.check_monday.setOnClickListener(this);
		this.check_tuesday.setOnClickListener(this);
		this.check_wednesday.setOnClickListener(this);
		this.check_thursday.setOnClickListener(this);
		this.check_friday.setOnClickListener(this);
		this.check_saturday.setOnClickListener(this);
		this.check_sunday.setOnClickListener(this);
		this.btnSure.setOnClickListener(this);
		this.check_workday.setChecked(true);
	}

	/**
	 * 初始化为工作日
	 */
	private void initWorkday() {// 工作日是选中
		if (this.check_workday.isChecked()) {
			this.check_monday.setChecked(true);
			this.check_tuesday.setChecked(true);
			this.check_wednesday.setChecked(true);
			this.check_thursday.setChecked(true);
			this.check_friday.setChecked(true);
			this.check_saturday.setChecked(false);
			this.check_sunday.setChecked(false);
		} else {
			this.check_monday.setChecked(false);
			this.check_tuesday.setChecked(false);
			this.check_wednesday.setChecked(false);
			this.check_thursday.setChecked(false);
			this.check_friday.setChecked(false);
		}
	}

	/**
	 * 初始化为每日
	 */
	private void initAll() {
		if (this.check_all.isChecked()) {// 每日选择中
			this.check_monday.setChecked(true);
			this.check_tuesday.setChecked(true);
			this.check_wednesday.setChecked(true);
			this.check_thursday.setChecked(true);
			this.check_friday.setChecked(true);
			this.check_saturday.setChecked(true);
			this.check_sunday.setChecked(true);
		} else {
			this.check_monday.setChecked(false);
			this.check_tuesday.setChecked(false);
			this.check_wednesday.setChecked(false);
			this.check_thursday.setChecked(false);
			this.check_friday.setChecked(false);
			this.check_saturday.setChecked(false);
			this.check_sunday.setChecked(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.week_workday:
			if (this.check_all.isChecked()) {
				this.check_all.setChecked(false);
			}
			initWorkday();// 设置为工作日
			break;
		case R.id.week_all:
			if (this.check_workday.isChecked()) {
				this.check_workday.setChecked(false);
			}
			initAll();// 设置为每日
			break;
		case R.id.week_sure_btn:
			handleResult();// 处理时间选择结果
			returnData();
			break;
		default:
			this.check_workday.setChecked(false);
			this.check_all.setChecked(false);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			handleResult();// 处理时间选择结果
			returnData();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void returnData() {
		Intent intent = new Intent();// 将选择结果返回到发布
		if (result.endsWith(",")) result = result.substring(0, result.length() - 1);
		if (numResult.endsWith(",")) numResult = numResult.substring(0, numResult.length() - 1);
		intent.putExtra("result", this.result);
		intent.putExtra("numResult", this.numResult);
		setResult(11, intent);
		WeekSelectActivity.this.finish();
	}

	/**
	 * 日期选择结果处理
	 */
	private void handleResult() {
		if ((this.check_monday.isChecked() && this.check_tuesday.isChecked()
				&& this.check_wednesday.isChecked() && this.check_thursday.isChecked()
				&& this.check_friday.isChecked() && this.check_saturday.isChecked() && this.check_sunday
					.isChecked()) || (this.check_all.isChecked())) {// 每日

			result = getString(R.string.every_day);
			numResult = getString(R.string.every_day_num_text);
		} else if ((this.check_monday.isChecked() && this.check_tuesday.isChecked()
				&& this.check_wednesday.isChecked() && this.check_thursday.isChecked()
				&& this.check_friday.isChecked() && !this.check_saturday.isChecked() && !this.check_sunday
					.isChecked()) || (this.check_workday.isChecked())) {// 工作日

			result = getString(R.string.work_day);
			numResult = getString(R.string.work_day_num_text);
		} else {// 单日选择.
			result = "星期";
			numResult = "";
			if (this.check_monday.isChecked()) {
				addTime(getString(R.string.one));
				addTimeNumText(getString(R.string.one_num));
			}
			if (this.check_tuesday.isChecked()) {
				addTime(getString(R.string.two));
				addTimeNumText(getString(R.string.two_num));
			}
			if (this.check_wednesday.isChecked()) {
				addTime(getString(R.string.three));
				addTimeNumText(getString(R.string.three_num));
			}
			if (this.check_thursday.isChecked()) {
				addTime(getString(R.string.four));
				addTimeNumText(getString(R.string.four_num));
			}
			if (this.check_friday.isChecked()) {
				addTime(getString(R.string.five));
				addTimeNumText(getString(R.string.five_num));
			}
			if (this.check_saturday.isChecked()) {
				addTime(getString(R.string.six));
				addTimeNumText(getString(R.string.six_num));
			}
			if (this.check_sunday.isChecked()) {
				addTime(getString(R.string.seven));
				addTimeNumText(getString(R.string.seven_num));
			}
		}
	}

	private void addTime(String day) {
		result = result + day + ",";
	}

	private void addTimeNumText(String day) {
		numResult = numResult + day + ",";
	}
}
