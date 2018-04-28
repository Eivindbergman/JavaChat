package ChatClient;

import ChatClient.Crypto.AES.MasterCipher;
import ChatClient.Crypto.AES.MasterSecret;
import ChatClient.Crypto.ECDHE.DHKeyGen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Model part for the chatclient of the MVC.
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

    /**
     * Instatiate the model
     * @param ip ip address of server.
     * @param port port that the server is listening on.
     */
    public ClientModel(String ip, int port) {
        this.ip         = ip;
        this.port       = port;
        this.dhKeyGen   = new DHKeyGen();

        this.masterSecret = establishSharedKey();
        this.masterCipher = new MasterCipher(this.masterSecret);
    }

    /**
     * Connect to server and perform Elliptic Curve Diffie-Hellman key-exchange with another client.
     * @return MasterSecret object holds the symmetric-key used for message encryption.
     */
    private MasterSecret establishSharedKey() {
        Socket socket;
        int     length;
        byte[]  clientNameBytes;
        byte[]  pubKeyBytes      = null;
        try {
            socket = new Socket(ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected");
            length = in.readInt();
            System.out.println("Length: "+length);
            if (length > 0) {
                clientNameBytes = new byte[length];
                in.readFully(clientNameBytes, 0, clientNameBytes.length);
                clientName = new String(clientNameBytes);
            }
            System.out.println("Client name is: " + clientName);
            out.write(dhKeyGen.getPublicKey().length);
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

    /**
     * Encrypt and send message to server, which will in turn send it to client.
     * @param m message to be encrypted and sent.
     * @throws IOException if the outputstream is closed, the program can not continue.
     */
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

    /**
     * Returns the InputStream which is connected to the server.
     * @return DataInputStream
     * @throws NullPointerException
     */
    public DataInputStream getIn() throws NullPointerException {
        return in;
    }

    /**
     * Returns the OutputStream which is connected to the server.
     * @return DataOutputStream
     * @throws NullPointerException
     */
    public DataOutputStream getOut() throws NullPointerException {
        return out;
    }

    /**
     * Checks whether or not a key has been established.
     * @return boolean, true if key exists.
     */
    public boolean isKeyEstablished() {
        return keyEstablished;
    }

    /**
     * Returns the name of the client.
     * @return String.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Returns the object responsible for the symmetric key.
     * @return MasterSecret, holds shared key.
     */
    public MasterSecret getMasterSecret() {
        return masterSecret;
    }

    /**
     * Returns the object responsible for encryption and decryption, uses MasterSecret.
     * @return MasterCipher
     */
    public MasterCipher getMasterCipher() {
        return masterCipher;
    }
}
