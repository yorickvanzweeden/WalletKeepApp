package com.walletkeep.walletkeep.api;

import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.ArrayList;
import java.util.List;

public abstract class ApiServiceTest {
    static Boolean threadsRunning;

    // Exchange credentials
    private ExchangeCredentials correct;
    protected ExchangeCredentials credentialsValidCorrect;
    protected ExchangeCredentials credentialsIncorrectKey;
    protected ExchangeCredentials credentialsIncorrectSecret;
    protected ExchangeCredentials credentialsIncorrectPassphrase;
    protected ExchangeCredentials credentialsIncorrectNonce;
    protected ExchangeCredentials credentialsInvalidKey;
    protected ExchangeCredentials credentialsInvalidSecret;
    protected ExchangeCredentials credentialsInvalidPassphrase;
    protected ExchangeCredentials credentialsNullKey;
    protected ExchangeCredentials credentialsNullSecret;
    protected ExchangeCredentials credentialsNullPassphrase;
    protected ExchangeCredentials credentialsNegativeNonce;

    public void init(ExchangeCredentials exchangeCredentials) {
        correct = exchangeCredentials;
        initCredentials();
    }

    private void initCredentials() {
        // Perfectly valid credentials
        credentialsValidCorrect = getNewCorrectCredentials();

        credentialsIncorrectKey = getNewCorrectCredentials();
        credentialsIncorrectKey.setKey("wrong");

        credentialsIncorrectSecret = getNewCorrectCredentials();
        credentialsIncorrectSecret.setSecret("ELzeozv4AaBkY2zbG2h1N2g2pHrxAIXcFav5p1PRHmhiGKhHCKNaJUNVIRFzoVPvoKBTq1NQKzFiGwf202KHBo==");

        credentialsIncorrectPassphrase = getNewCorrectCredentials();
        credentialsIncorrectPassphrase.setPassphrase("wrong");

        credentialsIncorrectNonce = getNewCorrectCredentials();
        credentialsIncorrectNonce.setNonce(0);

        credentialsInvalidKey = getNewCorrectCredentials();
        credentialsInvalidKey.setKey("\n\t\'01234567890,/.");

        credentialsInvalidSecret = getNewCorrectCredentials();
        credentialsInvalidSecret.setSecret("\n\t\'01234567890,/.");

        credentialsInvalidPassphrase = getNewCorrectCredentials();
        credentialsInvalidPassphrase.setPassphrase("\n\t\'01234567890,/.");

        credentialsNullKey = getNewCorrectCredentials();
        credentialsNullKey.setKey(null);

        credentialsNullSecret = getNewCorrectCredentials();
        credentialsNullSecret.setSecret(null);

        credentialsNullPassphrase = getNewCorrectCredentials();
        credentialsNullPassphrase.setPassphrase(null);

        credentialsNegativeNonce = getNewCorrectCredentials();
        credentialsNegativeNonce.setNonce(-1);
    }

    protected void runEntireFlow(WalletWithRelations wallet, ApiServiceTest.I i) {
        threadsRunning = true;

        ResponseHandler.ResponseListener listener = new ResponseHandler.ResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                i.onResponseAssertion(assets);
                threadsRunning = false;
            }

            @Override
            public void onError(String message) {
                i.onFailAssertion(message);
                threadsRunning = false;
            }
        };

        // Create ApiService
        ApiService.Factory apiServiceFactory = new ApiService.Factory(wallet, listener);
        ApiService apiService = apiServiceFactory.create();

        // Fetch data
        apiService.fetch();

        // Busy-wait till threads are done
        while(threadsRunning) {}
    }

    protected interface I {
        void onResponseAssertion(List<Asset> assets);
        void onFailAssertion(String message);
    }

    private ExchangeCredentials getNewCorrectCredentials(){
        return new ExchangeCredentials(correct.getKey(), correct.getSecret(), correct.getPassphrase());
    }
}

