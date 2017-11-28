package com.walletkeep.walletkeep.api.naked;

import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.db.entity.Coin;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;

import java.util.ArrayList;


public class EthereumService extends ApiService {


    public EthereumService(ArrayList<Coin> coins, CoinResponseListener listener, int walletId) {
        super(coins, listener, walletId);
    }

    @Override
    public void fetch(ExchangeCredentials exchangeCredentials) {

    }
}
