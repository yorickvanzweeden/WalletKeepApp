package com.walletkeep.walletkeep.ui.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.util.Converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditTransactionFragment extends Fragment implements EditWalletActivity.IWalletFragment {
    private View view;

    /**
     * Constructor: Required empty public constructor
     */
    public EditTransactionFragment() {}

    /**
     * Inflate view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.editwallet_transaction_fragment, container, false);
    }

    /**
     * Initialise view
     * @param view View of the fragment
     * @param savedInstanceState Previous state of the fragment
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = getView();
    }

    /**
     * Update form with saved wallet data
     * @param wallet Wallet containing the data for the form
     */
    @Override
    public void updateForm(WalletWithRelations wallet) {
        ((EditText)view.findViewById(R.id.editWallet_transaction_editText_currency))
                .setText(wallet.assets.get(0).getCurrencyTicker());
        ((EditText)view.findViewById(R.id.editWallet_transaction_editText_amount))
                .setText(Float.toString(wallet.assets.get(0).getAmount()));
    }

    /**
     * Save form data to a wallet
     * @param wallet Wallet to save data in
     * @return Updated wallet
     */
    @Override
    public WalletWithRelations updateWallet(WalletWithRelations wallet) {
        // Retrieve data from form
        String currency = ((EditText)getView().findViewById(R.id.editWallet_transaction_editText_currency)).getText().toString().trim().toUpperCase();
        String amountString = ((EditText)getView().findViewById(R.id.editWallet_transaction_editText_amount)).getText().toString();

        // Check user input
        float amount = Converters.userInputToFloat(amountString);

        // Define new asset
        List<Asset> assets = new ArrayList<Asset>() {{
            add(new Asset(wallet.wallet.getId(), currency, amount));
        }};
        assets.get(0).setTimestamp(new Date());

        // Overwrite wallet's assets
        wallet.assets = assets;

        return wallet;
    }
}
