package capsicum.game.wordfox.screen_local_wifi.wifi_direct_service;

import java.io.IOException;

public interface MessageHandler {
    void handleReceivedMessage(String message, ChatServer chatServer);
    void handleChatClosed(ChatServer chatServer);
    void log(String msg);
    void logCrash(IOException e);
}
