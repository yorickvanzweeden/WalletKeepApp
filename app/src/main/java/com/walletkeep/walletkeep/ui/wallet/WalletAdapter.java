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

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {
    // Data of the recycler view
    private List<WalletWithRelations> wallets;

    private WalletViewModel viewModel;

    // Access to the view
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.wallet_listitem_textView_name) TextView textViewName;
        @BindView(R.id.wallet_listitem_imageView) ImageView imageView;
        @BindView(R.id.wallet_listitem_cardView) CardView cardView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    /**
     * Constructor: Sets context
     *
     * @param context Allows for referencing of UI elements
     */
    WalletAdapter(Context context, WalletViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    /**
     * Update data of the list
     *
     * @param wallets Wallets containing assets, exchange credentials
     */
    void updateWallets(List<WalletWithRelations> wallets) {
        this.wallets = wallets;
        notifyDataSetChanged();
    }

    /**
     * Creates new view and specifies which layout to use
     *
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_content_listitem, parent, false);

        return new ViewHolder(v);
    }

    /**
     * Specifies the UI elements and actions per list item
     *
     * @param holder   View holder (list item) to use
     * @param position The index of the data item
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditWalletActivity.class);
            intent.putExtra("wallet_id", wallets.get(position).wallet.getId());
            intent.putExtra("fragment_type", wallets.get(position).getType().getValue());
            context.startActivity(intent);
        });

        // Set wallet name and type icon
        String walletName = wallets.get(position).getWalletName();

        switch (wallets.get(position).getType()) {
            case Exchange:
                holder.imageView.setImageResource(R.drawable.xc);
                break;
            case Naked:
                holder.imageView.setImageResource(R.drawable.wl);
                break;
            case Transaction:
                holder.imageView.setImageResource(R.drawable.tx);
                break;
        }
        holder.textViewName.setText(walletName);
    }

    /**
     * Return item count of the data set
     *
     * @return Item count of the data set
     */
    @Override
    public int getItemCount() {
        if (wallets == null) return 0;
        return wallets.size();
    }
}
