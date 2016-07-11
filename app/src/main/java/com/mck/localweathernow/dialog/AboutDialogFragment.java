package com.mck.localweathernow.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.mck.localweathernow.R;

/**
 * Created by Michael on 7/9/2016.
 */

public class AboutDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use a builder to construct the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_about, null))
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(R.string.linked_in, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(
                                Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/iammck"));
                        startActivity(browserIntent);
                    }
                })
                .setPositiveButton(R.string.git_hub, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(
                                Intent.ACTION_VIEW, Uri.parse("https://github.com/iammck"));
                        startActivity(browserIntent);
                    }
                })
                .setNeutralButton(R.string.open_weather_map, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent browserIntent = new Intent(
                                Intent.ACTION_VIEW, Uri.parse("http://openweathermap.org/"));
                        startActivity(browserIntent);
                    }
                });

        AlertDialog result = builder.create();
        return result;
    }
}