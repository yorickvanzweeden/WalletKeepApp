package com.walletkeep.walletkeep.ui.wallet;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.viewmodel.UpdateWalletViewModel;

public class EditWalletActivity extends AppCompatActivity {
    private UpdateWalletViewModel viewModel;
    private WalletWithRelations wallet;
    private ArrayAdapter<CharSequence> mAdapter;
    private Boolean deleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet);
        int walletId = getIntent().getExtras().getInt("wallet_id");

        // Initialise view model
        UpdateWalletViewModel.Factory factory = new UpdateWalletViewModel.Factory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(UpdateWalletViewModel.class);
        viewModel.init(walletId);

        viewModel.loadWallet().observe(this, wallet -> {
            if (!deleted) { loadWallet(wallet); }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner_editWallet_exchange);
        mAdapter = ArrayAdapter.createFromResource(this,
                R.array.exchange_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);

        Button saveButton = findViewById(R.id.button_editWallet_save);
        saveButton.setOnClickListener(view -> editWallet(walletId));

        Button deleteButton = findViewById(R.id.button_editWallet_delete);
        deleteButton.setOnClickListener(view -> deleteWallet());
    }

    private void editWallet(int walletId) {
        String key = ((EditText)findViewById(R.id.editText_editWallet_key)).getText().toString();
        String secret = ((EditText)findViewById(R.id.editText_editWallet_secret)).getText().toString();
        String passphrase = ((EditText)findViewById(R.id.editText_editWallet_passphrase)).getText().toString();
        String address = ((EditText)findViewById(R.id.editText_editWallet_address)).getText().toString();
        String exchange = ((Spinner)findViewById(R.id.spinner_editWallet_exchange)).getSelectedItem().toString();

        if (exchange != null && exchange.length() > 1) {
            switch (exchange) {
                case "Binance":
                    this.wallet.wallet.setExchangeId("1");
                    break;
                default:
                    this.wallet.wallet.setExchangeId("2");
            }
        } else {
            this.wallet.wallet.setAddress(address);
            this.wallet.wallet.setAddressCurrency("ETH");
        }
        this.wallet.wallet.setId(walletId);
        viewModel.updateWallet(this.wallet.wallet);

        if (exchange != null && exchange.length() > 1) {
            ExchangeCredentials exchangeCredentials = new ExchangeCredentials(key, secret, passphrase);
            exchangeCredentials.setWallet_id(this.wallet.wallet.getId());
            viewModel.addCredentials(exchangeCredentials);
        }
        finish();
    }

    private void loadWallet(WalletWithRelations wallet) {
        this.wallet = wallet;

        ExchangeCredentials credentials = wallet.getCredentials();
        if (credentials != null) {
            ((EditText)findViewById(R.id.editText_editWallet_key)).setText(wallet.getCredentials().getKey());
            ((EditText)findViewById(R.id.editText_editWallet_secret)).setText(wallet.getCredentials().getSecret());
            ((EditText)findViewById(R.id.editText_editWallet_passphrase)).setText(wallet.getCredentials().getPassphrase());
        }
        ((EditText)findViewById(R.id.editText_editWallet_address)).setText(wallet.getAddress());
        Exchange ec = wallet.getExchange();
        if (ec != null) {
            ((Spinner)findViewById(R.id.spinner_editWallet_exchange)).setSelection(
                    mAdapter.getPosition(ec.getName())
            );
        }
    }

    private void deleteWallet() {
        deleted = true;
        viewModel.deleteWallet(this.wallet.wallet);
        finish();
    }
}
