package com.lepin.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * 验证电子邮件、用户密码、电话号码、牌照工具类
 * 
 */
public class ValidateTool {

	/**
	 * 验证电子邮件的格式
	 * 
	 * @param paramString
	 * @return
	 */
	public static boolean validateEmailFormat(String paramString) {
		return Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+", 2).matcher(paramString)
				.matches();
	}

	/**
	 * 验证用户密码格式和长度
	 * 
	 * @param paramString
	 * @return
	 */
	public static boolean validateLoginPwd(String paramString) {
		return Pattern.compile("^[A-Za-z0-9]{6,16}$").matcher(paramString).matches();
	}

	/**
	 * 验证电话号码的合法性
	 * 
	 * @param paramString
	 * @return
	 */
	public static boolean validateMobileNum(String paramString) {
		return Pattern.compile("^1\\d{10}$").matcher(paramString).matches();
	}

	/**
	 * 验证汽车牌照合法性
	 * 
	 * @param paramString
	 *            如：川ALP889
	 * @return
	 */
	public static boolean validateLicense(String paramString) {
		return Pattern.compile("[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}").matcher(paramString)
				.matches();
	}

	/**
	 * 限制输入数字，字母，中文，标点，空格　
	 * 
	 * @param inputString
	 * @return
	 */
	public static boolean isInputLegitimate(String inputString) {
		String patternString = "([0-9]|[a-zA-Z]|[\u4e00-\u9fa5]|//pP|\\s)*";
		try {
			patternString = URLDecoder.decode(patternString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern pattern = Pattern.compile(patternString);
		if (pattern.matcher(inputString).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 限制输入数字，字母，中文
	 * 
	 * @param inputString
	 * @return
	 */
	public static boolean isSearchInputLegitimate(String inputString) {
		String patternString = "([0-9]|[a-zA-Z]|[\u4e00-\u9fa5])*";
		Pattern pattern = Pattern.compile(patternString);
		if (pattern.matcher(inputString).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 校验用户名是否正确
	 * 
	 * @param newName
	 * @return
	 */
	public static boolean isUserNameRight(String newName) {
		String all = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{1,10}$";
		Pattern pattern = Pattern.compile(all);
		return pattern.matcher(newName).matches();
	}
}
