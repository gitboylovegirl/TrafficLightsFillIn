package com.fred.trafficlightsfillin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidatorUtil{
    /**
     * 验证密码
     *
     * 密码必须为6-12位的数字和字母的组合
     *
     * @param pwd
     * @return
     */
    public static boolean checkPwd(String pwd){
        Pattern pattern = Pattern.compile("^(^$)|^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$");
        Matcher m = pattern.matcher(pwd);
        return m.matches();
    }
}
