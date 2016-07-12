package com.mck.quicktemps.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.mck.quicktemps.MainActivity;
import com.mck.quicktemps.R;


/**
 * LocationSettingsFailureDialogFragment
 * Created by Michael on 5/29/2016.
 */
public class WeatherDataErrorDialogFragment extends DialogFragment {
    public static final String TAG = "WeatherDataError";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use a builder to construct the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_weather_data_error, null);
        builder.setView(layout)
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).onRefresh();
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                            if (keyCode == KeyEvent.KEYCODE_BACK &&
                                    keyEvent.getAction() == KeyEvent.ACTION_UP){
                                dismissAllowingStateLoss();
                                getActivity().finish();
                            }
                            return false;
                        }
                });
        setCancelable(false);
        return builder.create();
    }

    public static WeatherDataErrorDialogFragment newInstance() {
        return new WeatherDataErrorDialogFragment();
    }
}
