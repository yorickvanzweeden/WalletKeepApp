package com.walletkeep.walletkeep.ui.portfolio;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.ui.wallet.WalletAdapter;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

public class PortfolioActivity extends AppCompatActivity {
    private PortfolioViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        setupOverlay();
        setupRecyclerView();
    }

    /**
     * Setup overlay items such as the toolbar and the fab
     */
    private void setupOverlay(){
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addPortfolio());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Sets up the recycler view containing the wallets
     */
    private void setupRecyclerView(){
        // Link to the right UI item
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_portfolios);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Initialise view model
        PortfolioViewModel.Factory factory = new PortfolioViewModel.Factory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(PortfolioViewModel.class);
        viewModel.init();

        // Create and set adapter
        PortfolioAdapter mAdapter = new PortfolioAdapter(this, viewModel.provideDataset());
        mRecyclerView.setAdapter(mAdapter);

        // Update recycler view if portfolios are changed
        viewModel.loadPortfolios().observe(this, portfolios ->
                mAdapter.updatePortfolios(viewModel.provideDataset()));
    }

    /**
     * Show dialog (todo) and add portfolio
     */
    private void addPortfolio(){
        Portfolio p = new Portfolio("Portfolio");
        viewModel.savePortfolio(p);
    }
}
