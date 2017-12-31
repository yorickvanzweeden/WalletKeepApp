package com.walletkeep.walletkeep.api.exchange;

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
public class KucoinApiTests extends ApiServiceTest {

    public KucoinApiTests(){
        init(MyApiCredentials.getKucoinCredentials());
    }

    @Override
    public void init(ExchangeCredentials exchangeCredentials) {
        super.init(exchangeCredentials);
    }

    private WalletWithRelations getDefaultWallet(ExchangeCredentials exchangeCredentials){
        WalletWithRelations wallet = new WalletWithRelations();
        wallet.exchangeCredentials = new ArrayList<ExchangeCredentials>(){{add(exchangeCredentials);}};
        wallet.wallet = new Wallet(1);
        wallet.wallet.setExchangeName("Kucoin");
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

    @Test
    public void noCredentials(){
        WalletWithRelations wr = getDefaultWallet(credentialsValidCorrect);
        wr.exchangeCredentials = new ArrayList<ExchangeCredentials>();
        runEntireFlow(wr, new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.contains("No credentials have been provided."));
            }
        });
    }


    @Test
    public void credentialsValidCorrect(){
        runEntireFlow(getDefaultWallet(credentialsValidCorrect), new I() {
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

    @Test
    public void credentialsIncorrectKey(){
        runEntireFlow(getDefaultWallet(credentialsIncorrectKey), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.toLowerCase().contains("invalid read-key"));
            }
        });
    }

    @Test
    public void credentialsIncorrectSecret(){
        runEntireFlow(getDefaultWallet(credentialsIncorrectSecret), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.toLowerCase().contains("signature for this request is not valid"));
            }
        });
    }

    @Test
    public void credentialsInvalidKey(){
        runEntireFlow(getDefaultWallet(credentialsInvalidKey), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.toLowerCase().contains("unexpected char"));
            }
        });
    }

    @Test
    public void credentialsInvalidSecret(){
        runEntireFlow(getDefaultWallet(credentialsInvalidSecret), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.toLowerCase().contains("signature for this request is not valid"));
            }
        });
    }

    @Test
    public void credentialsNullKey(){
        runEntireFlow(getDefaultWallet(credentialsNullKey), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.toLowerCase().contains("invalid read-key"));
            }
        });
    }

    @Test
    public void credentialsNullSecret(){
        runEntireFlow(getDefaultWallet(credentialsNullSecret), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.contains("Signature could not be created. Your secret is probably invalid."));
            }
        });
    }
}
