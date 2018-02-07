package com.walletkeep.walletkeep.api.data;

import com.google.firebase.database.Exclude;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;

import java.math.BigDecimal;

public class Price {
    String name;
    String percent_change_7d;
    String percent_change_24h;
    String percent_change_1h;
    String price_btc;
    String price_eur;
    String price_usd;

    public Price() {

    }

    @Exclude
    CurrencyPrice getCurrencyPrice(String ticker) {
        return new CurrencyPrice(ticker,
                new BigDecimal(price_eur),
                new BigDecimal(price_usd),
                new BigDecimal(price_btc),
                Float.parseFloat(percent_change_1h),
                Float.parseFloat(percent_change_24h),
                Float.parseFloat(percent_change_7d)
        );
    }
}