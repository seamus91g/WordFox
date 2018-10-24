package com.example.seamus.wordfox;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class WifiServiceConnection implements ServiceConnection {
    public boolean isBound;
    private WifiService wifiService;
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(HomeScreen.MONITOR_TAG, "Service starting ... ");
        WifiService.WifiBinder binder = (WifiService.WifiBinder) service;
        wifiService = binder.getService();
        isBound = true;
    }

    public WifiService getWifiService(){
        assert wifiService != null;         // TODO: Verify doesnt occur
        return wifiService;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(HomeScreen.MONITOR_TAG, "|||||||||| Disconnecting service ||||||||||");
        isBound = false;
    }
}
