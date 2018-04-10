package ChatClient;

import java.io.*;
import java.net.Socket;

/**
 * Created by beej15 on on 2/21/18
 */
public class Client {
    private String serverip;
    private String ip = "localhost";
    private int port;

    public Client(String ip, int port) {
        this.serverip = ip;
        this.port = port;
    }

    public void startClient() {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            WriteHandler writeHandler = new WriteHandler(socket);
            writeHandler.startThread();
        } catch (Exception e) {
            System.out.println("Failed");
            System.exit(0);
        }

        try {
            while (true) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while((line = in.readLine()) != null) {
                    System.out.println("SERVER - " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class WriteHandler extends Thread {
    private Thread t;
    private Socket socket;
    WriteHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String message = "Hej Från Client!";
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startThread () {
        if (t == null) {
            System.out.println("Starting... ");
            t = new Thread (this);
            t.start ();
        }
    }
}