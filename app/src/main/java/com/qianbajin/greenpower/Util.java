package com.qianbajin.greenpower;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
/**
 * @author wWX407408
 * @Created at 2017/12/19  10:00
 * @des
 */

public class Util {

    public static final String TAG = "Util";

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param data 要转换的字节数组
     * @return 转换后的结果
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] d = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            d[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return d;
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        boolean serviceOn = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d("", "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next();
                    if (accessibilityService.startsWith(context.getPackageName())) {
                        serviceOn = true;
                        break;
                    }
                }
            }
        }
        return serviceOn;
    }

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * wifi开关是否打开
     *
     * @param context 上下文
     * @return wifi状态
     */
    public static boolean isWifiSwitchOn(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }


    /**
     * 打开或关闭wifi,会弹窗询问是否打开
     *
     * @param context 上下文
     */
    public static boolean toggleWifi(Context context,boolean on) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(on);
    }

    public static boolean isMobileNetworkSwitchOn(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method getDataEnabled = tm.getClass().getDeclaredMethod("getDataEnabled", new Class[0]);
            Object invoke = getDataEnabled.invoke(tm, new Object[0]);
            return (boolean) invoke;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 切换移动网络开关,6.0前可以反射调用成功,6.0及6.0后涉及运行时权限,申请权限时被拒绝,调用失败
     * @param context 上下文
     * @param on      打开或关闭
     * @return   是否操作成功
     */
    public static boolean toggleMobileNetwork(Context context, boolean on) {
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method setDataEnabled = tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setDataEnabled.invoke(tm, on);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 启动支付宝
     * @param context 上下文
     * @return     是否启动成功
     */
    public static boolean startAliPay(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity");
            context.getApplicationContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActivity", "Exception");
        }
        return false;
    }
}
