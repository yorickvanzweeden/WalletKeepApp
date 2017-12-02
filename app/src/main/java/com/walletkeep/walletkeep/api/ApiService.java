package com.walletkeep.walletkeep.api;

import android.util.Base64;

import com.walletkeep.walletkeep.api.exchange.BinanceService;
import com.walletkeep.walletkeep.api.exchange.GDAXService;
import com.walletkeep.walletkeep.api.naked.EthereumService;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.util.Converters;

import java.util.ArrayList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public abstract class ApiService {
    private ArrayList<Asset> assets;
    protected ExchangeCredentials ec;
    private AssetResponseListener listener;
    private int walletId;

    /**
     * Constructor: Sets internal parameters
     * @param assets Old asset values
     * @param listener Listener to give callback to
     * @param walletId WalletId to use for the new assets
     */
    private void setParameters(ArrayList<Asset> assets,
                               ExchangeCredentials exchangeCredentials,
                               AssetResponseListener listener,
                               int walletId) {
        this.assets = assets;
        this.ec = exchangeCredentials;
        this.listener = listener;
        this.walletId = walletId;
    }

    public abstract void fetch();

    /**
     * Update assets from the callback of a specific ApiService if assets are updated
     * @param assets Coins from callback
     */
    protected void updateAssets(ArrayList<Asset> assets) {
        if ((this.assets == null & assets != null) || !this.assets.equals(assets)){
            // Update with walletId
            for(Asset asset : assets){
                asset.setWalletId(walletId);
            }

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
    protected String generateHmacSHA256Signature(String data, String secret, Boolean encoded) throws IllegalArgumentException {
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
     * Interface for returning data to the repository
     */
    public interface AssetResponseListener {
        void onAssetsUpdated(ArrayList<Asset> assets);
        void onError(String message);
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
            apiService.setParameters((ArrayList<Asset>)wr.assets, wr.getCredentials(), crl, wr.wallet.getId());

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
            return (T) new EthereumService();
        }
    }
}
