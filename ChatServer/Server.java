package ChatServer;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for Instant Messaging application.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class Server extends Thread {
    private ServerSocket    serverSocket    = null;
    private int             port;
    private boolean         running;

    /**
     * Instatiates the Server Object.
     * @param port network port to be listening to
     */
    public Server (int port) {
        this.port = port;
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
                socket = serverSocket.accept();
                if (socket.isConnected()) {
                    RequestHandler requestHandler = new RequestHandler(socket);
                    requestHandler.startThread();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
