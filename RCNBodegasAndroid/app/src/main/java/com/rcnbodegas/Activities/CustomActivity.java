package com.rcnbodegas.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class CustomActivity extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    public BroadcastReceiver mConnReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            @SuppressWarnings("deprecation")
            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()) {
                BlockActivity(true);
            }
            else {
                BlockActivity(false);
            }
        }
    };

    ProgressDialog pausingDialog;
    public void BlockActivity(boolean connected)
    {
        if (pausingDialog == null){
            pausingDialog = new ProgressDialog (getActivity());
            pausingDialog.setMessage ("Esperando conexión de internet…");
        }

        if (!connected)
        {
            //Toast.makeText(getApplicationContext(), "Desconectado", Toast.LENGTH_LONG).show();
            pausingDialog.show();
            pausingDialog.setCancelable(false);
        }
        else
        {
            //Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_LONG).show();
            pausingDialog.setCancelable(true);
            pausingDialog.dismiss();

        }
    }

}