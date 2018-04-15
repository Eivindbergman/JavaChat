package ChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for Instant Messaging application.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class Server extends Thread {
    private     ServerSocket    serverSocket;
    private     int             port;
    private     boolean         running;
    private     Sign            signature;

    /**
     * Instatiates the Server Object.
     * @param port network port to be listening to
     */
    public Server (int port) {
        this.port = port;
        this.signature = new Sign();
    }

    /**
     * Start the listening thread.
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            this.start();
            running = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Kill server.
     */
    public void kill() {
        running = false;
        this.interrupt();
    }

    /**
     * Is called by Server.start(), start listening for sockets.
     * When sockets are accepted, they are individually assigned to their own thread.
     */
    @Override
    public void run() {
        Socket socket;
        while (running) {
            try {
                RequestHandler requestHandler = new RequestHandler( );

                socket = serverSocket.accept();
                requestHandler.setAliceSocket(socket);
                System.out.println("Accepted first client");

                socket = serverSocket.accept();
                requestHandler.setBobSocket(socket);
                System.out.println("Accepted second client");
                if (socket.isConnected()) {
                    requestHandler.startThread();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
