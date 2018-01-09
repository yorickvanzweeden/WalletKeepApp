package com.walletkeep.walletkeep.api.naked;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.util.Converters;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class EtherscanService extends ApiService {

    @Override
    public void fetch() {
        // Create request
        EtherscanApi api = RetrofitClient.getClient("https://api.etherscan.io/api/").create(EtherscanApi.class);
        Call<EtherscanResponse> responseCall = api.getBalance( address );

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Retrofit request interfaces
     */
    private interface EtherscanApi {
        @Headers("Content-Type: application/json")
        @GET("?module=account&action=balance&tag=latest")
        Call<EtherscanResponse> getBalance(
                @Query("address") String address
        );

        @Headers("Content-Type: application/json")
        @GET("?module=account&action=tokenbalance&tag=latest")
        Call<EtherscanResponse> getTokenBalance(
                @Query("address") String address,
                @Query("contract_address") String contractAddress
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class EtherscanResponse extends AbstractResponse {
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("result")
        @Expose
        private String result;

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
               add(new Asset(walletId, "ETH", Converters.amountToFloat(result, 18)));
            }};
        }
    }
}
