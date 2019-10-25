package com.smartlines.laravel_restful.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ivan Galves on 20/10/2018.
 */

public class Connectivity extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public static String ip() {
        //return "hots.com";//usar hosts go en el dispositivo adnroid y agregar la ip de la maquina servidor
        return "192.168.0.2";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkChange(isConnected);
        }

       /* if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();
            Intent pushIntent = new Intent(context, RevisionActivity.class);
            context.startService(pushIntent);
        }*/

       /* WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());*/
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void setConnectivityReceiver(ConnectivityReceiverListener listener){
        connectivityReceiverListener = listener;
    }

    public interface ConnectivityReceiverListener {
        void onNetworkChange(boolean isConnected);
    }

}