package ChatClient.Crypto.HKDF;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/14/18
 */
public class HKDF {

    private final   int     HASH_OUTPUT_SIZE    = 32;
    private final   String  INSTANCE            = "HmacSHA256";

    public byte[] deriveSecret(byte[] ikm, byte[] info, int outputSize) {
        byte[] salt = new byte[HASH_OUTPUT_SIZE];
        return extractAndExpand(salt, ikm, info, outputSize);
    }

    private byte[] extractAndExpand(byte[] salt, byte[] ikm, byte[] info, int outputSize) {
        byte[] prk = extract(salt, ikm);
        return expand(prk, info, outputSize);
    }

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
    // prk, "", 32
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
                    System.out.println("dqd");
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
