package com.walletkeep.walletkeep.viewmodel;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.repository.WalletRepository;

import java.util.List;

public class WalletViewModel extends ViewModel {
    private LiveData<List<WalletWithRelations>> wallets;
    private WalletRepository walletRepository;

    /**
     * Constructor: Sets repository
     * @param walletRepository Repository to interact with database
     */
    public WalletViewModel(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * Gets the wallets
     * @param portfolioId Id of the portfolio containing the wallets
     */
    public void init(int portfolioId) {
        if (this.wallets == null)
            this.wallets = walletRepository.getWallets(portfolioId);
    }

    /**
     * Loads all the walletButton (async)
     * @return Livedata list of wallets
     */
    public LiveData<List<WalletWithRelations>> loadWallets() {
        return wallets;
    }

    /**
     * Adds walletButton (async)
     * @param wallet Wallet to add
     */
    public void addWallet(Wallet wallet){ this.walletRepository.addWallet(wallet); }

    public void fetch(WalletWithRelations wallet){ this.walletRepository.fetchWalletData(wallet);}


    /**
     * Returns view model with repository
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final WalletRepository walletRepository;

        public Factory(@NonNull Application application) {
            walletRepository = ((WalletKeepApp) application).getWalletRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new WalletViewModel(walletRepository);
        }
    }
}

