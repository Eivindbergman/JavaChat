package ChatClient.Crypto.AES;

import ChatClient.Crypto.HKDF.HKDF;
import Crypto.SecretKey;
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
        HKDF hkdf = new HKDF();
        byte[] temp = hkdf.deriveSecret(seed, "qäkqopkäcqockwåofkvweqkcåowkepowkecpowekc".getBytes(), 32);
        System.out.println("OKM: " + new String(temp));
        System.out.println(temp.length);
    }

}
