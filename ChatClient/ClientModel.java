package ChatClient;

import ChatClient.Crypto.AES.MasterCipher;
import ChatClient.Crypto.AES.MasterSecret;
import ChatClient.Crypto.ECDHE.DHKeyGen;
import ChatClient.Crypto.ECDHE.KeyGenException;

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyException;

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
    private     MasterSecret        masterSecret;
    private     MasterCipher        masterCipher;
    private     DHKeyGen            dhKeyGen;
    private     boolean             keyEstablished = false;
    private     DataInputStream     in  = null;
    private     DataOutputStream    out = null;

    public ClientModel(String ip, int port) {
        this.ip         = ip;
        this.port       = port;
        this.dhKeyGen   = new DHKeyGen();

        this.masterSecret = establishSharedKey();
        this.masterCipher = new MasterCipher(this.masterSecret);
    }

    private MasterSecret establishSharedKey() {
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

            keyEstablished = true;
            return new MasterSecret(dhKeyGen.getSecret());

        } catch (IOException e) {
            System.out.print("\033[31mServer not responding, may be down  \033[0m\n");
            System.exit(1);
            return null;
        }
    }

    public void sendMessage(String m) throws IOException {
        try {
            byte[] message;
            m = String.format("[%s]: %s", clientName, m);
            message = masterCipher.encrypt(m);
            out.writeInt(message.length);
            out.write(message);
        } catch (IOException e) {
            throw new IOException("Could not send message, OutputStream is closed.");
        }

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

    public MasterSecret getMasterSecret() {
        return masterSecret;
    }

    public MasterCipher getMasterCipher() {
        return masterCipher;
    }
}
