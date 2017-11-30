package com.walletkeep.walletkeep.api;

import android.support.test.runner.AndroidJUnit4;

import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class GDAXApiTests extends ApiServiceTest {

    public GDAXApiTests(){
        init(MyApiCredentials.getGDAXCredentials());
    }

    @Override
    public void init(ExchangeCredentials exchangeCredentials) {
        super.init(exchangeCredentials);
    }

    static Boolean threadsRunning;

    @Test
    public void happyFlow() throws Exception {
        threadsRunning = true;
        WalletWithRelations wallet = createWalletWithCredentials(credentialsValidCorrect);
        wallet.exchanges = new ArrayList<Exchange>() {{ add(new Exchange("GDAX")); }};
        wallet.wallet = new Wallet(1);
        wallet.assets = new ArrayList<Asset>() {{ add(new Asset(1, "ETH",  12)) ;}};

        ApiService.AssetResponseListener listener = new ApiService.AssetResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                Assert.assertTrue(assets != null && assets.size() > 0);
                threadsRunning = false;
            }

            @Override
            public void onError(String message) {
                Assert.fail("Expected no error: " + message);
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
}
