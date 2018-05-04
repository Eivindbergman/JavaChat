package ChatClient.tests;

import ChatClient.Crypto.AES.MasterCipher;
import ChatClient.Crypto.AES.MasterSecret;
import ChatClient.Crypto.ECDHE.DHKeyGen;
import ChatClient.Crypto.HKDF.HKDF;

import java.util.ArrayList;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/16/18
 */
public class cryptoTest {
    final static String toBeEncrypted = "This is a message!¤#%&&/(=)=??`~ˇ**ÄÄ>ØLPÅpååö;;¦¦|¦";
    public static void main(String[] args) {
        DHKeyGen dhKeyGen1 = new DHKeyGen();
        DHKeyGen dhKeyGen2 = new DHKeyGen();
        dhKeyGen1.generateSecret(dhKeyGen2.getPublicKey());
        dhKeyGen2.generateSecret(dhKeyGen1.getPublicKey());

        System.out.println(new String(dhKeyGen1.getSecret()));
        System.out.println(new String(dhKeyGen2.getSecret()));

        byte[] secret = dhKeyGen1.getSecret();

        MasterSecret masterSecret = new MasterSecret(secret);
        MasterCipher masterCipher = new MasterCipher(masterSecret);

        byte[] enc = masterCipher.encrypt(toBeEncrypted);
        secret = dhKeyGen2.getSecret();
        MasterSecret masterSecret1 = new MasterSecret(secret);
        MasterCipher masterCipher1 = new MasterCipher(masterSecret1);
        String dec = masterCipher1.decrypt(enc);
        if (dec.equals(toBeEncrypted)) {
            System.out.println(dec);
            System.out.println("Decrypted text equal original message");
        } else {
            throw new AssertionError("Decrypted text does not equal original message");
        }

        ArrayList<String> arrayList = new ArrayList<>();

        byte[]  ikm     = "DQOWWQVW87Q43IV812QV2735Q2N3OQ54O7834V".getBytes();
        byte[]  info    = "".getBytes();
        int     length  = 32;
        String kdf;
        for (int i = 1; i < 1000; i++) {
            HKDF hkdf = new HKDF();
            kdf = new String(hkdf.deriveSecret(ikm, info, length));
            arrayList.add(kdf);
        }
        for (int i = 0; i < arrayList.size()-1; i++) {
            if (!(arrayList.get(i).equals(arrayList.get(i+1)))) {
                throw new AssertionError("Arrays do not equal");
            }
        }
        System.out.println("Arrays are equal");
    }
}
