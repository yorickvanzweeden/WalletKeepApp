package com.walletkeep.walletkeep.db;

import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.ArrayList;
import java.util.List;


public class DataGenerator {
    public static List<Portfolio> generatePortfolios() {
        List<Portfolio> portfolios = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            int nr = i + 1;
            Portfolio portfolio = new Portfolio(nr, "Test portfolio " + nr);
            portfolios.add(portfolio);
        }
        return portfolios;
    }
}
