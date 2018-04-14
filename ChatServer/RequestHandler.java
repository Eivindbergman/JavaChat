package ChatServer;

import ChatClient.Crypto.AES.AESSecretKey;
import ChatServer.Crypto.ECDHE.DHKeyGen;

import java.io.*;
import java.net.Socket;

/**
 * Accepts sockets and handle them in individual threads.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class RequestHandler extends Thread {
    private Thread      t;
    private String      threadName = "Thread_" + (java.lang.Thread.activeCount());
    private Socket      socket;
    private DHKeyGen dhKeyGen;

    /**
     * Instatiate the RequestHandler object.
     * @param socket Socket to be handled by the RequestHandler.
     */
    RequestHandler(Socket socket) {
        this.socket = socket;
        this.dhKeyGen = new DHKeyGen();
    }

    /**
     * Start sending and receiving data streams.
     * Perform key exchange with client and store the secret
     */
    @Override
    public void run() {
        int length;
        byte[] pubKeyBytes;
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeInt(dhKeyGen.getPublicKey().length);
            out.write(dhKeyGen.getPublicKey());

            length = in.readInt();
            if (length > 0) {
                pubKeyBytes = new byte[length];
                in.readFully(pubKeyBytes, 0, pubKeyBytes.length);
                dhKeyGen.generateSecret(pubKeyBytes);
            }

            System.out.println(new String(dhKeyGen.getSecret()));
            System.out.println(dhKeyGen.getSecret().length);

            AESSecretKey aesSecretKey = new AESSecretKey(dhKeyGen.getSecret());
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