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
import retrofit2.http.Path;


public class BlockcypherService extends ApiService {

    @Override
    public void fetch() {
         // Create request
        EthereumApi api = RetrofitClient.getClient("https://api.blockcypher.com/v1/eth/main/").create(EthereumApi.class);
        Call<BlockcypherService.BlockcypherResponse> blockcypherResponseCall = api.getBalance( address );

        // Perform request
        performRequest(blockcypherResponseCall);
    }

    /**
     * Retrofit request interfaces
     */
    private interface EthereumApi {
        @Headers("Content-Type: application/json")
        @GET("addrs/{address}/balance")
        Call<BlockcypherService.BlockcypherResponse> getBalance(
                @Path("address") String address
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class BlockcypherResponse implements IResponse {

        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("total_received")
        @Expose
        private String totalReceived;
        @SerializedName("total_sent")
        @Expose
        private String totalSent;
        @SerializedName("balance")
        @Expose
        private String balance;
        @SerializedName("unconfirmed_balance")
        @Expose
        private String unconfirmedBalance;
        @SerializedName("final_balance")
        @Expose
        private String finalBalance;
        @SerializedName("n_tx")
        @Expose
        private String nTx;
        @SerializedName("unconfirmed_n_tx")
        @Expose
        private String unconfirmedNTx;
        @SerializedName("final_n_tx")
        @Expose
        private String finalNTx;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTotalReceived() {
            return totalReceived;
        }

        public void setTotalReceived(String totalReceived) {
            this.totalReceived = totalReceived;
        }

        public String getTotalSent() {
            return totalSent;
        }

        public void setTotalSent(String totalSent) {
            this.totalSent = totalSent;
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

        public String getFinalBalance() {
            return finalBalance;
        }

        public void setFinalBalance(String finalBalance) {
            this.finalBalance = finalBalance;
        }

        public String getNTx() {
            return nTx;
        }

        public void setNTx(String nTx) {
            this.nTx = nTx;
        }

        public String getUnconfirmedNTx() {
            return unconfirmedNTx;
        }

        public void setUnconfirmedNTx(String unconfirmedNTx) {
            this.unconfirmedNTx = unconfirmedNTx;
        }

        public String getFinalNTx() {
            return finalNTx;
        }

        public void setFinalNTx(String finalNTx) {
            this.finalNTx = finalNTx;
        }

        public Asset getAsset(int walletId) {
            return new Asset(walletId, "ETH", Converters.amountToFloat(getBalance(), 18));
        }

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
                add(new Asset(walletId, "ETH", Converters.amountToFloat(getBalance(), 18)));
            }};
        }
    }
}
