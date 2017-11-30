package com.walletkeep.walletkeep.api;

import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;


public class GDAXApiTests extends ApiServiceTest {

    public GDAXApiTests(){
        init(MyApiCredentials.getGDAXCredentials());
    }

    @Override
    public void init(ExchangeCredentials exchangeCredentials) {
        super.init(exchangeCredentials);
    }


    @Test
    public void happyFlow() throws Exception {
        WalletWithRelations wallet = createWalletWithCredentials(null, credentialsValidCorrect);

        ApiService.AssetResponseListener listener = new ApiService.AssetResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                //TODO
                Assert.assertTrue(assets != null && assets.size() > 0);
            }

            @Override
            public void onError(String message) {
                Assert.fail("Expected no error: " + message);
                //TODO: Do something with message
            }
        };

        // Create ApiService
        ApiService.Factory apiServiceFactory = new ApiService.Factory(wallet, listener);
        ApiService apiService = apiServiceFactory.create();

        // Fetch data
        apiService.fetch();
    }
}
