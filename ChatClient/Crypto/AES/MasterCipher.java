package ChatClient.Crypto.AES;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/16/18
 */
public class MasterCipher {

    //private final   int             AES_KEY_SIZE       = 128;
    private final   String          instance           = "AES/GCM/NoPadding";
    private final   String          provider           = "BC";
    private         Cipher          encryptionCipher   = null;
    private         Cipher          decryptionCipher   = null;
    private         MasterSecret    masterSecret;

    public MasterCipher(MasterSecret secret) {
        try {
            encryptionCipher    = Cipher.getInstance(instance, provider);
            decryptionCipher    = Cipher.getInstance(instance, provider);
            masterSecret        = secret;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String plainText) {
        byte[] cipherText = null;
        try {
            //cipherText = encryptBytes(encode(plainText.getBytes()));
            cipherText = encryptBytes(plainText.getBytes());
        } catch (BadCipherParametersException e) {
            e.getMessage();
            System.exit(1);
        }
        return cipherText;
    }

    public String decrypt(byte[] cipherText) {
        String plainText = null;
        try {
            //plainText = new String(decryptBytes(decode(cipherText)));
            plainText = new String(decryptBytes(cipherText));
        } catch (BadCipherParametersException e) {
            e.getMessage();
            System.exit(1);
        }
        return plainText;
    }

    private byte[] encryptBytes(byte[] plaintext) throws BadCipherParametersException {
        try {
            Cipher  cipher      = getEncryptionCipher(masterSecret.getSecretKey());
            byte[]  encrypted   = getEncryptedBodyWithIV(cipher, plaintext);

            return encrypted;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new BadCipherParametersException("Invalid Cipher Key.");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            throw new BadCipherParametersException("Invalid padding in plaintext.\n");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new BadCipherParametersException("Invalid blocksize.");
        }
    }

    private byte[] decryptBytes(byte[] ciphertext) throws BadCipherParametersException {
        try {
            Cipher      cipher      = getDecryptionCipher(masterSecret.getSecretKey(), ciphertext);
            byte[]      plainText   = getDecryptedIVandBody(cipher, ciphertext);

            return plainText;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new BadCipherParametersException("Invalid Cipher Key.");
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new BadCipherParametersException("Invalid algorithm parameters.");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            throw new BadCipherParametersException("Invalid padding in plaintext.\n");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new BadCipherParametersException("Invalid blocksize.");
        }
    }

    private byte[] encode(byte[] text) {
        return Base64.encode(text);
    }

    private byte[] decode(byte[] encodedBody) {
        return Base64.decode(encodedBody);
    }

    private byte[] getEncryptedBodyWithIV(Cipher cipher, byte[] encrypted) throws IllegalBlockSizeException, BadPaddingException {
        byte[] cipherText   = cipher.doFinal(encrypted);
        byte[] iv           = cipher.getIV();

        byte[] encryptedBody = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedBody, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedBody, iv.length, cipherText.length);
        return encryptedBody;
    }

    private byte[] getDecryptedIVandBody(Cipher cipher, byte[] cipherText) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(cipherText, cipher.getBlockSize(), cipherText.length - cipher.getBlockSize());
    }

    private Cipher getEncryptionCipher(SecretKey key) throws InvalidKeyException {
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptionCipher;
    }

    private Cipher getDecryptionCipher(SecretKey secretKey, byte[] ciphertext) throws InvalidKeyException, InvalidAlgorithmParameterException {
        IvParameterSpec iv = new IvParameterSpec(ciphertext, 0, decryptionCipher.getBlockSize());
        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        return decryptionCipher;
    }

}
