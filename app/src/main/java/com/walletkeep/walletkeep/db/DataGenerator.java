package com.walletkeep.walletkeep.db;

import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.db.entity.Wallet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DataGenerator {
    public static List<Exchange> generateExchanges() {
        List<Exchange> exchanges = new ArrayList<>(3);
        for (int i = 0; i < Exchange.Exchanges.values().length; i++) {
            Exchange exchange = new Exchange(Exchange.Exchanges.values()[i].toString());
            exchanges.add(exchange);
        }
        return exchanges;
    }

    public static List<Currency> generateCurrencies() {
        List<Currency> currencies = new ArrayList<>(3);
        String[] currencyNames = {"Bitcoin","Ethereum","Litecoin", "Euro"};
        String[] currencyTickers = {"BTC","ETH","LTC", "EUR"};
        for (int i = 0; i < currencyNames.length; i++) {
            Currency currency = new Currency(currencyNames[i], currencyTickers[i]);
            currencies.add(currency);
        }
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
            wallet.setExchangeId(Integer.toString(nr));
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
