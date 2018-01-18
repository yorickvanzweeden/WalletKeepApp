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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EthereumApiTests extends ApiServiceTest {

    private WalletWithRelations getDefaultWallet(ExchangeCredentials exchangeCredentials){
        WalletWithRelations wallet = new WalletWithRelations();
        wallet.exchangeCredentials = new ArrayList<ExchangeCredentials>();
        wallet.wallet = new Wallet(1);
        wallet.wallet.setAddressCurrency("ETH");
        wallet.wallet.setAddress(MyApiCredentials.getEthereumAddress());
        wallet.assets = new ArrayList<Asset>() {{ add(new Asset(1, "ETH",  new BigDecimal(12))) ;}};
        return wallet;
    }

    @Test
    public void assetsIsNull(){
        WalletWithRelations wr = getDefaultWallet(credentialsValidCorrect);
        wr.assets = null;
        runEntireFlow(wr, new ApiServiceTest.I() {
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