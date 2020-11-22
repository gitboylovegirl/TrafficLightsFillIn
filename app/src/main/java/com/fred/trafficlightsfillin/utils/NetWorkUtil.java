package com.fred.trafficlightsfillin.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


/**
 * 类名称 ：NetUtil
 * 类描述 ：网络相关的工具类
 * 创建人 ：陶孟
 */
public class NetWorkUtil {

    /**
     * 判断网络连接是否已开 true 已打开 false 未打开
     */
    public static boolean isNetDeviceAvailable(Context context) {
        boolean bisConnFlag = false;
        try {
            ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = conManager.getActiveNetworkInfo();
            if (network != null) {
                bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bisConnFlag;
    }

    /**
     * 判断使用网络情况
     *
     * @param context
     * @return
     */
    public static boolean isWifiAvailable(Context context) {
        boolean isAvailable = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && StringUtil.isNotNull(info.getTypeName())) {
                String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
                isAvailable = typeName.equals("wifi");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return isAvailable;
    }

    public static String getNetWorkName(Context context) {
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (null != mConnectivityManager) {
                    NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null) {
                        return networkInfo.getTypeName();
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "unKnow";
    }
    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }


    /**
     * 获取本地ip地址
     * @return
     */
    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }
}
