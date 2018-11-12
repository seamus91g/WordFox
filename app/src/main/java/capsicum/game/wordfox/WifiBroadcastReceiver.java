package capsicum.game.wordfox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    private final WifiP2pManager.PeerListListener peerListener;
    private final WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiInterface wifiActivity;

    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                 WifiInterface wifiActivity,
                                 WifiP2pManager.ConnectionInfoListener connectionInfoListener,
                                 WifiP2pManager.PeerListListener peerListener) {
        this.manager = manager;
        this.channel = channel;
        this.wifiActivity = wifiActivity;
        this.connectionInfoListener = connectionInfoListener;
        this.peerListener = peerListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String wifiAction = intent.getAction();
// Log
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(wifiAction)) {
            // Check if wifi is enabled or disabled
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                wifiActivity.setIsWifiP2pEnabled(true);
            } else {
                wifiActivity.setIsWifiP2pEnabled(false);
                wifiActivity.resetData();
            }
// Log
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(wifiAction)) {
            // Asynchronously request peers from manager. Notified by PeerListListener.onPeersAvailable
            if (manager == null) {
                return;
            }
            manager.requestPeers(channel, peerListener);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(wifiAction)) {
            // Request connection information about peers
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
// Log
                manager.requestConnectionInfo(channel, connectionInfoListener);
            }else{
                wifiActivity.resetData();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(wifiAction)) {
            // Check what the status of my own device is
// Log
            WifiP2pDevice myDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            wifiActivity.updateThisDevice(myDevice);
        }
    }
}
