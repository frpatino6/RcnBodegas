package com.rcnbodegas.Global;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.rcnbodegas.R;

import java.util.List;

public class KeepLiveApp extends Service {
    private static final int ID_NOTIFICACION_CREAR = 1;
    private NotificationManager mNotifyMgr;
    private static final String TAG = KeepLiveApp.class.getSimpleName();


    NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotification();
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private void createNotification() {
        Intent nIntent = new Intent();
//		working fine
//		homeIntent.setAction("android.intent.action.MAIN");
//		homeIntent.addCategory("android.intent.category.LAUNCHER");

//		working fine

        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final List<ActivityManager.RecentTaskInfo> recentTaskInfos = am.getRecentTasks(1024, 0);
        String myPkgNm = getPackageName();

        if (!recentTaskInfos.isEmpty()) {
            final List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_IGNORE_UNAVAILABLE);

            ActivityManager.RecentTaskInfo recentTaskInfo = null;

            for (int i = 0; i < recentTasks.size(); i++) {
                recentTaskInfo = recentTaskInfos.get(i);
                if (recentTaskInfo.baseIntent.getComponent().getPackageName().equals(myPkgNm)) {
                    nIntent = recentTaskInfo.baseIntent;
                    nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        }

        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent, ID_NOTIFICACION_CREAR);

        Notification notiBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Rcn")
                .setContentText("Bodegas se está ejecutando...")
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentIntent(pendingIntent).build();

        notiBuilder.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(ID_NOTIFICACION_CREAR, notiBuilder);
    }

    public void ClearNotifications(){
        notificationManager.cancel(ID_NOTIFICACION_CREAR);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(ID_NOTIFICACION_CREAR);
    }
}
