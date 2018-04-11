package Crypto;

import javax.crypto.Cipher;
import java.security.*;

/**
 *
 *
 * @author beej15
 * Created on 4/11/18
 */
public class PubKey {
    private static Cipher cipher;

    private static Key[] keys;
    private static Key publicKey;
    private static Key privateKey;
    // TODO
    public PubKey() {

    }

    public static void genKeys() {
        keys = genKey();
        publicKey = keys[0];
        privateKey = keys[1];
    }

    private static Key[] genKey()  {

        Key[] keyArray = new Key[2];
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            KeyPair keyPair = keyPairGenerator.genKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        keyArray[0] = publicKey;
        keyArray[1] = privateKey;

        return keyArray;
    }

    private static void crypt(String message, int iterations) throws Exception {
        String decrypted = "";
        byte[] encrypted = encrypt(message, publicKey);
        decrypted = decrypt(encrypted, privateKey);
    }

    private static byte[] encrypt(String message, Key publicKey) throws Exception {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] ciphertext = cipher.doFinal(message.getBytes("UTF-8"));

        return ciphertext;
    }

    public static String decrypt(byte[] ciphertext, Key privateKey) throws Exception {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] text = cipher.doFinal(ciphertext);

        return new String(text);
    }

    public static Key getPrivateKey() {
        return privateKey;
    }

    public static Key getPublicKey() {
        return publicKey;
    }
}
