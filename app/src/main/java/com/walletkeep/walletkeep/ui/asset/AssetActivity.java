package com.walletkeep.walletkeep.ui.asset;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.viewmodel.AssetViewModel;

import java.util.List;

public class AssetActivity extends AppCompatActivity {
    private AssetViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        int portfolioId = getIntent().getExtras().getInt("portfolio_id");

        setupOverlay();
        setupRecyclerView(portfolioId);
    }

    /**
     * Setup overlay items such as the toolbar and the fab
     */
    private void setupOverlay(){
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup fab
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Sets up the recycler view containing the wallets
     */
    private void setupRecyclerView(int portfolioId){
        // Link to the right UI item
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_assets);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Initialise view model
        AssetViewModel.Factory factory = new AssetViewModel.Factory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(AssetViewModel.class);
        viewModel.init(portfolioId);

        // Create and set adapter
        AssetAdapter mAdapter = new AssetAdapter(this, viewModel.getAggregatedAssets().getValue());
        mRecyclerView.setAdapter(mAdapter);

        // Update recycler view if portfolios are changed
        viewModel.getAggregatedAssets().observe(this, aggregatedAssets -> {
            mAdapter.updateAggregatedAssets(aggregatedAssets);
            updatePortfolioValue(aggregatedAssets);
        });
    }

    private void updatePortfolioValue(List<AggregatedAsset> aggregatedAssets) {
        float total = 0;

        for (AggregatedAsset asset: aggregatedAssets) {
            total += asset.getAmount() * asset.getLatestCurrencyPrice().getPriceEur();
        }

        TextView portfolioValueTextView = findViewById(R.id.asset_portfolio_value);
        portfolioValueTextView.setText(String.format("€%.2f", total));
    }

}