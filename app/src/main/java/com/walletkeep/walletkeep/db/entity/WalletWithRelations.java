package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class WalletWithRelations {
    @Embedded
    public Wallet wallet;

    @Relation(parentColumn = "exchange_id", entityColumn = "id", entity = Exchange.class)
    public List<Exchange> exchanges;

    @Relation(parentColumn = "id", entityColumn = "wallet_id", entity = ExchangeCredentials.class)
    public List<ExchangeCredentials> exchangeCredentials;

    @Relation(parentColumn = "id", entityColumn = "wallet_id", entity = Coin.class)
    public List<Coin> coins;
}