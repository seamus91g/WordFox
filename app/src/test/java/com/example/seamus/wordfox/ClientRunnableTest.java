package com.example.seamus.wordfox;

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