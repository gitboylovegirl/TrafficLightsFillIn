package com.fred.trafficlightsfillin.base;


/**
 *
 */

public class RequestApi {
    /**
     * 正式环境
     */
    public static String BASE_OFFICIAL_URL = "https://peishi.slinks.cn:28088";


    public static int ENVIORENMENT_MODE = 0;

    /**
     * 环境类型
     */
    public static final int ENVIORENMENT_ZY = 1;

    public static final int ENVIORENMENT_SHEQU = 2;


    public static String BASE_URL = BASE_OFFICIAL_URL;


    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static String getUrl(String restApi) {
        return BASE_OFFICIAL_URL + restApi;
    }


    public static String getUrl(String restApi, int type) {
        switch (type) {
            case ENVIORENMENT_ZY:
            case ENVIORENMENT_SHEQU:
                BASE_URL = BASE_OFFICIAL_URL;
                break;

            default:
                break;

        }

        return BASE_URL + restApi;
    }

    /**
     * 登录 post
     */
    public static final String LOGIN = "/service/engineer/login";

    /**
     * 修改密码  put
     */
    public static final String CHANGE_PASSWORD = "/service/engineer/pwd";

    /**
     * 发送验证码 get
     */
    public static final String SEND_SMS_CODE = "/service/engineer/send/code/";

    /**
     * 找回密码 post
     */
    public static final String FEED_PASSWORD = "/service/engineer/check/code";

    /**
     * 刷新token get
     */
    public static final String REFRESH_TOKEN = "/service/engineer/token";

    /**
     * 上报地理信息 post
     */
    public static final String LOCATIO = "/service/location";
    /**
     * 获取最新版本号 get
     */
    public static final String NEW_VERSION = "/service/app/ver/newest";
    /**
     * 获取任务列表 post
     */
    public static final String TASK_PAGE = "/service/task/page";
    /**
     * 获取红绿灯列表 post
     */
    public static final String TRAFFICLIGHT_PAGE = "/service/trafficlight/page";
    /**
     * 任务详情
     */
    public static final String TASK_DETAILS = "/service/task";
    /**
     * 查询红绿灯路口 get
     */
    public static final String ROAD_PLACE = "/service/trafficlight/roadplace";
    /**
     * 班组  post
     */
    public static final String TEAM_LIST = "/service/team/list";
    /**
     * 任务来源  post
     */
    public static final String TASK_SOURCE = "/service/tasksource/list";
    /**
     * 路口类型  get
     */
    public static final String ROAD_TYPE = "/service/trafficlight/roadPlaceType";
    /**
     * 修改任务状态
     */
    public static final String TASK_STATE = "/service/task/state";
    /**
     * 查询配时详情
     */
    public static final String TRAFFICLIGH_PEISHI = "/service/trafficlight/peishi";
    /**
     * 获取次序表接口 post
     */
    public static final String STAGE_LIST = "/service/stage/list";
    /**
     * 获取红路灯详情 get
     */
    public static final String TRAFFICLIGH_DETAILS = "/service/trafficlight";
    /**
     * 查询图片 get
     */
    public static final String TRAFFICLIGH_IMAGES = "/service/trafficlight/images";
    /**
     * 下载红路灯图片 get
     */
    public static final String DOWN_IMG = "/service/trafficlight/down/img";

    /**
     * 上传现场图片  post
     */
    public static final String UP_IMG = "/service/trafficlight/up/img";
    /**
     * 上传配时结  post
     */
    public static final String TASK_RESULT = "/service/task/result";

    /**
     * 上报错误 post
     */
    public static final String FEED_ADD = "/service/add";


    /**
     * 获取最新任务
     */
    public static final String NEW_LIST="/service/task/newlist";


    /**
     *
     * 工程师详情  get
     */
    public static final String ENGINEER_INFO="/service/engineer";

    /**
     * 删除图片  DELETE
     */
    public static final String DEL_IMG="/service/trafficlight/del/img";
}
