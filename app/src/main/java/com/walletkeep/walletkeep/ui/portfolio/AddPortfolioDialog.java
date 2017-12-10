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


    public AddPortfolioDialog() {
        // Required empty public constructor
    }

    public interface AddPortfolioDialogListener {
        void onDialogPositiveClick(String portfolioName);
    }

    // Use this instance of the interface to deliver action events
    AddPortfolioDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the AddPortfolioDialogListener
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


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set portfolio name");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.fragment_add_portfolio_dialog, null);
        builder.setView(mView);
        EditText mEditText = mView.findViewById(R.id.editPortfolioName);

        builder.setPositiveButton("Save", (dialog, id) -> mListener.onDialogPositiveClick(mEditText.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, id) -> {});

        return builder.create();
    }

}
