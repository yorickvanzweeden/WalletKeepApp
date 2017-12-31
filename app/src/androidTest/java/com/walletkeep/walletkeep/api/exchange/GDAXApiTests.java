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
public class GDAXApiTests extends ApiServiceTest {

    public GDAXApiTests(){
        init(MyApiCredentials.getGDAXCredentials());
    }

    @Override
    public void init(ExchangeCredentials exchangeCredentials) {
        super.init(exchangeCredentials);
    }

    private WalletWithRelations getDefaultWallet(ExchangeCredentials exchangeCredentials){
        WalletWithRelations wallet = new WalletWithRelations();
        wallet.exchangeCredentials = new ArrayList<ExchangeCredentials>(){{add(exchangeCredentials);}};
        wallet.wallet = new Wallet(1);
        wallet.wallet.setExchangeName("GDAX");
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
                Assert.assertTrue(message.toLowerCase().contains("invalid api key"));
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
                Assert.assertTrue(message.toLowerCase().contains("invalid signature"));
            }
        });
    }

    @Test
    public void credentialsIncorrectPassphrase(){
        runEntireFlow(getDefaultWallet(credentialsIncorrectPassphrase), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.toLowerCase().contains("invalid passphrase"));
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
                Assert.assertTrue(message.toLowerCase().contains("invalid signature"));
            }
        });
    }

    @Test
    public void credentialsInvalidPassphrase(){
        runEntireFlow(getDefaultWallet(credentialsInvalidPassphrase), new I() {
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
    public void credentialsNullKey(){
        runEntireFlow(getDefaultWallet(credentialsNullKey), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.toLowerCase().contains("key header is required"));
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

    @Test
    public void credentialsNullPassphrase(){
        runEntireFlow(getDefaultWallet(credentialsNullPassphrase), new I() {
            @Override
            public void onResponseAssertion(List<Asset> assets) {
                Assert.fail("Expected an error, but got a 200");
            }

            @Override
            public void onFailAssertion(String message) {
                Assert.assertTrue(message.toLowerCase().contains("passphrase header is required"));
            }
        });
    }
}
