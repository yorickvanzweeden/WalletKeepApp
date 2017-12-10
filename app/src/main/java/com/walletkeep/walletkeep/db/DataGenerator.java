package com.walletkeep.walletkeep.db;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.db.entity.Wallet;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.walletkeep.walletkeep.util.Converters.inputStreamToString;


public class DataGenerator {
    public static List<Exchange> generateExchanges() {
        List<Exchange> exchanges = new ArrayList<>();
        Context c =  WalletKeepApp.getContext();
        String[] exchangesArray = c.getResources().getStringArray(R.array.exchange_array);
        for (int i = 0; i < exchangesArray.length; i++) {
            Exchange exchange = new Exchange(exchangesArray[i].toString());
            exchanges.add(exchange);
        }
        return exchanges;
    }

    public static List<Currency> generateCurrencies() {
        Context c =  WalletKeepApp.getContext();
        InputStream inputStream = c.getResources().openRawResource(R.raw.cryptocurrencies);
        String json = inputStreamToString(inputStream);
        Type listType = new TypeToken<ArrayList<Currency>>(){}.getType();
        List<com.walletkeep.walletkeep.db.entity.Currency> currencies = new Gson().fromJson(json, listType);
        return currencies;
    }

    public static List<Portfolio> generatePortfolios() {
        List<Portfolio> portfolios = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            int nr = i + 1;
            Portfolio portfolio = new Portfolio("Test portfolio " + nr);
            portfolios.add(portfolio);
        }
        return portfolios;
    }

    public static List<Wallet> generateWallets() {
        List<Wallet> wallets = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            int nr = i + 1;
            Wallet wallet = new Wallet(1);
            wallet.setExchangeName(Integer.toString(nr));
            wallets.add(wallet);
        }
        for (int i = 0; i < 2; i++) {
            Wallet wallet = new Wallet(1);
            wallet.setAddress("0x12312412");
            wallet.setAddressCurrency("ETH");
            wallets.add(wallet);
        }
        return wallets;
    }

    public static List<Asset> generateAssets() {
        List<Asset> assets = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            int nr = i + 1;
            Asset asset = new Asset(nr, "ETH",11.11f);
            assets.add(asset);
        }
        return assets;
    }

    public static List<CurrencyPrice> generateCurrencyPrices() {
        List<CurrencyPrice> currencyPrices = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            int nr = i + 1;
            CurrencyPrice currencyPrice = new CurrencyPrice("ETH", "1", new Date(), 12);
            currencyPrices.add(currencyPrice);
        }
        return currencyPrices;
    }


}
