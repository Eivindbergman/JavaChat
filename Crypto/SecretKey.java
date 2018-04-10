package Crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by beej15 on on 2/23/18
 */
public class SecretKey {
    public String ENCRYTPION_ALGORITHM = "AES";

    private Cipher       cipher         = null;
    private Key          secretKey      = null;
    private KeyGenerator keyGenerator   = null;

    public SecretKey() {
        try {
            keyGenerator = KeyGenerator.getInstance(this.ENCRYTPION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(128);

        javax.crypto.SecretKey sKey = keyGenerator.generateKey();
        this.secretKey = sKey;
    }

    public Key getSecretKey() {
        return this.secretKey;
    }

}
