package com.walletkeep.walletkeep.api;

import android.util.Base64;

import com.walletkeep.walletkeep.api.exchange.GDAXService;
import com.walletkeep.walletkeep.api.naked.EthereumService;
import com.walletkeep.walletkeep.db.entity.Coin;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.ArrayList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public abstract class ApiService {
    private ArrayList<Coin> coins;
    protected ExchangeCredentials ec;
    private CoinResponseListener listener;
    private int walletId;

    /**
     * Constructor: Sets internal parameters
     * @param coins Old coin values
     * @param listener Listener to give callback to
     * @param walletId WalletId to use for the new coins
     */
    private void setParameters(ArrayList<Coin> coins,
                               ExchangeCredentials exchangeCredentials,
                               CoinResponseListener listener,
                               int walletId) {
        this.coins = coins;
        this.ec = exchangeCredentials;
        this.listener = listener;
        this.walletId = walletId;
    }

    public abstract void fetch();

    /**
     * Update coins from the callback of a specific ApiService if coins are updated
     * @param coins Coins from callback
     */
    protected void updateCoins(ArrayList<Coin> coins) {
        if (!this.coins.equals(coins)){
            // Update with walletId
            for(Coin coin: coins){
                coin.setWalletId(walletId);
            }

            // Call listener
            listener.onCoinsUpdated(coins);

            // Update internal list
            this.coins = coins;
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
    protected String generateHmacSHA256Signature(String data, String secret) throws IllegalArgumentException {
        try {
            byte[] decoded_key = Base64.decode(secret, Base64.DEFAULT);
            SecretKeySpec secretKey = new SecretKeySpec(decoded_key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hmacData = mac.doFinal(data.getBytes("UTF-8"));
            return Base64.encodeToString(hmacData, Base64.NO_WRAP);

        } catch (Exception e) {
            // Signature is invalid --> Secret is invalid
            throw new IllegalArgumentException("Signature could not be created. Your secret is probably invalid.");
        }
    }


    /**
     * Interface for returning data to the repository
     */
    public interface CoinResponseListener {
        void onCoinsUpdated(ArrayList<Coin> coins);
        void onError(String message);
    }

    /**
     * Returns API service of the right exchange/currency
     */
    public static class Factory {
        private final WalletWithRelations wr;
        private final CoinResponseListener crl;

        /**
         * Constructor: Provides initializes
         * @param wr Provides exchange/address, credentials and coins
         * @param listener
         */
        public Factory(WalletWithRelations wr, CoinResponseListener listener) {
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
                apiService = createNakedApiService(wr.getAddress());
            }

            // Set internal parameters
            apiService.setParameters((ArrayList<Coin>)wr.coins, wr.getCredentials(), crl, wr.wallet.getId());

            return (T) apiService;
        }

        /**
         * Picks right ApiService for exchange
         * @param exchange Exchange
         * @param <T> ApiService
         * @return Exchange ApiService
         */
        private <T extends ApiService> T createExchangeApiService(Exchange exchange){
            return (T) new GDAXService();
        }

        /**
         * Picks right ApiService for address
         * @param address Address
         * @param <T> ApiService
         * @return Exchange ApiService
         */
        private <T extends ApiService> T createNakedApiService(String address){
            return (T) new EthereumService();
        }
    }
}
