package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.math.BigDecimal;
import java.util.List;

public class CurrencyComplete {
    @Embedded
    public List<Asset> assets;

    @Relation(parentColumn = "currency", entityColumn = "asset", entity = CurrencyPrice.class)
    public List<CurrencyPrice> prices;

    /* Nodig:
    - Currency
    - Prijs data
    - Assetverdeling onder de wallet van deze currency
     */
}
