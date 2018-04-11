package Crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 *
 *
 * @author beej15
 * Created on 4/11/18
 */
public class test {

    private static Key secretKey = null;

    public static void main(String[] args) {

        // TODO
        PubKey.genKeys();
        String message = "Eivind";
        try {
            byte[] encrypted = encryptSecretKey(message.getBytes("UTF-8"));
            System.out.println(new String(encrypted));
            byte[] decrypted = decryptSecretKey(encrypted);
            System.out.println(new String(decrypted));
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static Cipher cipher = null;
    private static byte[] key = null;

    public static void createSecretKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(128);

        javax.crypto.SecretKey sKey = keyGenerator.generateKey();
        Key secretKey = sKey;
    }

    public Key getSecretKey() throws Exception {
        return secretKey;
    }

    private static byte[] encryptSecretKey(byte[] data) throws Exception {
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, PubKey.getPublicKey());
        key = cipher.doFinal(data);
        //key = cipher.doFinal(secretKey.getEncoded());
        return key;
    }

    private static byte[] decryptSecretKey(byte[] data) throws Exception {
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, PubKey.getPrivateKey());
        key = cipher.doFinal(data);
        return key;
    }

}
