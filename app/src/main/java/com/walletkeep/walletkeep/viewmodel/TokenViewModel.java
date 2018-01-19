package com.walletkeep.walletkeep.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.walletkeep.walletkeep.db.entity.WalletToken;
import com.walletkeep.walletkeep.repository.TokenRepository;

import java.util.List;

public class TokenViewModel extends ViewModel {
    private LiveData<List<WalletToken>> tokens;
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
    public LiveData<List<WalletToken>> loadTokens() {
        return tokens;
    }
    public void insertTokens(List<WalletToken> tokens) {
        if (tokens != null && tokens.size() > 0) tokenRepository.insertTokens(tokens);
    }
    public void deleteTokens(List<WalletToken> tokens) {
        if (tokens != null && tokens.size() > 0) tokenRepository.deleteTokens(tokens);
    }
}
