package com.example.seamus.wordfox;

public interface ChatServer extends Runnable {
    void sendMessage(String message);
    void close();
    void sendGreeting(String greeting);
}
