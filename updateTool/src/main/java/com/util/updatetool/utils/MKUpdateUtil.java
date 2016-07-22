package com.util.updatetool.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import com.util.updatetool.Beans.NtfBean;
import com.util.updatetool.service.MKUpdateService;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author ArielJin
 * @Email : jiasheng.jin@tom.com
 */
public final class MKUpdateUtil{

    private static int fileSize = 0;
    public HttpURLConnection conn;

    public static NtfBean ntfBean = null;

    public final static String BROADCASTACT = "com.util.updatetool.utils.MKUpdateUtil";

    private MKUpdateUtil() {

    }

    private static class MKUpdateUtilFactory{
        private static MKUpdateUtil UpdateUtil = new MKUpdateUtil();
    }

    public static MKUpdateUtil getInstance() {
        return MKUpdateUtilFactory.UpdateUtil;
    }

    public static String cachePath = null;

    public void setCachePath(String path){
        cachePath = path;
    }

    public static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void apkInstall(Context context) {
        context.startActivity(getIntallIntent(cachePath));
    }

    public static Intent getIntallIntent(String path){
        chmod("777", path);
        Uri uri = Uri.parse("file://" + path);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        return installIntent;
    }

    public synchronized static void startUpdateService(Context context, NtfBean ntfBean) {
        Intent downloadIntent = new Intent(context, MKUpdateService.class);
        downloadIntent.putExtra(MKUpdateService.NTF_BEAN, ntfBean);
        context.startService(downloadIntent);
    }

    public void getFileSize(final String urlPath, final Activity cxt, final getFileSizeListener listener) {
        ThreadPoolUtil.getThreadPool(ThreadPoolUtil.SINGLETHREAD).execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    URL url = new URL(urlPath);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setDoInput(true);

                    conn.connect();
                    // 文件大小
                    fileSize = conn.getContentLength();
                    if(listener != null){
                        listener.onGotFileSize(fileSize / (1024 * 1024) + "M");
                    }
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                } catch (ConnectTimeoutException cte) {

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }

    public interface getFileSizeListener{
        void onGotFileSize(String filesize);
    }

    public void createNtfBean(int resId, String appName, String apkUrl, String updateContent, int newVersion){
        ntfBean = new NtfBean();
        ntfBean.setIconId(resId);
        ntfBean.setAppName(appName);
        ntfBean.setApkUrl(apkUrl);
        ntfBean.setUpdateContent(updateContent);
        ntfBean.setNewVersion(newVersion);
        ntfBean.setApkPath(cachePath);
    }

    public static int getSceenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isNetworkOnWifi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
