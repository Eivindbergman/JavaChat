package ChatServer;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/15/18
 */
public class Sign {
    private final   String      instance    = "SHA256withRSA";
    private final   String      algorithm   = "RSA";
    private final   String      provider    = BouncyCastleProvider.PROVIDER_NAME;
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    private         Cipher      cipher;
    private         KeyPair     keyPair;
    private         Signature   signature;
    private         byte[]      nonce;

    public Sign() {
        genKeys();
    }

    private void genKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(2048, new SecureRandom());
            this.keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void genNonce() {
        SecureRandom random = new SecureRandom();
        this.nonce = new byte[2048];
        random.nextBytes(nonce);
    }

    public Signature createSignature() {
        try {
            signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(keyPair.getPrivate());
            //signature.update();
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
