package com.fred.trafficlightsfillin.base;


/**
 *
 */

public class RequestApi {
    /**
     * 正式环境
     */
    //public static String BASE_OFFICIAL_URL = "https://api.imifanlive.com";
    public static String BASE_OFFICIAL_URL = "http://peishi.slinks.cn:28088";

    public static String BASE_OFFICIAL_SHEQU_URL = "http://api.shequ.zhangyu.tv";


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
                BASE_URL = BASE_OFFICIAL_URL;
                break;

            case ENVIORENMENT_SHEQU:
                BASE_URL = BASE_OFFICIAL_SHEQU_URL;
                break;

            default:
                break;

        }

        return BASE_URL + restApi;
    }
    /**
     * 设置环境模式
     *
     * @param mode
     */
    public static final String GET_CHANNEL_INFO = "/mifan-channel/v1/channels/info";

    /**
     * 登录
     */
    public static final String LOGIN="/sys/user/login";

    //public static f
}
