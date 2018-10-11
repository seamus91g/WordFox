package com.example.seamus.wordfox;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.data.FoxDictionary;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.injection.DictionaryApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalWifiActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WifiP2pManager.PeerListListener,
        WifiInterface,
        WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener,
        DeviceActionListener {

    public static final String JSON_LETTERS = "json_letters_key";
    private static final String MONITOR_TAG = "myTag";
    private final WifiServiceConnection netConnService = new WifiServiceConnection();

    private WifiPeersAdapter peersAdapter;

    private List<WifiP2pDevice> peers = new ArrayList<>();
    private boolean isWifiP2pEnabled;
    private WifiP2pDevice myDevice;
    private boolean reTriedChannel = false;
    private com.example.seamus.wordfox.WifiBroadcastReceiver wifiReceiver;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    IntentFilter activityIntentFilter = new IntentFilter();
    IntentFilter wifiIntentFilter = new IntentFilter();
    public static final String INTENT_LETTERS = "intent_letters_key";
    public static final String INTENT_GROUP_OWNER = "group_owner_key";
    private boolean isGroupOwner = false;
    ArrayList<String> letters;

    private WifiBroadcastReceiver activityReceiver;

    class WifiBroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(MONITOR_TAG, "L : Received any ol thing : " + intent.getAction());
            if (intent.getAction().equals(WifiService.ACTION_SEND_LETTERS)) {
                String lettersString = intent.getExtras().getString(INTENT_LETTERS);
                try {
                    JSONArray jArray = new JSONObject(lettersString).getJSONArray(LocalWifiActivity.JSON_LETTERS);
                    letters = new ArrayList<>();
                    for (int i = 0; i < jArray.length(); ++i) {
                        letters.add(jArray.getString(i));
                    }
                    Log.d(MONITOR_TAG, "Received jArray: " + jArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(MONITOR_TAG, "L : Received intent in activity from service. Letters: " + lettersString);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_wifi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> discoverPeers());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setup();
    }

    private void setup() {

        activityIntentFilter.addAction(WifiService.ACTION_SEND_LETTERS);

        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        peersAdapter = new WifiPeersAdapter(this, R.id.peerListView, peers);
        ListView peerLV = findViewById(R.id.peerListView);
        peerLV.setAdapter(peersAdapter);
        peerLV.setOnItemClickListener(listener);

        findViewById(R.id.bDisconnect).setOnClickListener(v -> disconnect());
        findViewById(R.id.bStartWifiGame).setOnClickListener(v -> startWifiGame());

        activityReceiver = new WifiBroadcastReceiver();

        startService(new Intent(this, WifiService.class));
    }

    private String arrayToLetters(ArrayList<String> letterArray) {
        StringBuilder gameLetters = new StringBuilder();
        for (int i = 0; i < letterArray.size(); i++) {
            gameLetters.append(letterArray.get(i));
        }
        return gameLetters.toString();
    }

    private void startWifiGame() {
        if (letters == null) {
            Log.d(MONITOR_TAG, "Letters not set ... ");
            Toast.makeText(this, "Letters not set!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(MONITOR_TAG, "Starting wifi game ");
        if (isGroupOwner) {
            JSONArray jsonLetters = new JSONArray(letters);
            try {
                JSONObject jObj = new JSONObject();
                jObj.put(JSON_LETTERS, jsonLetters);
                netConnService.getWifiService().sendData(jObj.toString());
                Log.d(MONITOR_TAG, "Starting game. As GO, sending json " + jObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(MONITOR_TAG, "Starting game. As GO, sending lettersString " + jsonLetters.toString());
        }

        MainActivity.allGameInstances.clear();

        PlayerIdentity playerOne = GameData.getPlayer1Identity(this);
        GameInstance playerOneGame = new GameInstance(playerOne.ID, playerOne.username, 0, true, isGroupOwner);
        playerOneGame.setLetters(letters.get(0));
        playerOneGame.setLetters(letters.get(1));
        playerOneGame.setLetters(letters.get(2));
        MainActivity.allGameInstances.add(playerOneGame);

        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra(GameActivity.GAME_INDEX, 0);

        // Wait for dictionary to finish loading
        while (!FoxDictionary.isWordListLoaded) {
            Log.d(MONITOR_TAG, "Dictionary word list is not finished loading!");
            try {
                Thread.sleep(100);      // Wait for dictionary to finish loading
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.startActivity(gameIntent);
    }

    private void discoverPeers() {
        if (!isWifiP2pEnabled) {
            return;
        }

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(MONITOR_TAG, "Discovery started ... ");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(MONITOR_TAG, "Discovery failed. ");
            }
        });
    }

    private AdapterView.OnItemClickListener listener = (arg0, view, position, id) -> {
        WifiP2pDevice device = peersAdapter.getItem(position);
        WifiP2pConfig config = new WifiP2pConfig();
        assert device != null;
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        connect(config);
        Log.d(MONITOR_TAG, "Position clicked: " + position + ", Device: " + device.deviceName + ", " + device.deviceAddress + ", intent :" + config.groupOwnerIntent);
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(MONITOR_TAG, "W : Registering receiver ");
        registerReceiver(activityReceiver, activityIntentFilter);
        wifiReceiver = new com.example.seamus.wordfox.WifiBroadcastReceiver(manager, channel, this, this, this);
        registerReceiver(wifiReceiver, wifiIntentFilter);
        bindService();
//        new Handler().post(this::bindService);
    }

    private void unBindService() {
        if (netConnService.isBound) {
            Log.d(MONITOR_TAG, "Unbinding service in " + this.toString());
            unbindService(netConnService);
            netConnService.isBound = false;
        }
    }

    private void bindService() {
        Log.d(MONITOR_TAG, "Binding " + this.toString());
        bindService(new Intent(this, WifiService.class), netConnService,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(MONITOR_TAG, "W : unregistering receiver ");
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(activityReceiver);
        unBindService();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList availablePeers) {
        clearPeerList();
        this.peers.addAll(availablePeers.getDeviceList());
    }

    public void clearPeerList() {
        peers.clear();
        peersAdapter.notifyDataSetChanged();
    }

    @Override
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        isGroupOwner = info.isGroupOwner;
        if (netConnService.isBound) {
            signalWhoConnectedTo(info);
        } else {
            new Thread(() -> handleConnectionInfo(info)).start();
        }
        if (isGroupOwner && letters == null) {
            populateLetters();
        }
    }

    private void handleConnectionInfo(WifiP2pInfo info) {
        try {
            while (!netConnService.isBound) {       // TODO: Improve
                Thread.sleep(100);
                Log.d(MONITOR_TAG, "Waiting to be bound ...");
            }
            signalWhoConnectedTo(info);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void signalWhoConnectedTo(WifiP2pInfo info) {
        WifiService wfWifiDirect = netConnService.getWifiService();
        wfWifiDirect.connectedTo(info);
    }

    private void populateLetters() {
        DictionaryApplication dictionary = (DictionaryApplication) getApplication();
        letters = new ArrayList<>();
        letters.add(arrayToLetters(dictionary.getDictionary().getGivenLetters()));
        letters.add(arrayToLetters(dictionary.getDictionary().getGivenLetters()));
        letters.add(arrayToLetters(dictionary.getDictionary().getGivenLetters()));
        Log.d(MONITOR_TAG, "LW : letters as json: " + new JSONArray(letters).toString());
    }

    @Override
    public void updateThisDevice(WifiP2pDevice device) {
        this.myDevice = device;
        TextView view = (TextView) findViewById(R.id.my_device_name);
        view.setText(device.deviceName);
        view = (TextView) findViewById(R.id.my_device_status);
        String status = getDeviceStatus(device.status);
        view.setText(status);
    }

    @Override
    public void showDetails(WifiP2pDevice device) {

    }

    @Override
    public void cancelDisconnect() {        // TODO: When does this happen?
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(MONITOR_TAG, "Aborting connection");
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(MONITOR_TAG, "Connect succeeded ");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(MONITOR_TAG, "Connect failed ");
            }
        });

    }

    @Override
    public void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(MONITOR_TAG, "Disconnect succeeded");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(MONITOR_TAG, "Disconnect failed. Reason : " + reason);
            }
        });
    }

    @Override
    public void onChannelDisconnected() {
        if (manager != null && !reTriedChannel) {
            resetData();
            reTriedChannel = true;
            Log.d(MONITOR_TAG, "Retrying channel. ");
            manager.initialize(this, getMainLooper(), this);
        } else {
            Log.d(MONITOR_TAG, "Retry failed. Channel lost");
        }
    }

    public void resetData() {
        clearPeerList();
    }


    private class WifiPeersAdapter extends ArrayAdapter<WifiP2pDevice> {

        private List<WifiP2pDevice> peerDevices;

        public WifiPeersAdapter(@NonNull Context context, int resource, @NonNull List<WifiP2pDevice> peerDevices) {
            super(context, resource, peerDevices);
            this.peerDevices = peerDevices;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            return super.getView(position, convertView, parent);
            WifiP2pDevice d = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(LocalWifiActivity.this).inflate(R.layout.phone_device_item, parent, false);
            }
            TextView tv = convertView.findViewById(R.id.device_name);
            assert d != null;       // TODO: Tidy
            tv.setText(d.deviceName);

            return convertView;
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.local_wifi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private String getDeviceStatus(int deviceStatus) {
        Log.d(MONITOR_TAG, "Peer status :" + deviceStatus);
        String status;
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                status = "Available";
                break;
            case WifiP2pDevice.INVITED:
                status = "Invited";
                break;
            case WifiP2pDevice.CONNECTED:
                status = "Connected";
                break;
            case WifiP2pDevice.FAILED:
                status = "Failed";
                break;
            case WifiP2pDevice.UNAVAILABLE:
                status = "Unavailable";
                break;
            default:
                status = "Unknown";
        }
        Log.d(MONITOR_TAG, "status " + deviceStatus + " -> " + status);
        return status;
    }
}
