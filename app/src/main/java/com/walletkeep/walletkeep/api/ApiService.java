package com.walletkeep.walletkeep.api;

import com.walletkeep.walletkeep.api.exchange.BinanceService;
import com.walletkeep.walletkeep.api.exchange.BitfinexService;
import com.walletkeep.walletkeep.api.exchange.BittrexService;
import com.walletkeep.walletkeep.api.exchange.GDAXService;
import com.walletkeep.walletkeep.api.exchange.KrakenService;
import com.walletkeep.walletkeep.api.exchange.KucoinService;
import com.walletkeep.walletkeep.api.naked.BlockcypherService;
import com.walletkeep.walletkeep.api.naked.EtherscanService;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiService {
    // Wallet specific
    protected ExchangeCredentials ec;
    protected String address;
    protected int walletId;

    // Api service helpers
    protected SignatureGeneration sg;
    protected ResponseHandler responseHandler;

    /**
     * Constructor: Sets internal parameters
     * @param walletId WalletId to use for the new assets
     */
    private void setParameters(ExchangeCredentials exchangeCredentials,
                               String address,
                               int walletId,
                               ResponseHandler responseHandler) {
        this.ec = exchangeCredentials;
        this.address = address;
        this.walletId = walletId;
        this.responseHandler = responseHandler;
        this.sg = new SignatureGeneration(responseHandler);
    }

    public void fetch() {
        if (ec == null || ec.getKey() == null || ec.getSecret() == null) {
            this.responseHandler.returnError("No credentials have been provided.");
            return;
        }
    }

    /**
     * Perform request and handle callback
     * @param responseCall Call to perform
     */
    protected void performRequest(Call responseCall) {
        performRequest(responseCall, ErrorParser.getStandard());
    }
    protected void performRequest(Call responseCall, ErrorParser errorParser){
        responseCall.enqueue(new Callback<AbstractResponse>() {
            @Override
            public void onResponse(Call<AbstractResponse> call, Response<AbstractResponse> response) {
                // Success
                if (response.code() == 200) {
                    try{ handleSuccessResponse(response); }
                    // In case a service returns 200 no matter what
                    catch (Exception e) {
                        responseHandler.returnError(response.body().handleError());
                    }
                } else {
                    // If failure, return the server error (or the error for returning that)
                    try{ responseHandler.returnError(errorParser.parse(response.errorBody().string())); }
                    catch (Exception e) { responseHandler.returnError(e.getMessage()); }
                }
            }

            public void handleSuccessResponse(Object responseObject) {
                ArrayList<com.walletkeep.walletkeep.db.entity.Asset> assets = new ArrayList<>();

                // Response may be a list or a single item
                try{
                    Response<AbstractResponse> response = (Response<AbstractResponse>) responseObject;
                    for(Asset asset: response.body().getAssets(walletId)) {
                        if (asset.getAmount() != 0) assets.add(asset);
                    }

                } catch (Exception e){
                    Response<ArrayList<AbstractResponse>> responseList = (Response<ArrayList<AbstractResponse>>) responseObject;
                    for(AbstractResponse response: responseList.body()) {
                        for(Asset asset: response.getAssets(walletId)) {
                            if (asset.getAmount() != 0) assets.add(asset);
                        }
                    }
                }
                responseHandler.returnAssets(assets);
            }

            @Override
            public void onFailure(Call<AbstractResponse> call, Throwable t) {
                responseHandler.returnError(errorParser.parse(t));
            }
        });
    }

    /**
     * Abstract response that enforces the implementation of getAssets method
     */
    public abstract class AbstractResponse {
        public abstract ArrayList<Asset> getAssets(int walletId);
        public String handleError() {
             return "Unknown error on fetching assets.";
        }
    }

    /**
     * Returns API service of the right exchange/currency
     */
    public static class Factory {
        private final WalletWithRelations wr;
        private final ResponseHandler.ResponseListener crl;

        /**
         * Constructor: Provides initializes
         * @param wr Provides exchange/address, credentials and assets
         * @param listener
         */
        public Factory(WalletWithRelations wr, ResponseHandler.ResponseListener listener) {
            this.wr = wr;
            this.crl = listener;
        }

        /**
         * Create ApiService
         * @param <T> ApiService
         * @return Correct ApiService
         */
        public <T extends ApiService> T create() {
            ApiService apiService;

            // Pick right ApiService
            if (this.wr.getType() == WalletWithRelations.Type.Exchange) {
                apiService = createExchangeApiService(wr.getExchangeName());
            } else {
                apiService = createNakedApiService(wr.getAddressCurrency());
            }

            // Set internal parameters
            apiService.setParameters(wr.getCredentials(), wr.getAddress(), wr.wallet.getId(), new ResponseHandler(crl));

            return (T) apiService;
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
                case "ETH2":
                    return (T) new BlockcypherService();
                case "ETH":
                    return (T) new EtherscanService();
                default:
                    return null;
            }
        }
    }
}
