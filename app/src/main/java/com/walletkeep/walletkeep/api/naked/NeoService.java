package com.walletkeep.walletkeep.api.naked;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public class NeoService extends ApiService {
    private String baseUrl = "https://seed3.neo.org:10331/";
    private String jsonRpc = "2.0";
    private String id = "1";
    //Documentation: http://docs.neo.org/en-us/node/api.html

    @Override
    public void fetch() {
        String method = "getaccountstate";
        String params = String.format("[\"%s\"]", address);

         // Create request
        Api api = RetrofitClient.getClient(baseUrl).create(Api.class);
        Call<Response> responseCall = api.getBalance(
                jsonRpc, method, params, id );

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
                @Query("jsonrpc") String jsonrpc,
                @Query("method") String method,
                @Query("params") String params,
                @Query("id") String id
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    public class Response extends AbstractResponse {

        @SerializedName("jsonrpc")
        @Expose
        private String jsonrpc;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("result")
        @Expose
        private Result result;
        @SerializedName("error")
        @Expose
        private Error error;

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
                for (Balance balance: result.balances) add(getAsset(walletId, balance));
            }};
        }

        private Asset getAsset(int walletId, Balance balance) {
            String NEOhash = "0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";
            String GAShash = "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7";

            return new Asset(walletId, NEOhash.equals(balance.asset) ? "NEO" : "GAS", balance.value);
        }

        @Override
        public String handleError() {
            return error.message;
        }
    }

    public class Balance {

        @SerializedName("asset")
        @Expose
        private String asset;
        @SerializedName("value")
        @Expose
        private float value;

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

    }

    public class Result {

        @SerializedName("version")
        @Expose
        private Integer version;
        @SerializedName("script_hash")
        @Expose
        private String scriptHash;
        @SerializedName("frozen")
        @Expose
        private Boolean frozen;
        @SerializedName("votes")
        @Expose
        private List<Object> votes = null;
        @SerializedName("balances")
        @Expose
        private List<Balance> balances = null;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getScriptHash() {
            return scriptHash;
        }

        public void setScriptHash(String scriptHash) {
            this.scriptHash = scriptHash;
        }

        public Boolean getFrozen() {
            return frozen;
        }

        public void setFrozen(Boolean frozen) {
            this.frozen = frozen;
        }

        public List<Object> getVotes() {
            return votes;
        }

        public void setVotes(List<Object> votes) {
            this.votes = votes;
        }

        public List<Balance> getBalances() {
            return balances;
        }

        public void setBalances(List<Balance> balances) {
            this.balances = balances;
        }

    }

    public class Error {

        @SerializedName("code")
        @Expose
        private Integer code;
        @SerializedName("message")
        @Expose
        private String message;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
