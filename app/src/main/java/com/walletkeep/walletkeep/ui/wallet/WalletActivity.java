package com.walletkeep.walletkeep.ui.wallet;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

public class WalletActivity extends AppCompatActivity {
    private WalletViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        int portfolioId = getIntent().getExtras().getInt("portfolio_id");

        setupOverlay(portfolioId);
        setupRecyclerView(portfolioId);
    }

    /**
     * Sets up the recycler view containing the wallets
     * @param portfolioId Id of the portfolio containing the wallets
     */
    private void setupRecyclerView(int portfolioId){
        // Link to the right UI item
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_wallets);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Initialise view model
        WalletViewModel.Factory factory = new WalletViewModel.Factory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(WalletViewModel.class);
        viewModel.init(portfolioId);

        // Create and set adapter
        WalletAdapter mAdapter = new WalletAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Update recycler view if wallets are changed
        viewModel.loadWallets().observe(this, mAdapter::updateWallets);
    }

    /**
     * Setup overlay items such as the toolbar and the fab
     * @param portfolioId Id of the portfolio to which the wallets belong
     */
    private void setupOverlay(int portfolioId){
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addWallet(portfolioId));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Show dialog (todo) and add wallet
     * @param portfolioId Portfolio to which the wallet belongs
     */
    private void addWallet(int portfolioId){
        Wallet p = new Wallet(portfolioId);
        viewModel.addWallet(p);
    }
}
