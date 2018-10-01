package com.example.seamus.wordfox;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import static com.example.seamus.wordfox.MainActivity.MONITOR_TAG;

public class WifiServiceConnection implements ServiceConnection {
    public boolean isBound;
    private WifiService wifiService;
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        isBound = true;
        Log.d(MONITOR_TAG, "Service starting ... ");

        WifiService.WifiBinder binder = (WifiService.WifiBinder) service;
        wifiService = binder.getService();

    }

    public WifiService getWifiService(){
        return wifiService;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
