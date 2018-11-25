package com.example.pierreaverous.hermesproject;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import firebase.DB_File;
import firebase.DB_Part;
import firebase.DB_User;
import firebase.storageClasses.StoreQueryManager;

/**
 * A DialogFragment that pops up, asking user if he is sure to download the file.
 * Starts download once he clicks OK.
 */
public class DownloadDialogFragment extends DialogFragment {

    private static final String TAG = "DownloadDialogFragment";

    private List<DB_Part> parts;
    private DB_File fileClicked;
    private List<DB_User> users;


    public DownloadDialogFragment() {
        //Empty constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder;

        fileClicked = StoreQueryManager.getFile();
        parts = StoreQueryManager.getParts();

        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Souhaitez-vous procéder au téléchargement du fichier "+fileClicked.getFilename()+" ?" +
                "\n\tCe fichier fait "+fileClicked.getSize()+" octets. " +
                "\n\tIl a été créé par "+fileClicked.getAuthor()+"." +
                "\n\tIl est composé de "+fileClicked.getNumberOfParts()+" fragments.")
                .setTitle("Téléchargement");


        builder.setPositiveButton("Télécharger", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                parts = StoreQueryManager.getParts();
                if (parts.size() != fileClicked.getNumberOfParts()) {
                    String msg = "Found "+parts.size()+" chunks of "+fileClicked.getNumberOfParts()+" total. \nDownload cannot proceed.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
                else {
                    ((QueryResultActivity) getActivity()).getDownloadService().download(fileClicked, parts);
                }
            }
        });


        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.w(TAG, "Download canceled.");
            }
        });
        Log.w(TAG, "Dialog opened");
        return builder.create();
    }

}