package ChatClient.Crypto.AES;

import ChatClient.Crypto.HKDF.HKDF;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/14/18
 */
public class MasterSecret {
    private         byte[]          seed;
    private         SecretKey       secretKey;
    private final   String          algorithm   = "AES";
    private         HKDF            hkdf;

    public MasterSecret(byte[] seed) {
        this.seed = seed;
        generateKey();
    }

    private void generateKey() {
        hkdf = new HKDF();
        //System.out.println("SEED: " + new String(seed));
        byte[] OKM = hkdf.deriveSecret(seed, "".getBytes(), 32);
        //System.out.println("OKM KEY: " + new String(OKM));
        //System.out.println("OKM KEY HEX: " + new String(new DHKeyGen().bytesToHex(OKM)));
        //System.out.println(OKM.length);

        try {
            SecretKeySpec keySpec = new SecretKeySpec(OKM, algorithm);
            SecretKeyFactory kf = SecretKeyFactory.getInstance(algorithm);
            secretKey = kf.generateSecret(keySpec);
            //System.out.println("SEC KEY: " + new String(secretKey.getEncoded()));
            //System.out.println("SEC KEY HEX: " + new String(new DHKeyGen().bytesToHex(secretKey.getEncoded())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

}
