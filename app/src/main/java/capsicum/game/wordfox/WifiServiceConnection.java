package capsicum.game.wordfox;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import timber.log.Timber;

public class WifiServiceConnection implements ServiceConnection {
    public boolean isBound;
    private WifiService wifiService;
    private WifiActivityContract wifiActivityContract;

    @Deprecated
    public WifiServiceConnection(){
    }
    public WifiServiceConnection(WifiActivityContract wifiActivityContract) {
        this.wifiActivityContract = wifiActivityContract;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Timber.d( "|||||||||| Service starting ... |||||||||| ");
        WifiService.WifiBinder binder = (WifiService.WifiBinder) service;
        wifiService = binder.getService();
        isBound = true;
        if (wifiActivityContract != null) {
            wifiActivityContract.onServiceBound();
        }
    }

    public WifiService getWifiService() {
        return wifiService;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Timber.d( "|||||||||| Disconnecting service ||||||||||");
        isBound = false;
    }
}
