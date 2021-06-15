package com.example.notesh;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;


public class SecretKeyUtils {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void CheckAndGeneratePassword()
    {
        try {
            SecretKey key = getSecretKey();
            if (key == null)
            {
                generateSecretKey(new KeyGenParameterSpec.Builder(
                        "NoteHSecretKey",
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }
        } catch (Exception e)
        {
            System.out.println("CheckAndGeneratePassword: " + e.toString());
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(keyGenParameterSpec);
        keyGenerator.generateKey();
    }

    public static SecretKey getSecretKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");

        // Zanim uzyskamy dostęp do keystore należy go załadować.
        keyStore.load(null);
        return ((SecretKey)keyStore.getKey("NoteHSecretKey", null));
    }
    //tworzenie obiektu szyfru kryptograficznego niezbędnego przy szyfrowaniu
    public static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }
    //szyfrowanie wiadomości
    public static String encryptNote(String note, Context context)
    {
        try {


            Cipher cipher = getCipher();
            SecretKey secretKey = getSecretKey();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] iv = cipher.getIV();
            //odwołanie się do zapisanego w shared pref stringa z wektorem inicującym
            SharedPreferences sharedPref = context.getSharedPreferences("drugi", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString("wektor", Base64.encodeToString(iv, Base64.DEFAULT));
            editor.commit();

            byte[] encryptedInfo = cipher.doFinal(note.getBytes("UTF-8"));

            return Base64.encodeToString(encryptedInfo, Base64.NO_WRAP);
        }catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }

        return "";
    }
    // odszyfrowywanie wiadomości z pamięci urządzenia
    public static String decryptNote(String note, Context context)
    {
        try {
            ////odwołanie się do zapisanego w shared pref stringa
            SharedPreferences sharedPref = context.getSharedPreferences("drugi",Context.MODE_PRIVATE);
            String ivArray = sharedPref.getString("wektor","");
            byte[] iv = Base64.decode(ivArray, Base64.DEFAULT);

            Cipher cipher = getCipher();
            SecretKey secretKey = getSecretKey();
            cipher.init(Cipher.DECRYPT_MODE, secretKey,new IvParameterSpec(iv));
            return new String(cipher.doFinal(Base64.decode(note,Base64.NO_WRAP)));
        }catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }

        return "";
    }






}