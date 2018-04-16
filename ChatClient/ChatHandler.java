package ChatClient;

import ChatClient.Crypto.AES.MasterCipher;
import ChatClient.GUI.View;

import java.io.*;

/**
 * Accepts sockets and handle them in individual threads.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class ChatHandler extends Thread {
    private Thread          t;
    private String          threadName = "Thread_" + (java.lang.Thread.activeCount());
    private DataInputStream in;
    private View            view;
    private MasterCipher    masterCipher;
    /**
     * Instatiate the RequestHandler object.
     */
    public ChatHandler(View view, MasterCipher masterCipher) {
        this.view           = view;
        this.masterCipher   = masterCipher;
    }

    public void setInputStream(DataInputStream in) {
        this.in = in;
    }

    /**
     * Start sending and receiving data streams.
     * Perform key exchange with client and store the secret
     */
    @Override
    public void run() {
        int length;
        byte[] message;
        while (true) {
            try {
                length = in.readInt();
                System.out.println("Reading message");
                if (length > 0) {
                    message = new byte[length];
                    in.readFully(message, 0, message.length);
                    view.showMessage("\n"+ masterCipher.decrypt(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
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
