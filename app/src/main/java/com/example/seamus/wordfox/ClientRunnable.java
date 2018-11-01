package com.example.seamus.wordfox;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

class ClientRunnable implements ChatServer {

    private final InetAddress groupOwnerAddress;
    private final int port;
    private final MessageHandler messageHandler;
    private final Queue<String> outputMessages = new ArrayDeque<>();
    private volatile boolean isAlive = true;
    private Socket socket;
    private static int index = 0;   // TODO: delete. Only used for debugging.
    private final int myIndex;
    private Handler coordHandler;

    public ClientRunnable(Socket socket, MessageHandler messageHandler) {
        this.socket = socket;
        this.messageHandler = messageHandler;
        myIndex = ++index;
        groupOwnerAddress = null;
        port = -1;
    }

    //    private Handler coordHandler; = new
//
//            Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    messageHandler.log("here");
//                }
//            };
    private class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            messageHandler.log("here");
        }
    }

    @Override
    public void run() {
//        messageHandler.log("Looping a looper ");
//        Looper.prepare();
//        coordHandler = new myHandler();
//        Looper.loop();
//        messageHandler.log("Looping begun ..");

        // SNIP: Connection logic here

        messageHandler.log("Running Client runnable : " + myIndex);
        messageHandler.log("************* CR thread : " + Thread.currentThread().getPriority());
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        messageHandler.log("************* CR thread : " + Thread.currentThread().getPriority());
        try {
            if (socket == null) {
                messageHandler.log("C : Running non-host configuration");
                assert groupOwnerAddress != null;
                socket = new Socket(groupOwnerAddress, port);
            } else {
                messageHandler.log("C : Running host configuration");
            }
            new Thread(serverListener).start();
            OutputStreamWriter outputStreamWriter;
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            int sleepCount = 0;
            while (isAlive || !outputMessages.isEmpty()) {
                if (!outputMessages.isEmpty()) {
                    writeMessageOut(outputStreamWriter);
                    messageHandler.log("C : Wrote message on outstream ...");
                }
                if (++sleepCount % 8 == 0) {
                    messageHandler.log("C : Sleeping ... " + myIndex);
                }
                Thread.sleep(500);
            }
            messageHandler.log("C : Exiting client ... " + myIndex);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            messageHandler.log("C : Client is finished");
            Log.d(HomeScreen.MONITOR_TAG, "CR: Output stream dead.");
            messageHandler.handleChatClosed(this);
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void writeMessageOut(OutputStreamWriter outputStreamWriter) throws IOException {
        while (!outputMessages.isEmpty()) {
            String msg = outputMessages.remove();
            messageHandler.log("C : Sending message to client " + myIndex + " : " + msg);
            outputStreamWriter.write(msg + "\n");
            outputStreamWriter.flush();
        }
    }

    private Runnable serverListener = new Runnable() {
        @Override
        public void run() {
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while (isAlive) {
                    messageHandler.log("*********** Waiting for buffer ***********");
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        messageHandler.log("C : ********* Client dead *********");
                        isAlive = false;
                        return;
                    }
                    messageHandler.log("C : Received from server: " + line);
                    messageHandler.handleReceivedMessage(line, ClientRunnable.this);
                }
            } catch (IOException e) {
                messageHandler.logCrash(e);
                e.printStackTrace();
            } finally {
                Log.d(HomeScreen.MONITOR_TAG, "CR: Input stream dead.");
                isAlive = false;    // TODO: Will be false already in all cases??
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public synchronized void sendMessage(String message) {
        messageHandler.log("C : Adding message");
        outputMessages.add(message);
    }

    @Override
    public void close() {
        messageHandler.log("$$$$$$$$$$$$ Setting client to dead  .. " + myIndex);
        isAlive = false;
    }

    @Override
    public void sendGreeting(String greeting) {
        messageHandler.log("C : Sending greeting now. " + greeting);
        sendMessage(greeting);
        messageHandler.log("C : Greeting sent");
    }
}
