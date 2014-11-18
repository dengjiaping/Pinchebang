package com.lepin.util;

import java.util.HashMap;

import android.os.Environment;

import com.lepin.adapter.FragmentTabAdapter;

/**
 * 静态常量类
 * 
 */
public class Constant {
	public static int sLocalVersionCode = 0;// 本地安装版本
	public static String sLocalVersionName = "1.0";// 已安装apk版本号
	public static String currCity = "北京";// 默认城市
	public static int currCityCode = 131;// 成市id
	public static String currDistrict;// 行政区划

	public static String CURRENT_ADDRESS = "";// 当前定位位置
	public static double CURRENT_ADDRESS_LON = 0;// 当前定位经度
	public static double CURRENT_ADDRESS_LAT = 0;// 当前定位纬度

	public static int s_CURRENT_LON = 0;// 当前位置经度
	public static int s_CURRENT_LAT = 0;// 当前位置纬度

	/*
	 * 远程路径
	 */
	/*
	 * public static final String HOST = "api.52pcb.com"; public static final
	 * String URL_LOCAL = "http://api.52pcb.com"; // 图片 public static final
	 * String URL_RESOURCE = "http://sr.52pcb.com";
	 */

	// * 本地206（李）
	public static final String HOST = "192.168.4.206";
	public static final String URL_LOCAL = "http://" + HOST + ":80/pcb";
	public static final String URL_RESOURCE = "http://192.168.4.204:8080/sr";

	/*
	 * 本地 205(黄)
	 */

	// public static final String HOST = "192.168.4.205";
	// public static final String URL_LOCAL = "http://" + HOST + ":80/pcb";
	// public static final String URL_RESOURCE = "http://192.168.4.204:8080/sr";

	// 版本信息
	public final static String REQUEST_TYPE = "android";
	public final static int PCB_VERSION = 8;// 接口版本

	// 汽车
	public static final String URL_CAR_SERVER = "http://merchantmgr.52pcb.com";
	/*---------------------------------------------具体业务路径------------------------------------------*/
	// 获取信息
	public static final String URL_GET_INFO_BY_ID = URL_LOCAL + "/info/getInfoById.do?";
	public static final String URL_GET_INFOS = URL_LOCAL + "/info/getInfos.do";
	public static final String URL_GET_TRAIL_PERSONAL_INFO = URL_LOCAL // 线路详情获得个人主页
			+ "/logged/user/getHomepage.do?";
	public static final String URL_GET_TRAIL_PERSONAL_LINES = URL_LOCAL // 线路详情获得他发布的线路
			+ "/logged/info/getHisInfos.do?";

	// 订单
	public static final String URL_GET_ORDERS = URL_LOCAL + "/logged/infoOrder/gets.do?";// 获取信息
	public static final String URL_GET_ORDER_BY_ID = URL_LOCAL + "/logged/infoOrder/getInfoOrderById.do";// 获取订单信息

	public static final String URL_ADDBOOK = URL_LOCAL + "/logged/book/addBook.do";// 预约
	public static final String URL_CANCLEBOOK = URL_LOCAL + "/logged/infoOrder/cancel.do";// 取消订单

	public static final String URL_COMPLETEBOOK = URL_LOCAL + "/logged/book/completeBook.do";// 完成订单

	public static final String URL_CONFIRMBOOK = URL_LOCAL + "/logged/book/confirmBook.do";// 确认订单
	// 获取订单中的日历
	public static final String URL_GET_ORDER_CALENDAR = URL_LOCAL
			+ "/logged/carpoolProgram/getDetail.do";
	// 登录，注销
	public static final String URL_LOGIN = URL_LOCAL + "/user/login.do?";// 登录
	public static final String URL_LOGINED = URL_LOCAL + "/logged/user/getLoginUser.do";
	public static final String URL_LOGOUT = URL_LOCAL + "/logged/user/logout.do";// 注销
	public static final String URL_ADDUSER = URL_LOCAL + "/user/addUser.do?";// 注册用户

