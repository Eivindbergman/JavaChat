package ChatServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by beej15 on on 2/21/18
 */
public class Server extends Thread {
    private ServerSocket    serverSocket    = null;
    private int             port;
    private boolean         running;

    public Server (int port) {
        this.port = port;
    }

    public void startServer() {

        try {
            serverSocket = new ServerSocket(port);
            this.start();
            running = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        Socket socket;
        while (running) {
            try {
                socket = serverSocket.accept();
                if (socket.isConnected()) {
                    RequestHandler requestHandler = new RequestHandler(socket);
                    requestHandler.startThread();
                    WriteHandler writeHandler = new WriteHandler(socket);
                    writeHandler.startThread();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

class RequestHandler extends Thread {
    private Thread t;
    private String threadName = "Thread(read)_" + (java.lang.Thread.activeCount() -3);
    private Socket socket;
    RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while((line = in.readLine()) != null) {
                System.out.println("CLIENT - " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startThread () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start();
        }
    }
}

class WriteHandler extends Thread {
    private Thread t;
    private String threadName = "Thread(write)_" + (java.lang.Thread.activeCount() -3);
    private Socket socket;
    WriteHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String message = "Hej Från server!";
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startThread () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start();
        }
    }
}