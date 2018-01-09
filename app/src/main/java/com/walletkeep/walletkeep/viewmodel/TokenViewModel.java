package com.walletkeep.walletkeep.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.walletkeep.walletkeep.db.entity.WalletTokenA;
import com.walletkeep.walletkeep.repository.TokenRepository;

import java.util.List;

public class TokenViewModel extends ViewModel {
    private LiveData<List<WalletTokenA>> tokens;
    private TokenRepository tokenRepository;

    /**
     * Constructor: Sets repository
     * @param tokenRepository Repository to interact with database
     */
    public TokenViewModel(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Gets the tokens
     * @param walletId Id of the wallet containing the tokens
     */
    public void init(int walletId) {
        if (this.tokens == null)
            this.tokens = tokenRepository.getTokens(walletId);
    }

    /**
     * Loads all the walletButton (async)
     * @return Livedata list of tokens
     */
    public LiveData<List<WalletTokenA>> loadTokens() {
        return tokens;
    }
    public void insertTokens(List<WalletTokenA> tokens) { tokenRepository.insertTokens(tokens); }
    public void deleteTokens(List<WalletTokenA> tokens) { tokenRepository.deleteTokens(tokens); }
}
