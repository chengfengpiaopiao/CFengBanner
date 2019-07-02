package com.android.grice.cfengbanner.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.List;


public class DeviceUtils {

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static int getScreenWdith() {
        Display display = getDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight() {
        Display display = getDisplay();
        return display.getHeight();
    }

    public static Display getDisplay() {
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display;
    }

    public static DisplayMetrics getDisPlayMetrics() {
        Display display = getDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dm;
    }

    public static int getScreenDpi() {
        DisplayMetrics dm = getDisPlayMetrics();
        return dm.densityDpi;
    }

    public static int px2sp(float pxValue) {
        DisplayMetrics metrics = getDisPlayMetrics();
        return (int) (pxValue / metrics.scaledDensity + 0.5F);
    }

    public static int sp2px(float spValue) {
        DisplayMetrics metrics = getDisPlayMetrics();
        return (int) (spValue * metrics.scaledDensity + 0.5F);
    }

    public static int px2dip(float pxValue) {
        DisplayMetrics dm = getDisPlayMetrics();
        return (int) (pxValue / dm.density + 0.5F);
    }

    public static int dip2px(float dipValue) {
        DisplayMetrics dm = getDisPlayMetrics();
        return (int) (dipValue * dm.density + 0.5F);
    }

    public static int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize() / 1048576.0D;
        return (int) sdFreeMB;
    }

    public static int getStatuBarHeight() {
        Class c = null;
        Object obj = null;
        Field field = null;
        boolean x = false;
        int sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            int x1 = Integer.parseInt(field.get(obj).toString());
            sbar = mContext.getResources().getDimensionPixelSize(x1);
        } catch (Exception var6) {
            Log.i("error", "get status bar height fail");
            var6.printStackTrace();
        }

        return sbar;
    }

    public static PackageInfo getSystemPackageInfo() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static String getSystemVersion() {
        PackageInfo pi = getSystemPackageInfo();
        return pi != null ? pi.versionName : null;
    }

    public static int getSystemVersionCode() {
        PackageInfo pi = getSystemPackageInfo();
        return pi != null ? pi.versionCode : -1;
    }

    private static String[] permissions = {Manifest.permission.READ_PHONE_STATE};

    //获取设备IMEI
    public static String getImei() {
        /*
        if(!EasyPermissions.hasPermissions(mContext, permissions[0])){
            ToastUtils.showShortToast("请先进行授权");
        }
         */
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 判断微信是否可用
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断qq是否可用
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断设备是否支持闪光灯
     */
    public static boolean hasFlash() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * 获取闪光灯开关状态
     */
    public static String getFlashMode() {
        Camera cam = Camera.open();
        Camera.Parameters parameters = cam.getParameters();
        String flashMode = parameters.getFlashMode();
        return flashMode;
    }
}
