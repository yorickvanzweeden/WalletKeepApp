package com.walletkeep.walletkeep.db;

import com.walletkeep.walletkeep.db.entity.Coin;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.db.entity.Wallet;

import java.util.ArrayList;
import java.util.List;


public class DataGenerator {
    public static List<Exchange> generateExchanges() {
        List<Exchange> exchanges = new ArrayList<>(3);
        String[] exchangeNames = {"Coinbase","GDax","Kraken"};
        for (int i = 0; i < exchangeNames.length; i++) {
            Exchange exchange = new Exchange(exchangeNames[i]);
            exchanges.add(exchange);
        }
        return exchanges;
    }

    public static List<Currency> generateCurrencies() {
        List<Currency> currencies = new ArrayList<>(3);
        String[] currencyNames = {"Bitcoin","Ethereum","Litecoin"};
        String[] currencyTickers = {"BTC","ETH","LTC"};
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
            wallets.add(wallet);
        }
        return wallets;
    }

    public static List<Coin> generateCoins() {
        List<Coin> coins = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            int nr = i + 1;
            Coin coin = new Coin(nr, nr,11.11f);
            coins.add(coin);
        }
        return coins;
    }


}
