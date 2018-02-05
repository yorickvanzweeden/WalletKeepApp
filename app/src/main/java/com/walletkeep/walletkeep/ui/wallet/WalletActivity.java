package com.walletkeep.walletkeep.ui.wallet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.DaggerViewModelComponent;
import com.walletkeep.walletkeep.di.component.ViewModelComponent;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletActivity extends AppCompatActivity {
    @BindView(R.id.wallet_activity_toolbar) Toolbar toolbar;
    @BindView(R.id.wallet_activity_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.wallet_activity_fab_addTransaction) FloatingActionButton fabAddTransaction;
    @BindView(R.id.wallet_activity_fab_addNakedWallet) FloatingActionButton fabAddNakedWallet;
    @BindView(R.id.wallet_activity_fab_addExchangeWallet) FloatingActionButton fabAddExchangeWallet;
    @BindView(R.id.wallet_activity_fab_menu) FloatingActionsMenu fabMenu;
    @BindView(R.id.wallet_activity_fab_overlay) FrameLayout fabOverlay;
    private WalletViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_activity);
        ButterKnife.bind(this);
        int portfolioId = getIntent().getExtras().getInt("portfolio_id");
        setTitle("My Wallets");
        setupOverlay(portfolioId);
        setupRecyclerView(portfolioId);
    }

    /**
     * Sets up the recycler view containing the wallets
     *
     * @param portfolioId Id of the portfolio containing the wallets
     */
    private void setupRecyclerView(int portfolioId) {
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        // Initialise view model
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .repositoryComponent(((WalletKeepApp) getApplication()).component())
                .build();
        viewModel = component.getWalletViewModel();
        viewModel.init(portfolioId);

        // Create and set adapter
        WalletAdapter mAdapter = new WalletAdapter(this, viewModel);
        recyclerView.setAdapter(mAdapter);

        // Update recycler view if wallets are changed
        viewModel.loadWallets().observe(this, mAdapter::updateWallets);
    }

    /**
     * Setup overlay items such as the toolbar and the fab
     *
     * @param portfolioId Id of the portfolio to which the wallets belong
     */
    private void setupOverlay(int portfolioId) {
        // Setup toolbar
        setSupportActionBar(toolbar);

        // Setup fab
        fabOverlay.setOnClickListener(view -> fabMenu.collapse());
        fabOverlay.setClickable(false);

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                fabOverlay.setBackgroundColor(Color.argb(200, 255, 255, 255));
                fabOverlay.setClickable(true);
            }

            @Override
            public void onMenuCollapsed() {
                fabOverlay.setBackgroundColor(Color.TRANSPARENT);
                fabOverlay.setClickable(false);
            }
        });

        fabAddExchangeWallet.setOnClickListener(view -> {
            addWallet(portfolioId, WalletWithRelations.Type.Exchange);
            fabMenu.collapse();
        });
        fabAddNakedWallet.setOnClickListener(view -> {
            addWallet(portfolioId, WalletWithRelations.Type.Naked);
            fabMenu.collapse();
        });
        fabAddTransaction.setOnClickListener(view -> {
            addWallet(portfolioId, WalletWithRelations.Type.Transaction);
            fabMenu.collapse();
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Start add/edit wallet intent
     *
     * @param portfolioId Portfolio to which the walletButton belongs
     */
    private void addWallet(int portfolioId, WalletWithRelations.Type fragmentType) {
        Intent intent = new Intent(this, EditWalletActivity.class);
        intent.putExtra("portfolio_id", portfolioId);
        intent.putExtra("fragment_type", fragmentType.getValue());
        this.startActivity(intent);
    }
}