	public static final String URL_PUBLISH = URL_LOCAL + "/logged/info/addInfo.do";
	public static final String URL_GET_PINCHE_BI = URL_LOCAL + "/logged/user/getUserCoin.do";
	public static final String URL_CHECKUSERACCOUNT = URL_LOCAL + "/user/checkUserAccount.do?";// 检查账号是否被使用
	public static final String URL_SENDTELCODE = URL_LOCAL + "/common/sendTelCode.do?";// 检查账号是否被使用
	public static final String URL_SETTINGPAYPSW = URL_LOCAL + "/logged/user/editPayPwd.do";// 设置支付密码
	public static final String URL_FINDPWDBYTEL = URL_LOCAL + "/user/modifyPwdByTel.do?";// 忘记密码
	public static final String URL_GETUSERPUBINFOS = URL_LOCAL + "/logged/user/getUserInfos.do?";// 获取用户发布的拼车信息

	public static final String URL_OPENINFO = URL_LOCAL + "/logged/info/openInfo.do?";// 开启信息
	public static final String URL_CLOSEINFO = URL_LOCAL + "/logged/info/closeInfo.do?";// 关闭信息
	public static final String URL_DELINFO = URL_LOCAL + "/logged/info/delInfo.do?";// 删除信息
	public static final String URL_GETINFOBYID = URL_LOCAL + "/info/getInfoById.do?";// 获取详细信息
	public static final String URL_EDITINFO = URL_LOCAL + "/logged/info/editInfo.do?";// 修改信息
	public static final String URL_VERIFY_DRIVER = URL_LOCAL + "/logged/upload/allUpload.do"; // 上传图片
	// 订单
	public static final String URL_GETUSERCARINFO = URL_LOCAL + "/logged/car/getUserCarById.do";// 获取用户车辆信息
	public static final String URL_GETALLCARBRANDS = URL_LOCAL + "/car/getAllCarBrands.do";// 获取车辆品牌
	public static final String URL_GETCARTYPE = URL_LOCAL + "/car/getCarTypeByCarBrandId.do?";// 获取车辆型号
	public static final String URL_MODIFYCAR = URL_LOCAL + "/logged/car/modifyCar.do?";// 修改车辆
	public static final String URL_ADDCAR = URL_LOCAL + "/logged/car/addCar.do?";// 添加车辆
	public static final String URL_CARVERIFICATION = URL_LOCAL
			+ "/logged/car/submitCarVerification.do?";// 提交车辆验证信息
	public static final String URL_GETUSERCOINLOG = URL_LOCAL + "/logged/user/getUserCoinLog.do?";

	public static final String URL_UPDATE_USER_INFO = URL_LOCAL + "/logged/user/updateUser.do?";// 更新会员资料
	public static final String URL_MODIFYPWD = URL_LOCAL + "/logged/user/modifyPwd.do?";// 修改登录密码

	public static final String URL_GET_RECHARGE_ORDER_ID = URL_LOCAL
			+ "/logged/recharge/addRechargeServiceOrder.do"; // 获取充值前的订单号
	public static final String URL_RECHARGE = URL_LOCAL + "/logged/recharge/rechargeTel.do"; // 充值话费

	public static final String URL_FEDDBACK = URL_LOCAL + "/feedback/addFeedback.do";// 反馈
	public static final String URL_GETMESSAGE = URL_LOCAL + "/logged/pushMsg/getMessages.do?";// 获取信息中心信息
	public static final String URL_UPDATEMSGSTATE = URL_LOCAL
			+ "/logged/pushMsg/updateMsgState.do?";// 删除（阅读）消息
	public static final String URL_GET_UNREAD_MSG_COUNT = URL_LOCAL
			+ "/logged/pushMsg/getNewMsgCount.do";// 获取新信息数量
	public static final String URL_UPDATE = URL_LOCAL + "/version/getLastVersion.do?type=ANDROID";// 更新
	// 获取拼车计划
	public static final String URL_GET_CARPOOL_PROGRAM = URL_LOCAL
			+ "/logged/carpoolProgram/get.do";
	// 车主暂停取消拼车计划
	public static final String URL_DRIVER_PAUSE_OR_CANCEL_PLAN = URL_LOCAL
			+ "/logged/carpoolProgram/edit.do";
	// 乘客暂停取消拼车计划
	public static final String URL_PASSENGER_PAUSE_OR_CANCEL_PLAN = URL_LOCAL
			+ "/logged/carpoolProgram/passengerEdit.do";
	// 汽车服务
	public static final String URL_GETCOMMODITYS = URL_CAR_SERVER
			+ "/api_commodity/getCommoditys.do";// 获取汽车服务列表
	public static final String URL_GETCOMMODITYSBYID = URL_CAR_SERVER
			+ "/api_commodity/getCommodityById.do?";// 获取商品详情
	public static final String URL_QCFW = URL_CAR_SERVER + "/index.jsp";

