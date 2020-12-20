package com.fred.trafficlightsfillin.record.bean;

public class TrafficligthVo {

    public Long id;

    public String no;//编号
    public String roadPlace;// 路口
    public String area; //所属区域
    public String location;//地址位置
    public Double lat;//纬度
    public Double lon;//经度
    public String roadPlaceType;//路口类型
    public String roadPlaceLevel;//路口等级
    public String precincts;//管界
    public String modelNo;//信号机编号
    public String modelType;//信号机类型

    public String peishiRoadPlace;//配时路口
    public String peishiNo;//配时编号
    public String ip;//ip地址

    public String eastRoad;//东相交道路
    public Integer eastIn;//东进口
    public Integer eastOut;//东出口
    public String westRoad;//西相交道路
    public Integer westIn;//西进口
    public Integer westOut;//西出口
    public String southRoad;//南相交道路
    public Integer southIn;//南进口
    public Integer southOut;//南出口
    public String northRoad;//北相交道路
    public Integer northIn;//北进口
    public Integer northOut;//北出口

    public String manageCompany;//所属单位
    public String manageTeam;//所属支队
    public String createDate;//安装时间
    public String remark;//备注
    public Integer state;//红绿灯状态 0异常 1正常 2停用

    public String company;//调试单位
    /**
     *最新调试信息
     */
    public String person;//调试人
    public Long date;//调试时间

    public Long lastSelectTeamId;//路口派单最后选择的班组
    public Long lastSelectEngineerId;//路口派单最后选择工程师


}
