package capsicum.game.wordfox;

import com.crashlytics.android.Crashlytics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Predicate;

import io.fabric.sdk.android.Fabric;

import static org.junit.Assert.assertTrue;

public class ClientRunnableTest {
    private ServerSocket ss;
    private Socket s;
    private int port = 7878;

    @Before
    public void setUp() throws Exception {
        ss = new ServerSocket(port);
        s = new Socket(ss.getInetAddress().getHostAddress(), port);
    }

    @After
    public void tearDown() throws Exception {
        s.close();
        ss.close();
    }

    private <T> boolean isValueWithinTime(Predicate<T> validate, int time, T val) throws InterruptedException {
        for (int i = 0; i < time && !validate.test(val); ++i) {
            Thread.sleep(10);
        }
        return validate.test(val);
    }

    @Test
    public void inboundStreamTest() throws InterruptedException {
        String message = "TestMessage";
        MessageHandler mh = Mockito.mock(MessageHandler.class);
        new Thread(new ServerToClient(message)).start();

        ClientRunnable cr = new ClientRunnable(s, mh);
        new Thread(cr).start();

        Mockito.verify(mh, Mockito.timeout(1000)).handleReceivedMessage(message, cr);
        cr.close();
    }

    @Test
    public void outboundStreamTest() throws InterruptedException {
        String message = "TestMessage";
        MessageHandler mh = Mockito.mock(MessageHandler.class);
        ServerFromClient sfc = new ServerFromClient();
        new Thread(sfc).start();

        ClientRunnable cr = new ClientRunnable(s, mh);
        new Thread(cr).start();
        cr.sendMessage(message);
        assertTrue(isValueWithinTime(x -> x.equals(sfc.getMessageFromClient()), 100, message));
    }

    @Test
    public void bothSidesTest() throws IOException {
        String message = "TestMessage";

        createAndMessage(s, message);
        MessageHandler mh = Mockito.mock(MessageHandler.class);
        ClientRunnable cr = createToReceive(mh);

        Mockito.verify(mh, Mockito.timeout(1000)).handleReceivedMessage(message, cr);
        cr.close();
    }

    private void createAndMessage(Socket s, String message){
        MessageHandler mh = Mockito.mock(MessageHandler.class);
        ClientRunnable cr = new ClientRunnable(s, mh);
        new Thread(cr).start();
        cr.sendMessage(message);
    }
    private ClientRunnable createToReceive(MessageHandler mh) throws IOException {
        Socket testSoc = ss.accept();
        ClientRunnable cr2 = new ClientRunnable(testSoc, mh);
        new Thread(cr2).start();
        return cr2;
    }

    @Test
    public void reconnectTest() throws InterruptedException, IOException {
        String message = "TestMessage";
        MessageHandler mh = Mockito.mock(MessageHandler.class);
        ServerFromClient sfc = openOutbound();

        ClientRunnable cr = new ClientRunnable(s, mh);
        new Thread(cr).start();
        cr.sendMessage(message);
        assertTrue(isValueWithinTime(x -> x.equals(sfc.getMessageFromClient()), 100, message));

        s.close();
        ss.close();
        ss = new ServerSocket(port);
        s = new Socket(ss.getInetAddress().getHostAddress(), port);

        String message2 = "TestMessage2";
        MessageHandler mh2 = Mockito.mock(MessageHandler.class);
        ServerFromClient sfc2 = openOutbound();

        ClientRunnable cr2 = new ClientRunnable(s, mh2);
        new Thread(cr2).start();
        cr2.sendMessage(message2);
        assertTrue(isValueWithinTime(x -> x.equals(sfc2.getMessageFromClient()), 100, message2));
    }

