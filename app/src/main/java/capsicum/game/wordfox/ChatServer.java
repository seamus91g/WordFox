package capsicum.game.wordfox;

public interface ChatServer extends Runnable {
    void sendMessage(String message);
    void close();
    void sendGreeting(String greeting);
}
