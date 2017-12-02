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
import retrofit2.http.Query;

public class BinanceService extends ApiService {
    @Override
    public void fetch() {
        // Get signature
        int recvWindow = 60000;
        long timestamp = System.currentTimeMillis();
        String data =  "recvWindow=" + recvWindow + "&timestamp=" + timestamp;
        String signature;

        // In case of invalid secret
        try{ signature = generateHmacSHA256Signature(data, ec.getSecret(), false); }
        catch (IllegalArgumentException e) { this.returnError(e.getMessage()); return; }
        catch (NullPointerException e) { this.returnError("No credentials have been provided."); return; }

        // Create request
        BinanceApi api = RetrofitClient.getClient("https://api.binance.com").create(BinanceApi.class);
        Call<BinanceResponse> binanceResponseCall = api.getBalance(
                ec.getKey(), recvWindow, timestamp, signature
        );

        // Perform request
        performRequest(binanceResponseCall);
    }

    /**
     * Perform request and handle callback
     * @param binanceResponseCall Call to perform
     */
    private void performRequest(Call binanceResponseCall){
        binanceResponseCall.enqueue(new Callback<BinanceService.BinanceResponse>() {
            @Override
            public void onResponse(Call<BinanceService.BinanceResponse> call, Response<BinanceService.BinanceResponse> response) {
                // Success
                if (response.code() == 200) {
                    ArrayList<Asset> assets = new ArrayList<>();
                    for (Balance balance:response.body().getBalances()){
                        Asset asset = balance.getAsset(1);
                        if (asset.getAmount() != 0) assets.add(asset);
                    }
                    updateAssets(assets);
                } else {
                    // If failure, return the server error (or the error for returning that)
                    try{ returnError(response.errorBody().string()); }
                    catch (Exception e) { returnError(e.getMessage()); }
                }
            }

            @Override
            public void onFailure(Call<BinanceService.BinanceResponse> call, Throwable t) {
                returnError(t.getMessage());
            }
        });
    }

    protected interface BinanceApi {
        @Headers("Content-Type: application/json")
        @GET("/api/v3/account")
        Call<BinanceResponse> getBalance(
                @Header("X-MBX-APIKEY") String key,
                @Query("recvWindow") int recvWindow,
                @Query("timestamp") long timestamp,
                @Query("signature") String signature
        );
    }

    public class BinanceResponse {

        @SerializedName("makerCommission")
        @Expose
        private Integer makerCommission;
        @SerializedName("takerCommission")
        @Expose
        private Integer takerCommission;
        @SerializedName("buyerCommission")
        @Expose
        private Integer buyerCommission;
        @SerializedName("sellerCommission")
        @Expose
        private Integer sellerCommission;
        @SerializedName("canTrade")
        @Expose
        private Boolean canTrade;
        @SerializedName("canWithdraw")
        @Expose
        private Boolean canWithdraw;
        @SerializedName("canDeposit")
        @Expose
        private Boolean canDeposit;
        @SerializedName("balances")
        @Expose
        private List<Balance> balances = null;

        public Integer getMakerCommission() {
            return makerCommission;
        }

        public void setMakerCommission(Integer makerCommission) {
            this.makerCommission = makerCommission;
        }

        public Integer getTakerCommission() {
            return takerCommission;
        }

        public void setTakerCommission(Integer takerCommission) {
            this.takerCommission = takerCommission;
        }

        public Integer getBuyerCommission() {
            return buyerCommission;
        }

        public void setBuyerCommission(Integer buyerCommission) {
            this.buyerCommission = buyerCommission;
        }

        public Integer getSellerCommission() {
            return sellerCommission;
        }

        public void setSellerCommission(Integer sellerCommission) {
            this.sellerCommission = sellerCommission;
        }

        public Boolean getCanTrade() {
            return canTrade;
        }

        public void setCanTrade(Boolean canTrade) {
            this.canTrade = canTrade;
        }

        public Boolean getCanWithdraw() {
            return canWithdraw;
        }

        public void setCanWithdraw(Boolean canWithdraw) {
            this.canWithdraw = canWithdraw;
        }

        public Boolean getCanDeposit() {
            return canDeposit;
        }

        public void setCanDeposit(Boolean canDeposit) {
            this.canDeposit = canDeposit;
        }

        public List<Balance> getBalances() {
            return balances;
        }

        public void setBalances(List<Balance> balances) {
            this.balances = balances;
        }
    }

    protected class Balance {
        @SerializedName("asset")
        @Expose
        private String asset;
        @SerializedName("free")
        @Expose
        private String free;
        @SerializedName("locked")
        @Expose
        private String locked;

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getFree() {
            return free;
        }

        public void setFree(String free) {
            this.free = free;
        }

        public String getLocked() {
            return locked;
        }

        public void setLocked(String locked) {
            this.locked = locked;
        }

        public Asset getAsset(int walletId){
            return new Asset(walletId, asset, Float.parseFloat(free));
        }
    }
}
