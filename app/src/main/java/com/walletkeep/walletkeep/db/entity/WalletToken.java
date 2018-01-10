package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class WalletToken {
    @Embedded
    public WalletTokenWithoutAddress token;

    @Relation(parentColumn = "currency_ticker", entityColumn = "ticker", entity = Currency.class)
    public List<Currency> currency;

    // Empty constructor used by Room
    public WalletToken(){}

    /**
     * Constructor: Sets a new WalletTokenWithoutAddress
     * @param walletId Id of the wallet
     * @param currencyTicker Ticker of the currency
     */
    public WalletToken(int walletId, String currencyTicker) {
        this.token = new WalletTokenWithoutAddress();
        token.setWalletId(walletId);
        token.setCurrencyTicker(currencyTicker);
    }

    /**
     * Gets the address of the smart contract of the token
     * @return The address of the smart contract of the token
     */
    public String getAddress() {
        return (currency == null || currency.size() == 0) ? "" : currency.get(0).getTokenAddress();
    }

    /**
     * Gets the ticker of the currency the token
     * @return The ticker of the currency the token
     */
    public String getCurrencyTicker() {
        return token.getCurrencyTicker();
    }
}
