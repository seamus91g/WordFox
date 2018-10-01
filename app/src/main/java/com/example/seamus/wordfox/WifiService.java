package com.example.seamus.wordfox;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
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

import com.example.seamus.wordfox.game_screen.GameActivity;

import static com.example.seamus.wordfox.MainActivity.MONITOR_TAG;

public class WifiService extends Service implements MessageHandler {
    public static final String ACTION_STRING_SERVICE = "ToService";
    public static final String ACTION_SEND_LETTERS = "action_send_letters";
    public static final String ACTION_DECLARE_GROUP_OWNER = "declare_group_owner_key";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final IBinder wifiBinder = new WifiBinder();
    private int messageCount = 0;
    private static int port = 8888;
    private WifiP2pInfo info;
    private final IntentFilter intentFilter = new IntentFilter();
    IntentFilter activityIntentFilter = new IntentFilter();
    private ChatServer chat;

//    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(MONITOR_TAG, "S : Received any ol thing");
//            if (intent.getAction().equals(ACTION_STRING_SERVICE)) {
//
//            }
//        }
//    };

    private void sendBroadcast(String message) {
        Log.d(MONITOR_TAG, "W: Broadcasting: " + ACTION_SEND_LETTERS);
        Intent intent = new Intent();
        intent.setAction(ACTION_SEND_LETTERS);
        intent.putExtra(LocalWifiActivity.INTENT_LETTERS, message);
        sendBroadcast(intent);
    }

//    private void broadCastGroupOwner(Boolean isGroupOwner){
//        Log.d(MONITOR_TAG, "W: Broadcasting: " + ACTION_DECLARE_GROUP_OWNER);
//        Intent intent = new Intent();
//        intent.setAction(ACTION_DECLARE_GROUP_OWNER);
//        intent.putExtra(LocalWifiActivity.INTENT_GROUP_OWNER, isGroupOwner);
//        sendBroadcast(intent);
//    }

    public void connectedTo(WifiP2pInfo info) {
//        broadCastGroupOwner(info.isGroupOwner);
        if (chat != null) {
            Log.d(MONITOR_TAG, "WS : Not connecting. Already connected to " + info.groupOwnerAddress);
            Log.d(MONITOR_TAG, "WS : Group owner? : " + info.isGroupOwner);
            return;
        }
        this.info = info;
        Log.d(MONITOR_TAG, "~~~~~~~~ I'm group owner? ~~~~~~~~ : " + info.isGroupOwner);

        if (info.isGroupOwner) {
            chat = new GroupOwnerRunnable(port, this);
        } else {
            chat = new ClientRunnable(info.groupOwnerAddress, port, this);
        }
        new Thread(chat).start();
    }

    public void sendData(String data) {
        Log.d(MONITOR_TAG, "WS : Sending data " + data);
        chat.sendMessage(data);
        assert chat != null;
    }

    @Override
    public void handleReceivedMessage(String message, ChatServer c) {
        Log.d(MONITOR_TAG, "WS : Handling received message ... " + message);
        sendBroadcast(message);
    }

    @Override
    public void handleChatClosed(ChatServer chatServer) {

    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                Log.d(MONITOR_TAG, "star id: " + msg.arg1);
                String msge = (String) msg.obj;
                Log.d(MONITOR_TAG, "Message is: " + msge);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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

        activityIntentFilter.addAction(ACTION_STRING_SERVICE);
//        registerReceiver(activityReceiver, activityIntentFilter);


    }


    public void onCommunicate(String message) {
        Message msg = mServiceHandler.obtainMessage(2, message + ", " + messageCount);
        mServiceHandler.sendMessage(msg);
        ++messageCount;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        Log.d(MONITOR_TAG, "Finished binding ... ");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.d(MONITOR_TAG, "**************** Service is destroyed *********************");
//        unregisterReceiver(activityReceiver);
        if (chat != null) {
            chat.close();
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
