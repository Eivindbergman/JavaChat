package ChatServer.Crypto.ECDHE;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Handles the server side of an Elliptic-Curve Diffie-Hellman key-exchange.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class DHKeyGen {
    private final       String              ALGORITHM       = "ECDH";
    private final       String              paramSpec       = "curve25519";
    private final       String              INSTANCE        = "BC";
    private             KeyPair             keyPair;
    private             byte[]              secret;

    /**
     * Generates the same shared secret as the server.
     *
     */
    public DHKeyGen() {
        this.keyPair = generateKeys();
    }

    /**
     *
     * @return
     */
    private KeyPair generateKeys() {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, INSTANCE);

            X9ECParameters ecP = CustomNamedCurves.getByName(paramSpec);
            ECParameterSpec parameterSpec = new ECParameterSpec(ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());

            keyPairGenerator.initialize(parameterSpec, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            return keyPair;
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void generateSecret(byte[] alicePubKey) {
        try {
            X509EncodedKeySpec ks = new X509EncodedKeySpec(alicePubKey);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM, INSTANCE);

            KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM, INSTANCE);
            keyAgreement.init(this.keyPair.getPrivate());
            keyAgreement.doPhase(kf.generatePublic(ks), true);

            this.secret = keyAgreement.generateSecret();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public byte[] getSecret() {
        return this.secret;
    }

    public byte[] getPublicKey() {
        return this.keyPair.getPublic().getEncoded();
    }

    public String getALGORITHM() {
        return ALGORITHM;
    }

    public String getParamSpec() {
        return paramSpec;
    }

    public String getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Returns a hex value from a byte[].
     * @param bytes byte[] to convert.
     * @return hexadecimal value of byte[]
     */
    private static String bytesToHex(byte[] bytes) {
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
