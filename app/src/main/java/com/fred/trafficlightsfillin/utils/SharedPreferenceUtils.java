package com.fred.trafficlightsfillin.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.fred.trafficlightsfillin.SoftApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public String getAge(){return sharedPreferences.getString("age", "");}

    public void setAge(String str) {
        sharedPreferences.edit().putString("age", str).apply();
    }

    public String getCarNumber(){return sharedPreferences.getString("carNumber", "");}

    public void setCarNumber(String str) {
        sharedPreferences.edit().putString("carNumber", str).apply();
    }

    public String getId(){return sharedPreferences.getString("id", "");}

    public void setId(String str) {
        sharedPreferences.edit().putString("id", str).apply();
    }

    public String getName(){return sharedPreferences.getString("name", "");}

    public void setName(String str) {
        sharedPreferences.edit().putString("name", str).apply();
    }

    public String getRemark(){return sharedPreferences.getString("remark", "");}

    public void setRemark(String str) {
        sharedPreferences.edit().putString("remark", str).apply();
    }

    public String getSex(){return sharedPreferences.getString("sex", "");}

    public void setSex(String str) {
        sharedPreferences.edit().putString("sex", str).apply();
    }

    public String getTeamId(){return sharedPreferences.getString("teamId", "");}

    public void setTeamId(String str) {
        sharedPreferences.edit().putString("teamId", str).apply();
    }

    public String getTeamName(){return sharedPreferences.getString("teamName", "");}

    public void setTeamName(String str) {
        sharedPreferences.edit().putString("teamName", str).apply();
    }

    public String getPhone(){return sharedPreferences.getString("phone", "");}

    public void setPhone(String str) {
        sharedPreferences.edit().putString("phone", str).apply();
    }

    public long getCurrentTime(){return sharedPreferences.getLong("time", 0);}

    public void setCurrentTime(long time) {
        sharedPreferences.edit().putLong("time", time).apply();
    }

    public long getSeTime(){return sharedPreferences.getLong("setTime", 0);}

    public void setSetTime(long time) {
        sharedPreferences.edit().putLong("setTime", time).apply();
    }

    public boolean getIsTime(){return sharedPreferences.getBoolean("isTime", false);}

    public void setIsTime(boolean time) {
        sharedPreferences.edit().putBoolean("isTime", time).apply();
    }
    /**
     * 工作状态
     * @return
     */
    public String getFlag(){return sharedPreferences.getString("flag", "0");}

    public void setFlag(String str) {
        sharedPreferences.edit().putString("flag", str).apply();
    }


    public static <T> List<Field> getPublicFields(Class<?> clazz){
        if (clazz.equals(Object.class)) {
            return null;
        }
        //用来存储clazz中用public修饰的属性的list
        List<Field> list = new ArrayList<Field>();
        //获得clazz中所有用public修饰的属性
        Field[] fields = clazz.getFields();
        //将fields加入到list中
        for(int i=0 ; i<fields.length ; i++){
            list.add(fields[i]);
        }
        return list;
    }

    public static void putObjectToShare(String shareName , Object obj){
        //获得SharedPreferences实例
        SharedPreferences sharedPreferences = SoftApplication.mContext.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        //获得Editor
        SharedPreferences.Editor edit = sharedPreferences.edit();
        //存储数据之前先将之前的旧数据清掉
        edit.clear();
        //调用commit提交数据(这里为了清掉数据)
        edit.commit();

        List<Field> publicFields = getPublicFields(obj.getClass());
        for(Field f : publicFields){
            String name = f.getName();
            try {
                //获得当前属性的类型和值
                //类型的话如果是基本类型，会自动装箱
                Object type = f.get(obj);
                //判断各种类型，调用各种类型的put方法将数据存储进去
                if (type instanceof String) {
                    edit.putString(name, (String) type);
                }else if (type instanceof Integer) {
                    edit.putInt(name, (Integer) type);
                }else if (type instanceof Float) {
                    edit.putFloat(name, (Float) type);
                }else if (type instanceof Long) {
                    edit.putLong(name, (Long) type);
                }else if (type instanceof Boolean) {
                    edit.putBoolean(name, (Boolean) type);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //调用commit，提交数据
            edit.commit();
        }
    }

    public <T> T getObjectFromShare(String shareName , Class<T> clazz){
        //获得SharedPreferences实例
        SharedPreferences sharedPreferences = SoftApplication.mContext.getSharedPreferences(shareName, Activity.MODE_PRIVATE);
        //T是一个泛型，根据clazz不同而不同
        T t = null;
        try {
            //获得sharedPreferences中所有的数据，数据为键值对保存在map中
            Map<String,?> map = sharedPreferences.getAll();
            //调用getPublicFields方法得到clazz中所有的公有属性
            List<Field> publicFields = getPublicFields(clazz);
            //如果两者都不为空的话
            if (map.size()>0 && publicFields.size()>0) {
                //将T实例化出来
                t = clazz.newInstance();
                //遍历map中所有的键值对
                for(Map.Entry<String,?> entry : map.entrySet()){
                    //map中的键
                    String key = entry.getKey();
                    //map中的值
                    Object value = entry.getValue();
                    //遍历clazz中的所有公有属性
                    for(Field field : publicFields){
                        //获得属性名
                        String name = field.getName();
                        //如果属性名与键相同
                        if (name.equalsIgnoreCase(key)) {
                            //相当于给对象T中的属性field赋值，值为value
                            field.set(t, value);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //整个遍历结束后，我们的对象中的属性也都有值了
        return t;
    }


    /**
     * 存放实体类以及任意类型
     * @param
     * @param key
     * @param obj
     */
    public static void putBean(String key, Object obj) {
        if (obj instanceof Serializable) {// obj必须实现Serializable接口，否则会出问题
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(),0));
                SharedPreferences sharedPreferences = SoftApplication.mContext.getSharedPreferences(key, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, string64).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("fred","error:"+"the obj must implement Serializble");
            throw new IllegalArgumentException("the obj must implement Serializble");
        }

    }

    public static Object getBean(String key) {
        Object obj = null;
        try {
            SharedPreferences sharedPreferences = SoftApplication.mContext.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
            String base64 = sharedPreferences.getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;}
}
