package capsicum.game.wordfox;

import android.media.midi.MidiOutputPort;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

class GroupOwnerRunnable implements MessageHandler, ChatServer {
    //    private final int port;
    private final MessageHandler messageHandler;
    private volatile boolean isAlive = true;
    private final ArrayList<ChatServer> clients = new ArrayList<>();
    private final HashMap<ChatServer, JSONObject> clientGreetings = new HashMap<>();
    private final Queue<ChatServer> expectingGreeting = new ArrayDeque<>();
    private String groupOwnerGreeting;
    private final ServerSocket server;

    GroupOwnerRunnable(ServerSocket s, MessageHandler messageHandler) {
        this.server = s;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        log("Running GO runnable ");
        log("************* GO thread : " + Thread.currentThread().getPriority());
        try {
            log("GO address : " + server.getInetAddress().getHostAddress());
            log("GO address inet : " + server.getInetAddress());
            while (isAlive) {
                log("GO: Waiting for client to connect .. ");
                Socket serverToClient = server.accept();
                log("GO: Client connected!! There are " + (clients.size() + 1) + " clients. ");
                ChatServer cr = new ClientRunnable(serverToClient, this);
                greetNewClient(cr);
                synchronized (this) {
                    clients.add(cr);
                }
                new Thread(cr).start();
            }
        } catch (IOException e) {
            log("GO: Forcefully closed server socket");
            e.printStackTrace();
        } finally {
            log("C : GO is finished");
            for (ChatServer c : clients) {
                c.close();
            }
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void greetNewClient(ChatServer chatServer) {
        // If group owner greeting is not ready yet declare the client to be expecting a greeting
        if (groupOwnerGreeting == null) {
            log("GO : Waiting for owner greeting ..");
            expectingGreeting.add(chatServer);
        } else {
            chatServer.sendMessage(groupOwnerGreeting);
            log("GO : Owner greeting sent to client");
        }
        log("GO : Sending other greetings to client - " + clientGreetings.size());
        for (JSONObject greeting : clientGreetings.values()) {
            chatServer.sendMessage(greeting.toString());
        }
    }

    @Override
    public void sendGreeting(String greeting) {
        if (groupOwnerGreeting == null) {
            groupOwnerGreeting = greeting;
            log("GO : Greeting has been prepared");
            // Greeting may have arrived late. Message any client who is waiting.
            while (!expectingGreeting.isEmpty()) {
                log("GO : Greeting was late. Sending now - " + groupOwnerGreeting);
                expectingGreeting.remove().sendMessage(groupOwnerGreeting);
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        messageAllClients(message, null);
    }


    // Synchronise because other thread may remove elements
    private synchronized void messageAllClients(String message, ChatServer originator) {
        log("Messaging " + message + " to " + (clients.size() - (originator == null ? 0 : 1)) + " clients");
        for (ChatServer client : clients) {
            log("Sending " + message + " ... ");
            if (client.equals(originator)) {       // Don't send to message originator
                log("******** Skipping originator client ... *********");
                continue;
            }
            client.sendMessage(message);
        }
    }

    @Override
    public void close() {
        log("$$$$$$$$ GO : Setting GO to die ... ");
        isAlive = false;
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleReceivedMessage(String message, ChatServer chatServer) {
        JSONObject jso = null;
        try {
            jso = new JSONObject(message);
            if (jso.has(LocalWifiActivity.JSON_NEW_PLAYER)) {
                log("GO : Handling a greeting -> " + jso.toString());
                clientGreetings.put(chatServer, jso);
            } else {
                log("GO : Handling regular message -> " + jso.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageHandler.handleReceivedMessage(message, null);
        messageAllClients(message, chatServer);
    }

    @Override
    public void log(String msg) {
        messageHandler.log(msg);
    }

    @Override
    public void logCrash(IOException e) {
        messageHandler.logCrash(e);
    }

    @Override
    public void handleChatClosed(ChatServer chatServer) {
        // Synchronize because other thread may be iterating elements to message each client
        goodbyeClient(chatServer);
        synchronized (this) {
            clients.remove(chatServer);         // TODO: Should kill if clients.size == 0   ??
            clientGreetings.remove(chatServer);
        }
        log("Closed chat; cGr size, cli size : " + clientGreetings.size() + ", " + clients.size());
        if (clients.size() == 0) {
            messageHandler.handleChatClosed(null);
        }
    }

    private void goodbyeClient(ChatServer chatServer) {
        JSONObject greetingJso = clientGreetings.get(chatServer);
        if (greetingJso == null) {
            log("Goodbye json was null!  ");
            return;
        }

        try {
            log("json goodbye: " + greetingJso.toString());
            JSONObject player = greetingJso.getJSONObject(LocalWifiActivity.JSON_NEW_PLAYER);
            JSONObject goodbye = new JSONObject();
            goodbye.put(LocalWifiActivity.JSON_REMOVE_PLAYER, player);
            log("json goodbye fin: " + goodbye.toString());
            messageAllClients(goodbye.toString(), chatServer);
            messageHandler.handleReceivedMessage(goodbye.toString(), chatServer);   // TODO: Do this tidier
        } catch (JSONException e) {
            log("Json exception in goodbye");
            e.printStackTrace();
        }

    }
}
