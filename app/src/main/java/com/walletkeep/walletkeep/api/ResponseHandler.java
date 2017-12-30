package com.walletkeep.walletkeep.api;


import com.walletkeep.walletkeep.db.entity.Asset;

import java.util.ArrayList;

public class ResponseHandler {
    private ErrorParser errorParser;
    private ResponseListener listener;

    public ResponseHandler(ErrorParser errorParser, ResponseListener listener) {
        this.errorParser = errorParser;
        this.listener = listener;
    }

    public ResponseHandler(ResponseListener listener) {
        this.listener = listener;
    }

    /**
     * Update assets from the callback of a specific ApiService if assets are updated
     * @param assets Coins from callback
     */
    protected void returnAssets(ArrayList<Asset> assets) {
        // Call listener
        listener.onAssetsUpdated(assets);
    }

    /**
     * Returns error to listener
     * @param errorMessage Message of the error given
     */
    public void returnError(String errorMessage) {
        try {
            errorMessage = errorParser.parse(errorMessage);
        } catch (NullPointerException e) { //TODO: Specify certain exceptions
            if (errorMessage == null) errorMessage = "Unknown error occurred while fetching.";
        } catch (ArrayStoreException e) { //TODO: Specify certain exceptions
            errorMessage = "Unknown error occurred while fetching.";
        }

        // Return error to listener
        listener.onError(errorMessage);
    }



    /**
     * Interface for returning data to the repository
     */
    public interface ResponseListener {
        void onAssetsUpdated(ArrayList<Asset> assets);
        void onError(String message);
    }

}
