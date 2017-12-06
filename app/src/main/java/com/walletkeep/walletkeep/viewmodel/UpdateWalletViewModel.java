package com.walletkeep.walletkeep.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.repository.WalletRepository;

public class UpdateWalletViewModel extends ViewModel{
    private LiveData<WalletWithRelations> wallet;
    private WalletRepository walletRepository;

    /**
     * Constructor: Sets repository
     * @param walletRepository Repository to interact with database
     */
    public UpdateWalletViewModel(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * Gets the wallets
     * @param walletId Id of the wallet
     */
    public void init(int walletId) {
        if (this.wallet == null)
            this.wallet = walletRepository.getWallet(walletId);
    }

    /**
     * Loads all the walletButton (async)
     * @return Livedata list of wallets
     */
    public LiveData<WalletWithRelations> loadWallet() {
        return wallet;
    }

    public void updateWallet(Wallet wallet){ this.walletRepository.updateWallet(wallet); }
    public void addCredentials(ExchangeCredentials credentials){ this.walletRepository.addCredentials(credentials); }


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
            return (T) new UpdateWalletViewModel(walletRepository);
        }
    }
}
