package com.walletkeep.walletkeep.api.naked;

import android.support.test.runner.AndroidJUnit4;

import com.walletkeep.walletkeep.api.ApiServiceTest;
import com.walletkeep.walletkeep.api.MyApiCredentials;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class BitcoinApiTests extends ApiServiceTest {

    private WalletWithRelations getDefaultWallet(ExchangeCredentials exchangeCredentials){
        WalletWithRelations wallet = new WalletWithRelations();
        wallet.exchangeCredentials = new ArrayList<ExchangeCredentials>();
        wallet.wallet = new Wallet(1);
        wallet.wallet.setAddressCurrency("BTC");
        wallet.wallet.setAddress(MyApiCredentials.getBtcAddress());
        wallet.assets = new ArrayList<Asset>() {{ add(new Asset(1, "ETH",  12)) ;}};
        return wallet;
    }

    @Test
    public void assetsIsNull(){
        WalletWithRelations wr = getDefaultWallet(credentialsValidCorrect);
        wr.assets = null;
        runEntireFlow(wr, new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.assertTrue(assets != null && assets.size() > 0);
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.fail("Expected no error: " + message);
            }
        });
    }
}
