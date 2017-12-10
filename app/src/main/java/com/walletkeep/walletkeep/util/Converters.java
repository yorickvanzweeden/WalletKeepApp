package com.walletkeep.walletkeep.util;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.max;

public class Converters {
    /**
     * Converts byte array to hex string
     * @param bytes Byte array to convert
     * @return Hex string
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Convert a balance String to a float
     * @param balance String to convert
     * @param decimalPlace The place at which to place decimal dot
     * @return Balance as float
     */
    public static Float amountToFloat(String balance, int decimalPlace) {
        int split = max(balance.length() - decimalPlace, 0);
        String firstPart = balance.substring(0, split);
        String lastPart = balance.substring(split, balance.length());
        return Float.parseFloat(firstPart + "." + lastPart);
    }

    /**
     * Convert an inputStream to string
     * @param inputStream Inputstream to convert
     * @return Inputstream as string
     */
    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }
}
