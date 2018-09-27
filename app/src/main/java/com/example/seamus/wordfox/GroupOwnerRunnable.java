package com.example.seamus.wordfox;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static com.example.seamus.wordfox.MainActivity.MONITOR_TAG;

class GroupOwnerRunnable implements MessageHandler, ChatServer {
    private int port;
    private MessageHandler messageHandler;
    private boolean isAlive = true;
    private ArrayList<ChatServer> clients = new ArrayList<>();

    GroupOwnerRunnable(int port, MessageHandler messageHandler) {
        this.port = port;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        Log.d(MainActivity.MONITOR_TAG, "Running GO runnable ");
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            while (isAlive) {
                Log.d(MONITOR_TAG, "GO: Waiting for client to connect .. ");
                Socket serverToClient = server.accept();
                Log.d(MONITOR_TAG, "GO: Client connected!! There are " + clients.size() + " clients. ");
                ChatServer cr = new ClientRunnable(serverToClient, this);
                clients.add(cr);
                new Thread(cr).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d(MONITOR_TAG, "C : GO is finished");
            for(ChatServer c : clients){
                c.close();
            }
            if(server != null){
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        messageAllClients(message, null);
    }

    private void messageAllClients(String message, ChatServer originator) {
        Log.d(MONITOR_TAG, "Messaging " + message + " to " + clients.size() + " clients");
        for (ChatServer client : clients) {
            Log.d(MONITOR_TAG, "Sending " + message + " ... ");
            if (client.equals(originator)) {       // Don't send to message originator
                Log.d(MONITOR_TAG, "******** Skipping originator client ... *********");
                continue;
            }
            client.sendMessage(message);
        }
    }

    @Override
    public void close() {
        isAlive = false;
    }

    @Override
    public void handleReceivedMessage(String message, ChatServer chatServer) {
        messageHandler.handleReceivedMessage(message, null);
        messageAllClients(message, chatServer);
    }

    @Override
    public void handleChatClosed(ChatServer chatServer) {
        clients.remove(chatServer);         // TODO: Should kill if clients.size == 0   ??
    }
}
