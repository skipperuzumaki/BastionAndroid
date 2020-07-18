package com.skipperuzumaki.bastionandroid;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private SecretKey Key;
    private byte[] nonce = new byte[12];
    public
    Crypto(File Directory) throws NoSuchAlgorithmException, IOException {
        for (int i = 0; i < 12; i++){
            nonce[i] = 127;
        }
        File Data = new File(Directory.getPath() + "\\Keys.txt");
        String algorithm = "ChaCha20";
        if (Data.exists()){
            byte[] Keygen = Files.readAllBytes(Data.toPath());
            Key = new SecretKeySpec(Keygen, algorithm);
        }
        else{
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            Key = keyGen.generateKey();
            byte[] keyBytes = Key.getEncoded();
            FileOutputStream stream = new FileOutputStream(Data.getPath());
            stream.write(keyBytes);
            stream.close();
        }
    }

    public
    byte[] Encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        byte[] array = new byte[13];
        new Random().nextBytes(array);
        for (int i = 9; i < 13; i++){
            array[i] = 127;
        }
        Cipher cipher = Cipher.getInstance("ChaCha20");
        IvParameterSpec iv = new IvParameterSpec(nonce);
        cipher.init(Cipher.ENCRYPT_MODE, Key, iv);
        return cipher.doFinal(array);
    }

    boolean Verify(byte[] message, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        SecretKey TempKey = new SecretKeySpec(key, "ChaCha20");
        Cipher cipher = null;
        cipher = Cipher.getInstance("ChaCha20");
        IvParameterSpec iv = new IvParameterSpec(nonce);
        cipher.init(Cipher.DECRYPT_MODE, TempKey, iv);
        byte[] result = cipher.doFinal(message);
        boolean ret = true;
        for (int i = 9; i < 13; i++){
            if (result[i] != 127){
                ret = false;
                break;
            }
        }
        return ret;
    }

}
