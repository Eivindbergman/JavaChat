package ChatClient.tests;

import ChatClient.Crypto.AES.AESSecretKey;
import ChatClient.Crypto.AES.MasterCipher;
import ChatClient.Crypto.AES.MasterSecret;
import ChatClient.Crypto.ECDHE.DHKeyGen;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/16/18
 */
public class crypto {
    public static void main(String[] args) {
        DHKeyGen dhKeyGen1 = new DHKeyGen();
        DHKeyGen dhKeyGen2 = new DHKeyGen();
        dhKeyGen1.generateSecret(dhKeyGen2.getPublicKey());
        dhKeyGen2.generateSecret(dhKeyGen1.getPublicKey());

        System.out.println(new String(dhKeyGen1.getSecret()));
        System.out.println(new String(dhKeyGen2.getSecret()));

        byte[] secret = dhKeyGen1.getSecret();

        AESSecretKey aesSecretKey = new AESSecretKey(secret);
        MasterSecret masterSecret = new MasterSecret(aesSecretKey.getSecretKey());
        MasterCipher masterCipher = new MasterCipher(masterSecret);

        byte[] enc = masterCipher.encrypt("Hej jag heter eivind");

        secret = dhKeyGen2.getSecret();

        AESSecretKey aesSecretKey1 = new AESSecretKey(secret);
        MasterSecret masterSecret1 = new MasterSecret(aesSecretKey1.getSecretKey());
        MasterCipher masterCipher1 = new MasterCipher(masterSecret1);

        String dec = masterCipher1.decrypt(enc);
        System.out.println(dec);
    }
}
