package com.example.seamus.wordfox;

public interface MessageHandler {
    void handleReceivedMessage(String message, ChatServer chatServer);
    void handleChatClosed(ChatServer chatServer);
}
