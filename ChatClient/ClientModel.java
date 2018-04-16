package ChatClient;

import ChatClient.Crypto.AES.AESSecretKey;
import ChatClient.Crypto.ECDHE.DHKeyGen;

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/15/18
 */
public class ClientModel {
    private     String              ip;
    private     int                 port;
    private     String              clientName;
    private     AESSecretKey        aesSecretKey;
    private     DHKeyGen            dhKeyGen;
    private     boolean             keyEstablished = false;
    private     DataInputStream     in  = null;
    private     DataOutputStream    out = null;

    public ClientModel(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.dhKeyGen = new DHKeyGen();
        establishSharedKey();
    }

    private void establishSharedKey() {
        Socket socket;
        int     length;
        byte[]  clientNameBytes;
        byte[]  pubKeyBytes      = null;
        try {
            socket = new Socket(ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            length = in.readInt();
            if (length > 0) {
                clientNameBytes = new byte[length];
                in.readFully(clientNameBytes, 0, clientNameBytes.length);
                clientName = new String(clientNameBytes);
            }
            System.out.println("Client name is: " + clientName);
            out.writeInt(dhKeyGen.getPublicKey().length);
            out.write(dhKeyGen.getPublicKey());

            length = in.readInt();
            if (length > 0) {
                pubKeyBytes = new byte[length];
                in.readFully(pubKeyBytes, 0, pubKeyBytes.length);
            }

            dhKeyGen.generateSecret(pubKeyBytes);
            System.out.println("Shared Secret: " + new String(dhKeyGen.getSecret()));

            aesSecretKey = new AESSecretKey(dhKeyGen.getSecret());
            keyEstablished = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String m) throws IOException {
        m = clientName + ": "+ m;

        out.writeInt(m.getBytes().length);
        out.write(m.getBytes());
        System.out.println("Sending message.");
    }

    public DataInputStream getIn() throws NullPointerException {
        return in;
    }

    public DataOutputStream getOut() throws NullPointerException {
        return out;
    }

    public boolean isKeyEstablished() {
        return keyEstablished;
    }

    public String getClientName() {
        return clientName;
    }

}
