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
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.ArrayList;


public class EditExchangeWalletFragment extends Fragment implements EditWalletActivity.IWalletFragment {
    private ArrayAdapter<CharSequence> mAdapter;
    private View view;

    public EditExchangeWalletFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_exchange_wallet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = getView();

        // Setup spinner
        Spinner spinner = view.findViewById(R.id.spinner_editWallet_exchange);
        mAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.exchange_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);
    }

    @Override
    public void updateForm(WalletWithRelations wallet){
        //Update form with Wallet data
        ExchangeCredentials credentials = wallet.getCredentials();
        if (credentials != null) {
            ((EditText)view.findViewById(R.id.editText_editWallet_key)).setText(wallet.getCredentials().getKey());
            ((EditText)view.findViewById(R.id.editText_editWallet_secret)).setText(wallet.getCredentials().getSecret());
            ((EditText)view.findViewById(R.id.editText_editWallet_passphrase)).setText(wallet.getCredentials().getPassphrase());
        }
        Exchange ec = wallet.getExchange();
        if (ec != null) {
            ((Spinner)getActivity().findViewById(R.id.spinner_editWallet_exchange)).setSelection(
                    mAdapter.getPosition(ec.getName())
            );
        }
    }

    @Override
    public WalletWithRelations updateWallet(WalletWithRelations wallet){
        // Set exchange
        String exchange = ((Spinner)view.findViewById(R.id.spinner_editWallet_exchange)).getSelectedItem().toString();

        if (exchange == "Binance")
            wallet.wallet.setExchangeId("1");
        else
            wallet.wallet.setExchangeId("2");

        // Set credentials
        String key = ((EditText)view.findViewById(R.id.editText_editWallet_key)).getText().toString();
        String secret = ((EditText)view.findViewById(R.id.editText_editWallet_secret)).getText().toString();
        String passphrase = ((EditText)view.findViewById(R.id.editText_editWallet_passphrase)).getText().toString();

        ExchangeCredentials exchangeCredentials = new ExchangeCredentials(key, secret, passphrase);
        exchangeCredentials.setWallet_id(wallet.wallet.getId());
        if(wallet.getCredentials() != null) exchangeCredentials.setId(wallet.getCredentials().getId());
        wallet.exchangeCredentials = new ArrayList<ExchangeCredentials>() {{ add(exchangeCredentials); }};

        return wallet;
    }
}
