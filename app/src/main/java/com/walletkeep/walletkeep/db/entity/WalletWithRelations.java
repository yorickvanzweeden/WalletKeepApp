package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class WalletWithRelations {
    @Embedded
    public Wallet wallet;

    @Relation(parentColumn = "id", entityColumn = "wallet_id", entity = ExchangeCredentials.class)
    public List<ExchangeCredentials> exchangeCredentials;

    @Relation(parentColumn = "id", entityColumn = "wallet_id", entity = Asset.class)
    public List<Asset> assets;

    @Relation(parentColumn = "id", entityColumn = "wallet_id", entity = WalletTokenWithoutAddress.class)
    public List<WalletToken> tokens;

    // Direct calls
    public enum Type {
        Naked(0),
        Exchange(1),
        Transaction(2);

        private final int value;
        Type(int value) {
            this.value = value;
        }

        public int getValue() { return value; }
    }

    /**
     * Returns the type of the wallet
     * @return Exchange or Naked
     */
    public Type getType() {
        return Type.values()[this.wallet.getType()];
    }

    /**
     * Gets the name of the wallet
     * @return Name of the wallet (null if not present)
     */
    public String getWalletName() {
        return this.wallet.getName();
    }

    /**
     * Gets the name of the exchange of the wallet
     * @return Name of the exchange (null if not present)
     */
    public String getExchangeName() {
        return this.wallet.getExchangeName();
    }

    /**
     * Gets the address of the wallet
     * @return Address of the wallet (null if empty)
     */
    public String getAddress() {
        return this.wallet.getAddress();
    }

    /**
     * Gets the currency of the address
     * @return Currency of the address
     */
    public String getAddressCurrency() {
        return this.wallet.getAddressCurrency();
    }

    /**
     * Gets the exchange credentials related to the wallet
     * @return Exchange credentials (null if not present)
     */
    public ExchangeCredentials getCredentials() {
        return this.exchangeCredentials == null || this.exchangeCredentials.size() == 0 ?
                null : this.exchangeCredentials.get(0);
    }

    /**
     * Returns the portfolio id of the wallet
     * @return Portfolio id of the wallet
     */
    public int getPortfolioId() {
        return this.wallet.getPortfolioId();
    }
}