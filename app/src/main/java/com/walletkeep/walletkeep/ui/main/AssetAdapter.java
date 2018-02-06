package com.walletkeep.walletkeep.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.ui.asset.AssetActivity;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssetAdapter extends RecyclerView.Adapter<com.walletkeep.walletkeep.ui.main.AssetAdapter.ViewHolder> {
    // Data of the recycler view
    private List<AggregatedAsset> assets;
    private String currencySetting = "EUR";

    // Access to the view
    private Context context;

    /**
     * Provide a reference to the views for each data item
     * Each data/list item gets access to its own elements
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements to use of the list item layout
        @BindView(R.id.asset_listitem_textView_ticker) TextView mTextViewTicker;
        @BindView(R.id.asset_listitem_textView_holdings) TextView mTextViewAmount;
        @BindView(R.id.asset_listitem_textView_price) TextView mTextViewPrice;
        @BindView(R.id.asset_listitem_textView_total) TextView mTextViewTotal;
        @BindView(R.id.asset_listitem_textView_change) TextView mTextViewChange;
        @BindView(R.id.editWallet_naked_token_listitem_cardView) CardView mCardView;
        @BindView(R.id.imageView) ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }
    }

    /**
     * Constructor: Sets context
     * @param context Allows for referencing of UI elements
     */
    AssetAdapter(Context context, List<AggregatedAsset> assets) {
        this.assets = assets;
        this.context = context;
    }

    /**
     * Update data of the list
     * @param assets List of aggregated assets
     */
    void updateAggregatedAssets(List<AggregatedAsset> assets){
        this.assets = assets;
        notifyDataSetChanged();
    }

    /**
     * Update the currency setting (EUR, USD, BTC)
     * @param currencySetting change setting
     */
    void updateCurrencySetting(String currencySetting){
        this.currencySetting = currencySetting;
        notifyDataSetChanged();
    }
    String getCurrencySetting() { return this.currencySetting; }

    /**
     * Creates new view and specifies which layout to use
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public com.walletkeep.walletkeep.ui.main.AssetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                        int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.asset_content_listitem, parent, false);

        return new com.walletkeep.walletkeep.ui.main.AssetAdapter.ViewHolder(v);
    }

    /**
     * Specifies the UI elements and actions per list item
     * @param holder View holder (list item) to use
     * @param position The index of the data item
     */
    @Override
    public void onBindViewHolder(com.walletkeep.walletkeep.ui.main.AssetAdapter.ViewHolder holder, int position) {
        AggregatedAsset asset = assets.get(position);

        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.getDefault());
        nf.setCurrency(Currency.getInstance(currencySetting));

        holder.mTextViewTicker.setText(asset.getTicker());
        holder.mTextViewAmount.setText(String.format("%.2f", asset.getAmount()));
        holder.mTextViewPrice.setText(nf.format(asset.getPrice(currencySetting)));
        holder.mTextViewTotal.setText(nf.format(asset.getValue(currencySetting)));
        holder.mTextViewChange.setText(String.format("%.2f%%", asset.getChange(currencySetting)));
        Glide.with(context)
                .load(String.format("https://yorickvanzweeden.nl/walletkeep/icons/%s.png", asset.getTicker().toUpperCase()))
                .placeholder(R.drawable.ethereum)
                .error(R.drawable.ethereum)
                .centerCrop()
                .into(holder.mImageView);

        if (asset.getChange(currencySetting) > 0.001)
            holder.mTextViewChange.setBackgroundColor(context.getResources().getColor(R.color.price_change_positive));
        else if (asset.getChange(currencySetting) < -0.001)
            holder.mTextViewChange.setBackgroundColor(context.getResources().getColor(R.color.price_change_negative));

        holder.mCardView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AssetActivity.class);
            intent.putExtra("currency_ticker", asset.getTicker());
            view.getContext().startActivity(intent);
        });

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
