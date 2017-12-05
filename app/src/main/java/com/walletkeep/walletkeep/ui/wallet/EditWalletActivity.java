package com.walletkeep.walletkeep.ui.wallet;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

public class EditWalletActivity extends AppCompatActivity {
    private WalletViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet);
        int walletId = getIntent().getExtras().getInt("wallet_id");
        int portfolioId = getIntent().getExtras().getInt("portfolio_id");

        // Initialise view model
        WalletViewModel.Factory factory = new WalletViewModel.Factory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(WalletViewModel.class);
        viewModel.init(portfolioId);

        Button saveButton = findViewById(R.id.button_editWallet_save);
        saveButton.setOnClickListener(view -> editWallet(portfolioId, walletId));
    }

    private void editWallet(int portfolioId, int walletId) {
        String key = ((EditText)findViewById(R.id.editText_editWallet_key)).getText().toString();
        String secret = ((EditText)findViewById(R.id.editText_editWallet_secret)).getText().toString();
        String passphrase = ((EditText)findViewById(R.id.editText_editWallet_passphrase)).getText().toString();
        String address = ((EditText)findViewById(R.id.editText_editWallet_address)).getText().toString();
        String exchange = ((EditText)findViewById(R.id.editText_editWallet_exchange)).getText().toString();

        Wallet wallet = new Wallet(portfolioId);
        if (exchange != null && exchange.length() > 1) {
            switch (Exchange.Exchanges.valueOf(exchange)) {
                case BINANCE:
                    wallet.setExchangeId("1");
                default:
                    wallet.setExchangeId("2");
            }
        } else {
            wallet.setAddress(address);
            wallet.setAddressCurrency("ETH");
        }
        wallet.setId(walletId);
        viewModel.updateWallet(wallet);

        if (exchange != null && exchange.length() > 1) {
            ExchangeCredentials exchangeCredentials = new ExchangeCredentials(key, secret, passphrase);
            exchangeCredentials.setWallet_id(wallet.getId());
            viewModel.addCredentials(exchangeCredentials);
        }
    }
}
