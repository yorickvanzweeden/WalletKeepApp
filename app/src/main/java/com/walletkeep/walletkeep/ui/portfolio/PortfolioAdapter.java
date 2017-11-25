package com.walletkeep.walletkeep.ui.portfolio;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.ui.MainActivity;
import com.walletkeep.walletkeep.ui.wallet.WalletActivity;

import org.w3c.dom.Text;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {
    private String[] mDataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public Button wallet;
        public ViewHolder(View v) {
            super(v);
            // Find the TextView in the LinearLayout
            mCardView = v.findViewById(R.id.card_view);
            wallet = v.findViewById(R.id.button_wallet);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PortfolioAdapter(Context context, String[] myDataset) {
        mDataset = myDataset;
        this.context = context;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public PortfolioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_portfolio_listitem, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void updatePortfolios(String[] myDataset){
        this.mDataset = myDataset;
        notifyDataSetChanged();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        ((TextView)holder.mCardView.getChildAt(0)).setText(mDataset[position]);
        holder.wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WalletActivity.class);
                intent.putExtra("wallet_id", position);
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
