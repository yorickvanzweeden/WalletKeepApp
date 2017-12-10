package com.walletkeep.walletkeep.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.WalletKeepApp;
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
     * Provides dataset for Recyclerview
     * @return List of strings
     */
    public List<Portfolio> provideDataset(){
        if (this.portfolios == null || this.portfolios.getValue() == null){
            return null;
        }
        return this.portfolios.getValue();
    }

    /**
     * Returns view model with repository
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final PortfolioRepository mRepository;

        public Factory(@NonNull Application application) {
            mRepository = ((WalletKeepApp) application).getPortfolioRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new PortfolioViewModel(mRepository);
        }
    }
}
