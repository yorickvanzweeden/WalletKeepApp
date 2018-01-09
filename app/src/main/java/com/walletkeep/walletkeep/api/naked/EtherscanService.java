package com.walletkeep.walletkeep.api.naked;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.ErrorParser;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.WalletTokenA;
import com.walletkeep.walletkeep.util.Converters;

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

        // Perform request
        performRequest(responseCall);

        if (tokens != null && tokens.size() > 0) {
            for (WalletTokenA token: tokens) {
                responseCall = api.getTokenBalance( address, token.getAddress());
                performTokenRequest(responseCall, ErrorParser.getStandard(), token.getCurrency());
            }
        }
    }

    private void performTokenRequest(Call responseCall, ErrorParser errorParser, String currency){
        responseCall.enqueue(new Callback<EtherscanResponse>() {
            @Override
            public void onResponse(@NonNull Call<EtherscanResponse> call, @NonNull Response<EtherscanResponse> response) {
                // Success
                if (response.code() == 200) {
                    try{ handleSuccessResponse(response); }
                    // In case a service returns 200 no matter what
                    catch (Exception e) {
                        responseHandler.returnError(response.body().handleError());
                    }
                } else {
                    // If failure, return the server error (or the error for returning that)
                    try{ responseHandler.returnError(errorParser.parse(response.errorBody().string())); }
                    catch (Exception e) { responseHandler.returnError(e.getMessage()); }
                }
            }

            void handleSuccessResponse(Object responseObject) {
                ArrayList<com.walletkeep.walletkeep.db.entity.Asset> assets = new ArrayList<>();

                // Response may be a list or a single item
                try{
                    Response<AbstractResponse> response = (Response<AbstractResponse>) responseObject;
                    for(Asset asset: response.body().getAssets(walletId)) {
                        if (asset.getAmount() != 0) assets.add(asset);
                    }

                } catch (Exception e){
                    Response<ArrayList<AbstractResponse>> responseList = (Response<ArrayList<AbstractResponse>>) responseObject;
                    for(AbstractResponse response: responseList.body()) {
                        for(Asset asset: response.getAssets(walletId)) {
                            if (asset.getAmount() != 0) assets.add(asset);
                        }
                    }
                }
                responseHandler.returnAssets(assets);
            }

            @Override
            public void onFailure(@NonNull Call<EtherscanResponse> call,@NonNull Throwable t) {
                responseHandler.returnError(errorParser.parse(t));
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
                add(new Asset(walletId, "ETH", Converters.amountToFloat(result, 18)));
            }};
        }

        @Override
        public ArrayList<Asset> getAssets(int walletId, String currency) {
            return new ArrayList<Asset>() {{
               add(new Asset(walletId, currency, Converters.amountToFloat(result, 18)));
            }};
        }
    }
}
