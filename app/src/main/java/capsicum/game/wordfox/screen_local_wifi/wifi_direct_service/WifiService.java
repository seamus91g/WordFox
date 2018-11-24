package capsicum.game.wordfox.screen_local_wifi.wifi_direct_service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.screen_local_wifi.DeviceActionListener;
import capsicum.game.wordfox.screen_local_wifi.LocalWifiActivity;
import capsicum.game.wordfox.screen_results_game.RoundnGameResults;
import timber.log.Timber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class WifiService extends Service implements MessageHandler {
    public static final String ACTION_STRING_SERVICE = "ToService";
    public static final String ACTION_SEND_LETTERS = "action_send_letters";
    public static final String ACTION_GAME_RESULTS = "action_game_results";
    public static final String ACTION_PLAYER_ADDED = "action_player_added";
    public static final String ACTION_PLAYER_REMOVED = "action_player_removed";
    //    public static final String ACTION_PLAYER_COUNT = "action_player_count";
    public static final String ACTION_DECLARE_GROUP_OWNER = "declare_group_owner_key";
    private static final String CHAT_THREAD_NAME = "chat_thread_name";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final IBinder wifiBinder = new WifiBinder();
    private static int port = 8888;
    private ChatServer chat;
    private boolean isGameOver = false;     // Send result broadcasts when true
    private ArrayList<String> pendingResults = new ArrayList<>();
    private Queue<String> greetingQueue = new ArrayDeque<>();
    private DeviceActionListener deviceActionListener;
    private HandlerThread chatThread;

    private void sendBroadcast(String message, String intentAction, String key) {
        Timber.d( "W: Broadcasting: " + intentAction);
        Intent intent = new Intent();
        intent.setAction(intentAction);
        intent.putExtra(key, message);
        sendBroadcast(intent);
    }

    public void connectedTo(WifiP2pInfo info) {
        if (chat != null) {
            Timber.d( "WS : Not connecting. Already connected to " + info.groupOwnerAddress);
            Timber.d( "WS : Group owner? : " + ((GroupOwnerRunnable.class.equals(chat.getClass()))));
            return;
        }
        new Thread(() -> createChat(info)).start();

        log("$$$$$$ WS : GO address is:  (str, inet) - " + info.groupOwnerAddress.getHostAddress() + ", " + info.groupOwnerAddress);
    }

    private void createChat(WifiP2pInfo info) {
//        boolean retried = false;
        try {
            assignChatServer(info);
        } catch (IOException e) {
            log("######## Connecting to group failed : threw exception #########");
            try {
                Thread.sleep(1500);     // Try again after some time
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        if (chat == null) {
            log("######## Connecting to group second attempt #########");
            // Trying again after 1 second delay
            try {
                assignChatServer(info);
            } catch (IOException e) {
                log("######## Connecting to group failed again : threw exception #########");
                e.printStackTrace();
                deviceActionListener.disconnect();
                return;
                // TODO: Disconnect wifi - direct and alert user to try again.
            }
        }

//        Thread chatThread = new Thread(chat);       // TODO: Could just chat.run() instead since already on thread
        chatThread = new HandlerThread(CHAT_THREAD_NAME);

        chatThread.start();
        Handler h = new Handler(chatThread.getLooper());
        h.post(chat);

//        chatThread.setPriority(Thread.MAX_PRIORITY);
//        chatThread.start();
        while (!greetingQueue.isEmpty()) {
            Timber.d( "WS : Is chat null? " + (chat == null));
            Timber.d( "WS : greetingQueue null?" + (greetingQueue == null));
            Timber.d( "WS : greetingQueue size: " + greetingQueue.size());
            chat.sendGreeting(greetingQueue.remove());
        }
    }

    private void assignChatServer(WifiP2pInfo info) throws IOException {
        if (info.isGroupOwner) {
            log("#### #### Connecting as group owner ##### ####");
            log("######## Port " + port + " is available? " + (available(port)));
            chat = new GroupOwnerRunnable(new ServerSocket(port), WifiService.this);
        } else {
            log("######## Connecting as not group owner #########");
            chat = new ClientRunnable(new Socket(info.groupOwnerAddress, port), this);
        }
    }

    private boolean available(int port) {
        log("--------------Testing port " + port);
        Socket s = null;
        try {
            s = new Socket("localhost", port);

            // If the code makes it this far without an exception it means
            // something is using the port and has responded.
            log("--------------Port " + port + " is not available");
            return false;
        } catch (IOException e) {
            log("--------------Port " + port + " is available");
            return true;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error.", e);
                }
            }
        }
    }

    @Override
    public void log(String msg) {
        Timber.d( msg);
    }

    @Override
    public void logCrash(IOException e) {
        Crashlytics.logException(e);
    }

    public void sendData(String data) {
        Timber.d( "WS : Sending data " + data);
        chat.sendMessage(data);
        assert chat != null;
    }

    public void greetClient(String msg) {
        log("WS : Sending greet - " + msg);
        if (chat == null) {
            greetingQueue.add(msg);
        } else {
            chat.sendGreeting(msg);
        }
    }

    @Override
    public void handleReceivedMessage(String message, ChatServer c) {
        Timber.d( "WS : Handling received message ... " + message);
        try {
            JSONObject jso = new JSONObject(message);

            if (jso.has(GameInstance.PLAYER_NAME)) {
                if (!isGameOver) {
                    pendingResults.add(message);
                } else {
                    sendBroadcast(message, ACTION_GAME_RESULTS, RoundnGameResults.INTENT_GAME_RESULTS);
                }
            } else if (jso.has(LocalWifiActivity.JSON_LETTERS)) {

                sendBroadcast(message, ACTION_SEND_LETTERS, LocalWifiActivity.INTENT_LETTERS);
            } else if (jso.has(LocalWifiActivity.JSON_NEW_PLAYER)) {
                sendBroadcast(message, ACTION_PLAYER_ADDED, LocalWifiActivity.INTENT_NEW_PLAYER);
                log("WS : received message is Add player!");
            } else if (jso.has(LocalWifiActivity.JSON_REMOVE_PLAYER)) {
                log("WS : received message is remove player!");
                sendBroadcast(message, ACTION_PLAYER_REMOVED, LocalWifiActivity.INTENT_REMOVE_PLAYER);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void declareGameOver() {
        isGameOver = true;
        broadCastAllPendingResults();
    }

    public void closeService() {
        log("$$$$$$$$ Closing chat from WifiService ...");
        log("$$$$$$$$ $$$$$$$$ $$$$$$$$ $$$$$$$$");
        if (chat != null) {
            chat.close();
            chat = null;
        }
        if(chatThread != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                chatThread.quitSafely();
            }else {
                chatThread.quit();
            }
            chatThread = null;
        }
    }

    private void broadCastAllPendingResults() {
        for (String result : pendingResults) {
            sendBroadcast(result, ACTION_GAME_RESULTS, RoundnGameResults.INTENT_GAME_RESULTS);
        }
        pendingResults.clear();
    }

    @Override
    public void handleChatClosed(ChatServer chatServer) {
        closeService();
//        chat.close();
//        chat = null;
    }


    private final class ServiceHandler extends Handler {        // TODO: not used
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            String msge = (String) msg.obj;
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("************* WS thread pre : " + Thread.currentThread().getPriority());
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_AUDIO);
        thread.start();

        log("************* WS thread post : " + Thread.currentThread().getPriority());
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Message msg = mServiceHandler.obtainMessage();
//        msg.arg1 = startId;
//        mServiceHandler.sendMessage(msg);
        Timber.d( "Finished binding ... ");

        return START_NOT_STICKY;
    }

    public void setActionListener(DeviceActionListener deviceActionListener){
        this.deviceActionListener = deviceActionListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Timber.d( "**************** Service is destroyed *********************");
        closeService();
        if(deviceActionListener != null){
            Timber.d( "Disconnecting the Wifi Action Listener");
            deviceActionListener.disconnect();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return wifiBinder;
    }

    public class WifiBinder extends Binder {
        WifiService getService() {
            return WifiService.this;
        }

    }

}
