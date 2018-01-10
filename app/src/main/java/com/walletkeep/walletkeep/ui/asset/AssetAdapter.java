package com.walletkeep.walletkeep.ui.asset;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;

import java.util.List;

public class AssetAdapter extends RecyclerView.Adapter<com.walletkeep.walletkeep.ui.asset.AssetAdapter.ViewHolder> {
    // Data of the recycler view
    private List<AggregatedAsset> assets;
    private String changeSetting;

    // Access to the view
    private Context context;

    /**
     * Provide a reference to the views for each data item
     * Each data/list item gets access to its own elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements to use of the list item layout
        public TextView mTextViewTicker;
        public TextView mTextViewAmount;
        public TextView mTextViewPrice;
        public TextView mTextViewTotal;
        public TextView mTextViewChange;
        public Button wallet;

        public ViewHolder(View v) {
            super(v);
            // Initialise UI elements
            mTextViewTicker = v.findViewById(R.id.asset_listitem_textView_ticker);
            mTextViewAmount = v.findViewById(R.id.asset_listitem_textView_holdings);
            mTextViewPrice = v.findViewById(R.id.asset_listitem_textView_price);
            mTextViewTotal = v.findViewById(R.id.asset_listitem_textView_total);
            mTextViewChange = v.findViewById(R.id.asset_listitem_textView_change);
            wallet = v.findViewById(R.id.portfolio_listitem_button_wallet);
        }
    }

    /**
     * Constructor: Sets context
     * @param context Allows for referencing of UI elements
     */
    public AssetAdapter(Context context, List<AggregatedAsset> assets) {
        this.assets = assets;
        this.context = context;
    }

    /**
     * Update data of the list
     * @param assets List of aggregated assets
     */
    public void updateAggregatedAssets(List<AggregatedAsset> assets){
        this.assets = assets;
        notifyDataSetChanged();
    }

    /**
     * Update the change setting (1H, 24H, 7D)
     * @param changeSetting change setting
     */
    public void updateChangeSetting(String changeSetting){
        this.changeSetting = changeSetting;
        notifyDataSetChanged();
    }

    /**
     * Creates new view and specifies which layout to use
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public com.walletkeep.walletkeep.ui.asset.AssetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.asset_content_listitem, parent, false);

        return new com.walletkeep.walletkeep.ui.asset.AssetAdapter.ViewHolder(v);
    }

    /**
     * Specifies the UI elements and actions per list item
     * @param holder View holder (list item) to use
     * @param position The index of the data item
     */
    @Override
    public void onBindViewHolder(com.walletkeep.walletkeep.ui.asset.AssetAdapter.ViewHolder holder, int position) {
        AggregatedAsset asset = assets.get(position);

        holder.mTextViewTicker.setText(asset.getTicker());
        holder.mTextViewAmount.setText(String.format("%.4f", asset.getAmount()));
        holder.mTextViewPrice.setText(String.format("€%.2f", asset.getLatestCurrencyPrice()));
        holder.mTextViewTotal.setText(String.format("€%.2f", asset.getAmount() * asset.getLatestCurrencyPrice()));
        holder.mTextViewChange.setText(String.format("%.2f%%", asset.getChange(changeSetting)));
    }

    /**
     * Return item count of the data set
     * @return Item count of the data set
     */
    @Override
    public int getItemCount() {
        if (assets == null) return 0;
        return assets.size();
    }
}
