package com.walletkeep.walletkeep.ui.wallet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.DaggerViewModelComponent;
import com.walletkeep.walletkeep.di.DatabaseModule;
import com.walletkeep.walletkeep.di.ViewModelComponent;
import com.walletkeep.walletkeep.viewmodel.UpdateWalletViewModel;

public class EditWalletActivity extends AppCompatActivity {
    private UpdateWalletViewModel viewModel;
    private WalletWithRelations wallet;
    private Fragment fragment;
    private int fragmentType;

    /**
     * Setup the activity
     * @param savedInstanceState Previous state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editwallet_activity);

        // Get intent data
        int walletId = getIntent().getExtras().getInt("wallet_id");
        int portfolioId = getIntent().getExtras().getInt("portfolio_id");
        fragmentType = getIntent().getExtras().getInt("fragment_type");

        // Setup fragment
        setupFragment(savedInstanceState, fragmentType);

        // Initialise view model
        AppDatabase appDatabase = ((WalletKeepApp)getApplication()).getDatabase();
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .databaseModule(new DatabaseModule(appDatabase))
                .build();
        viewModel = component.getUpdateWalletViewModel();
        viewModel.init(walletId);

        // Observe wallet --> Update form if changed
        viewModel.loadWallet().observe(this, wallet -> {
            this.wallet = wallet;
            if (wallet == null) return;
            ((IWalletFragment) fragment).updateForm(wallet);

            // Set name
            String name = wallet.wallet.getName();
            ((EditText)findViewById(R.id.editwallet_activity_textView_name)).setText(name);
        });

        // Setup save button
        Button saveButton = findViewById(R.id.editWallet_activity_button_save);
        saveButton.setOnClickListener(view -> saveWallet(portfolioId));

        // Setup delete button
        Button deleteButton = findViewById(R.id.editWallet_activity_button_delete);
        deleteButton.setOnClickListener(view -> deleteWallet());
    }

    /**
     * Setup the fragment
     * @param savedInstanceState
     * @param fragmentType Type of fragment (exchange|naked|transaction)
     */
    private void setupFragment(Bundle savedInstanceState, int fragmentType){
        // Check if fragment exists
        if (findViewById(R.id.editwallet_activity_fragmentContainer) == null || savedInstanceState != null) {
            return;
        }

        // Add exchange fragment for exchange wallets, naked fragment for naked wallets
        switch (WalletWithRelations.Type.values()[fragmentType]) {
            case Exchange:
                fragment = new EditExchangeWalletFragment();
                break;
            case Naked:
                fragment = new EditNakedWalletFragment();
                break;
            case Transaction:
                fragment = new EditTransactionFragment();
                break;
        }

        // Place fragment in container
        fragment.setArguments(getIntent().getExtras()); //TODO: Is this line necessary?
        getSupportFragmentManager().beginTransaction()
                .add(R.id.editwallet_activity_fragmentContainer, fragment).commit();
    }

    /**
     * Save a wallet
     * @param portfolioId Id of the portfolio the wallet belongs to
     */
    private void saveWallet(int portfolioId){
        Boolean shouldInsert = false;

        // Create wallet if not existent
        if (wallet == null) {
            wallet = new WalletWithRelations();
            wallet.wallet = new Wallet(portfolioId);
            wallet.wallet.setType(fragmentType);
            shouldInsert = true;
        }

        // Update wallet with form data
        wallet = ((IWalletFragment) fragment).updateWallet(wallet);
        String name = ((EditText)findViewById(R.id.editwallet_activity_textView_name)).getText().toString();
        wallet.wallet.setName(name);

        // Save wallet to database
        if(shouldInsert) { viewModel.addWallet(wallet);
        } else { viewModel.updateWallet(wallet); }

        // Kill activity --> Return to previous activity
        finish();
    }

    /**
     * Delete wallet
     */
    private void deleteWallet() {
        // Don't delete if not saved
        if(wallet != null) viewModel.deleteWallet(wallet.wallet);

        // Kill activity --> Return to previous activity
        finish();
    }

    /**
     * Interface for naked and exchange wallet fragments
     */
    public interface IWalletFragment{
        void updateForm(WalletWithRelations wallet);
        WalletWithRelations updateWallet(WalletWithRelations wallet);
    }
}
