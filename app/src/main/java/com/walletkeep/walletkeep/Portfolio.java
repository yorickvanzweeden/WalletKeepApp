package com.walletkeep.walletkeep;


import java.util.List;

class Portfolio {
    private List<Wallet> wallets;

    /**
     * Constructor: Initialize portfolio
     */
    public Portfolio() {
    }

    /**
     * Add naked wallet
     * @param address Address of the wallet
     */
    public void addWallet(String address) {
        //TODO: Check for duplicates
        wallets.add(new NakedWallet(address));
    }

    /**
     * Add exchange wallet
     * @param exchange Exchange of the wallet
     */
    public void addWallet(Exchange exchange) {
        //TODO: Check for duplicates
        wallets.add(new ExchangeWallet(exchange));
    }

    /**
     * Returns list of wallets
     * @return List of wallets
     */
    public List<Wallet> getWallets() {
        return this.wallets;
    }

    /**
     * Update all assets in all wallets
     */
    public void updateWallets() {
        for (Wallet wallet:wallets) wallet.updateAssets();
    }

}
