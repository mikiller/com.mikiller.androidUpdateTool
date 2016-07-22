package com.util.updatetool.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.util.updatetool.Beans.NtfBean;
import com.util.updatetool.net.DownloadAPKThread;
import com.util.updatetool.utils.MKUpdateUtil;
import com.util.updatetool.R;


/**
 * @author ArielJin
 * @Email : jiasheng.jin@tom.com
 */
public class MKUpdateService extends Service {

    public static final String TAG = MKUpdateService.class.getName();
    public static final String NTF_BEAN = "ntfBean";
    private Thread thread;
    private NtfBean ntfBean = null;
    
    private NotificationManager ntfMgr = null;
    private NotificationCompat.Builder ntfBuilder;
    RemoteViews rv;

    @Override
    public void onCreate() {
        super.onCreate();
        ntfMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            ntfBean = (NtfBean) intent.getSerializableExtra(NTF_BEAN);
            initNotification();
            if (ntfBean != null) {
                startThread();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    
    private void initNotification(){
        if(ntfBuilder == null)
            ntfBuilder = new NotificationCompat.Builder(this);
        ntfBuilder.setSmallIcon(ntfBean.getIconId());
        ntfBuilder.setTicker(String.format("正在更新%1$s...", ntfBean.getAppName()));
        rv = new RemoteViews(getPackageName(), R.layout.notification_layout);
        rv.setTextViewText(R.id.tv_percent, "0%");
        rv.setTextViewText(R.id.tv_ntf_title, "当前更新进度...");
        rv.setProgressBar(R.id.pgs_progress, 100, 0, false);
        ntfBuilder.setContent(rv);
        ntfBuilder.setOngoing(true);
        ntfBuilder.setAutoCancel(true);
        ntfBuilder.setNumber(0);
    	ntfBuilder.setDefaults(-1);
    }

    private void startThread() {
        thread = new DownloadAPKThread(this, ntfBean.getApkUrl(), ntfBean.getApkPath(), MKUpdateUtil.getInstance().conn);
        thread.start();
    }

    public void sendUpdateBroadcast(){
        Intent updateIntent = new Intent();
        updateIntent.setAction(MKUpdateUtil.BROADCASTACT);
        sendBroadcast(updateIntent);
    }
    
    public void refreshNotification(int currentDownload){

        if(ntfBuilder != null){
            rv.setTextViewText(R.id.tv_percent, currentDownload + "%");
            rv.setProgressBar(R.id.pgs_progress, 100, currentDownload, false);
            rv.setImageViewResource(R.id.iv_icon, ntfBean.getIconId());
            if(currentDownload == 100){
                PendingIntent pi = PendingIntent.getActivity(this, 1, MKUpdateUtil.getIntallIntent(ntfBean.getApkPath()), PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setOnClickPendingIntent(R.id.rl_notification_download, pi);
                ntfBuilder.setOngoing(false);
                stopSelf();
            }
        }
    	ntfMgr.notify(1, ntfBuilder.build());
    }


}
