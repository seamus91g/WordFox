package com.example.seamus.wordfox;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.example.seamus.wordfox.MainActivity.MONITOR_TAG;

class ClientRunnable implements ChatServer {

    private InetAddress groupOwnerAddress;
    private int port;
    private MessageHandler messageHandler;
    private Queue<String> outputMessages = new ArrayDeque<>();
    private boolean isAlive = true;
    private Socket socket;
    private static int index = 0;
    private int myIndex = 0;

    public ClientRunnable(Socket socket, MessageHandler messageHandler) {
        this.socket = socket;
        this.messageHandler = messageHandler;
        myIndex = ++index;
    }

    public ClientRunnable(InetAddress groupOwnerAddress, int port, MessageHandler messageHandler) {
        this.groupOwnerAddress = groupOwnerAddress;
        this.port = port;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        Log.d(MainActivity.MONITOR_TAG, "Running Client runnable : " + myIndex);
        try {
            if (socket == null) {
                Log.d(MainActivity.MONITOR_TAG, "C : Running non-host configuration");
                assert groupOwnerAddress != null;
                socket = new Socket(groupOwnerAddress, port);
            }
            else {
                Log.d(MainActivity.MONITOR_TAG, "C : Running host configuration");
            }

            new Thread(serverListener).start();

            OutputStreamWriter outputStreamWriter;
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

//            outputStreamWriter.write("I am the serverToClient! \n");
//            outputStreamWriter.flush();
            while (isAlive) {
                if (!outputMessages.isEmpty()) {
                    String msg = outputMessages.remove();
                    Log.d(MainActivity.MONITOR_TAG, "C : Sending message to client " + myIndex + " : " + msg);
                    outputStreamWriter.write(msg + "\n");
                    outputStreamWriter.flush();
                }
                Log.d(MainActivity.MONITOR_TAG, "C : Sleeping ... " + myIndex);
                Thread.sleep(3500);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d(MONITOR_TAG, "C : Client is finished");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
                    Log.d(MONITOR_TAG, "*********** Waiting for buffer ***********");
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        Log.d(MONITOR_TAG, "C : ********* Client dead *********");
                        isAlive = false;
                        return;
                    }
                    Log.d(MONITOR_TAG, "C : Received from server: " + line);
                    messageHandler.handleReceivedMessage(line, ClientRunnable.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
    public void sendMessage(String message) {
        Log.d(MainActivity.MONITOR_TAG, "C : Adding message");
        outputMessages.add(message);
    }

    @Override
    public void close() {
        isAlive = false;
    }
}
