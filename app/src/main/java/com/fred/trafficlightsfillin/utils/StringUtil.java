package com.fred.trafficlightsfillin.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 类名称 ：StringUtil
 * 类描述 ：String判断处理工具类
 * 创建时间：下午3:02:09
 */
public class StringUtil {

    /**
     * 判断字符串是否为null或者空字符串
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        boolean result = false;
        if (null == str || "".equals(str.trim()) || " ".equals(str.trim()) || "  ".equals(str.trim())) {
            result = true;
        }
        return result;
    }

    /**
     * 如果小于两位数，添加0后生成string
     *
     * @param ballNum
     * @return
     */
    public static String addZreoIfLessThanTen(long ballNum) {

        String string = "";
        if (ballNum < 10) {
            string = "0" + ballNum;
        } else {
            string = String.valueOf(ballNum);
        }
        return string;
    }

    /**
     * @param string
     * @return
     */
    public static boolean isNotNull(String string) {
        return null != string && !"".equals(string.trim()) && !" ".equals(string.trim()) && !"  ".equals(string.trim());
    }

    /**
     * 去掉一个字符串中的所有的单个空格" "
     *
     * @param string
     */
    public static String replaceSpaceCharacter(String string) {
        if (null == string || "".equals(string)) {
            return "";
        }
        return string.replaceAll(" ", "");
    }

