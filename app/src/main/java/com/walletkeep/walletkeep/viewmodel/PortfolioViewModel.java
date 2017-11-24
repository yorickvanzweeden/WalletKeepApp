package com.walletkeep.walletkeep.viewmodel;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.ArrayList;
import java.util.List;


public class PortfolioViewModel extends ViewModel {
    private LiveData<List<Portfolio>> portfolios;
    private AppDatabase mDatabase;

    public PortfolioViewModel(AppDatabase mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void init() {
        if (this.portfolios != null) return;
        this.portfolios = this.loadPortfolios();
    }

    /**
     * Create new portfolio
     * @param id Id of portfolio
     * @param name Name of portfolio
     */
    public void createPortfolio(int id, String name){
        List<Portfolio> portfolios = new ArrayList<Portfolio>() {{
            add(new Portfolio(id, name));
        }};

        AsyncTask.execute(() -> mDatabase.portfolioDao().insertAll(portfolios));

    }

    /**
     * Gets all portfolios from database
     * @return
     */
    public LiveData<List<Portfolio>> loadPortfolios() {
        return mDatabase.portfolioDao().loadPortfolios();
    }

    /**
     * A creator is used to inject the product ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the product ID can be passed in a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final AppDatabase mDatabase;

        public Factory(@NonNull Application application) {
            mDatabase = ((WalletKeepApp) application).getDatabase();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new PortfolioViewModel(mDatabase);
        }
    }
}
