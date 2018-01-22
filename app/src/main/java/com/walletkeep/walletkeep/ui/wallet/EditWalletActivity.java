package com.walletkeep.walletkeep.ui.wallet;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.DaggerViewModelComponent;
import com.walletkeep.walletkeep.di.component.ViewModelComponent;
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

        // Get intent data
        int walletId = getIntent().getExtras().getInt("wallet_id");
        int portfolioId = getIntent().getExtras().getInt("portfolio_id");
        fragmentType = getIntent().getExtras().getInt("fragment_type");

        // Setup fragment
        getFragment(fragmentType);

        // Set theme first
        setContentView(R.layout.editwallet_activity);
        setupFragment(savedInstanceState);


        // Initialise view model
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .repositoryComponent(((WalletKeepApp)getApplication()).component())
                .build();
        viewModel = component.getUpdateWalletViewModel();
        viewModel.init(walletId);

        // Observe wallet --> Update form if changed
        viewModel.loadWallet().observe(this, wallet -> {
            this.wallet = wallet;
            if (wallet == null || fragment == null) return;
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
     * Get the fragment
     * @param fragmentType Type of fragment (exchange|naked|transaction)
     */
    private void getFragment(int fragmentType){
        // Add exchange fragment for exchange wallets, naked fragment for naked wallets
        switch (WalletWithRelations.Type.values()[fragmentType]) {

            case Exchange:
                fragment = new EditExchangeWalletFragment();
                setTheme(R.style.ExchangeTheme);
                setTitle("Exchange Account");
                break;
            case Naked:
                fragment = new EditNakedWalletFragment();
                setTheme(R.style.NakedWalletTheme);
                setTitle("Wallet Adress");
                break;
            case Transaction:
                fragment = new EditTransactionFragment();
                setTheme(R.style.TransactionTheme);
                setTitle("Transaction");
                break;
        }
    }

    /**
     * Set the fragment
     * @param savedInstanceState Previous instance of activity
     */
    private void setupFragment(Bundle savedInstanceState) {
        // Check if fragment exists
        if (fragment == null || savedInstanceState != null) {
            return;
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
        if (name.trim().length() == 0) {
            if (wallet.getType() == WalletWithRelations.Type.Exchange) {
                name = wallet.getExchangeName() + " Wallet";
            } else if (wallet.getType() == WalletWithRelations.Type.Naked) {
                name = wallet.getAddressCurrency() + " Wallet";
            } else { name = wallet.assets.get(0).getCurrencyTicker() + " Wallet"; }
        }
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
        if(wallet == null) {
            finish();
            return;
        }

        // Ask for confirmation
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirmation_delete_wallet)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    viewModel.deleteWallet(wallet.wallet);

                    // Kill activity --> Return to previous activity
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * Interface for naked and exchange wallet fragments
     */
    public interface IWalletFragment{
        void updateForm(WalletWithRelations wallet);
        WalletWithRelations updateWallet(WalletWithRelations wallet);
    }
}
