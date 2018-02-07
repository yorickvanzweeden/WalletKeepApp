package com.walletkeep.walletkeep.ui.asset;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssetActivity extends AppCompatActivity {
    @BindView(R.id.asset_name) TextView mTextViewName;
    @BindView(R.id.holdings_text) TextView mTextViewHoldings;
    @BindView(R.id.asset_price) TextView mTextViewPrice;
    @BindView(R.id.asset_change) TextView mTextViewChange;
    @BindView(R.id.asset_icon) ImageView mImageView;
    @BindView(R.id.asset_market_cap) TextView mTextViewMarketCap;
    @BindView(R.id.asset_volume) TextView mTextViewVolume;
    @BindView(R.id.exposure_text) TextView mTextViewExposure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seperate_asset_screen);
        ButterKnife.bind(this);

        String currencyTicker = "ETH";

        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            currencyTicker = getIntent().getExtras().getString("currency_ticker", "ETH");
        }

        setupView(currencyTicker);

    }

    private void setupView(String currencyTicker) {
        // Initialise view model
//        ViewModelComponent component = DaggerViewModelComponent.builder()
//                .repositoryComponent(((WalletKeepApp) getApplication()).component())
//                .build();
//        viewModel = component.getSeperateAssetViewModel();
//        viewModel.init(currencyTicker);
//
//        // Initialise UI elements
//        viewModel.loadCurrency().observe(this, currency -> {
//            if (currency == null) return;
//
//            mTextViewName.setText(currency.getName());
//
//            Glide.with(this)
//                    .load(String.format("https://yorickvanzweeden.nl/walletkeep/icons/%s.png", currency.getTicker().toUpperCase()))
//                    .placeholder(R.drawable.ethereum)
//                    .error(R.drawable.ethereum)
//                    .centerCrop()
//                    .into((ImageView)findViewById(R.id.asset_icon));
//        });
    }
}
