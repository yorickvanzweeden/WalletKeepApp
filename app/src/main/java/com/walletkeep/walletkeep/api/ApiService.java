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
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;

public abstract class ApiService {
    public List<Coin> fetch(WalletWithRelations wr){
        float amount = wr.coins.get(0).getAmount();
        wr.coins.get(0).setAmount(amount + 1);
        return wr.coins;

        //Call<Coin> call = createCall(wr.getCredentials());
        //return parseResponse(call, wr.coins);
    }

    protected abstract Call<Coin> createCall(ExchangeCredentials exchangeCredentials);
    protected abstract List<Coin> parseResponse(Call<Coin> call, List<Coin> oldCoins);

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
            return Base64.encodeToString(hmacData, Base64.DEFAULT);

        } catch (UnsupportedEncodingException e) {
            // TODO: handle exception
            throw new GeneralSecurityException(e);
        }
    }

    /**
     * Returns API service of the right exchange/currency
     */
    public static class Factory {
        private final WalletWithRelations wr;

        public Factory(WalletWithRelations wr) {
            this.wr = wr;
        }

        public <T extends ApiService> T create() {
            ApiService apiService;

            if (this.wr.getType() == WalletWithRelations.Type.Exchange) {
                return createExchangeApiService(wr.getExchange());
            } else {
                return createNakedApiService(wr.getAddress());
            }
        }

        private <T extends ApiService> T createExchangeApiService(Exchange exchange){
            return (T) new GDAXService();
        }

        private <T extends ApiService> T createNakedApiService(String address){
            return (T) new EthereumService();
        }
    }
}
