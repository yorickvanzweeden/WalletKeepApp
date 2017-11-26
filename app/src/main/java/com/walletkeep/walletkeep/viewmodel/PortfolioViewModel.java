package com.walletkeep.walletkeep.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.db.repository.PortfolioRepository;

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
    public String[] provideDataset(){
        if (this.portfolios == null || this.portfolios.getValue() == null){
            String[] dataset = {"No portfolios found"};
            return dataset;
        }

        List<Portfolio> portfolios = this.portfolios.getValue();
        String[] dataset = new String[portfolios.size()];
        for (int i = 0; i < portfolios.size(); i++) {
            Portfolio p = portfolios.get(i);
            dataset[i] = p.getName();
        }
        return dataset;
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
