package com.walletkeep.walletkeep.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class Converters {
    /**
     * Convert a balance String to a float
     * @param balance String to convert
     * @param decimalPlace The place at which to place decimal dot
     * @return Balance as float
     */
    public static BigDecimal amountToBD(String balance, int decimalPlace) {
        int split = balance.length() - decimalPlace;
        if (split < 0){
            String prefix = "";
            for(int i = split; i < 0; i++) {
                prefix = prefix.concat("0");
            }
            balance = prefix + balance;
            split = 0;
        }
        String firstPart = balance.substring(0, split);
        String lastPart = balance.substring(split, balance.length());
        return new BigDecimal(firstPart + "." + lastPart);
    }

    /**
     * Convert a user input String to a float
     * @param amount String to convert
     * @return Amount as float
     */
    public static BigDecimal userInputToBD(String amount) {
        amount = amount.replace(" ", "").replace(',', '.');

        try {
            return new BigDecimal(amount);
        } catch (NumberFormatException e) {
            return new BigDecimal(0);
        }
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
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }
}
