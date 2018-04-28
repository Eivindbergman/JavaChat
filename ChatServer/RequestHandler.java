package ChatServer;

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
    private Socket      aliceSocket;
    private Socket      bobSocket;
    DataInputStream     aliceIn;
    DataOutputStream    aliceOut;
    DataInputStream     bobIn;
    DataOutputStream    bobOut;

    /**
     * Instatiate the RequestHandler object.
     */
    RequestHandler() {}

    public void setAliceSocket(Socket aliceSocket) {
        this.aliceSocket = aliceSocket;
    }

    public void setBobSocket(Socket bobSocket) {
        this.bobSocket = bobSocket;
    }

    /**
     * Start sending and receiving data streams.
     * Perform key exchange with client and store the secret
     */
    @Override
    public void run() {
        try {
            aliceIn     = new DataInputStream(aliceSocket.getInputStream());
            aliceOut    = new DataOutputStream(aliceSocket.getOutputStream());
            bobIn       = new DataInputStream(bobSocket.getInputStream());
            bobOut      = new DataOutputStream(bobSocket.getOutputStream());
            establishSharedSecret(aliceIn, aliceOut, bobIn, bobOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 0;
        while (true) {
            byte[] message;
            int aliceLen, bobLen;
            try {
                if (i == 0) {
                    aliceLen    = aliceIn.readInt();
                    if (aliceLen > 0) {
                        message = new byte[aliceLen];
                        aliceIn.readFully(message, 0, message.length);
                        bobOut.writeInt(message.length);
                        bobOut.write(message);
                        bobOut.flush();
                        i++;
                    }
                } else {
                    bobLen      = bobIn.readInt();
                    if (bobLen > 0) {
                        message = new byte[bobLen];
                        bobIn.readFully(message, 0, message.length);
                        aliceOut.writeInt(message.length);
                        aliceOut.write(message);
                        aliceOut.flush();
                        i--;
                    }
                }
            } catch (EOFException e) {
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void establishSharedSecret(DataInputStream aliceIn, DataOutputStream aliceOut, DataInputStream bobIn, DataOutputStream bobOut) {
        int aliceLen, bobLen;
        byte[] alicePubKey,
                bobPubKey;
        try {

            //System.out.println((InetSocketAddress)aliceSocket.getRemoteSocketAddress());
            //System.out.println((InetSocketAddress)bobSocket.getRemoteSocketAddress());

            byte[] alice = "Alice".getBytes();
            aliceOut.write(alice.length);
            aliceOut.write(alice);

            byte[] bob = "Bob".getBytes();
            bobOut.write(bob.length);
            bobOut.write(bob);

            // Reading alice's public key.
            aliceLen = aliceIn.readInt();
            bobLen = bobIn.readInt();
            if (aliceLen > 0 && bobLen > 0) {
                alicePubKey = new byte[aliceLen];
                aliceIn.readFully(alicePubKey, 0, alicePubKey.length);
                bobOut.write(alicePubKey.length);
                bobOut.write(alicePubKey);

                bobPubKey = new byte[bobLen];
                bobIn.readFully(bobPubKey, 0, bobPubKey.length);
                aliceOut.write(bobPubKey.length);
                aliceOut.write(bobPubKey);
            }

            aliceOut.flush();
            bobOut.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the thread given to serve the communication with individual client
     */
    public void startThread () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start();
        }
    }
}