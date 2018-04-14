package ChatClient;

import ChatClient.Crypto.AES.AESSecretKey;
import ChatClient.Crypto.ECDHE.DHKeyGen;

import java.io.*;
import java.net.Socket;

/**
 * Client for Instant Messaging application.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class Client {
    private     String      ip;
    private     int         port;
    private DHKeyGen dhKeyGen;

    /**
     * Creates the client object which is used to communicate with the server.
     * @param ip IP-address of the server
     * @param port used network port on the server
     */
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.dhKeyGen = new DHKeyGen();
    }

    /**
     * Establish connection and a shared secret with the server.
     */
    public void startClient() {
        establishSharedKey();
    }

    private void establishSharedKey() {
        Socket socket;
        int length;
        byte[] pubKeyBytes;
        try {
            socket = new Socket(ip, port);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            length = in.readInt();
            if (length > 0) {
                pubKeyBytes = new byte[length];
                in.readFully(pubKeyBytes, 0, pubKeyBytes.length);
                dhKeyGen.generateSecret(pubKeyBytes);
            }

            out.writeInt(dhKeyGen.getPublicKey().length);
            out.write(dhKeyGen.getPublicKey());

            System.out.println(new String(dhKeyGen.getSecret()));
            System.out.println(dhKeyGen.getSecret().length);

            AESSecretKey aesSecretKey = new AESSecretKey(dhKeyGen.getSecret());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
