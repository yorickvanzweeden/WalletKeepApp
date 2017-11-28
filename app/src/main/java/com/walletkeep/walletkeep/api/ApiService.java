package com.walletkeep.walletkeep.api;

import android.util.Base64;
import android.util.Log;

import com.walletkeep.walletkeep.api.exchange.GDAXService;
import com.walletkeep.walletkeep.api.naked.EthereumService;
import com.walletkeep.walletkeep.db.entity.Coin;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public abstract class ApiService {
    // Coins that are returned by specific service and watched by repository
    private ArrayList<Coin> coins;
    protected CoinResponseListener listener;
    private int walletId;


    public ApiService(ArrayList<Coin> coins, CoinResponseListener listener, int walletId) {
        this.coins = coins;
        this.listener = listener;
        this.walletId = walletId;
    }

    public abstract void fetch(ExchangeCredentials exchangeCredentials);

    /**
     * Update coins from the callback of a specific ApiService
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


    protected void getSignature(){
        long timestamp = System.currentTimeMillis() / 1000 + 3;
        String data = timestamp + "GET/accounts";
        try {
            String signature = generateHmacSHA256Signature(data, "secret");
        } catch (Exception e)
        {
            Log.d("ERROR","error => "+e.toString());
        }
    }
    protected static String generateHmacSHA256Signature(String data, String secret)   throws GeneralSecurityException {
        try {
            byte[] decoded_key = Base64.decode(secret, Base64.DEFAULT);
            SecretKeySpec secretKey = new SecretKeySpec(decoded_key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hmacData = mac.doFinal(data.getBytes("UTF-8"));
            return Base64.encodeToString(hmacData, Base64.NO_WRAP);

        } catch (UnsupportedEncodingException e) {
            // TODO: handle exception
            throw new GeneralSecurityException(e);
        }
    }


    public interface CoinResponseListener {
        void onCoinsUpdated(ArrayList<Coin> coins);
    }
    /**
     * Returns API service of the right exchange/currency
     */
    public static class Factory {
        private final WalletWithRelations wr;
        private final CoinResponseListener crl;

        public Factory(WalletWithRelations wr, CoinResponseListener listener) {
            this.wr = wr;
            this.crl = listener;
        }

        public <T extends ApiService> T create() {
            if (this.wr.getType() == WalletWithRelations.Type.Exchange) {
                return createExchangeApiService(wr.getExchange());
            } else {
                return createNakedApiService(wr.getAddress());
            }
        }

        private <T extends ApiService> T createExchangeApiService(Exchange exchange){
            return (T) new GDAXService((ArrayList<Coin>)wr.coins, crl, wr.wallet.getId());
        }

        private <T extends ApiService> T createNakedApiService(String address){
            return (T) new EthereumService((ArrayList<Coin>)wr.coins, crl, wr.wallet.getId());
        }
    }
}
