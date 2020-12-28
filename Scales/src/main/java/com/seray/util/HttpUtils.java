package com.seray.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class HttpUtils {

    private HttpUtils() {
    }

    /**
     * 获取本地IP地址
     * 增加获取有线网口IP地址
     */
    public static String getLocalIpStr(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_ETHERNET) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface netInter = en.nextElement();
                        for (Enumeration<InetAddress> enumIp = netInter.getInetAddresses(); enumIp.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIp.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    LogUtil.e(e.getMessage());
                }
            } else if (type == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().
                        getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return intToIp(wifiInfo.getIpAddress());
            }
        }
        return "0.0.0";
    }

    private static String intToIp(int ip) {
        return (ip & 0xff) + "." + ((ip >> 8) & 0xff) + "." + ((ip >> 16) & 0xff) + "." + ((ip >>
                24) & 0xff);
    }
}
