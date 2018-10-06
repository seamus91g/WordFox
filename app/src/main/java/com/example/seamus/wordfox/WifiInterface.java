package com.example.seamus.wordfox;

import android.net.wifi.p2p.WifiP2pDevice;

interface WifiInterface {

    void setIsWifiP2pEnabled(boolean isWifiP2pEnabled);
    void updateThisDevice(WifiP2pDevice device);
    void resetData();

}
