package com.walletkeep.walletkeep.api;

import android.support.test.runner.AndroidJUnit4;

import com.walletkeep.walletkeep.api.data.CryptocompareService;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class CryptoCompareApiTests {
    Boolean stillRunning = true;

    @Test
    public void credentialsValidCorrect(){
        CryptocompareService.PricesResponseListener listener = new CryptocompareService.PricesResponseListener() {
            @Override
            public void onPricesUpdated(ArrayList<CurrencyPrice> prices) {
                stillRunning = false;
                return;
            }

            @Override
            public void onError(String message) {
                stillRunning = false;
                return;
            }
        };
        CryptocompareService service = new CryptocompareService(listener);

        ArrayList<String> currencies = new ArrayList<String>() {{
            add("ETH"); add("QASH");
        }};

        service.fetch(currencies);
        while(stillRunning){}
    }
}
