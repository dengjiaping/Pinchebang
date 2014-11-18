package com.lepin.entity;

import java.io.Serializable;

/**
 * 服务器返回结果处理，包括是否请求成功，返回结果
 * 
 * @param <T>
 *            把JSON格式转换为的实体
 */
public class JsonResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean success;
	private String errorMsg;
	private T data;
	private String errorType;
	public static final String ERRORTYPE_SERVER_ERROR = "SERVER_ERROR";// 服务器错误，请联系管理员
	public static final String ERRORTYPE_LACK_PARAM = "LACK_PARAM";// 缺少必要参数
	public static final String ERRORTYPE_PARAM_TYPE_ERROR = "PARAM_TYPE_ERROR";// 参数类型错误
	public static final String ERRORTYPE_UN_LOGIN = "UN_LOGIN";// 未登录或登录已超时
	public static final String ERRORTYPE_OTHER_DEVICE_LOGGED = "OTHER_DEVICE_LOGGED";// 其他设备登录了

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public JsonResult() {

	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "JsonResult [success=" + success + ", errorMsg=" + errorMsg + ", data="
				+ ((data == null)?"":data.toString()) + "]";
	}

}
