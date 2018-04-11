package ChatClient;

import javax.crypto.ShortBufferException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;

/**
 * Client for Instant Messaging application.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class Client {
    private     String      ip;
    private     int         port;
    private     DHKeyGen    dhKeyGen;
    private     String      DHSecret;

    /**
     * Creates the client object which is used to communicate with the server.
     * @param ip IP-address of the server
     * @param port used network port on the server
     */
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Establish connection and a shared secret with the server.
     */
    public void startClient() {
        try {
            establishSharedKey();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (KeyGenException kge) {
            kge.getMessage();
        } finally {
            System.out.println("Success");
        }
    }

    private void establishSharedKey() throws KeyGenException, IOException {
        byte[] message;
        byte[] tosend;
        int length;

        Socket socket = new Socket(ip, port);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        length = in.readInt();
        if (length > 0) {
            message = new byte[length];
            in.readFully(message, 0, message.length);

            dhKeyGen = new DHKeyGen(message);
            try {
                dhKeyGen.doPhase();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        tosend = dhKeyGen.getBobPubKeyEnc();
        out.writeInt(tosend.length);
        out.write(tosend);

        try {
            this.DHSecret = dhKeyGen.getSecretHex(in.readInt());
            System.out.println(this.DHSecret);

        } catch (ShortBufferException e) {
            e.printStackTrace();
        }
    }
}
