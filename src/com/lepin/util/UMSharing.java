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

public class UMSharing {
	UMSocialService mController = null;
	// UMServiceFactory.getUMSocialService("com.umeng.share",RequestType.SOCIAL);
	private String appId = "wx535fcb7c3cf2e820";// 微信AppID
	private String contentUrl = Constant.SAPKDOWNLOAD;// 更新地址
	private Context mContext;
	private String title = "拼车帮";
	private String sContent = "——小伙伴们快来和我一起使用吧！";

	// public UMSharing(Context context) {
	// this.mContext = context;
	// }

	public UMSharing(Context context, UMSocialService Controller) {
		this.mContext = context;
		this.mController = Controller;
	}

	public void init() {

		String shareContent = getInfo() + sContent;
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
		UMImage mUMImgBitmap = new UMImage(mContext, Constant.SHAREICO);
		// 微信平台
		WeiXinShareContent weixinContent = new WeiXinShareContent(mUMImgBitmap);
		weixinContent.setShareContent(shareContent);
		weixinContent.setTitle(title);
		weixinContent.setTargetUrl(contentUrl);
		mController.setShareMedia(weixinContent);

		// 微信朋友
		CircleShareContent circleContent = new CircleShareContent(mUMImgBitmap);
		circleContent.setShareContent(shareContent);
		circleContent.setTitle(title + sContent);
		circleContent.setTargetUrl(contentUrl);
		mController.setShareMedia(circleContent);

		// 腾讯微博
		TencentWbShareContent tencentContent = new TencentWbShareContent(mUMImgBitmap);
		tencentContent.setShareContent(shareContent + Constant.SAPKDOWNLOAD);
		tencentContent.setAppWebSite(Constant.SAPKDOWNLOAD);
		mController.setShareMedia(tencentContent);

		// QQZONE
		QZoneShareContent qZoneShareContent = new QZoneShareContent(mUMImgBitmap);
		qZoneShareContent.setShareContent(shareContent);
		// qZoneShareContent.setAppWebSite(contentUrl);
		qZoneShareContent.setTitle(title);
		qZoneShareContent.setTargetUrl(contentUrl);
		mController.setShareMedia(qZoneShareContent);

		// QQ
		QQShareContent qqShareContent = new QQShareContent(mUMImgBitmap);
		qqShareContent.setShareContent(shareContent);
		qqShareContent.setTitle(title);
		qqShareContent.setTargetUrl(contentUrl);
		mController.setShareMedia(qqShareContent);

		// 新浪微博
		SinaShareContent sinaShareContent = new SinaShareContent(mUMImgBitmap);
		sinaShareContent.setShareContent(shareContent + Constant.SAPKDOWNLOAD);
		mController.setShareMedia(sinaShareContent);

		/*
		 * // 人人 RenrenShareContent renrenShareContent = new
		 * RenrenShareContent(mUMImgBitmap);
		 * renrenShareContent.setShareContent(shareContent +
		 * Constant.SAPKDOWNLOAD); renrenShareContent.setAppWebSite(contentUrl);
		 * mController.setShareMedia(renrenShareContent);
		 */

		// 短信
		SmsShareContent smsShareContent = new SmsShareContent();
		smsShareContent.setShareContent(shareContent + Constant.SAPKDOWNLOAD);
		mController.setShareMedia(smsShareContent);
		mController.registerListener(new SnsPostListener() {

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
				// TODO Auto-generated method stub
				Util.getInstance().getSharePrise(mContext, "SHARE_APP");
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
			}
		});
	}

	public void startShare() {
		// mController.getConfig().setSinaCallbackUrl("http://api.52pcb.com/share.jsp");
		mController.openShare((Activity) mContext, false);
	}

	public static String getInfo() {
		String[] items = new String[] { "拼车出行，舒服又省钱", "行遇知音，拼车随行", "绿色出行，拼车由我", "出行不易，拼车帮你" };
		int index = (int) (Math.random() * 4);
		return items[index];
	}
}
