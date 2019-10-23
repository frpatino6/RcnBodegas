package com.rcnbodegas.Global;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SyncService extends Service {
    private static final int ID_NOTIFICACION_SYNC = 2;
    private NotificationManager mNotifyMgr;
    private static final String TAG = KeepLiveApp.class.getSimpleName();
    private Handler mHandler = new Handler();
    private Timer mTimer = null;    //timer handling
    public static final int notify = 60000;  //interval between two services(Here Service run every 5 seconds)
    NotificationManager notificationManager;
    private boolean isOk;
    private String lastCreatedNUmberDocument;

    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
    }


    private void createNotification() {
        Intent nIntent = new Intent();

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

        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent, ID_NOTIFICACION_SYNC);

        Notification notiBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Legalización")
                .setContentText("Hay pendiente " + GlobalClass.getInstance().getListMaterialForSync().size() + " documentos por sincronizar")
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentIntent(pendingIntent).build();

        notiBuilder.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(ID_NOTIFICACION_SYNC, notiBuilder);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(ID_NOTIFICACION_SYNC);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void asyncListMaterialsByProduction(ArrayList<MaterialViewModel> listForSync) {


        String wareHouse = GlobalClass.getInstance().getQueryByInventory() ? GlobalClass.getInstance().getIdSelectedWareHouseInventory() : GlobalClass.getInstance().getIdSelectedWareHouseWarehouse();

        String url = GlobalClass.getInstance().getUrlServices() + "warehouse/CreateElement/" + wareHouse;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        String tipo = "application/json";

        StringEntity entity = null;
        Gson json = new Gson();

        for (MaterialViewModel materialViewModel : listForSync) {
            materialViewModel.getListaImagenesBmp().clear();
        }
        String resultJson = json.toJson(listForSync);

        entity = new StringEntity(resultJson, StandardCharsets.UTF_8);

        client.post(getApplicationContext(), url, entity, tipo, new TextHttpResponseHandler() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                isOk = true;
                Gson gson = new GsonBuilder().create();
                // Define Response class to correspond to the JSON response returned
                lastCreatedNUmberDocument = gson.fromJson(responseString, String.class);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                isOk = false;
            }
            @SuppressLint("RestrictedApi")
            @Override
            public void onFinish() {
                super.onFinish();
                if (isOk) {
                    notificationManager.cancel(ID_NOTIFICACION_SYNC);
                    GlobalClass.getInstance().getListMaterialForSync().clear();
                }
            }
        });
    }

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {


                    if (GlobalClass.getInstance().isNetworkAvailable()) {

                        ArrayList<ArrayList<MaterialViewModel>> materialViewModels = GlobalClass.getInstance().getListMaterialForSync();

                        for (ArrayList<MaterialViewModel> materialViewModel : materialViewModels) {
                            asyncListMaterialsByProduction(materialViewModel);

                        }

                    }
                }
            });

        }

    }
}
