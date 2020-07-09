package com.skipperuzumaki.bastionandroid;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    public
    SecretKey Key;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public
    Crypto(File Directory) throws NoSuchAlgorithmException, IOException {
        File Data = new File(Directory.getPath() + "\\Keys.txt");
        String algorithm = "AES";
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
    byte[] Encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] array = new byte[8];
        new Random().nextBytes(array);
        for (int i = 5; i < 8; i++){
            array[i] = 127;
        }
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, Key);
        byte[] cipherText = cipher.doFinal(array);
        return cipherText;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    boolean Verify(byte[] message, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey TempKey = new SecretKeySpec(key, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, TempKey);
        byte[] result = cipher.doFinal(message);
        boolean ret = true;
        for (int i = 5; i < 8; i++){
            if (result[i] != 127){
                ret = false;
                break;
            }
        }
        return ret;
    }

}
