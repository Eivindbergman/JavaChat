package ChatClient.Crypto.ECDHE;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * Handles the client side of an Elliptic-Curve Diffie-Hellman key-exchange.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class DHKeyGen {
    private final       String              ALGORITHM       = "ECDH";
    private final       String              curve           = "curve25519";
    private final       String              PROVIDER        = "BC";
    private             KeyPair             keyPair;
    private             byte[]              secret;

    /**
     * Instantiate the key generation object and generate keypair.
     */
    public DHKeyGen() {
        this.keyPair = generateKeys();
        System.out.println(keyPair.getPrivate());
        System.out.println(keyPair.getPublic());
    }

    /**
     * Generate keypair on instatitation. The KeyPair contains the ECDH public and private keys, i.e:
     * the private key (d; 1 <= d <= n-1) and the public key Q.
     * @return keypair containing public and private key, i.e private key (d, 1 <= d <= n-1) and a public key Q.
     */
    private KeyPair generateKeys() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            X9ECParameters  ecParameters    = CustomNamedCurves.getByName(curve);
            ECParameterSpec ecParameterSpec = new ECParameterSpec(  ecParameters.getCurve(),
                                                                    ecParameters.getG(),
                                                                    ecParameters.getN(),
                                                                    ecParameters.getH(),
                                                                    ecParameters.getSeed());

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
            keyPairGenerator.initialize(ecParameterSpec, new SecureRandom());
            return keyPairGenerator.generateKeyPair();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate the same secret as the other client with their public key along with this client's private key.
     * Saves the shared secret as a byte[].
     * @param pubKey the other party's public key.
     */
    public void generateSecret(byte[] pubKey) {
        try {
            ECParameterSpec parameterSpec   = ECNamedCurveTable.getParameterSpec(curve);
            ECPublicKeySpec publicKeySpec   = new ECPublicKeySpec(parameterSpec.getCurve().decodePoint(pubKey), parameterSpec);

            KeyFactory keyFactory      = KeyFactory.getInstance(ALGORITHM, PROVIDER);

            KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM, PROVIDER);
            keyAgreement.init(keyPair.getPrivate());
            keyAgreement.doPhase(keyFactory.generatePublic(publicKeySpec), true);

            secret = keyAgreement.generateSecret();
            System.out.println(bytesToHex(secret));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns secret.
     * @return byte[].
     */
    public byte[] getSecret() {
        return this.secret;
    }

    /**
     * Returns the public key to send to the other party.
     * @return public key as a byte[].
     */
    public byte[] getPublicKey() {
        ECPublicKey key = (ECPublicKey) this.keyPair.getPublic();
        return key.getQ().getEncoded(true);
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
