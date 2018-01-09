package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class WalletTokenA{
    @Embedded
    public WalletToken token;

    @Relation(parentColumn = "currency_ticker", entityColumn = "ticker", entity = Currency.class)
    public List<Currency> currency;

    public String getAddress() {
        return (currency == null || currency.size() == 0) ? "" : currency.get(0).getTokenAddress();
    }

    public String getCurrency() {
        return token.getCurrencyTicker();
    }
}
