package com.walletkeep.walletkeep.ui.portfolio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.di.DaggerPortfolioViewModelComponent;
import com.walletkeep.walletkeep.di.DatabaseModule;
import com.walletkeep.walletkeep.di.PortfolioViewModelComponent;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;

public class PortfolioActivity extends AppCompatActivity
        implements AddPortfolioDialog.AddPortfolioDialogListener {
    private PortfolioViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_activity);

        setupOverlay();
        setupRecyclerView();
    }

    /**
     * Setup overlay items such as the toolbar and the fab
     */
    private void setupOverlay(){
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.portfolio_dialog_editText_name);
        setSupportActionBar(toolbar);

        // Setup fab
        FloatingActionButton fab = findViewById(R.id.portfolio_activity_fab);
        fab.setOnClickListener(view -> buildPortfolioDialog());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Sets up the recycler view containing the wallets
     */
    private void setupRecyclerView(){
        // Link to the right UI item
        RecyclerView mRecyclerView = findViewById(R.id.portfolio_content_recyclerView);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        AppDatabase appDatabase = ((WalletKeepApp)getApplication()).getDatabase();
        PortfolioViewModelComponent component = DaggerPortfolioViewModelComponent.builder()
                .databaseModule(new DatabaseModule(appDatabase))
                .build();
        viewModel = component.getPortfolioViewModel();
        viewModel.init();

        // Create and set adapter
        PortfolioAdapter mAdapter = new PortfolioAdapter(this, viewModel.provideDataset(), viewModel);
        mRecyclerView.setAdapter(mAdapter);

        // Update recycler view if portfolios are changed
        viewModel.loadPortfolios().observe(this, portfolios ->
                mAdapter.updatePortfolios(portfolios));
    }

    /**
     * Display portfolio dialog to add a new portfolio
     */
    private void buildPortfolioDialog(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("portfolio_add_dialog_fragment");
        if (frag != null) { manager.beginTransaction().remove(frag).commit(); }
        AddPortfolioDialog editNameDialog = new AddPortfolioDialog();
        editNameDialog.show(manager, "portfolio_add_dialog_fragment");
    }

    /**
     * Callback from the dialog listener: Add portfolio with given name
     * @param portfolioName Name of the portfolio
     */
    @Override
    public void onDialogPositiveClick(String portfolioName) {
        Portfolio p = new Portfolio(portfolioName);
        viewModel.savePortfolio(p);
    }
}
