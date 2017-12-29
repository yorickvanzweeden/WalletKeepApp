package com.walletkeep.walletkeep.ui.wallet;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

public class WalletActivity extends AppCompatActivity {
    private WalletViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_activity);
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
        WalletAdapter mAdapter = new WalletAdapter(this, viewModel);
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
        FloatingActionsMenu fabmenu = findViewById(R.id.fab_menu_add_wallet);
        View overlay = findViewById(R.id.fab_overlay);
        overlay.setOnClickListener(view -> fabmenu.collapse());
        overlay.setClickable(false);

        fabmenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                overlay.setBackgroundColor(Color.argb(200,255,255,255));
                overlay.setClickable(true);
            }
            @Override
            public void onMenuCollapsed() {
                overlay.setBackgroundColor(Color.TRANSPARENT);
                overlay.setClickable(false);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_exchange_wallet);
        fab.setOnClickListener(view -> {
            addWallet(portfolioId, WalletWithRelations.Type.Exchange);
            fabmenu.collapse();
        });
        FloatingActionButton fab2 = findViewById(R.id.fab_add_naked_wallet);
        fab2.setOnClickListener(view -> {
            addWallet(portfolioId, WalletWithRelations.Type.Naked);
            fabmenu.collapse();
        });
        FloatingActionButton fab3 = findViewById(R.id.fab_add_transaction);
        fab3.setOnClickListener(view -> {
            addWallet(portfolioId, WalletWithRelations.Type.Transaction);
            fabmenu.collapse();
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Start add/edit wallet intent
     * @param portfolioId Portfolio to which the walletButton belongs
     */
    private void addWallet(int portfolioId, WalletWithRelations.Type fragmentType){
        Intent intent = new Intent(this, EditWalletActivity.class);
        intent.putExtra("portfolio_id", portfolioId);
        intent.putExtra("fragment_type", fragmentType.getValue());
        this.startActivity(intent);
    }
}
