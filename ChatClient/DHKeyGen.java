package ChatClient;

import javax.crypto.KeyAgreement;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * Handles the client side of a Diffie-Hellman key-exchange.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class DHKeyGen {
    private final       String              ALGORITHM = "DH";
    private             byte[]              bobPubKeyEnc;
    private             byte[]              sharedSecret;
    private             KeyAgreement        bobKeyAgree;
    private             PublicKey           alicePubKey;

    /**
     * Generates the same shared secret as the server.
     * @throws KeyGenException KeyGen exceptions. As we only use hardcoded parameters for the algorithms, the only problem would be a network-related one.
     */
    public DHKeyGen(byte[] alicePubKeyEnc) throws KeyGenException {
        this.bobPubKeyEnc = bobKeyGen(alicePubKeyEnc);
    }

    /**
     * When the client receives the public key from the server, it creates the same instance of Diffie-Hellman and creates it's own keypair.
     * The client then initializes a key agreement object and sends the public key to the server.
     * @param alicePubKeyEnc server's encoded public key
     * @return bobPubKeyEnc client's encoded public key
     * @throws KeyGenException KeyGen exceptions. As we only use hardcoded parameters for the algorithms, the only problem would be a network-related one.
     */
    private byte[] bobKeyGen(byte[] alicePubKeyEnc) throws KeyGenException {
        try {
            final KeyFactory bobKeyFac = KeyFactory.getInstance(ALGORITHM);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(alicePubKeyEnc);
            alicePubKey = bobKeyFac.generatePublic(keySpec);

            final DHParameterSpec dhParameterSpecAPubKey = ((DHPublicKey) alicePubKey).getParams();
            final KeyPairGenerator bobKeyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
            bobKeyPairGen.initialize(dhParameterSpecAPubKey);
            final KeyPair bobKeyPair = bobKeyPairGen.generateKeyPair();

            bobKeyAgree = KeyAgreement.getInstance(ALGORITHM);
            bobKeyAgree.init(bobKeyPair.getPrivate());

            bobPubKeyEnc = bobKeyPair.getPublic().getEncoded();

            return bobPubKeyEnc;
        } catch (Exception e) {
            throw new KeyGenException("Error when generating key.");
        }
    }

    /**
     * Instatiate the server's public key.
     * @throws InvalidKeyException java.security exceptions. As we only use hardcoded parameters for the algorithms, the only problem would be a network-related one.
     */
    public void doPhase() throws InvalidKeyException {
        bobKeyAgree.doPhase(alicePubKey, true);
    }

    /**
     * When the client receives the length of the shared secret, it can be generated on both sides,
     * thus having the exact same key without ever compromising it.
     * @param aliceLen length of shared secret
     * @return sharedSecret Shared Diffie-Hellman secret between the client and server.
     * @throws ShortBufferException javax.crypto exceptions. As we only use hardcoded parameters for the algorithms, the only problem would be a network-related one.
     */
    private byte[] generateSecret(int aliceLen) throws ShortBufferException {
        sharedSecret = new byte[aliceLen];
        int bobLen = bobKeyAgree.generateSecret(sharedSecret, 0);

        return sharedSecret;
    }

    /**
     * Returns the client's public key to send to the server.
     * @return client public key
     */
    public byte[] getBobPubKeyEnc() {
        return bobPubKeyEnc;
    }

    /**
     * Returns the Diffie-Hellman secret as a hexadecimal String.
     * @param aliceLen length of shared secret
     * @return hexadecimal string of the Diffie-Hellman secret
     * @throws ShortBufferException javax.crypto exceptions. As we only use hardcoded parameters for the algorithms, the only problem would be a network-related one.
     */
    public String getSecretHex(int aliceLen) throws ShortBufferException {
        return bytesToHex(generateSecret(aliceLen));
    }

    /**
     * Returns a hex value from a byte[].
     * @param bytes byte[] to convert.
     * @return hexadecimal value of byte[]
     */
    private static String bytesToHex(byte[] bytes) {
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
