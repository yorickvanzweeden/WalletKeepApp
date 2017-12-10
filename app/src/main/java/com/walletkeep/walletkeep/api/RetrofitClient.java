package com.walletkeep.walletkeep.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    /**
     * Gets Retrofit client (singleton)
     * @param baseUrl Base url for the client
     * @return Retrofit client
     */
    public static Retrofit getClient(String baseUrl) {
        // Create new retrofit if baseUrl has changed
        if (retrofit == null || !baseUrl.equals(retrofit.baseUrl())) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
