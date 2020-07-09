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
        byte[] array = new byte[16];
        new Random().nextBytes(array);
        for (int i = 11; i < 16; i++){
            array[i] = 0;
        }
        // TODO: CONVERSIONS
        // values between -127 and 127 inclusive convert to 0 to 255
        // if possible add two values to convert to 0 to 510
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
        for (int i = 11; i < 16; i++){
            if (result[i] != 0){
                ret = false;
                break;
            }
        }
        return ret;
    }

}
