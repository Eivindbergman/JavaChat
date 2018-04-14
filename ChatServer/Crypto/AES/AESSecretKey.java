package ChatServer.Crypto.AES;

import Crypto.SecretKey;

import java.security.MessageDigest;
import java.security.spec.KeySpec;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/14/18
 */
public class AESSecretKey {
    private         byte[]      seed;
    private         KeySpec     keySpec;
    private         SecretKey   secretKey;
    private final   String      instance    = "SHA-256";

    public AESSecretKey(byte[] seed) {
        this.seed = seed;
        generateKey();
    }

    private void generateKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance(instance);
            byte[] hash = digest.digest(seed);
            System.out.println("Hash: \n" + new String(hash));
            System.out.println("Hash length: " + hash.length);
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

}
