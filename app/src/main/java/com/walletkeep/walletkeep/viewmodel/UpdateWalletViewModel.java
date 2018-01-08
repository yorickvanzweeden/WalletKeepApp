package com.walletkeep.walletkeep.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

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

    /**
     * Adds walletButton (async)
     * @param wallet Wallet to add
     */
    public void addWallet(WalletWithRelations wallet){
        this.walletRepository.addWalletWithRelations(wallet);
    }
    public void updateWallet(WalletWithRelations wallet){
        this.walletRepository.updateWallet(wallet.wallet);
        if (wallet.getCredentials() != null){
            this.walletRepository.updateCredentials(wallet.getCredentials());
        }
    }
    public void deleteWallet(Wallet wallet){ this.walletRepository.deleteWallet(wallet); }
}