    /**
     * 获取小数位为5位的经纬度
     *
     * @param value
     * @return
     */
    public static String getStringLongitudeOrLatitude(double value) {
        if (value == 0) {
            return "0.00000";
        }
        try {
            return new DecimalFormat("#0.00000").format(new BigDecimal(value).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "0.00000";
        }
    }


    /**
     * 保留小数点后两位
     *
     * @param value
     * @return
     */
    public static String getStringFormat(double value) {
        if (value == 0) {
            return "0.00";
        }
        try {
            return new DecimalFormat("#0.00").format(keepDecimal(2, value));
        } catch (Exception e) {
            e.printStackTrace();
            return "0.00";
        }
    }

    public static double keepDecimal(int newScale, double num) {
        num = new BigDecimal(num).setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }


    /**
     * 保留小数点后一位
     *
     * @param value
     * @return
     */
    public static String getOneStringFormat(double value) {
        if (value == 0) {
            return "0.0";
        }
        try {
            return new DecimalFormat("#0.0").format(new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    /**
     * 保留整数
     *
     * @param value
     * @return
     */
    public static String getNumFormat(double value) {
        if (value == 0) {
            return "0";
        }
        try {
            return new DecimalFormat("#0").format(new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * 获取去掉空格,+86的电话号码
     *
     * @param phone
     * @return
     */
    public static String getPhone(String phone) {
        phone = phone.trim().replace("-", "");
        if (phone.startsWith("+86")) {
            phone = phone.substring(3, phone.length());
        }
        if (phone.startsWith("86")) {
            phone = phone.substring(2, phone.length());
        }
        phone = phone.replace(" ", "");
        return phone;
    }

    /**
     * 替换字符
     *
     * @param str   字符串
     * @param begin 开始
     * @param end   结束
     * @return
     */
    public static String getHintString(String str, int begin, int end) {
        if (StringUtil.isNullOrEmpty(str)) {
            return "";
        }
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.delete(0, strBuffer.length());
        strBuffer.append(str, 0, begin);
        for (int i = 0; i < end - begin; i++) {
            strBuffer.append("*");
        }
        strBuffer.append(str, end, str.length());
        return strBuffer.toString();
    }

    /**
     * 判断是否存在某个字符
     *
     * @param str      字符串
     * @param flag_str 开始 是否存在字符
     * @return
     */
    public static Boolean getExistString(String str, String flag_str) {
        boolean flag = false;
        if (StringUtil.isNullOrEmpty(str)) {
            return flag;
        }
        if (str.contains(flag_str)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 判断是否相同
     *
     * @param phone1
     * @param phone2
     * @return
     */

    public static boolean checkPhoneNumbersSame(String phone1, String phone2) {
        return TextUtils.equals(phone1, phone2);
    }

    /**
     * 判断数字第一次出现的位置
     */
    public static int getPositionsNumbersStr(String str) {
        if (StringUtil.isNullOrEmpty(str)) {
            return 0;
        }
        Pattern p = Pattern.compile("\\d+");
        Matcher matcher = p.matcher(str);
        matcher.find();
        return matcher.start();
    }


    /**
     * 数字加粗显示
     *
     * @param str   内容
     * @param size  字体大小
     * @param start 开始位置
     * @param end   结束位置
     * @return
     */
    public static SpannableString getSpannableBoldString(String str, int size, int start, int end) {
        SpannableString spannable_str = new SpannableString(str);
        try {
            spannable_str.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable_str.setSpan(new AbsoluteSizeSpan(size, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannable_str;
    }

    /**
     * 数字加粗显示
     *
     * @param str   内容
     * @param size  字体大小
     * @param start 开始位置
     * @param end   结束位置
     * @return
     */
    public static SpannableString getSpannableColorString(String str, int color, int size, int start, int end) {
        SpannableString spannable_str = new SpannableString(str);
        try {
            spannable_str.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable_str.setSpan(new AbsoluteSizeSpan(size, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannable_str;
    }

    /**
     * 数字中间加星号
     *
     * @param str
     * @param tv
     */
    public static void trunNumberToHide(TextView tv, String str, int loa1, int loa2) {
        if (!TextUtils.isEmpty(str)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (i >= loa1 && i <= loa2) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            dealNull(tv, sb.toString());
        }
    }

    public static String trunNumberToHide(String str, int loa1, int loa2) {
        if (!TextUtils.isEmpty(str)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (i > loa1 && i < loa2) {
                    sb.append('*');
                } else if (i == loa1){
                    sb.append(" *");
                } else if (i == loa2){
                    sb.append("* ");
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 设置字体大小
     *
     * @param str
     * @param size
     * @return
     */
    public static CharSequence setStringSize(CharSequence str, int size) {
        SpannableString ss = new SpannableString(str);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size, true);
        ss.setSpan(ass, 0, ss.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new SpannedString(ss);
    }

    /**
     * 详细信息没有填写处理
     *
     * @param tv
     * @param str
     */
    public static void dealNull(TextView tv, String str) {
        if (StringUtil.isNullOrEmpty(str) || str.contains("未填")) {
            tv.setText("");
        } else {
            tv.setText(str);
        }
    }
    /**
     * 检查是否改变数据:含＊数据未改变（含＊点击数据清空，未清空说明未改变）
     *
     * @param tv
     * @param oldstr
     * @return
     */
    public static String checkEditeTextHasHide(TextView tv, String oldstr) {
        if (tv.getText().toString().trim().contains("*")) {
            return oldstr;
        } else {
            return tv.getText().toString().trim();
        }
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(long s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToTime(long time){
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time > 0) {
            hour = time / 60 / 60 / 1000;
            minute = time / 60 / 1000 % 60;
            second = time / 1000 % 60;
        }
        return singleToDouble(hour)+":"+singleToDouble(minute)+":"+singleToDouble(second);
    }

    public static String singleToDouble(long time) {
        if (time < 10) {
            return "0"+time;
        }
        return time+"";
    }

    /**
     * 年月日
     * @param s
     * @return
     */
    public static String stampToDateYy(String s){
        if (isNullOrEmpty(s)) {
            return "";
        }
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 今日
     * @param s
     * @return
     */
    public static String stampToDateToday(String s){
        if (isNullOrEmpty(s)) {
            return "";
        }
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);

        long lt2 = System.currentTimeMillis();
        Date date2 = new Date(lt2);
        String res2 = simpleDateFormat.format(date2);
        String resMMdd = res.substring(0,5);
        String resMMdd2 = res2.substring(0,5);
        if (TextUtils.equals(resMMdd,resMMdd2)) {
            res = "今日 " +res.substring(5,res.length());
        }
        return res;
    }

    /**
     * 转为两位小数
     * @param d
     * @return
     */
    public static String strToTwoNum(String d){
        if (isNullOrEmpty(d)) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String s = df.format(Double.valueOf(d));
        return s;
    }

    /**
     * 格式化数字为千分位显示；
     * @param text 要格式化的数字；
     * @return
     */
    public static String fmtMicrometer(String text) {
        DecimalFormat df = null;
        if(text.indexOf(".") > 0) {
            if(text.length() - text.indexOf(".")-1 == 0) {
                df = new DecimalFormat("###,##0.");
            }else if(text.length() - text.indexOf(".")-1 == 1) {
                df = new DecimalFormat("###,##0.0");
            }else {
                df = new DecimalFormat("###,##0.00");
            }
        }else {
            df = new DecimalFormat("###,##0");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }

    /**
     * Description:MD5工具生成token
     * @param value
     * @return
     */
    public static String getMD5Value(String value){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5ValueByteArray = messageDigest.digest(value.getBytes());
            BigInteger bigInteger = new BigInteger(1 , md5ValueByteArray);
            return bigInteger.toString(16).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 生成签名
     * @param map
     * @return
     */
    public static String getSignToken(Map<String, String> map) {
        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).compareTo(o2.getKey());
                }
            });
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }
            }
            sb.append("key=ANDROID");
            result = sb.toString();
            //进行MD5加密
            result = getMD5Value(result);
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public static int valueOfInt(String str){
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double valueOfDouble(String str){
        try {
            return Double.valueOf(str);
        } catch (Exception e) {
            return 0.00;
        }
    }

    public static String deleteString(String str, char delChar){
        String delStr = "";
        final String strTable = "|^$*+?.(){}\\";
        String tmpRegex = "[";
        for (int i = 0; i < strTable.length(); i++) {
            if (strTable.charAt(i) == delChar) {
                tmpRegex += "//";
                break;
            }
        }
        tmpRegex += delChar + "]";
        delStr = str.replaceAll(tmpRegex, "");
        return delStr;
    }

    public static int toTwotwo(){
        int min = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
            Date date = new Date(System.currentTimeMillis());
            String res = simpleDateFormat.format(date);
            String hour = res.substring(6,8);
            String minute = res.substring(9,11);
            int h = Integer.valueOf(hour);
            int m = Integer.valueOf(minute);
            if (h > 22) {
                return -1;
            }

            min = (22 - h) * 60 + m;
        } catch (Exception e) {

        }

        return min;
    }

    /**
     * 6点到 22点之间
     * @return
     */
    public static boolean isSixToTwentyTwo(){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
            Date date = new Date(System.currentTimeMillis());
            String res = simpleDateFormat.format(date);
            String hour = res.substring(6,8);
            int h = Integer.valueOf(hour);
            if (h >= 6 && h < 22) {
                return true;
            }
        } catch (Exception e) {

        }

        return false;
    }

}
