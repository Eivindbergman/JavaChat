package ChatClient.Crypto.AES;

import org.bouncycastle.crypto.Mac;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/16/18
 */
public class MasterSecret {
    private         SecretKey       secretKey;

    public MasterSecret(SecretKey encryptionKey) {
        this.secretKey  = encryptionKey;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

}
