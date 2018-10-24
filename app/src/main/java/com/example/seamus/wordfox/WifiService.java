package com.example.seamus.wordfox;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.seamus.wordfox.results_screen.RoundnGameResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WifiService extends Service implements MessageHandler {
    public static final String ACTION_STRING_SERVICE = "ToService";
    public static final String ACTION_SEND_LETTERS = "action_send_letters";
    public static final String ACTION_GAME_RESULTS = "action_game_results";
    public static final String ACTION_DECLARE_GROUP_OWNER = "declare_group_owner_key";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final IBinder wifiBinder = new WifiBinder();
    private static int port = 8888;
    private ChatServer chat;
    private boolean isGameOver = false;     // Send result broadcasts when true
    private ArrayList<String> pendingResults = new ArrayList<>();

    private void sendBroadcast(String message, String intentAction, String key) {
        Log.d(HomeScreen.MONITOR_TAG, "W: Broadcasting: " + intentAction);
        Intent intent = new Intent();
        intent.setAction(intentAction);
        intent.putExtra(key, message);
        sendBroadcast(intent);
    }

    public void connectedTo(WifiP2pInfo info) {
        if (chat != null) {
            Log.d(HomeScreen.MONITOR_TAG, "WS : Not connecting. Already connected to " + info.groupOwnerAddress);
            Log.d(HomeScreen.MONITOR_TAG, "WS : Group owner? : " + info.isGroupOwner);
            return;
        }
        Log.d(HomeScreen.MONITOR_TAG, "~~~~~~~~ I'm group owner? ~~~~~~~~ : " + info.isGroupOwner);

        if (info.isGroupOwner) {
            chat = new GroupOwnerRunnable(port, this);
        } else {
            chat = new ClientRunnable(info.groupOwnerAddress, port, this);
        }
        new Thread(chat).start();
    }

    @Override
    public void log(String msg) {
        Log.d(HomeScreen.MONITOR_TAG, msg);
    }

    public void sendData(String data) {
        Log.d(HomeScreen.MONITOR_TAG, "WS : Sending data " + data);
        chat.sendMessage(data);
        assert chat != null;
    }

    @Override
    public void handleReceivedMessage(String message, ChatServer c) {
        Log.d(HomeScreen.MONITOR_TAG, "WS : Handling received message ... " + message);
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
        if (chat != null) {
            chat.close();
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
        chat = null;
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
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Message msg = mServiceHandler.obtainMessage();
//        msg.arg1 = startId;
//        mServiceHandler.sendMessage(msg);
        Log.d(HomeScreen.MONITOR_TAG, "Finished binding ... ");

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.d(HomeScreen.MONITOR_TAG, "**************** Service is destroyed *********************");
        closeService();
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
