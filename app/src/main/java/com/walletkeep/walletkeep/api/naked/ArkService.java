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


public class ArkService extends ApiService {
    @Override
    public void fetch() {
        String baseUrl = "https://api.arknode.net/";
        String nethash = "6e84d08bd299ed97c212c886c98a57e36545c8f5d645ca7eeae63a8bd62d8988";
        String version = "1.0.1";
        String port = "4000";
        //Documentation: https://ark.brianfaust.me/#/

        // Create request
        Api api = RetrofitClient.getClient(baseUrl).create(Api.class);
        Call<Response> responseCall = api.getBalance(
                address, nethash, version, port );

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Retrofit request interfaces
     */
    private interface Api {
        @Headers("Content-Type: application/json")
        @GET("api/accounts/getBalance")
        Call<Response> getBalance(
                @Query("address") String address,
                @Query("nethash") String nethash,
                @Query("version") String version,
                @Query("port") String port
         );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class Response extends AbstractResponse {
        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("balance")
        @Expose
        private String balance;
        @SerializedName("unconfirmedBalance")
        @Expose
        private String unconfirmedBalance;
        @SerializedName("error")
        @Expose
        private String error;

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
                add(new Asset(walletId, "ARK", Converters.amountToBD(balance, 8)));
            }};
        }

        @Override
        public String handleError() {
            return error;
        }
    }
}
