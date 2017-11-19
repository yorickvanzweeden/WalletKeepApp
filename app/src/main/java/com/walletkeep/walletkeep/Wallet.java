package com.walletkeep.walletkeep;

import java.util.HashMap;

abstract class Wallet {
    private HashMap<Currency, Float> assets;

    public enum WalletType {
        EXCHANGE, NAKED
    }

    protected WalletType walletType;

    /**
     * Gets the type of the Wallet
     * @return WalletType (Exchange or Naked)
     */
    public WalletType getWalletType() {
        return this.walletType;
    }


    /**
     * Get a list of currencies and amounts in the wallet
     * @return List of currencies and amount pairs
     */
    public HashMap<Currency, Float> getAssets() {
        return this.assets;
    }

    /**
     * Gets the asset amount using the currency
     * @param currency Currency of the asset
     * @return Amount of the asset
     */
    public float getAssetAmount(Currency currency) {
        return this.assets.get(currency);
    }

    /**
     * Sets the amount of an asset (currency, amount)
     * @param currency Currency of the asset
     * @param amount Amount of the asset
     */
    protected void setAsset(Currency currency, float amount) {
        this.assets.put(currency, amount);
    }

    /**
     * Update the coins (currency-value pairs)
     */
    public abstract void updateAssets();
}
