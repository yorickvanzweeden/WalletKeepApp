package com.walletkeep.walletkeep.ui.asset;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.DaggerViewModelComponent;
import com.walletkeep.walletkeep.di.component.ViewModelComponent;
import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.ui.IntroSlider;
import com.walletkeep.walletkeep.ui.portfolio.PortfolioActivity;
import com.walletkeep.walletkeep.ui.wallet.WalletActivity;
import com.walletkeep.walletkeep.util.AssetDistribution;
import com.walletkeep.walletkeep.viewmodel.AssetViewModel;

import java.util.Collections;
import java.util.List;

public class AssetActivity extends AppCompatActivity {
    private AssetViewModel viewModel;
    private List<WalletWithRelations> wallets;
    private List<AggregatedAsset> assets;
    private AssetAdapter mAdapter;
    private SurfaceView mSurfaceView;
    private AssetRepository.ErrorListener errorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asset_activity);

        // Check if IntroSlider should be shown
        checkFirstRun();

        int portfolioId = 1;
        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            portfolioId = getIntent().getExtras().getInt("portfolio_id", 1);
        }

        setupOverlay(portfolioId);
        setupRecyclerView(portfolioId);
        setupSwipeRefreshLayout();
    }

    @Override
    public void onResume(){
        super.onResume();

        // When the surfaceView is ready, draw distribution bar
        SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                updateDistributionBar();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
        };
        mSurfaceView.getHolder().addCallback(mSurfaceHolderCallback);
    }


    /**
     * Check if Introslider should be shown
     */
    private void checkFirstRun() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean show_intro = sharedPref.getBoolean("show_intro", true);

        if (show_intro) {
            startActivity(new Intent(this, IntroSlider.class));
            sharedPref.edit().putBoolean("show_intro", false).apply();
        }
    }

    private void setupOverlay(int portfolioId){
        // Setup volume change
        TextView volumeChange = findViewById(R.id.asset_activity_textView_change_setting);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.price_change, android.R.layout.simple_spinner_item);
        volumeChange.setOnClickListener(view -> {
            int pos = adapter.getPosition(volumeChange.getText().toString());
            String setting = adapter.getItem((pos + 1) % 3).toString();
            volumeChange.setText(setting);
            mAdapter.updateChangeSetting(setting);
        });

        // Setup fabs
        FloatingActionsMenu fabmenu = findViewById(R.id.asset_activity_fab_menu);

        com.getbase.floatingactionbutton.FloatingActionButton fab = findViewById(R.id.asset_activity_fab_wallets);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, WalletActivity.class);
            intent.putExtra("portfolio_id", portfolioId);
            this.startActivity(intent);
        });

        com.getbase.floatingactionbutton.FloatingActionButton fab2 = findViewById(R.id.asset_activity_fab_portfolios);
        fab2.setOnClickListener(view -> startActivity(new Intent(this, PortfolioActivity.class)));

        // Initialise surfaceView
        mSurfaceView = findViewById(R.id.asset_activity_surfaceView);
        mSurfaceView.setVisibility(View.GONE);
    }

    private void setupSwipeRefreshLayout(){
        // Observe wallets
        viewModel.getWallets().observe(this, wallets -> this.wallets = wallets);

        // Refresh --> Update wallets
        SwipeRefreshLayout swipeContainer = findViewById(R.id.asset_content_swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            viewModel.assetFetch(wallets, errorListener);
            viewModel.priceFetch(assets, errorListener, true);
            swipeContainer.setRefreshing(false);
        });
    }

    /**
     * Sets up the recycler view containing the wallets
     */
    private void setupRecyclerView(int portfolioId){
        // Link to the right UI item
        RecyclerView mRecyclerView = findViewById(R.id.asset_content_recyclerView);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Add error listener
        errorListener = message -> Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG).show();

        // Initialise view model
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .repositoryComponent(((WalletKeepApp)getApplication()).component())
                .build();
        viewModel = component.getAssetViewModel();
        viewModel.init(portfolioId, errorListener);

        // Create and set adapter
        mAdapter = new AssetAdapter(this, viewModel.getAggregatedAssets().getValue());
        mRecyclerView.setAdapter(mAdapter);

        // Update recycler view and portfolio value if portfolios are changed
        viewModel.getAggregatedAssets().observe(this, aggregatedAssets -> {
            this.assets = aggregatedAssets;
            onUpdated();
        });
    }

    private void onUpdated() {
        // Sort on size
        Collections.sort(assets, new AggregatedAsset.AssetComparator());

        // Remove assets which are valued less than 1 euro
        int index = -1;
        int priceFetchIndex = -1;

        for (int i = assets.size() - 1; i >= 0; i--) {
            if (assets.get(i).getLatestCurrencyPrice() == 0) priceFetchIndex = i;
            if (assets.get(i).getEurValue() > 1) break;
            index = i;
        }
        if (priceFetchIndex != -1) viewModel.priceFetch(assets.subList(priceFetchIndex, assets.size()), errorListener, false);
        if (index != -1) assets = assets.subList(0, index);

        // Update recycler view
        mAdapter.updateAggregatedAssets(assets);

        // Update portfolio total and distribution bar
        updatePortfolioValue();
        updateDistributionBar();
    }


    /**
     * Updates portfolio value
     */
    private void updatePortfolioValue() {
        // Calculate total
        float total = 0;
        for (AggregatedAsset asset: this.assets) {
            total += asset.getAmount() * asset.getLatestCurrencyPrice();
        }

        // Set text of TextView
        TextView portfolioValueTextView = findViewById(R.id.asset_activity_textView_portfolio_value);
        portfolioValueTextView.setText(String.format("€%.2f", total));
    }

    /**
     * Updates distribution bar
     */
    private void updateDistributionBar() {
        if (this.assets == null || this.assets.size() == 0) {
            if (mSurfaceView.getVisibility() != View.GONE)
                mSurfaceView.setVisibility(View.GONE);
            return;
        }
        mSurfaceView.setVisibility(View.VISIBLE);

        // Calculate distribution
        AssetDistribution distribution = new AssetDistribution(this.assets,
                mSurfaceView.getWidth(), mSurfaceView.getHeight());

        // Get paint
        Boolean color = false;
        Paint paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(getResources().getColor(R.color.black));
        // Get canvas
        Canvas canvas = mSurfaceView.getHolder().lockCanvas();
        if (canvas == null) return;

        //set colors to list
        // Read string array
        int[] colors = this.getResources().getIntArray(R.array.distributionbar);

        // Paint canvas
        for (int i =0; i<distribution.elements.size(); i++) {
            AssetDistribution.DistributedElement element = distribution.elements.get(i);
            ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
            mDrawable.getPaint().setColor(colors[i % 18]);
            mDrawable.setBounds(element.getBounds());
            mDrawable.draw(canvas);
            if (element.getPercentage() > 5)
                canvas.drawText(element.getTicker(),
                        element.getBounds().centerX() - paint.measureText(element.getTicker())/2,
                        element.getBounds().centerY() + paint.getTextSize() / 2,
                        paint);
            color = !color;
        }

        // Unlock canvas
        mSurfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    // Force exit to Homescreen on backbutton press in asset activity
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }

}
