package com.walletkeep.walletkeep.api.naked;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.ErrorParser;
import com.walletkeep.walletkeep.api.ResponseHandler;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.WalletToken;
import com.walletkeep.walletkeep.util.Converters;
import com.walletkeep.walletkeep.util.SynchronisedTicket;

import java.math.BigDecimal;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class EtherscanService extends ApiService {

    @Override
    public void fetch() {
        // Create request
        EtherscanApi api = RetrofitClient.getClient("https://api.etherscan.io/api/").create(EtherscanApi.class);
        Call<EtherscanResponse> responseCall = api.getBalance( address );

        if (tokens == null || tokens.size() == 0) {
            // Perform request
            performRequest(responseCall);
            return;
        }

        // ___
        // There are tokens

        // Setup ticketing so only the last returns the assets
        SynchronisedTicket ticketing = new SynchronisedTicket(tokens.size() + 1);

        // Get actual responseHandler to return assets/errors to
        ResponseHandler apiServiceResponseHandler = this.responseHandler;

        // Collect assets
        ArrayList<Asset> assetList = new ArrayList<>();

        // Setup local responseHandler
        ResponseHandler responseHandler = new ResponseHandler(  new ResponseHandler.ResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                for (Asset asset: assets) if (asset.getAmount().compareTo(BigDecimal.ZERO) != 0) assetList.add(asset);
                if (ticketing.isLast()) apiServiceResponseHandler.returnAssets(assetList);
            }

            @Override
            public void onError(String message) { apiServiceResponseHandler.returnError(message); }
        });

        // Perform request for Ethereum
        performRequest(responseCall, responseHandler);

        // Perform requests for tokens
        for (WalletToken token: tokens) {
            (new AppExecutors()).networkIO().execute(() -> {
                Call<EtherscanResponse> responseCall2 = api.getTokenBalance( address, token.getAddress());
                performTokenRequest(responseCall2, ErrorParser.getStandard(), responseHandler, token.getCurrencyTicker());

                // Avoid Etherscan ban at 5 req/s
                if (tokens.size() > 3) {
                    try { Thread.sleep(500); }
                    catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
                }
            });
        }
    }

    private void performTokenRequest(Call responseCall, ErrorParser errorParser, ResponseHandler responseHandler, String currency){
        responseCall.enqueue(new Callback<EtherscanResponse>() {
            @Override
            public void onResponse(@NonNull Call<EtherscanResponse> call, @NonNull Response<EtherscanResponse> response) {
                // Success
                try{ responseHandler.returnAssets(response.body().getAssets(walletId, currency)); }
                // In case a service returns 200 no matter what
                catch (Exception e) { responseHandler.returnError(response.body().handleError()); }
            }

            @Override
            public void onFailure(@NonNull Call<EtherscanResponse> call,@NonNull Throwable t) {
                responseHandler.returnError(errorParser.parse(t.getMessage()));
            }
        });
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
                @Query("contractaddress") String contractAddress
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
                add(new Asset(walletId, "ETH", Converters.amountToBD(result, 18)));
            }};
        }

        public ArrayList<Asset> getAssets(int walletId, String currency) {
            return new ArrayList<Asset>() {{
               add(new Asset(walletId, currency, Converters.amountToBD(result, 18)));
            }};
        }
    }
}
