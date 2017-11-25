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
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;

public class PortfolioActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private PortfolioAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private PortfolioViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupRecyclerView();
    }

    private void setupRecyclerView(){
        // Setup portfolio recycler view
        mRecyclerView = findViewById(R.id.recycler_view_portfolios);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        PortfolioViewModel.Factory factory = new PortfolioViewModel.Factory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(PortfolioViewModel.class);
        viewModel.init();
        mAdapter = new PortfolioAdapter(this, viewModel.provideDataset());
        mRecyclerView.setAdapter(mAdapter);
        viewModel.loadPortfolios().observe(this, credentials -> {
            // update UI
            mAdapter.updatePortfolios(viewModel.provideDataset());
        });
    }

}
