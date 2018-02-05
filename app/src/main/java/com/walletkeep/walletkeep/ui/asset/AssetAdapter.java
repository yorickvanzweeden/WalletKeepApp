package com.walletkeep.walletkeep.ui.asset;

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
import com.walletkeep.walletkeep.ui.assetScreen.SeperateAssetScreen;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class AssetAdapter extends RecyclerView.Adapter<com.walletkeep.walletkeep.ui.asset.AssetAdapter.ViewHolder> {
    // Data of the recycler view
    private List<AggregatedAsset> assets;
    private String currencySetting = "EUR";

    // Access to the view
    private Context context;

    /**
     * Provide a reference to the views for each data item
     * Each data/list item gets access to its own elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements to use of the list item layout
        TextView mTextViewTicker;
        TextView mTextViewAmount;
        TextView mTextViewPrice;
        TextView mTextViewTotal;
        TextView mTextViewChange;
        CardView mCardView;
        ImageView mImageView;

        ViewHolder(View v) {
            super(v);
            // Initialise UI elements
            mTextViewTicker = v.findViewById(R.id.asset_listitem_textView_ticker);
            mTextViewAmount = v.findViewById(R.id.asset_listitem_textView_holdings);
            mTextViewPrice = v.findViewById(R.id.asset_listitem_textView_price);
            mTextViewTotal = v.findViewById(R.id.asset_listitem_textView_total);
            mTextViewChange = v.findViewById(R.id.asset_listitem_textView_change);
            mCardView = v.findViewById(R.id.asset_listitem_card_view);
            mImageView = v.findViewById(R.id.imageView);
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

        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.getDefault());
        nf.setCurrency(Currency.getInstance(currencySetting));

        holder.mTextViewTicker.setText(asset.getTicker());
        holder.mTextViewAmount.setText(String.format("%.2f", asset.getAmount()));
        holder.mTextViewPrice.setText(nf.format(asset.getPrice(currencySetting)));
        holder.mTextViewTotal.setText(nf.format(asset.getValue(currencySetting)));
        holder.mTextViewChange.setText(String.format("%.2f%%", asset.getChange(currencySetting)));
        Glide.with(context)
                .load(String.format("https://yorickvanzweeden.nl/walletkeep/icons/%s.png", asset.getTicker().toLowerCase()))
                .placeholder(R.drawable.ethereum)
                .error(R.drawable.ethereum)
                .centerCrop()
                .into(holder.mImageView);

        if (asset.getChange(currencySetting) > 0)
            holder.mTextViewChange.setBackgroundColor(context.getResources().getColor(R.color.price_change_positive));
        else if (asset.getChange(currencySetting) < 0)
            holder.mTextViewChange.setBackgroundColor(context.getResources().getColor(R.color.price_change_negative));
        else if (asset.getChange(currencySetting)== 0);

        holder.mCardView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SeperateAssetScreen.class);
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
