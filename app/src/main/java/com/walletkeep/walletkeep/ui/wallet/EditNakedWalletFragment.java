package com.walletkeep.walletkeep.ui.wallet;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

public class EditNakedWalletFragment extends Fragment implements EditWalletActivity.IWalletFragment{
    private ArrayAdapter<CharSequence> mAdapter;
    private View view;

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
     * Setup spinner
     * @param view View of the fragment
     * @param savedInstanceState Previous state of the fragment
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = getView();

        // Setup spinner
        Spinner spinner = view.findViewById(R.id.editWallet_naked_spinner_currency);
        mAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.supported_currency_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);
    }

    /**
     * Update form with saved wallet data
     * @param wallet Wallet containing the data for the form
     */
    @Override
    public void updateForm(WalletWithRelations wallet) {
        ((EditText)view.findViewById(R.id.editWallet_naked_editText_address)).setText(wallet.getAddress());
        ((Spinner)getActivity().findViewById(R.id.editWallet_naked_spinner_currency)).setSelection(
                mAdapter.getPosition(wallet.getAddressCurrency())
        );
    }

    /**
     * Save form data to a wallet
     * @param wallet Wallet to save data in
     * @return Updated wallet
     */
    @Override
    public WalletWithRelations updateWallet(WalletWithRelations wallet) {
        String address = ((EditText)view.findViewById(R.id.editWallet_naked_editText_address)).getText().toString();
        wallet.wallet.setAddress(address);

        String currency = ((Spinner)view.findViewById(R.id.editWallet_naked_spinner_currency)).getSelectedItem().toString();
        wallet.wallet.setAddressCurrency(currency);

        return wallet;
    }
}
