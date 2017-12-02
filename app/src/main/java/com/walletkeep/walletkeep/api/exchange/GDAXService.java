package com.walletkeep.walletkeep.api.exchange;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public class GDAXService extends ApiService {
    @Override
    public void fetch() {
        // Get signature
        long timestamp = System.currentTimeMillis() / 1000 + 3;
        String data =  timestamp + "GET/accounts";
        String signature;

        // In case of invalid secret
        try{ signature = generateHmacSHA256Signature(data, this.ec.getSecret(), true); }
        catch (IllegalArgumentException e) { this.returnError(e.getMessage()); return; }
        catch (NullPointerException e) { this.returnError("No credentials have been provided."); return; }

        // Create request
        GDAXApi api = RetrofitClient.getClient("https://api.gdax.com").create(GDAXApi.class);
        Call<List<GDAXResponse>> gdaxResponseCall = api.getBalance(
                signature, timestamp, ec.getKey(), ec.getPassphrase()
        );

        // Perform request
        performRequest(gdaxResponseCall);
    }

    /**
     * Perform request and handle callback
     * @param gdaxResponseCall Call to perform
     */
    private void performRequest(Call gdaxResponseCall){
        gdaxResponseCall.enqueue(new Callback<List<GDAXResponse>>() {
            @Override
            public void onResponse(Call<List<GDAXResponse>> call, Response<List<GDAXResponse>> response) {
                // Success
                if (response.code() == 200) {
                    ArrayList<Asset> assets = new ArrayList<>();
                    for (GDAXResponse gdaxResponse:response.body()){
                        assets.add(gdaxResponse.getAsset(1));
                    }
                    updateAssets(assets);
                } else {
                    // If failure, return the server error (or the error for returning that)
                    try{ returnError(response.errorBody().string()); }
                    catch (Exception e) { returnError(e.getMessage()); }
                }
            }

            @Override
            public void onFailure(Call<List<GDAXResponse>> call, Throwable t) {
                returnError(t.getMessage());
            }
        });
    }

    protected interface GDAXApi {
        @Headers("Content-Type: application/json")
        @GET("/accounts")
        Call<List<GDAXResponse>> getBalance(
                @Header("CB-ACCESS-SIGN") String signature,
                @Header("CB-ACCESS-TIMESTAMP") long timestamp,
                @Header("CB-ACCESS-KEY") String key,
                @Header("CB-ACCESS-PASSPHRASE") String passphrase
        );
    }

    protected class GDAXResponse {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("balance")
        @Expose
        private String balance;
        @SerializedName("available")
        @Expose
        private String available;
        @SerializedName("hold")
        @Expose
        private String hold;
        @SerializedName("profile_id")
        @Expose
        private String profileId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getAvailable() {
            return available;
        }

        public void setAvailable(String available) {
            this.available = available;
        }

        public Asset getAsset(int walletId){
            return new Asset(walletId, currency, Float.parseFloat(balance));
        }

    }
}