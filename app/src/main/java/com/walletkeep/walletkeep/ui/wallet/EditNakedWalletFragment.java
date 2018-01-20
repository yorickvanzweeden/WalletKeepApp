package com.walletkeep.walletkeep.ui.wallet;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.DaggerViewModelComponent;
import com.walletkeep.walletkeep.di.component.ViewModelComponent;
import com.walletkeep.walletkeep.viewmodel.TokenViewModel;

public class EditNakedWalletFragment extends Fragment implements EditWalletActivity.IWalletFragment{
    private ArrayAdapter<CharSequence> mAdapter;
    private View view;
    private TokenAdapter tokenAdapter;
    private TokenViewModel viewModel;
    private int walletId;

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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (mAdapter.getPosition("ETH") == position) setupRecyclerView();
                else resetRecyclerView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { resetRecyclerView(); }
        });

    }

    /**
     * Sets up the recycler view containing the wallets
     */
    private void setupRecyclerView(){
        // Link to the right UI items
        RecyclerView mRecyclerView = view.findViewById(R.id.editWallet_naked_recyclerView_token);

        TextView mTokenText = view.findViewById(R.id.editWallet_naked_tokenLabel);
        mTokenText.setVisibility(View.VISIBLE);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // Initialise view model
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .repositoryComponent(((WalletKeepApp)getActivity().getApplication()).component())
                .build();
        viewModel = component.getTokenViewModel();
        viewModel.init(walletId);
        viewModel.loadTokens().observe(this, tokens -> tokenAdapter.setWalletTokens(tokens));

        // Create and set adapter
        tokenAdapter = new TokenAdapter(view.getResources().getStringArray(R.array.tokens), walletId);
        mRecyclerView.setAdapter(tokenAdapter);
    }

    private void resetRecyclerView() {
        // Link to the right UI item
        RecyclerView mRecyclerView = view.findViewById(R.id.editWallet_naked_recyclerView_token);
        TextView mTokenText = view.findViewById(R.id.editWallet_naked_tokenLabel);
        mTokenText.setVisibility(View.INVISIBLE);
        tokenAdapter = null;
        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);
    }

    /**
     * Update form with saved wallet data
     * @param wallet Wallet containing the data for the form
     */
    @Override
    public void updateForm(WalletWithRelations wallet) {
        if (wallet.wallet != null) this.walletId = wallet.wallet.getId();

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

        if (wallet.wallet != null && tokenAdapter != null) {
            viewModel.deleteTokens(tokenAdapter.getWalletTokens(false));
            if (wallet.wallet.getId() > 0)
                viewModel.insertTokens(tokenAdapter.getWalletTokens(true));
            else wallet.tokens = tokenAdapter.getWalletTokens(true);
        }

        return wallet;
    }
}
