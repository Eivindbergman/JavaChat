package ChatClient.Crypto.AES;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Base64;

/**
 * This class is responsible for encrypting and decrypting data with AES-GCM-256
 *
 * @author beej15
 * Created on 4/16/18
 */
public class MasterCipher {
    private final   String          instance           = "AES/GCM/NoPadding";
    private final   String          provider           = "BC";
    private         Cipher          encryptionCipher;
    private         Cipher          decryptionCipher;
    private         MasterSecret    masterSecret;

    /**
     * Instatiates the MasterCipher object.
     * @param secret secret AES key derived from ECDH secret
     */
    public MasterCipher(MasterSecret secret) {
        try {
            encryptionCipher    = Cipher.getInstance(instance, provider);
            decryptionCipher    = Cipher.getInstance(instance, provider);
            masterSecret        = secret;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts and encodes the plaintext.
     * @param plainText message to be encrypted.
     * @return encoded ciphertext.
     */
    public byte[] encrypt(String plainText) {
        try {
            byte[] cipherText = encryptBytes(plainText.getBytes());
            return encode(cipherText);
        } catch (BadCipherParametersException e) {
            e.getMessage();
            System.exit(1);
        }
        return null;
    }

    /**
     * Decodes and decrypts the ciphertext.
     * @param cipherText data to decrypt.
     * @return decoded plaintext.
     */
    public String decrypt(byte[] cipherText) {
        try {
            byte[] plainText = decode(cipherText);
            return new String(decryptBytes(plainText));
        } catch (BadCipherParametersException e) {
            e.getMessage();
            System.exit(1);
        }
        return null;
    }

    /**
     * Encrypts message with IV.
     * @param plainText
     * @return Encrypted message concatenated with IV.
     * @throws BadCipherParametersException
     */
    private byte[] encryptBytes(byte[] plainText) throws BadCipherParametersException {
        try {
            Cipher cipher = getEncryptionCipher(masterSecret.getSecretKey());
            return getEncryptedBodyWithIV(cipher, plainText);

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

    /**
     * Decrypts the decoded ciphertext.
     * @param cipherText ciphertext to be decrypted.
     * @return decrypted message.
     * @throws BadCipherParametersException
     */
    private byte[] decryptBytes(byte[] cipherText) throws BadCipherParametersException {
        try {
            Cipher cipher = getDecryptionCipher(masterSecret.getSecretKey(), cipherText);
            return getDecryptedBodyAndIV(cipher, cipherText);

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

    /**
     * Encodes the encrypted ciphertext.
     * @param text to be encoded
     * @return encoded ciphertext
     */
    private byte[] encode(byte[] text) {
        return Base64.getEncoder().encode(text);

    }

    /**
     * Decodes the ciphertext before decrypting.
     * @param encodedBody to be decoded
     * @return decoded ciphertext.
     */
    private byte[] decode(byte[] encodedBody) {
        return Base64.getDecoder().decode(encodedBody);
    }

    /**
     * Adds the IV used to encrypt by concatenating it with the ciphertext.
     * @param cipher encryptionCipher
     * @param encrypted ciphertext
     * @return Concatenated IV and ciphertext:
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private byte[] getEncryptedBodyWithIV(Cipher cipher, byte[] encrypted) throws IllegalBlockSizeException, BadPaddingException {
        byte[] cipherText   = cipher.doFinal(encrypted);
        byte[] iv           = cipher.getIV();

        byte[] encryptedBody = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedBody, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedBody, iv.length, cipherText.length);
        return encryptedBody;
    }

    /**
     * Returns the decrypted message.
     * @param cipher
     * @param cipherText
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private byte[] getDecryptedBodyAndIV(Cipher cipher, byte[] cipherText) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(cipherText, cipher.getBlockSize(), cipherText.length - cipher.getBlockSize());
    }

    /**
     * The Cipher used for encrypting.
     * @param key AES Key
     * @return Cipher
     * @throws InvalidKeyException
     */
    private Cipher getEncryptionCipher(SecretKey key) throws InvalidKeyException {
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptionCipher;
    }

    /**
     * The Cipher used for decrypting.
     * @param key AES Key
     * @param cipherText data to be decrypted.
     * @return Cipher
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    private Cipher getDecryptionCipher(SecretKey key, byte[] cipherText) throws InvalidKeyException, InvalidAlgorithmParameterException {
        IvParameterSpec iv = new IvParameterSpec(cipherText, 0, decryptionCipher.getBlockSize());
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, iv);
        return decryptionCipher;
    }

}
