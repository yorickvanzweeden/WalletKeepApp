package com.walletkeep.walletkeep.db;

import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DataGenerator {
    public static List<Portfolio> generatePortfolios() {
        List<Portfolio> portfolios = new ArrayList<>(2);
        Random rnd = new Random();
        for (int i = 0; i < portfolios.size(); i++) {
            int nr = i + 1;
            Portfolio portfolio = new Portfolio();
            portfolio.setUid(nr);
            portfolio.setName("Test portfolio " + nr);
            portfolios.add(portfolio);
        }
        return portfolios;
    }
}
