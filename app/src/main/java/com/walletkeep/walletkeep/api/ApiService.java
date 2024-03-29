package com.walletkeep.walletkeep.api;

import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiService {
    // Wallet specific
    protected ExchangeCredentials ec;
    protected String address;
    protected int walletId;
    protected List<WalletToken> tokens;

    // Api service helpers
    protected SignatureGeneration sg;
    protected ResponseHandler responseHandler;
    protected static final byte slow_device_delay = 2;

    /**
     * Constructor: Sets internal parameters
     * @param walletId WalletId to use for the new assets
     */
    public void setParameters(ExchangeCredentials exchangeCredentials,
                              String address,
                              int walletId,
                              List<WalletToken> tokens,
                              ResponseHandler responseHandler) {
        this.ec = exchangeCredentials;
        this.address = address;
        this.tokens = tokens;
        this.walletId = walletId;
        this.responseHandler = responseHandler;
        this.sg = new SignatureGeneration(responseHandler);
    }

    public void fetch() {
        if (ec == null || ec.getKey() == null || ec.getSecret() == null) {
            this.responseHandler.returnError("No credentials have been provided.");
        }
    }

    /**
     * Perform request and handle callback
     * @param responseCall Call to perform
     */
    protected void performRequest(Call responseCall) {
        performRequest(responseCall, ErrorParser.getStandard(), responseHandler);
    }
    protected void performRequest(Call responseCall, ErrorParser errorParser) {
        performRequest(responseCall, errorParser, responseHandler);
    }
    protected void performRequest(Call responseCall, ResponseHandler responseHandler) {
        performRequest(responseCall, ErrorParser.getStandard(), responseHandler);
    }
    protected void performRequest(Call responseCall, ErrorParser errorParser, ResponseHandler responseHandler){
        responseCall.enqueue(getCallBack(errorParser, responseHandler));
    }

    private Callback<AbstractResponse> getCallBack(ErrorParser errorParser, ResponseHandler responseHandler) {
        return new Callback<AbstractResponse>() {
            @Override
            public void onResponse(@NonNull Call<AbstractResponse> call, @NonNull Response<AbstractResponse> response) {
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
                        if (asset.getAmount().compareTo(BigDecimal.ZERO) != 0) assets.add(asset);
                    }

                } catch (Exception e){
                    Response<ArrayList<AbstractResponse>> responseList = (Response<ArrayList<AbstractResponse>>) responseObject;
                    for(AbstractResponse response: responseList.body()) {
                        for(Asset asset: response.getAssets(walletId)) {
                            if (asset.getAmount().compareTo(BigDecimal.ZERO) > 0) assets.add(asset);
                        }
                    }
                }
                responseHandler.returnAssets(assets);
            }

            @Override
            public void onFailure(@NonNull Call<AbstractResponse> call,@NonNull Throwable t) {
                try {
                    responseHandler.returnError(errorParser.parse(t));
                } catch (Exception e) {
                    responseHandler.returnError(t.getMessage());
                }
            }
        };
    }

    /**
     * Abstract response that enforces the implementation of getAssets method
     */
    public abstract class AbstractResponse {
        public abstract ArrayList<Asset> getAssets(int walletId);
        public String handleError() {
             return "Unknown error on fetching assets.";
        }
    }
}