	// 精彩服务
	public static final String URL_JCHD = URL_LOCAL + "/activity.jsp";

	// 获取某人线路数量
	public static final String URL_TRAIL_LINE_NUMBER = URL_LOCAL + "/logged/info/getInfoCounts.do";

	// 拼车计划进入后的线下支付
	public static final String URL_CARPOOL_PAY = URL_LOCAL + "/logged/carpoolProgram/pay.do";
	/* －－－－－－－－－－－－－－－－－－－－－分享－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－ */
	public final static String SAPKDOWNLOAD = URL_LOCAL + "/share.jsp";// 软件分享

	public final static String URL_SHARE_ORDER = URL_LOCAL + "/share4order.jsp?";// 订单分享
	public final static String URL_SHARE_LINE = URL_LOCAL + "/share4info.jsp?";// 线路分享

	public final static String SHAREICO = URL_LOCAL + "/static/images/icon.png";// 分享图片

	public final static String GET_LOADING_PAGE = URL_LOCAL + "/loadPage/getLoadPage.do";// 获取loading页
	public final static String EDIT_COMMON_ADDRESS = URL_LOCAL + "/logged/user/editMyAddr.do";// 编辑常用地址
	public final static String GET_CURRENT_CASH = URL_LOCAL + "/logged/user/getUserGold.do";// 获取用户余额
	public final static String GET_CONSUMER_RECORDS = URL_LOCAL + "/logged/user/getGoldLogs.do";// 获取用户余额消费记录
	public final static String GET_CASH_ACCOUNT = URL_LOCAL + "/logged/user/getGoldBanks.do";// 获取提现账户
	public final static String ADD_DELETE_ACCOUNT = URL_LOCAL + "/logged/user/editGoldBank.do";// 获取提现账户
	public final static String APPLY_TO_CASH = URL_LOCAL + "/logged/user/withdrawCash.do";// 申请提现
	public final static String LOTTERY = URL_LOCAL + "/logged/coinLottery/lottery.do";// 拼车币抽奖
	public final static String GET_CIRCULATION = URL_LOCAL + "/logged/coinLottery/getBroadcast.do";// 获取抽奖的轮播信息
	public final static String GET_SHARE_PRISE = URL_LOCAL + "/logged/user/shareReward.do";// 获取获取分享奖励

	public final static String URL_PAY = URL_LOCAL + "/logged/infoOrder/pay.do";// 付款

	public final static int EXCHANGERATE = 10;// 人民币对拼车币汇率

	public static boolean isGetLocation = false;// 是否已经定位成功
	public static double TwoPoinstsDistances = 1000.0;// 上下班直线距离不能小于1000米

	// 支付宝操作状态
	public final static HashMap<String, String> sResultStatus = new HashMap<String, String>();
	static {
		sResultStatus.put("9000", "操作成功");
		sResultStatus.put("4000", "系统异常");
		sResultStatus.put("4001", "数据格式不正确");
		sResultStatus.put("4003", "该用户绑定的支付宝账户被冻结或不允许支付");
		sResultStatus.put("4004", "该用户已解除绑定");
		sResultStatus.put("4005", "绑定失败或没有绑定");
		sResultStatus.put("4006", "订单支付失败");
		sResultStatus.put("4010", "重新绑定账户");
		sResultStatus.put("6000", "支付服务正在进行升级操作");
		sResultStatus.put("6001", "取消支付操作");
		sResultStatus.put("7001", "网页支付失败");
	}

	private final static int SONE = 1;
	private final static int SZERO = 0;
	// plaza type
	// 上下班
	public final static int SWORK = SONE;
	// 长途
	public final static int SLONG = SZERO;
	// 车主
	public final static int SDRIVER = SZERO;
	// 乘客
	public final static int SPASSGER = SONE;

