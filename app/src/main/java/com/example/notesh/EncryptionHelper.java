package com.example.notesh;

import android.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class EncryptionHelper {

    public static String getSecureHash(String password,String salt)
    {
        try
        {
            //ilość iteracji podczas generowania hasha
            int iterations = 1000;

            //hasło oraz salt
            char[] chars = password.toCharArray();
            byte[] saltBytes = salt.getBytes("UTF-8");

            PBEKeySpec spec = new PBEKeySpec(chars, saltBytes, iterations, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.encodeToString(hash,Base64.NO_WRAP);
        }catch (Exception ex)
        {
            System.out.println("getSecureHash: " + ex.toString());
            return "";
        }
    }
}
