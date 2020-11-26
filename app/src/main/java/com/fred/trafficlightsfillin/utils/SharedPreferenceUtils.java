package com.fred.trafficlightsfillin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.fred.trafficlightsfillin.SoftApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 轻量级存储
 *
 * @author fred
 */
public class SharedPreferenceUtils {
    private static Map<String, SharedPreferenceUtils> sharedPreMap = new HashMap<>();

    public static final String FILE_NAME = "mf_share_data";
    private SharedPreferences sharedPreferences;

    public static SharedPreferenceUtils getInstance() {
        return getInstance(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static  SharedPreferenceUtils getInstance(String spName) {
        return getInstance(spName, Context.MODE_PRIVATE);
    }

    public static  SharedPreferenceUtils getInstance(String spName, int spMode) {
         SharedPreferenceUtils sharedPrefHelper = sharedPreMap.get(spName);
        if (sharedPrefHelper == null) {
            sharedPrefHelper = new  SharedPreferenceUtils(spName, spMode);
            sharedPreMap.put(spName, sharedPrefHelper);
        }
        return sharedPrefHelper;
    }

    private SharedPreferenceUtils(String spName, int spMode) {
        if (TextUtils.isEmpty(spName)) {
            spName = FILE_NAME;
        }
        sharedPreferences = SoftApplication.mContext.getSharedPreferences(spName, spMode);
    }

    public SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    /**
     * 异步 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param fileName
     * @param key
     * @param object
     */
    public static void put(Context context, String fileName, String key, Object object) {
        put(context, false, fileName, key, object);
    }

    /**
     * 异步 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {
        put(context, false, FILE_NAME, key, object);
    }

    /**
     * 同步 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void putSyn(Context context, String key, Object object) {
        put(context, true, FILE_NAME, key, object);
    }

    /**
     * 可控制 同步 或者 异步保存
     *
     * @param context
     * @param isSyn    是否同步操作 （保存操作 有同步 和 异步）
     * @param fileName
     * @param key
     * @param object
     */
    public static void put(Context context, boolean isSyn, String fileName, String key, Object object) {
        if (object == null || context == null) {
            return;
        }

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        if (isSyn) {
            editor.commit();
        } else {
            SharedPreferencesCompat.apply(editor);
        }
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject) {
        return get(context, FILE_NAME, key, defaultObject);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String fileName, String key, Object defaultObject) {
        if (context == null || TextUtils.isEmpty(fileName)) return null;
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    public static void remove() {
        SharedPreferences sp = SoftApplication.mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("token");
        SharedPreferencesCompat.apply(editor);
    }
    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param fileName
     * @param key
     */
    public static void remove(Context context, String fileName, String key) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除指定文件数据数据
     *
     * @param context
     */
    public static void clear(Context context, String fileName) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        if (context == null) return false;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        if (context == null) return null;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

    /**
     * 调试状态
     *
     * @param debug
     */
    public void setDebugStatus(boolean debug) {
        sharedPreferences.edit().putBoolean("DebugStatus", debug).apply();
    }

    public boolean getDebugStatus() {
        return sharedPreferences.getBoolean("DebugStatus", false);
    }

    /**
     * log输出状态
     *
     * @param status
     */
    public void setLogPrintStatus(boolean status) {
        sharedPreferences.edit().putBoolean("LogPrintStatus", status).apply();
    }

    public boolean getLogPrintStatus() {
        return sharedPreferences.getBoolean("LogPrintStatus", false);
    }

    /**
     * 保存本地设置状态
     *
     * @param status
     */
    public void setLocalSettingsStatus(boolean status) {
        sharedPreferences.edit().putBoolean("LocalSettingsStatus", status).apply();
    }

    public boolean getLocalSettingsStatus() {
        return sharedPreferences.getBoolean("LocalSettingsStatus", false);
    }

    /**
     * 测试环境状态
     *
     * @param status
     */
    public void setTextEnvironmentStatus(boolean status) {
        sharedPreferences.edit().putBoolean("TextEnvironmentStatus", status).apply();
    }

    public boolean getTextEnvironmentStatus() {
        return sharedPreferences.getBoolean("TextEnvironmentStatus", false);
    }

    /**
     * 预发布环境状态
     *
     * @param status
     */
    public void setPrepareEnvironmentStatus(boolean status) {
        sharedPreferences.edit().putBoolean("PrepareEnvironmentStatus", status).apply();
    }

    public boolean getPrepareEnvironmentStatus() {
        return sharedPreferences.getBoolean("PrepareEnvironmentStatus", false);
    }

    /**
     * 首次开启调试彩蛋状态
     *
     * @param status
     */
    public void setFirstOpenSettingsStatus(boolean status) {
        sharedPreferences.edit().putBoolean("FirstOpenSettingsStatus", status).apply();
    }

    public boolean getFirstOpenSettingsStatus() {
        return sharedPreferences.getBoolean("FirstOpenSettingsStatus", false);
    }

    /**
     * 本地手动输入的baseUrl
     *
     * @param baseUrl
     */
    public void setRequestBaseUrl(String baseUrl) {
        sharedPreferences.edit().putString("RequestBaseUrl", baseUrl).apply();
    }

    public String getRequestBaseUrl() {
        return sharedPreferences.getString("RequestBaseUrl", "");
    }


    /**
     * uuid
     *
     * @param uuid
     */
    public void setUUID(String uuid) {
        sharedPreferences.edit().putString("MF_UUID", uuid).apply();
    }

    public String getUUID() {
        return sharedPreferences.getString("MF_UUID", "");
    }


    /**
     * 设置ua
     *
     * @param ua
     */
    public void setUser_Agent(String ua) {
        sharedPreferences.edit().putString("MF_USER_AGENT", ua).apply();
    }

    public String getUser_Agent() {
        return sharedPreferences.getString("MF_USER_AGENT", "");
    }


    public String getBroadcastTitle() {
        return sharedPreferences.getString("BroadcastTitle", "");
    }

    /**
     * 存储个人信息
     *
     * @param userInfo
     */
//    public void setUserInfo(UserInfoDataChannel userInfo) {
//        sharedPreferences.edit().putString("migrateConfirm", String.valueOf(userInfo.migrateConfirm)).apply();
//        setHeadUrl(userInfo.avatarUrl);
//        setCnname(userInfo.uname);
//        setToken(userInfo.token);
//        if (userInfo.uid != null){
//            setUserUid(userInfo.uid);
//        }
//    }

    /**
     * 获取token
     */
    public String getToken() {
        return sharedPreferences.getString("token", "");
    }

    public void setToken(String str) {
        sharedPreferences.edit().putString("token", str).apply();
    }

    /**
     * refreshToken
     * @return
     */
    public String getrefreshToken(){return sharedPreferences.getString("refreshToken", "");}

    public void setrefreshToken(String str) {
        sharedPreferences.edit().putString("refreshToken", str).apply();
    }

    /**
     * 获取uid
     */
    public String getUserUid() {
        return sharedPreferences.getString("uid", "");
    }

    public void setUserUid(String str) {
        sharedPreferences.edit().putString("uid", str).apply();
    }

    /**
     * 获取昵称
     */
    public String getCnname() {
        return sharedPreferences.getString("uname", "");
    }

    public void setCnname(String str) {
        sharedPreferences.edit().putString("uname", str).apply();
    }

    /**
     * 头像地址
     */
    public void setHeadUrl(String url) {
        sharedPreferences.edit().putString("head", url).apply();
    }

    public String getHeadUrl() {
        return sharedPreferences.getString("head", "");
    }


    /**
     * 存隐私协议状态
     *
     * @param state
     */
    public void setPrivacyProtocolState(String state) {
        sharedPreferences.edit().putString("PrivacyProtocol", state).apply();
    }

    public String getPrivacyProtocolState() {
        return sharedPreferences.getString("PrivacyProtocol", "");
    }


    /**
     * 获取今日
     *
     * @param today
     */
    public void setTodayFormat(String today) {
        sharedPreferences.edit().putString("TodayFormat", today).apply();
    }

    public String getTodayFormat() {
        return sharedPreferences.getString("TodayFormat", "");
    }


    /**
     *  一天一次时间记录
     * @param today
     */
    public void setTeenagerTodayOnce(String today) {
        sharedPreferences.edit().putString("TeenagerTodayOnce", today).apply();
    }

    public String getTeenagerTodayOnce() {
        return sharedPreferences.getString("TeenagerTodayOnce", "");
    }


    public void setIsFirstLogin(String flag){
        sharedPreferences.edit().putString("isfirstLogin", flag).apply();
    }

    public String getIsFirstLogin(){
        return sharedPreferences.getString("isfirstLogin", "");
    }

}
