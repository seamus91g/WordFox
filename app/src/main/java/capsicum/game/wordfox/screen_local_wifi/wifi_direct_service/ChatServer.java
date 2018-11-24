package capsicum.game.wordfox.screen_local_wifi.wifi_direct_service;

public interface ChatServer extends Runnable {
    void sendMessage(String message);
    void close();
    void sendGreeting(String greeting);
}
