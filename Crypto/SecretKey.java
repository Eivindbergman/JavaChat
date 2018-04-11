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
public class SecretKey {
    private final String ENCRYTPION_ALGORITHM = "AES";

    private Cipher       cipher         = null;
    private Key          secretKey      = null;
    private KeyGenerator keyGenerator   = null;

    // TODO
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
