package com.lepin.util;

import android.app.Activity;
import android.content.Context;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class UMSharingMyOrder {
	UMSocialService mController = null;
	private String appId = "wx535fcb7c3cf2e820";// 微信AppID
	// private String contentUrl = Constant.URL_SHARE_ORDER;// 订单地址
	private String contentUrl = "";// 订单地址
	private Context mContext;
	private String share_id;// 订单编号
	// private String title = "拼车帮-订单分享";
	private String title = "拼车帮-订单分享";

	public static final String SHARE_TYPE_ORDER = "bookId";// 订单分享
	public static final String SHARE_TYPE_LINE = "infoId";// 订单分享
	private String shareType = "";
	private String shareContent = "";

	public UMSharingMyOrder(Context context, String share_id, UMSocialService Controller,
			String shareType, String shareUrl, String shareContent, String title) {
		this.mContext = context;
		this.share_id = share_id;
		this.mController = Controller;
		this.contentUrl = shareUrl;
		this.shareType = shareType;
		this.shareContent = shareContent;
		this.title = title;
	}

	public void init() {
		// contentUrl = contentUrl + "?bookId=" + share_id;
		contentUrl = contentUrl + shareType + "=" + share_id;
		Util.printLog("分享路径:" + contentUrl);

		// String shareContent =
		// "我在拼车帮找到了顺路的小伙伴，舒适出行，分摊邮费，更重要的是路上有个人可以说说话，看看我的路线！和你，也顺路么？";
		mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL,
				SHARE_MEDIA.RENREN);

		/* QQ */
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) mContext, "1101169557",
				"5xG2WJogqGLGqAbg");
		qqSsoHandler.addToSocialSDK();

		/* QQ空间 */
		// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，
		// 参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) mContext, "100424468",
				"c7394704798a158208a74ab60104f0ba");
		qZoneSsoHandler.addToSocialSDK();

		/* 微信 */
		UMWXHandler wxHandler = new UMWXHandler((Activity) mContext, appId);
		wxHandler.addToSocialSDK();

		/* 微信朋友圈 */
		UMWXHandler wxCircleHandler = new UMWXHandler((Activity) mContext, appId);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		/* 设置新浪SSO handler */
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		// 设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		/*
		 * //添加人人网SSO授权功能 //APPID:201874 //API
		 * Key:28401c0964f04a72a14c812d6132fcef
		 * //Secret:3bf66e42db1e4fa9829b955cc300b737 RenrenSsoHandler
		 * renrenSsoHandler = new RenrenSsoHandler(mContext, "201874",
		 * "28401c0964f04a72a14c812d6132fcef",
		 * "3bf66e42db1e4fa9829b955cc300b737");
		 * mController.getConfig().setSsoHandler(renrenSsoHandler);
		 */

		/* 短信 */
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();

		mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
				SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA,
				/* SHARE_MEDIA.RENREN, */
				SHARE_MEDIA.SMS);
		// 分享时展示的图片链接
		UMImage mUMImgBitmap = new UMImage(mContext, contentUrl);
		// 微信平台
		WeiXinShareContent weixinContent = new WeiXinShareContent(mUMImgBitmap);
		weixinContent.setShareContent(shareContent + contentUrl);
		weixinContent.setTitle(title);
		weixinContent.setTargetUrl(contentUrl);
		mController.setShareMedia(weixinContent);

		// 微信朋友
		CircleShareContent circleContent = new CircleShareContent(mUMImgBitmap);
		circleContent.setShareContent(shareContent + contentUrl);
		circleContent.setTitle(title);
		circleContent.setTargetUrl(contentUrl);
		mController.setShareMedia(circleContent);

		// 腾讯微博
		TencentWbShareContent tencentContent = new TencentWbShareContent(mUMImgBitmap);
		tencentContent.setShareContent(shareContent + contentUrl);
		mController.setShareMedia(tencentContent);

		// QQ
		QQShareContent qqShareContent = new QQShareContent(mUMImgBitmap);
		qqShareContent.setShareContent(shareContent + contentUrl);
		qqShareContent.setTitle(title);
		mController.setShareMedia(qqShareContent);

		// QQZONE
		QZoneShareContent qZoneShareContent = new QZoneShareContent(mUMImgBitmap);
		qZoneShareContent.setShareContent(shareContent + contentUrl);
		qZoneShareContent.setAppWebSite(contentUrl);
		qZoneShareContent.setTitle(title);
		mController.setShareMedia(qZoneShareContent);

		// 新浪微博
		SinaShareContent sinaShareContent = new SinaShareContent();
		sinaShareContent.setShareContent(shareContent + contentUrl);
		mController.setShareMedia(sinaShareContent);

		/*
		 * // 人人 RenrenShareContent renrenShareContent = new
		 * RenrenShareContent(mUMImgBitmap);
		 * renrenShareContent.setShareContent(shareContent + contentUrl);
		 * renrenShareContent.setAppWebSite(contentUrl);
		 * mController.setShareMedia(renrenShareContent);
		 */

		// 短信
		SmsShareContent smsShareContent = new SmsShareContent();
		smsShareContent.setShareContent(shareContent + contentUrl);
		mController.setShareMedia(smsShareContent);
		mController.registerListener(new SnsPostListener() {

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
				// TODO Auto-generated method stub
				Util.getInstance().getSharePrise(mContext, "SHARE_INFO");
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
			}
		});

	}

	public void startShare() {
		// mController.getConfig().setSinaCallbackUrl(contentUrl);
		mController.openShare((Activity) mContext, false);
	}

}
