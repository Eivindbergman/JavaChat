package ChatClient.Crypto.AES;

import ChatClient.Crypto.HKDF.HKDF;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

/**
 * Holds the key for assymmetric cryptography.
 *
 * @author beej15
 * Created on 4/14/18
 */
public class MasterSecret {
    private         byte[]          seed;
    private         SecretKey       secretKey;
    private final   String          algorithm   = "AES";
    private         HKDF            hkdf;

    /**
     * Instatiate the MasterSecret object and generate a key from the shared secret.
     * @param seed shared ECDH secret.
     */
    public MasterSecret(byte[] seed) {
        this.seed = seed;
        generateKey();
    }

    /**
     * Use a Key Derivation Function to derive a key.
     */
    private void generateKey() {
        try {
            hkdf = new HKDF();
            byte[] OKM = hkdf.deriveSecret(seed, "".getBytes(), 32);
            SecretKeySpec keySpec = new SecretKeySpec(OKM, algorithm);
            SecretKeyFactory kf = SecretKeyFactory.getInstance(algorithm);
            secretKey = kf.generateSecret(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the secret key for encryption and decryption.
     * @return SecretKey
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }

}
