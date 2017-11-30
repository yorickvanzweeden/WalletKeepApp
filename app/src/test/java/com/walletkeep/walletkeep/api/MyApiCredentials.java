package com.walletkeep.walletkeep.api;

import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;

/**
 * Insert your actual credentials here
 */
public class MyApiCredentials {
    public static ExchangeCredentials getGDAXCredentials() {
        return new ExchangeCredentials(
                "[YOUR KEY]",
                "[YOUR SECRET]",
                "[YOUR PASSPHRASE]"
        );
    }

    public static ExchangeCredentials getBinanceCredentials() {
        return new ExchangeCredentials(
                "[YOUR KEY]",
                "[YOUR SECRET]",
                "[YOUR PASSPHRASE]"
        );
    }

    public static String getEthereumAddress() {
        return "YOUR WALLET ADDRESS";
    }
}
