package MITM;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        DataInputStream in;
        while (running) {
            try {
                socket = serverSocket.accept();
                System.out.println("\nSuccessfully intercepted TCP packet from victim.");
                in = new DataInputStream(socket.getInputStream());
                int aliceLen, bobLen;
                byte[] alicePubKey,
                        bobPubKey;
                try {

                    // Reading alice's public key.
                    aliceLen = in.readInt();
                    if (aliceLen > 0) {
                        alicePubKey = new byte[aliceLen];
                        in.readFully(alicePubKey, 0, alicePubKey.length);
                        System.out.println("Victim's public key is: " + bytesToHex(alicePubKey));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
