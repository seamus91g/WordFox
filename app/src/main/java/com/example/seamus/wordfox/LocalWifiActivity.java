package com.example.seamus.wordfox;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.data.FoxDictionary;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.injection.DictionaryApplication;
import com.example.seamus.wordfox.profile.ProfileActivity;

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
        WifiActivityContract,
        PeersUpdated,
        WifiP2pManager.ConnectionInfoListener,
        DeviceActionListener {

    public static final String JSON_LETTERS = "json_letters_key";
    private static final String MONITOR_TAG = "myTag";
    public static final String JSON_NEW_PLAYER = "json_new_player";
    public static final String JSON_REMOVE_PLAYER = "json_remove_player";
    private static final String UNKNOWN_USER = "Unknown User";
    private final WifiServiceConnection netConnService = new WifiServiceConnection(this);

    private WifiPeersAdapter peersAdapter;

    private List<WifiP2pDevice> wifiDirectPeers = new ArrayList<>();
    private boolean isWifiP2pEnabled;
    private WifiP2pDevice myDevice;
    private boolean reTriedChannel = false;
    private com.example.seamus.wordfox.WifiBroadcastReceiver wifiReceiver;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private IntentFilter activityIntentFilter = new IntentFilter();
    private IntentFilter wifiIntentFilter = new IntentFilter();
    public static final String INTENT_LETTERS = "intent_letters_key";
    public static final String INTENT_GROUP_OWNER = "group_owner_key";
    public static final String INTENT_NEW_PLAYER = "intent_new_player";
    public static final String INTENT_REMOVE_PLAYER = "intent_remove_player";
    private boolean isGroupOwner = false;
    private ArrayList<String> letters;
    private NavigationBurger navBurger = new NavigationBurger();
    private PlayerIdentityRemote myGroupOwner;
    private WifiConnectedPlayers connectedPlayers;
    private WifiBroadcastReceiver activityReceiver;
    private String myPlayerName;
    private boolean isAnimationAlive;


    class WifiBroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(MONITOR_TAG, "L : Received any ol thing : " + intent.getAction() + "// Player count: " + connectedPlayers.size());
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
            } else if (intent.getAction().equals(WifiService.ACTION_PLAYER_ADDED)) {
                String newPlayerString = intent.getExtras().getString(INTENT_NEW_PLAYER);
                try {
                    JSONObject player = new JSONObject(intent.getExtras().getString(INTENT_NEW_PLAYER));
                    PlayerIdentityRemote playerFound = new PlayerIdentityRemote(player.getJSONObject(LocalWifiActivity.JSON_NEW_PLAYER));
                    if (playerFound.macAddress.equals(myDevice.deviceAddress)) {
                        return;
                    }
                    connectedPlayers.addConnectedPlayer(playerFound);
                    showConnectedPlayerCount(connectedPlayers.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                makeToast(newPlayerString);
                Log.d(MONITOR_TAG, "Found new player : " + newPlayerString + ", count is now " + connectedPlayers.size());
            } else if (intent.getAction().equals(WifiService.ACTION_PLAYER_REMOVED)) {
                pLog("Action player removed detected. : " + intent.getAction());
                String removedPlayerString = intent.getExtras().getString(INTENT_REMOVE_PLAYER);
                JSONObject player = null;
                try {
                    player = new JSONObject(intent.getExtras().getString(INTENT_REMOVE_PLAYER));
                    PlayerIdentityRemote playerFound = new PlayerIdentityRemote(player.getJSONObject(LocalWifiActivity.JSON_REMOVE_PLAYER));
                    pLog("Attempting to remove connected player ... Currently at " + connectedPlayers.size());
                    connectedPlayers.removeConnectedPlayer(playerFound);
                    pLog("Finished attempting to remove connected player ... now at " + connectedPlayers.size());
                    showConnectedPlayerCount(connectedPlayers.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pLog("Removed: " + removedPlayerString);
            }
        }
    }


    private void showConnectedPlayerCount(int count) {
        TextView pCount = findViewById(R.id.my_device_player_count);
        pCount.setText(String.valueOf(count));
    }

//    private <T> void safeRemove(ArrayList<T> array, T item) {
//        ListIterator<T> iter = array.listIterator();
//        while (iter.hasNext()) {
//            if (item.equals(iter.next())) {
//                iter.remove();
//                logToast("=============== Success removed player ===============");
//                return;
//            }
//        }
//        logToast("=============== Failed to remove player ===============");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_wifi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!isStoragePermissionGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            makeToast("Permission not granted. Can not use Wifi-Direct.");
        }
        pLog("************* LoWA thread : " + Thread.currentThread().getPriority());

        findViewById(R.id.wifiSearchButton).setOnClickListener(view -> discoverPeers());
        connectedPlayers = new WifiConnectedPlayers(this);
        setup();
        setupHelperFox();
        new Handler().post(() -> sizeButtons());
        new Handler().post(() -> new Thread(() -> setupWifiPhone()).start());
//        setupWifiPhone();
    }

    private void sizeButtons() {
        ArrayList<Button> buttons = new ArrayList<>();
        Button start = findViewById(R.id.bStartWifiGame);
        buttons.add(start);
        buttons.add((Button) findViewById(R.id.bDisconnect));
        buttons.add((Button) findViewById(R.id.wifiSearchButton));

        int maxWidth = 0;
        for (Button b : buttons) {
            if (b.getWidth() > maxWidth) {
                maxWidth = b.getWidth();
            }
        }
        for (Button b : buttons) {
            b.setWidth(maxWidth);
        }
    }

    private void setupWifiPhone() {
        // TODO: Async Task
        int maxHeight = findViewById(R.id.peerListView_container).getHeight();
        int maxWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        ImageView fox = findViewById(R.id.fox_holding_wifi_phone);
        int foxHeight = maxHeight / 2;
        Bitmap foxBmp = ImageHandler.getScaledBitmap(R.drawable.ppfox1silcoloured, foxHeight, getResources());

        // Set margin left to 1/4 screen
        // Set fox height as half listview height but max 400px;

        // Set phone height as 1/3 of fox
        ArrayList<Bitmap> wifiPhones;
        wifiPhones = new ArrayList<>();
        wifiPhones.add(ImageHandler.getScaledBitmap(R.drawable.phone_wifi_two_bars, foxHeight / 3, getResources()));
        wifiPhones.add(ImageHandler.getScaledBitmap(R.drawable.phone_wifi_no_bar, foxHeight / 3, getResources()));
        wifiPhones.add(ImageHandler.getScaledBitmap(R.drawable.phone_wifi_one_bar, foxHeight / 3, getResources()));
        ImageView phone = findViewById(R.id.with_friends_wifi_phone);

        int leftMargin = -(foxBmp.getWidth() * 7) / 100;
        int topMargin = (foxBmp.getHeight() * 25) / 100;

        runOnUiThread(() -> {
            fox.setImageBitmap(foxBmp);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fox.getLayoutParams();
            lp.setMargins(maxWidth / 5, 0, 0, 0);
            fox.setLayoutParams(lp);

            lp = (RelativeLayout.LayoutParams) phone.getLayoutParams();
            lp.setMargins(leftMargin, topMargin, 0, 0);
            phone.setRotation(35);
            phone.setLayoutParams(lp);
        });
        // margin top, margin left negative
        // rotate 30 degrees
        isAnimationAlive = true;
        Integer count = 0;
        while (isAnimationAlive) {
            switch (count % 3) {
                case 0:
                    runOnUiThread(() -> phone.setImageBitmap(wifiPhones.get(0)));
                    break;
                case 1:
                    runOnUiThread(() -> phone.setImageBitmap(wifiPhones.get(1)));
                    break;
                case 2:
                    runOnUiThread(() -> phone.setImageBitmap(wifiPhones.get(2)));
                    break;
            }
            ++count;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setServiceListener() {
        pLog("Setting action listener on wifiservice");
        netConnService.getWifiService().setActionListener(LocalWifiActivity.this);
    }

    private void setupHelperFox() {
        ImageView helperFox = findViewById(R.id.fox_wifi_helper);

        helperFox.post(() -> {
            int height = helperFox.getHeight();
            // TODO: .. why 9/10???
            helperFox.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.datafoxsilcoloured_facingright, height*9/10, getResources()));
        });

        ImageView helperFoxSpeechBubble = findViewById(R.id.fox_wifi_helper_speech_bubble);
        helperFoxSpeechBubble.post(new Runnable() {
            @Override
            public void run() {
                int height = helperFoxSpeechBubble.getHeight();
                helperFoxSpeechBubble.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.speechbubbleright, height*9/10, getResources()));
            }
        });
    }

    public void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void setup() {

        activityIntentFilter.addAction(WifiService.ACTION_SEND_LETTERS);
        activityIntentFilter.addAction(WifiService.ACTION_PLAYER_ADDED);
        activityIntentFilter.addAction(WifiService.ACTION_PLAYER_REMOVED);

        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        peersAdapter = new WifiPeersAdapter(this, R.id.peerListView, wifiDirectPeers);
        ListView peerLV = findViewById(R.id.peerListView);
        peerLV.setAdapter(peersAdapter);
        peerLV.setOnItemClickListener(listener);

        findViewById(R.id.bDisconnect).setOnClickListener(v -> disconnect());
        findViewById(R.id.bStartWifiGame).setOnClickListener(v -> startWifiGame());

        activityReceiver = new WifiBroadcastReceiver();

        startService(new Intent(this, WifiService.class));
    }

    private boolean isStoragePermissionGranted(String permission) {
        Log.v(MONITOR_TAG, "Checking permission " + permission);
        if (Build.VERSION.SDK_INT >= 23) {
            if (LocalWifiActivity.this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Log.v(MONITOR_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(MONITOR_TAG, "Permission is revoked. Requesting ... ");
                ActivityCompat.requestPermissions(LocalWifiActivity.this, new String[]{permission}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
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
            makeToast("Letters not set!");
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

        HomeScreen.allGameInstances.clear();

        PlayerIdentity playerOne = GameData.getPlayer1Identity(this);
        GameInstance playerOneGame = new GameInstance(playerOne.ID, playerOne.username, 0, true, isGroupOwner);
        playerOneGame.setLetters(letters.get(0));
        playerOneGame.setLetters(letters.get(1));
        playerOneGame.setLetters(letters.get(2));
        HomeScreen.allGameInstances.add(playerOneGame);

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
        Log.d(MONITOR_TAG, "Binding " + this.toString());   // TODO: Check if bound
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
        isAnimationAlive = false;
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
        logToast("Peers available : " + availablePeers.getDeviceList().size());
        clearPeerList();
        this.wifiDirectPeers.addAll(availablePeers.getDeviceList());
        findViewById(R.id.heading_listview_wifi_players).setVisibility(View.VISIBLE);
        peersAdapter.notifyDataSetChanged();
        if (isGroupOwner) {
            pLog("Checking for wifiDirectPeers disconnected");
            signalDisconnectedPlayers();
        }
    }

    private void signalDisconnectedPlayers() {
        ArrayList<PlayerIdentityRemote> playersToDrop = new ArrayList<>();
        for (WifiP2pDevice d : wifiDirectPeers) {
            if (d.status == WifiP2pDevice.CONNECTED) {
                continue;
            }
            for (PlayerIdentityRemote p : connectedPlayers.getConnectedPlayers()) {
                if (p.macAddress.equals(d.deviceAddress)) {
                    pLog("Peer not connected: Removing " + p.username + " : " + p.macAddress);
                    signalRemovePlayer(p);
                    playersToDrop.add(p);
                    break;
                }
            }
        }
//        connectedPlayers.removeAll(playersToDrop);
        connectedPlayers.removeMultipleConnectedPlayers(playersToDrop);
        showConnectedPlayerCount(connectedPlayers.size());
    }

    private void signalRemovePlayer(PlayerIdentityRemote playerToRemove) {
        JSONObject jObj = new JSONObject();
        try {
            jObj.put(JSON_REMOVE_PLAYER, playerToRemove.toJson());
            netConnService.getWifiService().sendData(jObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearPeerList() {
        wifiDirectPeers.clear();
        findViewById(R.id.heading_listview_wifi_players).setVisibility(View.INVISIBLE);
//        wifiDirectPeers.add(null);
    }

    private void pLog(String msg) {
        Log.d(MONITOR_TAG, msg);
    }

    @Override
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }


    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d(MONITOR_TAG, "Received connection info : " + info.toString());
        isGroupOwner = info.isGroupOwner;
        // TODO: Check if group formed. if no, return
        if (netConnService.isBound) {
            signalWhoConnectedTo(info);
        } else {
            new Thread(() -> handleConnectionInfo(info)).start();
        }
        if (isGroupOwner && letters == null) {
            populateLetters();
        }

        if (netConnService.isBound) {
//            PlayerIdentity userIdentity = new PlayerIdentity()
//            String newPlayerMessage = GameData.getPlayer1Identity(this).username + ", " + GameData.getPlayer1Identity(this).ID;
            JSONObject jObj = new JSONObject();
            pLog("OnConnect : Attempting to greet ... ");
            try {
                PlayerIdentity p1 = GameData.getPlayer1Identity(this);
                PlayerIdentityRemote p1remote = new PlayerIdentityRemote(p1.ID, p1.username, p1.rank, myDevice.deviceAddress, isGroupOwner);   // TODO: What if myDevice is still nulL??
                jObj.put(JSON_NEW_PLAYER, p1remote.toJson());
                if (netConnService.getWifiService() == null) {
                    logToast("Null service: Cannot message wifiDirectPeers");
                } else {
                    Log.d(MONITOR_TAG, "Sending this greeting: " + jObj.toString());
                    netConnService.getWifiService().greetClient(jObj.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            logToast("Unbound: Cannot message wifiDirectPeers");
        }
//        discoverPeers();
//        peersAdapter.notifyDataSetChanged();
    }

    private void logToast(String msg) {
        makeToast(msg);
        pLog(msg);
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
        pLog("''''''' My Dev name : " + device.deviceName);

        // Set player name
        if (myPlayerName == null) {
            myPlayerName = GameData.getPlayer1Identity(this).username;
            TextView playerNameTV = findViewById(R.id.my_player_name);
            playerNameTV.setText(myPlayerName);
        }
        // Set device name
        TextView deviceNameTV = (TextView) findViewById(R.id.my_device_name);
        deviceNameTV.setText(device.deviceName);
        // Set device status
        TextView deviceStatusTV = (TextView) findViewById(R.id.my_device_status);
        deviceStatusTV.setText(getDeviceStatus(device.status));
        // Set device description, such as what group you're in
        updateMyDescription();
    }

    private boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
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

    private void clearInfo() {
        connectedPlayers.clear();
        isGroupOwner = false;
        myGroupOwner = null;
        clearPeerList();
        peersAdapter.notifyDataSetChanged();
        updateMyDescription();
    }

    @Override
    public void disconnect() {
//        clearInfo();
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(MONITOR_TAG, "Disconnect succeeded");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(MONITOR_TAG, "Disconnect failed. Reason : ");
            }
        });
    }

    @Override
    public void onChannelDisconnected() {
        pLog("~~~~~~~~~~~~ Channel disconnected ~~~~~~~~~~~~");
        clearInfo();
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
        pLog("Resetting data .......... ");
//        clearPeerList();
        clearInfo();
        peersAdapter.notifyDataSetChanged();
//        updateMyDescription();
    }

    private final String findConnectedPlayerUsername(String macAddress) {
        for (PlayerIdentityRemote pir : connectedPlayers.getConnectedPlayers()) {
            if (pir.macAddress.equals(macAddress)) {
                return pir.username;
            }
        }
        return UNKNOWN_USER;
    }

    @Override
    public void notifyPeersUpdated() {
        if (peersAdapter != null) {
            peersAdapter.notifyDataSetChanged();
        }
        showConnectedPlayerCount(connectedPlayers.size());
        myGroupOwner = connectedPlayers.getGroupOwner();
        updateMyDescription();
    }

    private void updateMyDescription() {
        String myStatu;
        if (isGroupOwner) {
            myStatu = "You are the game host!";
        } else if (myGroupOwner != null) {
            myStatu = "You are in " + myGroupOwner.username + "'s game!";
        } else {
            myStatu = "Not in a game yet";
        }
        TextView my_status_descriontion = findViewById(R.id.my_connection_description);
        my_status_descriontion.setText(myStatu);
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
            pLog("||||||||||||||||||| Device description |||||||||||||||||||");
            pLog("Position: " + position);
            pLog("Position is null? " + (wifiDirectPeers.get(position) == null));
//            if (position == 0 && wifiDirectPeers.get(position) == null) {
//                if (peerDevices.size() <= 1) {
//
//                }
//                Log.d(MONITOR_TAG, "!! header ");
//                return LayoutInflater.from(LocalWifiActivity.this).inflate(R.layout.list_of_players_heading, parent, false);
//            }
            if (convertView == null) {
                convertView = LayoutInflater.from(LocalWifiActivity.this).inflate(R.layout.phone_device_list_item, parent, false);
            }
            WifiP2pDevice localDevice = getItem(position);
            if (localDevice == null) {
                Log.d(MONITOR_TAG, "Local device is null?   true");
                return convertView;
            }
            String deviceName = localDevice.deviceName;  // Default
            String playerName = findConnectedPlayerUsername(localDevice.deviceAddress);
            String deviceStatus;
            if (playerName.equals(UNKNOWN_USER)) {
                deviceStatus = getDeviceStatus(localDevice.status);
            } else {
//                deviceStatus = getDeviceStatus(WifiP2pDevice.CONNECTED);
                deviceStatus = "Connected!";
            }

            Log.d(MONITOR_TAG, "Address:  " + localDevice.deviceAddress);
            Log.d(MONITOR_TAG, "Device Name: " + deviceName);
            Log.d(MONITOR_TAG, "Player Name: " + playerName);
            Log.d(MONITOR_TAG, "Device says its group owner? : " + localDevice.isGroupOwner());
            pLog("||||||||||||||||||| |||||||||||||||||||");

            TextView nameTV = convertView.findViewById(R.id.player_name_wifi_list_item);
            nameTV.setText(playerName);

            TextView tv = convertView.findViewById(R.id.device_name_wifi_list_item);
            tv.setText(deviceName);

            TextView status_tv = convertView.findViewById(R.id.connection_state_wifi_list_item);
            status_tv.setText(deviceStatus);

            String description;
            if (!playerName.equals(UNKNOWN_USER)) {
                if (isGroupOwner) {
                    description = "Connected to my game!";
                } else if (myGroupOwner != null) {
                    if (localDevice.deviceAddress.equals(myGroupOwner.macAddress)) {
                        description = "Hosting a game!";
                    } else {
                        description = "In " + myGroupOwner.username + "'s game!";
                    }
                } else {
                    description = "Click to invite!";
                }
            } else {
                description = "Click to invite!";
            }
            TextView description_tv = convertView.findViewById(R.id.connection_description_wifi_list_item);
            description_tv.setText(description);

            ImageView countImage = convertView.findViewById(R.id.player_count_image_wifi_list_item);
            TextView playerCount = convertView.findViewById(R.id.player_count_wifi_list_item);
            if (localDevice.status == WifiP2pDevice.CONNECTED || !playerName.equals(UNKNOWN_USER)) {
                String pCount = String.valueOf(connectedPlayers.size());
                playerCount.setText(pCount);
                countImage.setVisibility(View.VISIBLE);
            } else {
                playerCount.setText("");
                countImage.setVisibility(View.INVISIBLE);
            }
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Intent profileScreenIntent = new Intent(LocalWifiActivity.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        navBurger.navigateTo(item, LocalWifiActivity.this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private String getDeviceStatus(int deviceStatus) {
//        Log.d(MONITOR_TAG, "Peer status :" + deviceStatus);
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
//        Log.d(MONITOR_TAG, "status " + deviceStatus + " -> " + status);
        return status;
    }
}
