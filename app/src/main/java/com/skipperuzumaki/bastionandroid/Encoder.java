package com.skipperuzumaki.bastionandroid;

public class Encoder {
    public
    String Encode(byte[] value){
        String ret = new String();
        for (int i = 0; i < value.length; i++) {
            int j = (int) value[i];
            String st = String.format("%02X", (j + 128));
            System.out.print(st);
            System.out.print(' ');
            ret += st;
        }
        System.out.println();
        return ret.toLowerCase();
    }
    byte[] Decode(String value){
        value = value.toUpperCase();
        int len = value.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int j = (Character.digit(value.charAt(i), 16) << 4)
                    + Character.digit(value.charAt(i+1), 16) - 128;
            data[i / 2] = (byte) j;
        }
        return data;
    }
}
