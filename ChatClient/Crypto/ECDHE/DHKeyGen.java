package ChatClient.Crypto.ECDHE;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Handles the client side of an Elliptic-Curve Diffie-Hellman key-exchange.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class DHKeyGen {
    private final       String              ALGORITHM       = "ECDH";
    private final       String              paramSpec       = "curve25519";
    private final       String              PROVIDER        = "BC";
    private             KeyPair             keyPair;
    private             byte[]              secret;

    /**
     * Instantiate the key generation object and generate keypair.
     */
    public DHKeyGen() {
        this.keyPair = generateKeys();
    }

    /**
     * Generate keypair on instatitation. The KeyPair contains the ECDH public and private keys, i.e:
     * the private key (d; 1 <= d <= n-1) and the public key Q.
     * @return keypair containing public and private key, i.e private key (d, 1 <= d <= n-1) and a public key Q.
     */
    private KeyPair generateKeys() {
        try {
            Security.addProvider(new BouncyCastleProvider());

            X9ECParameters ecP = CustomNamedCurves.getByName(paramSpec);
            ECParameterSpec parameterSpec = new ECParameterSpec(ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
            keyPairGenerator.initialize(parameterSpec, new SecureRandom());

            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate the same secret at the server (Alice) with its public key along with the client's private key.
     * Saves the shared secret as a byte[].
     * @param pubKey the other party's public key.
     */
    public void generateSecret(byte[] pubKey) {
        try {
            X509EncodedKeySpec ks = new X509EncodedKeySpec(pubKey);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);

            KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM, PROVIDER);
            keyAgreement.init(this.keyPair.getPrivate());
            keyAgreement.doPhase(kf.generatePublic(ks), true);

            this.secret = keyAgreement.generateSecret();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns secret as a hex byte[].
     * @return byte[] of hexadecimal secret.
     */
    public byte[] getSecret() {
        //return bytesToHex(this.secret).getBytes();
        return this.secret;
    }

    /**
     * Returns the public key to send to the other party.
     * @return public key as a byte[].
     */
    public byte[] getPublicKey() {
        return this.keyPair.getPublic().getEncoded();
    }

    public byte[] getPrivateKey() {
        //return bytesToHex(this.keyPair.getPrivate().getEncoded());
        return this.keyPair.getPrivate().getEncoded();
    }

    /**
     * Returns a hex value from a byte[].
     * @param bytes byte[] to convert.
     * @return hexadecimal value of byte[]
     */
    public String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
