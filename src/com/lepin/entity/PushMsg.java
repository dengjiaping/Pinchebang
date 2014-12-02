package com.lepin.entity;

import java.io.Serializable;
/**
 * 推送消息类
 * @author zhiqiang
 *
 */
public class PushMsg implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 推送消息的类型
	 * 
	 * @author zhiqiang
	 * 
	 */
	public enum PUSH_MSG_TYPE {
		ORDER, // 订单统称
		/** 新订单推送信息 */
		NEW_CARPOOL_ORDER,
		/** 确认订单推送信息 */
		CONFIRM_CARPOOL_ORDER,
		/** 完成订单推送信息 */
		COMPLETE_CARPOOL_ORDER,
		/** 取消订单推送信息 */
		CANCEL_CARPOOL_ORDER,
		/** 拼车币充值推送信息 */
		RECHARGE_COIN,
		/** 话费充值推送信息 */
		RECHARGE_ORDER,
		/** 车主验证未通过 */
		VERIFY_CAR_FAILURE,
		/** 余额充值成功 */
		RECHARGE_GOLD,
		/** 单条推荐路线 */
		RECOMMEND_SINGLE,
		/** 多条推荐路线 */
		RECOMMEND_MULTI,
		/** 活动 */
		ACTIVITY,
		/** 其它未识别的类型 */
		OTHER

	};

	public static String DELETE = "-1";
	public static String NEW = "0";
	public static String SENT = "1";
	public static String READED = "2";

	private Integer pushMsgId;
	private Integer userId;// 要推送的会员
	private String content;// 推送内容
	private String expand;// 推送扩展信息
	private PUSH_MSG_TYPE type;// 推送类型
	private long createTime;// 创建时间
	private String state;// 信息状态

	public Integer getPushMsgId() {
		return pushMsgId;
	}

	public void setPushMsgId(Integer pushMsgId) {
		this.pushMsgId = pushMsgId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExpand() {
		return expand;
	}

	public void setExpand(String expand) {
		this.expand = expand;
	}

	public PUSH_MSG_TYPE getType() {
		return type;
	}

	public void setType(PUSH_MSG_TYPE type) {
		this.type = type;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "PushMsg [pushMsgId=" + pushMsgId + ", userId=" + userId + ", content=" + content
				+ ", expand=" + expand + ", type=" + type + ", createTime=" + createTime
				+ ", state=" + state + "]";
	}

}
