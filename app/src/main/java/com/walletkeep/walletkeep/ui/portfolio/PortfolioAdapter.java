package com.walletkeep.walletkeep.ui.portfolio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.ui.main.MainActivity;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {
    // Data of the recycler view
    private List<Portfolio> portfolios;

    private PortfolioViewModel viewModel;

    // Access to the view
    private Context context;

    /**
     * Provide a reference to the views for each data item
     * Each data/list item gets access to its own elements
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements to use of the list item layout
        @BindView(R.id.portfolio_listitem_button_assets) Button buttonAssets;
        @BindView(R.id.portfolio_listitem_button_delete) Button buttonDelete;
        @BindView(R.id.portfolio_listitem_cardView) CardView cardView;

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
    PortfolioAdapter(Context context, List<Portfolio> portfolios, PortfolioViewModel viewModel) {
        this.portfolios = portfolios;
        this.context = context;
        this.viewModel = viewModel;
    }

    /**
     * Update data of the list
     *
     * @param portfolios List of portfolios
     */
    void updatePortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
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
                .inflate(R.layout.portfolio_content_listitem, parent, false);

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
        TextView name = (TextView) ((ConstraintLayout) holder.cardView.getChildAt(0)).getChildAt(0);
        name.setText(portfolios.get(position).getName());

        holder.buttonAssets.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("portfolio_id", portfolios.get(position).getId());
            context.startActivity(intent);
        });
        holder.buttonDelete.setOnClickListener(view -> {
            // Ask for confirmation
            new AlertDialog.Builder(this.context)
                    .setMessage(R.string.confirmation_delete_portfolio)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                            viewModel.deletePortfolio(portfolios.get(position)))
                    .setNegativeButton(android.R.string.no, null).show();
        });
    }

    /**
     * Return item count of the data set
     *
     * @return Item count of the data set
     */
    @Override
    public int getItemCount() {
        if (portfolios == null) return 0;
        return portfolios.size();
    }
}
