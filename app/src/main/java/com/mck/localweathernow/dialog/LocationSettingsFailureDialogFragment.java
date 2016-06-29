package com.mck.localweathernow.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;


/**
 * LocationSettingsFailureDialogFragment
 * Created by Michael on 5/29/2016.
 */
public class LocationSettingsFailureDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use a builder to construct the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        /*View layout = inflater.inflate(R.layout.fragment_loc_settings_fail, null);
        builder.setView(layout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        */AlertDialog result = builder.create();
        return result;
    }

}
