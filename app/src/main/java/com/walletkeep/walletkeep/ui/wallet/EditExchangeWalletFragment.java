package com.walletkeep.walletkeep.ui.wallet;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class EditExchangeWalletFragment extends Fragment implements EditWalletActivity.IWalletFragment {
    @BindView(R.id.editWallet_exchange_textView_label_passphrase) TextView textViewLabelPassphrase;
    @BindView(R.id.editWallet_exchange_editText_key) EditText editTextKey;
    @BindView(R.id.editWallet_exchange_editText_passphrase) EditText editTextPassphrase;
    @BindView(R.id.editWallet_exchange_editText_secret) EditText editTextSecret;
    @BindView(R.id.editWallet_exchange_spinner_exchange) Spinner spinnerExchange;
    Unbinder unbinder;

    private ArrayAdapter<CharSequence> mAdapter;
    private View view;

    public EditExchangeWalletFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editwallet_exchange_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

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
            public void onNothingSelected(AdapterView<?> adapterView) {
                resetRecyclerView();
            }
        });
    }

    // Set passphrase content
    private void setupRecyclerView() {
        textViewLabelPassphrase.setVisibility(View.VISIBLE);
        editTextPassphrase.setVisibility(View.VISIBLE);
    }

    // Remove passphrase content
    private void resetRecyclerView() {
        // Link to the right UI item
        textViewLabelPassphrase.setVisibility(View.INVISIBLE);
        textViewLabelPassphrase.setVisibility(View.INVISIBLE);
    }

    /**
     * Update form with saved wallet data
     *
     * @param wallet Wallet containing the data for the form
     */
    @Override
    public void updateForm(WalletWithRelations wallet) {
        //Update form with Wallet data
        ExchangeCredentials credentials = wallet.getCredentials();
        if (credentials != null) {
            editTextKey.setText(wallet.getCredentials().getKey());
            editTextSecret.setText(wallet.getCredentials().getSecret());
            editTextPassphrase.setText(wallet.getCredentials().getPassphrase());
        }
        spinnerExchange.setSelection(mAdapter.getPosition(wallet.getExchangeName()));
    }

    /**
     * Save form data to a wallet
     *
     * @param wallet Wallet to save data in
     * @return Updated wallet
     */
    @Override
    public WalletWithRelations updateWallet(WalletWithRelations wallet) {
        // Set exchange
        String exchange = spinnerExchange.getSelectedItem().toString();
        wallet.wallet.setExchangeName(exchange);

        // Set credentials
        String key = editTextKey.getText().toString();
        String secret = editTextSecret.getText().toString();
        String passphrase = editTextPassphrase.getText().toString();

        ExchangeCredentials exchangeCredentials = new ExchangeCredentials(key, secret, passphrase);
        exchangeCredentials.setWallet_id(wallet.wallet.getId());
        if (wallet.getCredentials() != null)
            exchangeCredentials.setId(wallet.getCredentials().getId());
        wallet.exchangeCredentials = new ArrayList<ExchangeCredentials>() {{
            add(exchangeCredentials);
        }};

        return wallet;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
