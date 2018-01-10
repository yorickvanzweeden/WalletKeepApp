package com.walletkeep.walletkeep.api;

import com.walletkeep.walletkeep.db.entity.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CurrencyTickerCorrection {
    // Instance of hashMap
    private static HashMap<String, String> hashMap = null;

    /**
     * Get instance of hashMap or initialise if null
     * @return HashMap containing all ticker conversions
     */
    private static HashMap<String, String> getInstance() {
        if(hashMap == null) {
            hashMap = new HashMap<>();
            addEntries();
        }
        return hashMap;
    }

    /**
     * Adds entries to the conversions table
     *
     * This is not a json in order to optimise for performance
     */
    private static void addEntries(){
        hashMap.put("ANCC", "ANC");
        hashMap.put("BCCOIN", "BCC");
        hashMap.put("BTE", "BCN");
        hashMap.put("BTCL", "BTLC");
        hashMap.put("CND", "CDN");
        hashMap.put("COC", "COMM");
        hashMap.put("CRAFT", "CRC");
        hashMap.put("CFT", "CREDO");
        hashMap.put("DBIC", "DBIX");
        hashMap.put("DOV", "DOVU");
        hashMap.put("FIRST", "FRST");
        hashMap.put("GMC", "GRM");
        hashMap.put("MIOTA", "IOTA");
        hashMap.put("IOT", "IOTA");
        hashMap.put("NLC", "NLC2");
        hashMap.put("OCTO", "888");
        hashMap.put("QSH", "QASH");
        hashMap.put("RYC", "ROYAL");
        hashMap.put("SPOTS", "SPT");
        hashMap.put("STAR", "STR");
        hashMap.put("UNIKRN", "UKG");
        hashMap.put("PYC", "XPY");
        hashMap.put("WC", "XWC");
    }

    /**
     * Correct the ticker of a currency (if needed)
     * @param ticker Ticker to correct
     * @return (Corrected) ticker
     */
    public static String correct(String ticker) {
        getInstance();
        ticker = ticker.toUpperCase(); // Set to uppercase anyway
        String newTicker = correctString(ticker);
        return newTicker == null ? ticker : newTicker;
    }

    /**
     * Correct the ticker of a currency (if needed)
     * @param tickers Tickers to correct
     * @return (Corrected) tickers
     */
    public static List<String> correct(ArrayList<String> tickers) {
        getInstance();
        List<String> newTickers = new ArrayList<>();
        String newTicker;
        for (String ticker:tickers) {
            newTicker = correctString(ticker);
            newTickers.add(newTicker == null ? ticker : newTicker);
        }
        return newTickers;
    }

    /**
     * Correct the ticker of a currency (if needed)
     * @param currency Tickers to correct
     * @return (Corrected) currency
     */
    public static void correct(Currency currency){
        getInstance();
        correctCurrency(currency);
    }

    /**
     * Correct the ticker of a currency (if needed)
     * @param currencies Tickers to correct
     * @return (Corrected) currencies
     */
    public static void correct(List<Currency> currencies) {
        getInstance();
        for (Currency currency: currencies) correctCurrency(currency);
    }

    /**
     * Correct individual currency
     * @param currency Currency to correct (if needed)
     */
    private static void correctCurrency(Currency currency) {
        String ticker = currency.getTicker().toUpperCase();

        if(hashMap.containsKey(ticker)) {
            currency.setTicker(hashMap.get(ticker));
        } else {
            currency.setTicker(ticker); // Set to uppercase either way
        }
    }

    /**
     * Correct individual ticker
     * @param ticker Ticker to correct (if needed)
     * @return Corrected ticker string (returns null if not existent)
     */
    private static String correctString(String ticker) {
        return hashMap.get(ticker);
    }
}
