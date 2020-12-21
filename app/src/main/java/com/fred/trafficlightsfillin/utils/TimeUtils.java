package com.fred.trafficlightsfillin.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by fred
 */
public class TimeUtils {
    /**
     * 毫秒数转化标准时间
     *
     * @param time
     * @return
     */
    public static String time(String time) {
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }
    public static String time2(String time) {
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }
    public static String time3(String time) {
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        return sdf.format(d);
    }
    public static String time4(String time) {
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(d);
    }
    public static String time5(String time) {
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        return sdf.format(d);
    }

    public static String time6(String time) {
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(d);
    }

    public static String time7(String time) {
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日    HH:mm");
        return sdf.format(d);
    }


    public static String time8(String inputTime){

        if(TextUtils.isEmpty(inputTime)){
            return "";
        }
        SimpleDateFormat format =  new SimpleDateFormat("yyyy年MM月dd日");
        Date date = null;
        try {
            date = format.parse(inputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //日期转时间戳（毫秒）
        long time=date.getTime();
        return String.valueOf(time);
    }

    public static String time11(String inputTime){
        SimpleDateFormat format =  new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(inputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //日期转时间戳（毫秒）
        long time=date.getTime();
        return String.valueOf(time);
    }

    public static String time9(String time) {
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(d);
    }

    public static String time10(String time) {
        if(TextUtils.isEmpty(time)){
            return "";
        }
        Date d = new Date();
        d.setTime(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return sdf.format(d);
    }
    /**
     * 获取日期是周几
     * @param strDate
     * @return
     */
    public static String strToDate(String strDate) {
        String s = time2(strDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(date);
        return week;
    }

    /**
     *毫秒数转化为时分秒
     * @param time 毫秒
     * @return
     */
    public static String formatDuring(long time){
        String hour=null;
        String minute=null;
        String second = null;
        if(time<60000){
            long s= (time % 60000) / 1000;
            if(s<10){
                second="00:00:0"+s;
            }else{
                second="00:00:"+s;
            }
            return second;
        }else if((time>=60000)&&(time<3600000)){
            long s= (time % 60000) / 1000;
            long m=(time % 3600000)/60000;
            if(m<10){
                if(s<10){
                    return  "00:0"+m+":0"+s;
                }else{
                    return  "00:0"+m+":"+s;
                }
            }else{
                if(s<10){
                    return  "00:"+m+":0"+s;
                }else{
                    return  "00:"+m+":"+s;
                }
            }
        }else {
            long s= (time % 60000) / 1000;
            long m=(time % 3600000)/60000;
            long h=time / 3600000;
            if(h<10){
                hour="0"+h;
            }else{
                hour=String.valueOf(h);
            }
            if(m<10){
                minute="0"+m;
            }else{
                minute=String.valueOf(m);
            }
            if(s<10){
                second="0"+s;
            }else{
                second=String.valueOf(s);
            }
            return hour+":"+minute+":"+second;
        }
    }

    /**
     * 保留两位小数
     *
     * @param str
     * @return
     */
    public static String saveAsTwo(String str) {
        Double f = Double.parseDouble(str);
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format((f / 100));
    }
}
