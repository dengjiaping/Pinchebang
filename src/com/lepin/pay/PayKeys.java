/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.lepin.pay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class PayKeys {

	// 合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088311535610423";

	// 收款支付宝账号
	public static final String DEFAULT_SELLER = "lepinpinche@126.com";

	// RSA商户私钥，自助生成
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMVSUrJKMku+mSHHbSeTubHEr9lrS1HYN81rVZ1pZ0A1U3KumM4Hf0hA4mq89w8JW2VYTU+Q33un90en0Sw5X2omn1cOqCbODlYRNkYCi4cAkAd2u82dEHTEh21jZ6sbDt4V/3qiDFsoKA2peHrbFG+z44jhxkSHXn3BDxc2cKi9AgMBAAECgYAFVdB+mZr6iVDCpQ3CgDAJKnRGwKvsrpfzqe9KrkroZYi4FRh0madLYNW+ZRWZBnu+JcERYa1pPoVlHph9RZ/axrWLMmPvYHUNz1kP57+6ZbDrTGtCQDr9MASvPjAd2Kw0cgIx/eWprxqGa013H4NI7KERa0lDU6Lp/11w5kDzUQJBAPUAabc5zGIhHjIJm0MteaHsMWMoWK1MgpY6Ze/t5A26EtCNAaAIuvdCIafbHvOhsKxGvy/1NyE4IYj/2alD5LMCQQDOLfcsyyLjfDRYsRvk+4C9gsEUXMAicIbWxVWgpIka52neNA/SGaVH+6lJSctLEsFaif/2xkXmsWSO0oZogFTPAkEA4TfY9nLuXAKftFW6YX2rQ1tRZxqOsfgUTy8tx+pCaw/y/b2xkcCUxnkHYTt/72xicWuzks1zOVlVEpBHMV9VGwJBAKifYTrqO/Nu3mT3HIguxUC40m5z2NOR0kWOiJRkXJl7T9NiItpDDTEM+ous5a3VffstSAEscXjV9hb0yOZopnMCQCXYWFsfmXDMTQScc6u7nr5XeyuSZzUdka6mqcoCVLzYhps3MpRMyVXWqCSCIsBWTAylqYo/7i+bixuOMnfLILM=";
	// 支付宝公钥
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

}
