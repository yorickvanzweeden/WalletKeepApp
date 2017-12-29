package com.walletkeep.walletkeep.ui.wallet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {
    // Data of the recycler view
    private List<WalletWithRelations> wallets;

    private WalletViewModel viewModel;

    // Access to the view
    private Context context;

    /**
     * Provide a reference to the views for each data item
     * Each data/list item gets access to its own elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements to use of the list item layout
        public TextView mTextView;
        public CardView mCardView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            // Initialise UI elements
            mTextView = v.findViewById(R.id.wallet_listitem_textView_name);
            mCardView = v.findViewById(R.id.wallet_listitem_cardView);
            mImageView = v.findViewById(R.id.wallet_listitem_imageView);
        }
    }

    /**
     * Constructor: Sets context
     * @param context Allows for referencing of UI elements
     */
    public WalletAdapter(Context context, WalletViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    /**
     * Update data of the list
     * @param wallets Wallets containing assets, exchange credentials
     */
    public void updateWallets(List<WalletWithRelations> wallets){
        this.wallets = wallets;
        notifyDataSetChanged();
    }

    /**
     * Creates new view and specifies which layout to use
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public WalletAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_content_listitem, parent, false);

        return new com.walletkeep.walletkeep.ui.wallet.WalletAdapter.ViewHolder(v);
    }

    /**
     * Specifies the UI elements and actions per list item
     * @param holder View holder (list item) to use
     * @param position The index of the data item
     */
    @Override
    public void onBindViewHolder(WalletAdapter.ViewHolder holder, int position) {
        holder.mCardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditWalletActivity.class);
            intent.putExtra("wallet_id", wallets.get(position).wallet.getId());
            intent.putExtra("fragment_type", wallets.get(position).getType().getValue());
            context.startActivity(intent);
        });

        // Set wallet name and type icon
        String walletName = wallets.get(position).getWalletName();

        switch (wallets.get(position).getType()){
            case Exchange:
                if (walletName.length() == 0) walletName = wallets.get(position).getExchangeName() + " Wallet";
                holder.mImageView.setImageResource(R.drawable.xc);
                break;
            case Naked:
                if (walletName.length() == 0) walletName = wallets.get(position).getAddressCurrency() + " Wallet";
                holder.mImageView.setImageResource(R.drawable.wl);
                break;
            case Transaction:
                if (walletName.length() == 0) walletName = wallets.get(position).assets.get(0).getCurrencyTicker() + " Wallet";
                holder.mImageView.setImageResource(R.drawable.tx);
                break;
        }
        holder.mTextView.setText(walletName);
    }

    /**
     * Return item count of the data set
     * @return Item count of the data set
     */
    @Override
    public int getItemCount() {
        if (wallets == null) return 0;
        return wallets.size();
    }
}
