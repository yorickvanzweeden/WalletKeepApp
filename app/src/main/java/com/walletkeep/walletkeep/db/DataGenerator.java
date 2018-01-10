package com.walletkeep.walletkeep.db;

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


class DataGenerator {
    /**
     * Loads the exchanges from a string array
     * @return List of exchanges
     */
    static List<Exchange> loadExchanges() {
        //TODO: Add required credentials to exchanges
        //TODO: Replace source of string array to json or something better

        // Read string array
        String[] exchangesArray = WalletKeepApp
                .context()
                .getResources()
                .getStringArray(R.array.exchange_array);

        // Fill list
        return new ArrayList<Exchange>() {{
            for (String anExchangesArray : exchangesArray) add(new Exchange(anExchangesArray));
        }};
    }

    /**
     * Loads the currencies from a json file (located in /res/raw)
     * @return List of currencies (name and ticker)
     */
    static List<Currency> loadCurrencies() {
        // Read json file
        InputStream inputStream = WalletKeepApp
                .context()
                .getResources()
                .openRawResource(R.raw.cryptocurrencies);
        String json = inputStreamToString(inputStream);

        // Fill list
        Type listType = new TypeToken<ArrayList<Currency>>(){}.getType();
        return new Gson().fromJson(json, listType);
    }

    /**
     * Loads an empty default portfolio
     * @return Empty portfolio named "My portfolio"
     */
    static Portfolio loadDefaultPortfolio() {
        return new Portfolio("My portfolio");
    }
}
