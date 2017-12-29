package com.walletkeep.walletkeep.ui.wallet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

public class EditNakedWalletFragment extends Fragment implements EditWalletActivity.IWalletFragment{

    /**
     * Constructor:
     *
     * Required empty public constructor
     */
    public EditNakedWalletFragment() {}

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
        return inflater.inflate(R.layout.editwallet_naked_fragment, container, false);
    }

    /**
     * Update form with saved wallet data
     * @param wallet Wallet containing the data for the form
     */
    @Override
    public void updateForm(WalletWithRelations wallet) {
        ((EditText)getView().findViewById(R.id.editText_editWallet_address)).setText(wallet.getAddress());
    }

    /**
     * Save form data to a wallet
     * @param wallet Wallet to save data in
     * @return Updated wallet
     */
    @Override
    public WalletWithRelations updateWallet(WalletWithRelations wallet) {
        String address = ((EditText)getView().findViewById(R.id.editText_editWallet_address)).getText().toString();
        wallet.wallet.setAddress(address);
        wallet.wallet.setAddressCurrency("ETH");
        return wallet;
    }
}