    @Test
    public void reconnectTestSameServer() throws InterruptedException, IOException {
        String message = "TestMessage";
        MessageHandler mh = Mockito.mock(MessageHandler.class);
        ServerFromClient sfc = openOutbound();

        ClientRunnable cr = new ClientRunnable(s, mh);
        new Thread(cr).start();
        cr.sendMessage(message);
        assertTrue(isValueWithinTime(x -> x.equals(sfc.getMessageFromClient()), 100, message));

        s.close();
        s = new Socket(ss.getInetAddress().getHostAddress(), port);

        String message2 = "TestMessage2";
        MessageHandler mh2 = Mockito.mock(MessageHandler.class);
        ServerFromClient sfc2 = openOutbound();

        ClientRunnable cr2 = new ClientRunnable(s, mh2);
        new Thread(cr2).start();
        cr2.sendMessage(message2);
        assertTrue(isValueWithinTime(x -> x.equals(sfc2.getMessageFromClient()), 100, message2));

    }

    @Test
    public void multipleConnections() throws IOException {
        String message1 = "TestMessage1";
        String message2 = "TestMessage2";
        String message3 = "TestMessage3";
        String message4 = "TestMessage4";
        String message5 = "TestMessage5";
        String message6 = "TestMessage6";
        Socket s1 = s;
        Socket s2 = new Socket(ss.getInetAddress().getHostAddress(), port);
        Socket s3 = new Socket(ss.getInetAddress().getHostAddress(), port);
        Socket s4 = new Socket(ss.getInetAddress().getHostAddress(), port);
        Socket s5 = new Socket(ss.getInetAddress().getHostAddress(), port);
        Socket s6 = new Socket(ss.getInetAddress().getHostAddress(), port);

        createAndMessage(s1, message1);
        createAndMessage(s2, message2);
        createAndMessage(s3, message3);
        createAndMessage(s4, message4);
        createAndMessage(s5, message5);
        createAndMessage(s6, message6);
        MessageHandler mh1 = Mockito.mock(MessageHandler.class);
        MessageHandler mh2 = Mockito.mock(MessageHandler.class);
        MessageHandler mh3 = Mockito.mock(MessageHandler.class);
        MessageHandler mh4 = Mockito.mock(MessageHandler.class);
        MessageHandler mh5 = Mockito.mock(MessageHandler.class);
        MessageHandler mh6 = Mockito.mock(MessageHandler.class);
        ClientRunnable cr1 = createToReceive(mh1);
        ClientRunnable cr2 = createToReceive(mh2);
        ClientRunnable cr3 = createToReceive(mh3);
        ClientRunnable cr4 = createToReceive(mh4);
        ClientRunnable cr5 = createToReceive(mh5);
        ClientRunnable cr6 = createToReceive(mh6);

        Mockito.verify(mh1, Mockito.timeout(1000)).handleReceivedMessage(message1, cr1);
        Mockito.verify(mh2, Mockito.timeout(1000)).handleReceivedMessage(message2, cr2);
        Mockito.verify(mh3, Mockito.timeout(1000)).handleReceivedMessage(message3, cr3);
        Mockito.verify(mh4, Mockito.timeout(1000)).handleReceivedMessage(message4, cr4);
        Mockito.verify(mh5, Mockito.timeout(1000)).handleReceivedMessage(message5, cr5);
        Mockito.verify(mh6, Mockito.timeout(1000)).handleReceivedMessage(message6, cr6);

        s1.close();
        s2.close();
        s3.close();
        s4.close();
        s5.close();
        s6.close();
    }

    private ServerFromClient openOutbound(){
        ServerFromClient sfc = new ServerFromClient();
        new Thread(sfc).start();
        return sfc;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    class ServerToClient implements Runnable {
        private String messageToClient;

        ServerToClient(String messageToClient) {
            this.messageToClient = messageToClient;
        }

        @Override
        public void run() {
            try {
                Socket testSoc = ss.accept();
                OutputStreamWriter outputStreamWriter;
                outputStreamWriter = new OutputStreamWriter(testSoc.getOutputStream());
                outputStreamWriter.write((messageToClient + "\n"));
                outputStreamWriter.flush();
                System.out.println("T : Wrote to stream");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ServerFromClient implements Runnable {
        private String messageFromClient = "";

        @Override
        public void run() {
            try {
                Socket testSoc = ss.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(testSoc.getInputStream()));
                messageFromClient = br.readLine();
                System.out.println("Buffer complete.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getMessageFromClient() {
            return messageFromClient;
        }
    }
}