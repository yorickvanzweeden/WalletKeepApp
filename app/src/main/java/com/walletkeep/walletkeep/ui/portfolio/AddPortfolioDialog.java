package com.walletkeep.walletkeep.ui.portfolio;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.walletkeep.walletkeep.R;


public class AddPortfolioDialog extends DialogFragment {
    AddPortfolioDialogListener mListener;

    /**
     * Constructor:
     *
     * Required empty public constructor
     */
    public AddPortfolioDialog() {}

    /**
     * Listener to pass the name back to the activity
     */
    public interface AddPortfolioDialogListener {
        void onDialogPositiveClick(String portfolioName);
    }

    /**
     * Instantiate the AddPortfolioDialogListener
     * @param activity Activity to display dialog over
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AddPortfolioDialogListener so we can send events to the host
            mListener = (AddPortfolioDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddPortfolioDialogListener");
        }
    }

    /**
     * Setup dialog on creation
     * @param savedInstanceState Previous state of the dialog
     * @return Dialog to display
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set portfolio name");

        // Setup UI elements
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.portfolio_add_dialog_fragment, null);
        builder.setView(mView);
        EditText mEditText = mView.findViewById(R.id.portfolio_toolbar);

        // Setup button actions
        builder.setPositiveButton("Save", (dialog, id) ->
                mListener.onDialogPositiveClick(mEditText.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, id) -> {});

        return builder.create();
    }

}
