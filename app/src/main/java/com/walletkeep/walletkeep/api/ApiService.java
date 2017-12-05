package com.walletkeep.walletkeep.api;

import android.util.Base64;

import com.walletkeep.walletkeep.api.exchange.BinanceService;
import com.walletkeep.walletkeep.api.exchange.GDAXService;
import com.walletkeep.walletkeep.api.naked.BlockcypherService;
import com.walletkeep.walletkeep.api.naked.EtherscanService;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.util.Converters;

import java.util.ArrayList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiService {
    private ArrayList<Asset> assets;
    private AssetResponseListener listener;

    protected ExchangeCredentials ec;
    protected String address;
    protected int walletId;


    /**
     * Constructor: Sets internal parameters
     * @param assets Old asset values
     * @param listener Listener to give callback to
     * @param walletId WalletId to use for the new assets
     */
    private void setParameters(ArrayList<Asset> assets,
                               ExchangeCredentials exchangeCredentials,
                               String address,
                               AssetResponseListener listener,
                               int walletId) {
        this.assets = assets;
        this.ec = exchangeCredentials;
        this.address = address;
        this.listener = listener;
        this.walletId = walletId;
    }

    public abstract void fetch();


    /**
     * Update assets from the callback of a specific ApiService if assets are updated
     * @param assets Coins from callback
     */
    protected void returnAssets(ArrayList<Asset> assets) {
        if ((this.assets == null & assets != null) || !this.assets.equals(assets)){

            // Call listener
            listener.onAssetsUpdated(assets);

            // Update internal list
            this.assets = assets;
        }
    }

    /**
     * Returns error to listener
     * @param errorMessage Message of the error given
     */
    protected void returnError(String errorMessage) {
        listener.onError(errorMessage);
    }


    /**
     * Generated HMAC SHA-256 signature
     * @param data Data to encrypt
     * @param secret Secret to encrypt with
     * @return Signature
     */
    protected String generateSignature(String data, String secret, Boolean encoded) throws IllegalArgumentException {
        try {
            byte[] decoded_key = encoded ? Base64.decode(secret, Base64.DEFAULT) : secret.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(decoded_key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hMacData = mac.doFinal(data.getBytes("UTF-8"));
            return encoded ? Base64.encodeToString(hMacData, Base64.NO_WRAP) : Converters.bytesToHex(hMacData);
        } catch (Exception e) {
            // Signature is invalid --> Secret is invalid
            throw new IllegalArgumentException("Signature could not be created. Your secret is probably invalid.");
        }
    }

    /**
     * Perform request and handle callback
     * @param responseCall Call to perform
     */
    protected void performRequest(Call responseCall){
        responseCall.enqueue(new Callback<IResponse>() {
            @Override
            public void onResponse(Call<IResponse> call, Response<IResponse> response) {
                // Success
                if (response.code() == 200) {
                    handleSuccessResponse(response);
                } else {
                    // If failure, return the server error (or the error for returning that)
                    try{ returnError(response.errorBody().string()); }
                    catch (Exception e) { returnError(e.getMessage()); }
                }
            }

            public void handleSuccessResponse(Object responseObject) {
                ArrayList<com.walletkeep.walletkeep.db.entity.Asset> assets = new ArrayList<>();

                // Response may be a list or a single item
                try{
                    Response<IResponse> response = (Response<IResponse>) responseObject;
                    for(Asset asset: response.body().getAssets(walletId)) {
                        if (asset.getAmount() != 0) assets.add(asset);
                    }

                } catch (Exception e){
                    Response<ArrayList<IResponse>> responseList = (Response<ArrayList<IResponse>>) responseObject;
                    for(IResponse response: responseList.body()) {
                        for(Asset asset: response.getAssets(walletId)) {
                            if (asset.getAmount() != 0) assets.add(asset);
                        }
                    }
                }

                returnAssets(assets);
            }

            @Override
            public void onFailure(Call<IResponse> call, Throwable t) {
                returnError(t.getMessage());
            }
        });
    }


    /**
     * Interface for returning data to the repository
     */
    public interface AssetResponseListener {
        void onAssetsUpdated(ArrayList<Asset> assets);
        void onError(String message);
    }

    /**
     * Abstract response that enforces the implementation of getAssets method
     */
    public interface IResponse {
        ArrayList<Asset> getAssets(int walletId);
    }

    /**
     * Returns API service of the right exchange/currency
     */
    public static class Factory {
        private final WalletWithRelations wr;
        private final AssetResponseListener crl;

        /**
         * Constructor: Provides initializes
         * @param wr Provides exchange/address, credentials and assets
         * @param listener
         */
        public Factory(WalletWithRelations wr, AssetResponseListener listener) {
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
                apiService = createExchangeApiService(wr.getExchange());
            } else {
                apiService = createNakedApiService(wr.getAddressCurrency());
            }

            // Set internal parameters
            apiService.setParameters((ArrayList<Asset>)wr.assets, wr.getCredentials(), wr.getAddress(), crl, wr.wallet.getId());

            return (T) apiService;
        }

        /**
         * Picks right ApiService for exchange
         * @param exchange Exchange
         * @param <T> ApiService
         * @return Exchange ApiService
         */
        private <T extends ApiService> T createExchangeApiService(Exchange exchange){
            switch (exchange.getName()){
                case "GDAX":
                    return (T) new GDAXService();
                case "Binance":
                    return (T) new BinanceService();
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
                case "ETH":
                    return (T) new BlockcypherService();
                case "ETH2":
                    return (T) new EtherscanService();
                default:
                    return null;
            }
        }
    }
}
