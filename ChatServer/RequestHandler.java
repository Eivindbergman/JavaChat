package ChatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    private DHKeyGen    dhKeyGen;
    private byte[]      pubKeyEnc;
    private String      DHSecret;

    /**
     * Start sending and receiving data streams.
     * Perform key exchange with client and store the secret
     */
    @Override
    public void run() {
        try {
            byte[] message;
            int length;
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeInt(pubKeyEnc.length);
            out.write(pubKeyEnc);

            length = in.readInt();
            if (length > 0) {
                message = new byte[length];
                in.readFully(message, 0, message.length);

                dhKeyGen.doPhase(message);
            }

            this.DHSecret = dhKeyGen.getSecretHex();
            out.writeInt(dhKeyGen.getAliceLen());

            System.out.println(this.DHSecret);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem with key agreement. Could not generate secret.");
        }
    }


    /**
     * Instatiate the RequestHandler object.
     * @param socket Socket to be handled by the RequestHandler.
     */
    RequestHandler(Socket socket) {
        this.socket = socket;
        try {
            this.dhKeyGen = new DHKeyGen();
            this.pubKeyEnc = dhKeyGen.getAlicePubKeyEnc();
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