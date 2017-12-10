package com.walletkeep.walletkeep.ui.wallet;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.viewmodel.UpdateWalletViewModel;

public class EditWalletActivity extends AppCompatActivity {
    private UpdateWalletViewModel viewModel;
    private WalletWithRelations wallet;
    private Boolean addExchange;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet);

        // Get intent data
        int walletId = getIntent().getExtras().getInt("wallet_id");
        int portfolioId = getIntent().getExtras().getInt("portfolio_id");
        addExchange = getIntent().getExtras().getBoolean("add_exchange");

        // Setup fragment
        setupFragment(savedInstanceState, addExchange);

        // Initialise view model
        UpdateWalletViewModel.Factory factory = new UpdateWalletViewModel.Factory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(UpdateWalletViewModel.class);
        viewModel.init(walletId);

        // Observe wallet
        viewModel.loadWallet().observe(this, wallet -> {
            this.wallet = wallet;
            this.updateForm();
        });

        // Setup save button
        Button saveButton = findViewById(R.id.button_editWallet_save);
        saveButton.setOnClickListener(view -> saveWallet(portfolioId));

        // Setup delete button
        Button deleteButton = findViewById(R.id.button_editWallet_delete);
        deleteButton.setOnClickListener(view -> deleteWallet());
    }

    private void setupFragment(Bundle savedInstanceState, boolean addExchange){
        if (findViewById(R.id.fragment_container) == null || savedInstanceState != null) {
            return;
        }

        if (addExchange) fragment = new EditExchangeWalletFragment();
        else fragment = new EditNakedWalletFragment();

        fragment.setArguments(getIntent().getExtras()); //TODO: Is this line necessary?
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment).commit();
    }

    private void updateForm(){
        if (wallet == null) return;
        ((IWalletFragment) fragment).updateForm(wallet);
    }

    private void saveWallet(int portfolioId){
        Boolean shouldInsert = false;

        // Create wallet if not existent
        if (wallet == null) {
            wallet = new WalletWithRelations();
            wallet.wallet = new Wallet(portfolioId);
            shouldInsert = true;
        }

        // Update wallet with form data
        wallet = ((IWalletFragment) fragment).updateWallet(wallet);

        // Save wallet to database
        if(shouldInsert) { viewModel.addWallet(wallet);
        } else { viewModel.updateWallet(wallet); }

        finish();
    }

    private void deleteWallet() {
        if(wallet != null) viewModel.deleteWallet(wallet.wallet);
        finish();
    }

    public interface IWalletFragment{
        void updateForm(WalletWithRelations wallet);
        WalletWithRelations updateWallet(WalletWithRelations wallet);
    }
}
