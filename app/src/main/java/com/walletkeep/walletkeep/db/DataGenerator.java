package com.walletkeep.walletkeep.db;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.walletkeep.walletkeep.util.Converters.inputStreamToString;


class DataGenerator {
    /**
     * Loads the exchanges from a string array
     * @return List of exchanges
     */
    static List<Exchange> loadExchanges() {
        //TODO: Add required credentials to exchanges
        //TODO: Replace source of string array to json or something better

        // Read string array
        Context c =  WalletKeepApp.context();
        String[] exchangesArray = c.getResources().getStringArray(R.array.exchange_array);

        // Fill list
        List<Exchange> exchanges = new ArrayList<>();
        for (int i = 0; i < exchangesArray.length; i++) {
            Exchange exchange = new Exchange(exchangesArray[i].toString());
            exchanges.add(exchange);
        }

        return exchanges;
    }

    /**
     * Loads the currencies from a json file (located in /res/raw)
     * @return List of currencies (name and ticker)
     */
    static List<Currency> loadCurrencies() {
        // Read json file
        Context c =  WalletKeepApp.context();
        InputStream inputStream = c.getResources().openRawResource(R.raw.cryptocurrencies);
        String json = inputStreamToString(inputStream);

        // Fill list
        Type listType = new TypeToken<ArrayList<Currency>>(){}.getType();
        return new Gson().fromJson(json, listType);
    }

    static Portfolio loadDefaultPortfolio() {
        return new Portfolio("My portfolio");
    }

    static List<CurrencyPrice> loadDefaultPrices() {
        return new ArrayList<CurrencyPrice>() {{
            add(new CurrencyPrice("EUR", 0.83f, 1f, 0.001f, 0f, 0f, 0f, new Date()));
            add(new CurrencyPrice("USD", 1f, 1.20f, 0.001f, 0f, 0f, 0f, new Date()));
        }};
    }

}
