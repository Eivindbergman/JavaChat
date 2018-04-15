package ChatClient.Crypto.HKDF;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * HMAC-SHA256 Key Derivation Function. Takes a seed as input and generates a key of a given length.
 * This function follows the "extract -and expand" paradigm, the KDF consists of two modules. The first stage extracts a prk (Pseudo random key)
 * of a fixed length from the input key. Afterwards, the expand method will expand the key into a given length by several additional PRKs.
 *
 * @author beej15
 * Created on 4/14/18
 */
public class HKDF {

    private final   int     HASH_OUTPUT_SIZE    = 32;
    private final   String  INSTANCE            = "HmacSHA256";

    /**
     * Generates key by creating an empty array of a given length and performs an extract and an expand function.
     * This method will call all other methods in this class ( extractAndExpand() -> extract() -> expand() ) and return the value from the last method.
     * @param ikm Input key material, the seed for the key, in this case, the shared Diffie-Hellman secret.
     * @param info an optional context -and application specific information. Can be empty. Although a proper info is preferred.
     * @param outputSize the size of the generated key.
     * @return the final key of a given length.
     */
    public byte[] deriveSecret(byte[] ikm, byte[] info, int outputSize) {
        byte[] salt = new byte[HASH_OUTPUT_SIZE];
        return extractAndExpand(salt, ikm, info, outputSize);
    }

    /**
     * Performs the extract and expand functions.
     * @param salt empty byte[] of a given length.
     * @param ikm Input key material, the seed of the key, in this case, the shared Diffie-Hellman secret.
     * @param info an optional context -and application specific information. Can be empty. Although a proper info is preferred.
     * @param outputSize the size of the generated key.
     * @return the final key of a given length.
     */
    private byte[] extractAndExpand(byte[] salt, byte[] ikm, byte[] info, int outputSize) {
        byte[] prk = extract(salt, ikm);
        return expand(prk, info, outputSize);
    }

    /**
     * Performs an extract function, hashing the salt (empty byte[] with a HMAC-SHA256.
     * @param salt an empty byte[] of a given length.
     * @param ikm Input key material, the seed of the key, in this case, the shared Diffie-Hellman secret.
     * @return the final key of a given length.
     */
    private byte[] extract(byte[] salt, byte[] ikm) {
        try {
            Mac mac = Mac.getInstance(INSTANCE);
            mac.init(new SecretKeySpec(salt, INSTANCE));
            return mac.doFinal(ikm);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generates the key of a given length. Iterates n + 1 times.
     * @param prk Pseudo random key, i.e, the empty byte[] hashed with HMAC-SHA256.
     * @param info an optional context -and application specific information. Can be empty. Although a proper info is preferred.
     * @param outputSize the size of the final key.
     * @return the final key.
     */
    private byte[] expand(byte[] prk, byte[] info, int outputSize) {
        try {

            int                     n       = (int) Math.ceil((double) outputSize / (double) HASH_OUTPUT_SIZE);
            byte[]                  mix     = new byte[0];
            ByteArrayOutputStream   result  = new ByteArrayOutputStream();
            int                     bytes   = outputSize;

            for (int i = 0; i < n + 1; i++) {
                Mac mac = Mac.getInstance(INSTANCE);
                mac.init(new SecretKeySpec(prk, INSTANCE));

                mac.update(mix);

                if (info.length > 0) {
                    mac.update(info);
                }
                mac.update((byte) i);

                byte[]  stepResult  = mac.doFinal();
                int     stepSize    = Math.min(bytes, stepResult.length);
                result.write(stepResult, 0, stepSize);

                mix     = stepResult;
                bytes  -= stepSize;
            }
            return result.toByteArray();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

}
