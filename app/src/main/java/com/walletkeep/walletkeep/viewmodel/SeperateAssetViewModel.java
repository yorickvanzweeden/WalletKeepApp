package com.walletkeep.walletkeep.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.repository.CurrencyRepository;
import com.walletkeep.walletkeep.repository.PortfolioRepository;

import java.util.List;


public class SeperateAssetViewModel extends ViewModel {
    private LiveData<Currency> currency;
    private CurrencyRepository currencyRepository;

    /**
     * Constructor: Sets repository
     * @param currencyRepository Repository to interact with database
     */
    public SeperateAssetViewModel(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public void init(String currencyTicker) {
        if (this.currency == null) {
            this.currency = currencyRepository.getCurrency(currencyTicker);
        }
    }

    public LiveData<Currency> loadCurrency (){
        return this.currency;
    }
}
