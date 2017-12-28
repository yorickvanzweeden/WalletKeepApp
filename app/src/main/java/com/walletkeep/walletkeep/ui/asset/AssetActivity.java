package com.walletkeep.walletkeep.ui.asset;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.ui.portfolio.PortfolioActivity;
import com.walletkeep.walletkeep.ui.wallet.WalletActivity;
import com.walletkeep.walletkeep.viewmodel.AssetViewModel;

import java.util.List;

public class AssetActivity extends AppCompatActivity {
    private AssetViewModel viewModel;
    private List<WalletWithRelations> wallets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);

        int portfolioId = 1;
        String portfolioName = "My portfolio";
        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            portfolioId = getIntent().getExtras().getInt("portfolio_id", 1);
            portfolioName = getIntent().getExtras().getString("portfolio_name", " My Portfolio");
        }

        setupOverlay(portfolioId);
        setupRecyclerView(portfolioId);
        setupSwipeRefreshLayout();
    }

    private void setupOverlay(int portfolioId){
        // Setup fabs
        FloatingActionButton fab = findViewById(R.id.fab_edit_wallets);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, WalletActivity.class);
            intent.putExtra("portfolio_id", portfolioId);
            this.startActivity(intent);
        });

        FloatingActionButton fab2 = findViewById(R.id.fab_edit_portfolios);
        fab2.setOnClickListener(view -> startActivity(new Intent(this, PortfolioActivity.class)));
    }

    private void setupSwipeRefreshLayout(){
        // Observe wallets
        viewModel.getWallets().observe(this, wallets -> this.wallets = wallets);

        // Add error listener
        AssetRepository.ErrorListener errorListener = message -> {
            Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };

        // Refresh --> Update wallets
        SwipeRefreshLayout swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            viewModel.fetch(wallets, errorListener);
            swipeContainer.setRefreshing(false);
        });
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

        // Update recycler view and portfolio value if portfolios are changed
        viewModel.getAggregatedAssets().observe(this, aggregatedAssets -> {
            mAdapter.updateAggregatedAssets(aggregatedAssets);
            updatePortfolioValue(aggregatedAssets);
        });
    }

    /**
     * Updates portfolio value
     * @param aggregatedAssets List of aggregates assets
     */
    private void updatePortfolioValue(List<AggregatedAsset> aggregatedAssets) {
        // Calculate total
        float total = 0;
        for (AggregatedAsset asset: aggregatedAssets) {
            total += asset.getAmount() * asset.getLatestCurrencyPrice();
        }

        // Set text of TextView
        TextView portfolioValueTextView = findViewById(R.id.asset_portfolio_value);
        portfolioValueTextView.setText(String.format("€%.2f", total));
    }

}