	// for onActivityResult
	// 广场选择城市
	public static final int SELECT_CITY_REQUEST_CODE = 100;
	public static final int SELECT_CITY_RESULT_CODE = 101;

	// 搜索界面onActivityResult
	public final static int SSEARCHWORK = 200;// 上下班
	public final static int SSEARCHLONG = 201;// 长途
	// 搜索界面返回

	public final static int S_SEARCH_REQUEST = 211;// 搜索地址
	public final static int S_SEARCH_START = 202;// 搜索返回起点
	public final static int S_SEARCH_END = 203;// 搜索返回终点
	// 个人中心

	// 选择地址返回
	public final static int S_CHOICE_ADRRR_RQUEST = 204;//
	public final static int S_CHOICE_ADRR_RESULT_START = 205;// 返回起点
	public final static int S_CHOICE_ADRR_RESULT_END = 206;// 返回起点

	// 选择途经点
	public final static int S_CHOICE_THROUTH_POINT = 401;
	// 选择常用地址
	public final static int REQUESTCODE_COMMON_ADDRESS = 501;

	// 点击搜索时的icon
	// 上下班时的icon
	public final static int SICON_WORK_REQUEST = 207;// 上下班请求
	public final static int SICON_WORK_START_RESULT = 208;// 上下班起点
	public final static int SICON_WORK_END_RESULT = 209;// 上下班起点
	// c长途时的icon
	public final static int SICON_LONG_REQUEST = 300;// 上下班请求
	public final static int SICON_LONG_START_RESULT = 301;// 上下班起点
	public final static int SICON_LONG_END_RESULT = 302;// 上下班起点

	public final static int SPAGESIZE = 10;

	public final static String SGETUSERLOCATION = "getuserlocation";

	public final static String S_ICON = "icon";
	public final static String S_START = "start";
	public final static String S_END = "end";
	public final static String S_ADDR = "addr";
	public final static String S_THROUTH_POINT = "THROUTH_POINT";

	public final static String SLON = "lon";
	public final static String SLAT = "lat";
	public final static String CITY_CODE = "citycode";
	public final static int I_START = SZERO;
	public final static int I_END = SONE;
	public final static int I_THROUTH_POINT = 2;// 选择途经点

	public static int s_PinCheBi = -1;
	// 选择城市货搜索地址
	public final static String PAY_ONLINE = "ONLINE";// 线上支付
	public final static String PAY_OFFLINE = "OFFLINE";// 线下支付
	public final static String CITY = "city";// 城市

	public static String deviceKey = "";
	public static String sessionId = "";
	public static String signCiphertext = "";

	/* 区别 个人主页 的 精彩活动 1 汽车中心2 */
	public final static String JCHDOrQCFW = "JCHD OR QCFW";
	public final static String JCHD = "JCHD";
	public final static String QCFW = "QCFW";
	public final static String IDENTITY = "Identity";// 身份

	public static boolean logout_swtch_to_home = false;// 退出登录后回到首页切换到首页图标
	public static boolean reload_plan = false;
	public static boolean is_refresh_orders = false;// 是否刷新订单
	public static boolean is_comfirm_dialog_show = false;// 确认对话框是否在显示

	public static int home_current_fragment = FragmentTabAdapter.LEFT_FRAGMENT;// 首页当前是展示的那个fragment

	public static final String BOOK_ID = "book_id";
	public static final String SHOW_CALENDAR = "show_calendar";

	public static final String START_LAT = "start_lat";// 起点纬度
	public static final String START_LON = "start_lon";// 起点经度

	public static final String END_LAT = "end_lat";// 终点纬度
	public static final String END_LON = "end_lon";// 终点经度

	public static final String CACHE = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/pinchebang/";
	// 首页点击拼车计划进入拼车详情所带参数
	public static final String CARPOOLPROGRAMPASSENGERID = "carpoolProgramPassengerId";
	public static final String IS_DRIVER = "is_driver";

	public static final String DRIVER = "Driver";
	public static final String PASSENGER = "Passenger";

	public static final String ARGEMENT = "file:///android_asset/agreement.html";// 使用条款路径
	public static final String GET_PCB_WAYS = "file:///android_asset/coinrules.html";// 如何获取拼车币
}
