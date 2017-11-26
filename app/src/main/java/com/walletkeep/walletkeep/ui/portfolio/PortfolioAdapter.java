package com.walletkeep.walletkeep.ui.portfolio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.ui.wallet.WalletActivity;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {
    // Data of the recycler view
    private String[] mDataset;

    // Access to the view
    private Context context;

    /**
     * Provide a reference to the views for each data item
     * Each data/list item gets access to its own elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements to use of the list item layout
        public CardView mCardView;
        public Button wallet;

        public ViewHolder(View v) {
            super(v);
            // Initialise UI elements
            mCardView = v.findViewById(R.id.card_view);
            wallet = v.findViewById(R.id.button_wallet);
        }
    }

    /**
     * Constructor: Sets context
     * @param context Allows for referencing of UI elements
     */
    public PortfolioAdapter(Context context, String[] myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    /**
     * Update data of the list
     * @param myDataset List of portfolios
     */
    public void updatePortfolios(String[] myDataset){
        this.mDataset = myDataset;
        notifyDataSetChanged();
    }

    /**
     * Creates new view and specifies which layout to use
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public PortfolioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_portfolio_listitem, parent, false);

        return new ViewHolder(v);
    }
    
    /**
     * Specifies the UI elements and actions per list item
     * @param holder View holder (list item) to use
     * @param position The index of the data item
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ((TextView)holder.mCardView.getChildAt(0)).setText(mDataset[position]);
        holder.wallet.setOnClickListener(view -> {
            Intent intent = new Intent(context, WalletActivity.class);
            intent.putExtra("portfolio_id", position);
            context.startActivity(intent);
        });
    }

    /**
     * Return item count of the data set
     * @return Item count of the data set
     */
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
