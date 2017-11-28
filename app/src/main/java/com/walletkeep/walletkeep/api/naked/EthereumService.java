package com.walletkeep.walletkeep.api.naked;

import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.db.entity.Coin;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;

import java.util.List;

import retrofit2.Call;


public class EthereumService extends ApiService {

    @Override
    protected Call<Coin> createCall(ExchangeCredentials exchangeCredentials) {
        return null;
    }

    @Override
    protected List<Coin> parseResponse(Call<Coin> call, List<Coin> oldCoins) {
        return null;
    }
}
