package com.skipperuzumaki.bastionandroid;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UuidHelper {
    String Generate(String Data){
        String uuid = Data.substring(0, 8) + "-" +
                Data.substring(8, 12) + "-4" +
                Data.substring(12, 15) + "-8" +
                Data.substring(15, 18) + "-" +
                Data.substring(18) + "trac";
        return uuid;
    }

    String Recouperate(String uuid){
        String regex = "([a-f0-9]{8})-([a-f0-9]{4})-4([a-f0-9]{3})-8([a-f0-9]{3})-([a-f0-9]{8})trac";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(uuid.toLowerCase());
        if (m.find()) {
            String hexData = m.group(1) + m.group(2) + m.group(3) + m.group(4) + m.group(5);
            return hexData;
        }
        else {
            return "";
        }
    }
}
