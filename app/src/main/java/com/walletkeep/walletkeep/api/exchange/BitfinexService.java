package com.walletkeep.walletkeep.api.exchange;

import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public class BitfinexService extends ApiService {
    @Override
    public void fetch() {
        String key;
        try { key = ec.getKey(); } catch (NullPointerException e) {
            this.returnError("No credentials have been provided.");
            return;
        }

        // Get signature
        long timestamp = System.currentTimeMillis();
        String data = String.format("/api/v2/auth/r/wallets%s{}", timestamp);
        String signature;

        // In case of invalid secret
        try {
            signature = generateSignature(data, ec.getSecret(), false, "HmacSHA384");
        } catch (IllegalArgumentException e) {
            this.returnError(e.getMessage());
            return;
        } catch (NullPointerException e) {
            this.returnError("No credentials have been provided.");
            return;
        }

        // Create request
        BitfinexApi api = RetrofitClient.getClient("https://api.bitfinex.com").create(BitfinexApi.class);
        Call<List<BitfinexResponse>> responseCall = api.getBalance(
                signature, key, timestamp, "{}"
        );

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Retrofit request interfaces
     */
    private interface BitfinexApi {
        @Headers("Content-Type: application/json")
        @POST("/v2/auth/r/wallets")
        Call<List<BitfinexResponse>> getBalance(
                @Header("bfx-signature") String signature,
                @Header("bfx-apikey") String key,
                @Header("bfx-nonce") long timestamp,
                @Body String body
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    public class BitfinexResponse extends IResponse {
        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return null;
        }
    }
}
