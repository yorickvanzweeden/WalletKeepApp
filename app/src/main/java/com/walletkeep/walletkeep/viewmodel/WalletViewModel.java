package com.walletkeep.walletkeep.viewmodel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

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
}

