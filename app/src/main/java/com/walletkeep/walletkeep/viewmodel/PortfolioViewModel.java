package com.walletkeep.walletkeep.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.repository.PortfolioRepository;

import java.util.List;


public class PortfolioViewModel extends ViewModel {
    private LiveData<List<Portfolio>> portfolios;
    private PortfolioRepository portfolioRepository;

    /**
     * Constructor: Sets repository
     * @param portfolioRepository Repository to interact with database
     */
    public PortfolioViewModel(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    /**
     * Gets the portfolios
     */
    public void init() {
        if (this.portfolios != null) return;
        this.portfolios = portfolioRepository.getPortfolios();
    }

    /**
     * Loads all the portfolios (async)
     * @return Livedata list of portfolios
     */
    public LiveData<List<Portfolio>> loadPortfolios() {
        return portfolios;
    }

    /**
     * Adds portfolio (async)
     * @param portfolio Portfolio to add
     */
    public void savePortfolio(Portfolio portfolio){
        this.portfolioRepository.addPortfolio(portfolio);
    }

    /**
     * Delete portfolio (async)
     * @param portfolio Portfolio to delete
     */
    public void deletePortfolio(Portfolio portfolio){
        this.portfolioRepository.deletePortfolio(portfolio);
    }

    /**
     * Provides dataset for Recyclerview
     * @return List of strings
     */
    public List<Portfolio> provideDataset(){
        if (this.portfolios == null || this.portfolios.getValue() == null){
            return null;
        }
        return this.portfolios.getValue();
    }
}
