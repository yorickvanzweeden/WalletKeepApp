package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class WalletWithRelations {
    @Embedded
    public Wallet wallet;

    @SuppressWarnings("ROOM_RELATION_TYPE_MISMATCH" )
    @Relation(parentColumn = "exchange_id", entityColumn = "id", entity = Exchange.class)
    public List<Exchange> exchanges;

    @Relation(parentColumn = "id", entityColumn = "wallet_id", entity = ExchangeCredentials.class)
    public List<ExchangeCredentials> exchangeCredentials;

    @Relation(parentColumn = "id", entityColumn = "wallet_id", entity = Asset.class)
    public List<Asset> assets;


    // Direct calls
    public enum Type { Naked, Exchange }

    public Exchange getExchange(){
        return this.exchanges == null || this.exchanges.size() == 0 ? null : this.exchanges.get(0);
    }

    public Type getType() {
        return this.exchanges == null || this.exchanges.size() == 0 ? Type.Naked : Type.Exchange;
    }

    public String getAddress() {
        return this.wallet.getAddress();
    }
    public String getAddressCurrency() { return this.wallet.getAddressCurrency(); }

    public ExchangeCredentials getCredentials() {
        return this.exchangeCredentials.size() == 0 ? null : this.exchangeCredentials.get(0);
    }

    public int getPortfolioId() {
        return this.wallet.getPortfolioId();
    }
}