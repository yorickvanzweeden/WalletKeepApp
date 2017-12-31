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
    private String baseUrl = "https://api.arknode.net/";
    private String nethash = "6e84d08bd299ed97c212c886c98a57e36545c8f5d645ca7eeae63a8bd62d8988";
    private String version = "1.0.1";
    private String port = "4000";

    @Override
    public void fetch() {
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

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getUnconfirmedBalance() {
            return unconfirmedBalance;
        }

        public void setUnconfirmedBalance(String unconfirmedBalance) {
            this.unconfirmedBalance = unconfirmedBalance;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
                add(new Asset(walletId, "ARK", Converters.amountToFloat(balance, 8)));
            }};
        }

        @Override
        public String handleError() {
            return error;
        }
    }
}
