package com.walletkeep.walletkeep.viewmodel;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.ui.portfolio.PortfolioRepository;

import java.util.List;


public class PortfolioViewModel extends ViewModel {
    private LiveData<List<Portfolio>> portfolios;
    private PortfolioRepository portfolioRepository;

    public PortfolioViewModel(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public void init() {
        if (this.portfolios != null) return;
        this.portfolios = portfolioRepository.loadPortfolios();
    }

    public LiveData<List<Portfolio>> loadPortfolios() {
        return portfolios;
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
     * A creator is used to inject the product ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the product ID can be passed in a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final PortfolioRepository mRepository;

        public Factory(@NonNull Application application) {
            mRepository = ((WalletKeepApp) application).getRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new PortfolioViewModel(mRepository);
        }
    }
}
