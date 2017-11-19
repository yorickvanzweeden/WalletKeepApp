package com.walletkeep.walletkeep;

import java.util.List;

class NakedWallet extends Wallet {
    private String address;

    /**
     * Constructor: Sets wallet type and exchange
     * @param address Address of the wallet
     */
    public NakedWallet(String address) {
        this.walletType = WalletType.NAKED;
        this.address = address;
    }

    @Override
    public void updateAssets() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
