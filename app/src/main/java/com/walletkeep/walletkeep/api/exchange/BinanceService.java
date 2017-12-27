package com.walletkeep.walletkeep.api.exchange;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.ErrorParser;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class BinanceService extends ApiService {
    @Override
    public void fetch() {
        // Get signature
        int recvWindow = 60000; // Timeframe for allowing the request
        long timestamp = System.currentTimeMillis() - 4000;
        String data =  "recvWindow=" + recvWindow + "&timestamp=" + timestamp;
        String signature;

        // In case of invalid secret
        try{ signature = generateSignature(data.getBytes("UTF-8"), ec.getSecret(), false); }
        catch (IllegalArgumentException e) { this.returnError(e.getMessage()); return; }
        catch (NullPointerException e) { this.returnError("No credentials have been provided."); return; }
        catch (UnsupportedEncodingException e) { this.returnError("Encoding UTF-8 not supported"); return; }

        // Create request
        BinanceApi api = RetrofitClient.getClient("https://api.binance.com").create(BinanceApi.class);
        Call<BinanceResponse> responseCall = api.getBalance(
                ec.getKey(), recvWindow, timestamp, signature
        );

        // Perform request
        performRequest(responseCall, new ErrorParser("msg"));
    }

    /**
     * Retrofit request interfaces
     */
    private interface BinanceApi {
        @Headers("Content-Type: application/json")
        @GET("/api/v3/account")
        Call<BinanceResponse> getBalance(
                @Header("X-MBX-APIKEY") String key,
                @Query("recvWindow") int recvWindow,
                @Query("timestamp") long timestamp,
                @Query("signature") String signature
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class BinanceResponse extends IResponse {

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

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
               for(Balance balance:balances) {
                   add(balance.getAsset(walletId));
               }
            }};
        }
    }

    private class Balance {
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
            return new Asset(walletId, CurrencyTickerCorrection.correct(asset), Float.parseFloat(free));
        }
    }
}
