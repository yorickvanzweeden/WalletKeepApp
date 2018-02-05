package com.walletkeep.walletkeep.ui.portfolio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.di.component.DaggerViewModelComponent;
import com.walletkeep.walletkeep.di.component.ViewModelComponent;
import com.walletkeep.walletkeep.ui.main.MainActivity;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PortfolioActivity extends AppCompatActivity
        implements AddPortfolioDialog.AddPortfolioDialogListener {
    @Inject
    public PortfolioViewModel viewModel;
    @BindView(R.id.portfolio_toolbar) Toolbar mToolbar;
    @BindView(R.id.portfolio_content_recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.portfolio_activity_fab) FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_activity);
        ButterKnife.bind(this);

        setupOverlay();
        setupRecyclerView();
    }

    /**
     * Setup overlay items such as the toolbar and the fab
     */
    private void setupOverlay() {
        // Setup toolbar
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(v
                -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // Setup fab
        mFab.setOnClickListener(view -> buildPortfolioDialog());
    }

    /**
     * Sets up the recycler view containing the wallets
     */
    private void setupRecyclerView() {
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Initialise view model
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .repositoryComponent(((WalletKeepApp) getApplication()).component())
                .build();
        viewModel = component.getPortfolioViewModel();
        viewModel.init();

        // Create and set adapter
        PortfolioAdapter mAdapter = new PortfolioAdapter(this, viewModel.provideDataset(), viewModel);
        mRecyclerView.setAdapter(mAdapter);

        // Update recycler view if portfolios are changed
        viewModel.loadPortfolios().observe(this, mAdapter::updatePortfolios);
    }

    /**
     * Display portfolio dialog to add a new portfolio
     */
    private void buildPortfolioDialog() {
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("portfolio_add_dialog_fragment");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        AddPortfolioDialog editNameDialog = new AddPortfolioDialog();
        editNameDialog.show(manager, "portfolio_add_dialog_fragment");
    }

    /**
     * Callback from the dialog listener: Add portfolio with given name
     *
     * @param portfolioName Name of the portfolio
     */
    @Override
    public void onDialogPositiveClick(String portfolioName) {
        Portfolio p = new Portfolio(portfolioName);
        viewModel.savePortfolio(p);
    }
}
