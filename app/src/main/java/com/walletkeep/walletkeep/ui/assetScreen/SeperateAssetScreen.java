package com.walletkeep.walletkeep.ui.assetScreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.di.component.DaggerViewModelComponent;
import com.walletkeep.walletkeep.di.component.ViewModelComponent;
import com.walletkeep.walletkeep.viewmodel.SeperateAssetViewModel;

public class SeperateAssetScreen extends AppCompatActivity {
    private SeperateAssetViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seperate_asset_screen);

        String currencyTicker = "ETH";

        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            currencyTicker = getIntent().getExtras().getString("currency_ticker", "ETH");
        }

        setupView(currencyTicker);

    }

    private void setupView(String currencyTicker) {
        // Initialise view model
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .repositoryComponent(((WalletKeepApp) getApplication()).component())
                .build();
        viewModel = component.getSeperateAssetViewModel();
        viewModel.init(currencyTicker);

        // Initialise UI elements
        TextView mTextViewName = findViewById(R.id.asset_name);
        TextView mTextViewHoldings = findViewById(R.id.holdings_text);
        TextView mTextViewPrice = findViewById(R.id.asset_price);
        TextView mTextViewChange = findViewById(R.id.asset_change);
        ImageView mImageView = findViewById(R.id.asset_icon);
        TextView mTextViewMarketCap = findViewById(R.id.asset_market_cap);
        TextView mTextViewVolume = findViewById(R.id.asset_volume);
        TextView mTextViewExposure = findViewById(R.id.exposure_text);

        viewModel.loadCurrency().observe(this, currency -> {
            if (currency == null) return;

            mTextViewName.setText(currency.getName());

            Glide.with(this)
                    .load(String.format("https://yorickvanzweeden.nl/walletkeep/icons/%s.png", currency.getTicker().toUpperCase()))
                    .placeholder(R.drawable.ethereum)
                    .error(R.drawable.ethereum)
                    .centerCrop()
                    .into((ImageView)findViewById(R.id.asset_icon));
        });


    }
}
