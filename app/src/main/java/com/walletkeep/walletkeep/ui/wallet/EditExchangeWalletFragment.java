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
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.DaggerViewModelComponent;
import com.walletkeep.walletkeep.di.component.ViewModelComponent;

import java.util.ArrayList;


public class EditExchangeWalletFragment extends Fragment implements EditWalletActivity.IWalletFragment {
    private ArrayAdapter<CharSequence> mAdapter;
    private View view;

    /**
     * Constructor:
     *
     * Required empty public constructor
     */
    public EditExchangeWalletFragment() {}

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
        return inflater.inflate(R.layout.editwallet_exchange_fragment, container, false);
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
        Spinner spinner = view.findViewById(R.id.editWallet_exchange_spinner_exchange);
        mAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.exchange_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);

        // Set Passphrase when right exchanges are picked
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (mAdapter.getPosition("GDAX") == position) setupRecyclerView();
                else resetRecyclerView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { resetRecyclerView(); }
        });
    }

    // Set passphrase content
    private void setupRecyclerView(){
        EditText mEditPassphrase = view.findViewById(R.id.editWallet_exchange_editText_passphrase);
        TextView mPassphraseText = view.findViewById(R.id.editWallet_exchange_textView_label_passphrase);
        mPassphraseText.setVisibility(View.VISIBLE);
        mEditPassphrase.setVisibility(View.VISIBLE);
    }

    // Remove passphrase content
    private void resetRecyclerView() {
        // Link to the right UI item
        EditText mEditPassphrase = view.findViewById(R.id.editWallet_exchange_editText_passphrase);
        TextView mPassphraseText = view.findViewById(R.id.editWallet_exchange_textView_label_passphrase);
        mPassphraseText.setVisibility(View.INVISIBLE);
        mEditPassphrase.setVisibility(View.INVISIBLE);
    }

    /**
     * Update form with saved wallet data
     * @param wallet Wallet containing the data for the form
     */
    @Override
    public void updateForm(WalletWithRelations wallet){
        //Update form with Wallet data
        ExchangeCredentials credentials = wallet.getCredentials();
        if (credentials != null) {
            ((EditText)view.findViewById(R.id.editWallet_exchange_editText_key)).setText(wallet.getCredentials().getKey());
            ((EditText)view.findViewById(R.id.editWallet_exchange_editText_secret)).setText(wallet.getCredentials().getSecret());
            ((EditText)view.findViewById(R.id.editWallet_exchange_editText_passphrase)).setText(wallet.getCredentials().getPassphrase());
        }
        ((Spinner)getActivity().findViewById(R.id.editWallet_exchange_spinner_exchange)).setSelection(
                mAdapter.getPosition(wallet.getExchangeName())
        );
    }

    /**
     * Save form data to a wallet
     * @param wallet Wallet to save data in
     * @return Updated wallet
     */
    @Override
    public WalletWithRelations updateWallet(WalletWithRelations wallet){
        // Set exchange
        String exchange = ((Spinner)view.findViewById(R.id.editWallet_exchange_spinner_exchange)).getSelectedItem().toString();
        wallet.wallet.setExchangeName(exchange);

        // Set credentials
        String key = ((EditText)view.findViewById(R.id.editWallet_exchange_editText_key)).getText().toString();
        String secret = ((EditText)view.findViewById(R.id.editWallet_exchange_editText_secret)).getText().toString();
        String passphrase = ((EditText)view.findViewById(R.id.editWallet_exchange_editText_passphrase)).getText().toString();

        ExchangeCredentials exchangeCredentials = new ExchangeCredentials(key, secret, passphrase);
        exchangeCredentials.setWallet_id(wallet.wallet.getId());
        if(wallet.getCredentials() != null) exchangeCredentials.setId(wallet.getCredentials().getId());
        wallet.exchangeCredentials = new ArrayList<ExchangeCredentials>() {{ add(exchangeCredentials); }};

        return wallet;
    }
}
