package com.example.seamus.wordfox;

import java.io.IOException;

public interface MessageHandler {
    void handleReceivedMessage(String message, ChatServer chatServer);
    void handleChatClosed(ChatServer chatServer);
    void log(String msg);
    void logCrash(IOException e);
}
