package com.walletkeep.walletkeep.di.module;

import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.ResponseHandler;
import com.walletkeep.walletkeep.api.exchange.BinanceService;
import com.walletkeep.walletkeep.api.exchange.BitfinexService;
import com.walletkeep.walletkeep.api.exchange.BittrexService;
import com.walletkeep.walletkeep.api.exchange.GDAXService;
import com.walletkeep.walletkeep.api.exchange.KrakenService;
import com.walletkeep.walletkeep.api.exchange.KucoinService;
import com.walletkeep.walletkeep.api.naked.ArkService;
import com.walletkeep.walletkeep.api.naked.BlockcypherService;
import com.walletkeep.walletkeep.api.naked.EtherscanService;
import com.walletkeep.walletkeep.api.naked.NeoService;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.scope.AppScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ApiServiceModule {

    private final WalletWithRelations walletWithRelations;
    private final ResponseHandler.ResponseListener listener;

    public ApiServiceModule(WalletWithRelations context, ResponseHandler.ResponseListener listener) {
        this.walletWithRelations = context;
        this.listener = listener;
    }

    @Provides
    public WalletWithRelations getWalletWithRelations() {
        return walletWithRelations;
    }

    @Provides
    public ResponseHandler.ResponseListener getListener() {
        return listener;
    }

    @AppScope @Provides
    public ApiService apiService(WalletWithRelations wr, ResponseHandler.ResponseListener listener){
        ApiService apiService;

        // Pick right ApiService
        if (wr.getType() == WalletWithRelations.Type.Exchange) {
            apiService = createExchangeApiService(wr.getExchangeName());
        } else {
            apiService = createNakedApiService(wr.getAddressCurrency());
        }

        // Set internal parameters
        apiService.setParameters(wr.getCredentials(), wr.getAddress(), wr.wallet.getId(), new ResponseHandler(listener));

        return apiService;
    }

    /**
     * Picks right ApiService for exchange
     * @param exchangeName Exchange
     * @param <T> ApiService
     * @return Exchange ApiService
     */
    private <T extends ApiService> T createExchangeApiService(String exchangeName){
        switch (exchangeName){
            case "Binance":
                return (T) new BinanceService();
            case "Bitfinex":
                return (T) new BitfinexService();
            case "Bittrex":
                return (T) new BittrexService();
            case "GDAX":
                return (T) new GDAXService();
            case "Kraken":
                return (T) new KrakenService();
            case "Kucoin":
                return (T) new KucoinService();
            default:
                return null;
        }
    }

    /**
     * Picks right ApiService for address
     * @param currency Currency
     * @param <T> ApiService
     * @return Exchange ApiService
     */
    private <T extends ApiService> T createNakedApiService(String currency){
        switch (currency){
            case "ARK":
                return (T) new ArkService();
            case "ETH2":
                return (T) new BlockcypherService();
            case "ETH":
                return (T) new EtherscanService();
            case "NEO":
                return (T) new NeoService();
            default:
                return null;
        }
    }

}