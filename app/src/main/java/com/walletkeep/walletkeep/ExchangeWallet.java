package com.walletkeep.walletkeep;

import java.util.List;

class ExchangeWallet extends Wallet {
    private Exchange exchange;

    /**
     * Constructor: Sets wallet type and exchange
     * @param exchange Exchange of the wallet
     */
    public ExchangeWallet(Exchange exchange) {
        this.walletType = WalletType.EXCHANGE;
        this.exchange = exchange;
    }

    @Override
    public void updateAssets() {
        exchange.getData();
        throw new UnsupportedOperationException("Not implemented");
    }
}
