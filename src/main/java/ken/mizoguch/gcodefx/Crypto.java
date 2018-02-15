/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author mizoguch-ken
 */
public class Crypto {

    /**
     *
     * @param key
     * @param text
     * @param algorithm
     * @return
     */
    public static byte[] encrypt(String key, String text, String algorithm) {
        try {
            SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
            return encrypted;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalStateException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
        }
        return null;
    }

    /**
     *
     * @param key
     * @param encrypted
     * @param algorithm
     * @return
     */
    public static String decrypt(String key, byte[] encrypted, String algorithm) {
        try {
            SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, sksSpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return (new String(decrypted, "UTF-8"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalStateException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
        }
        return null;
    }
}
