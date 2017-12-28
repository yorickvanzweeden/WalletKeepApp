package com.walletkeep.walletkeep.db;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.walletkeep.walletkeep.util.Converters.inputStreamToString;


public class DataGenerator {
    /**
     * Loads the exchanges from a string array
     * @return List of exchanges
     */
    public static List<Exchange> loadExchanges() {
        //TODO: Add required credentials to exchanges
        //TODO: Replace source of string array to json or something better

        // Read string array
        Context c =  WalletKeepApp.getContext();
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
    public static List<Currency> loadCurrencies() {
        // Read json file
        Context c =  WalletKeepApp.getContext();
        InputStream inputStream = c.getResources().openRawResource(R.raw.cryptocurrencies);
        String json = inputStreamToString(inputStream);

        // Fill list
        Type listType = new TypeToken<ArrayList<Currency>>(){}.getType();
        List<com.walletkeep.walletkeep.db.entity.Currency> currencies = new Gson().fromJson(json, listType);

        return currencies;
    }

    public static Portfolio loadDefaultPortfolio() {
        return new Portfolio("My portfolio");
    }
}
