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

import com.mck.quicktemps.R;


/**
 * Shows a dialog box that informs the user that this app requires perms to work.
 * Created by Michael on 5/16/2016.
 */
public class RequiresSettingsRationaleDialogFragment extends DialogFragment {
    public static final String TAG = "ReqSettingsDialogFrag";
    private RequiresSettingsRationaleCallback callback;

    public static RequiresSettingsRationaleDialogFragment
    newInstance(RequiresSettingsRationaleCallback requiresSettingsRationaleCallback) {
        RequiresSettingsRationaleDialogFragment result =
                new RequiresSettingsRationaleDialogFragment();
        result.callback = requiresSettingsRationaleCallback;
        return result;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use a builder to construct the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_settings_req, null);
        builder .setView(layout)
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.requiresSettingsRationaleTryAgain();
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
                                callback.requiresSettingsRationaleTryAgain();
                            }
                            return false;
                        }
                });
        setCancelable(false);
        return builder.create();
    }

    public interface RequiresSettingsRationaleCallback {
        void requiresSettingsRationaleTryAgain();
    }
}
