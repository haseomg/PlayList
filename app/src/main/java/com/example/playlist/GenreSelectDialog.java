package com.example.playlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class GenreSelectDialog extends DialogFragment {
    private ArrayList<String> selectedGenres = new ArrayList<>();
    private String[] allGenres = {"Rock", "R&B", "Jazz", "Acoustic", "Hip-Hop", "Ballad"};

    public interface OnGenreSelectedListener {
        void onGenresSelected(ArrayList<String> selectedGenres);
    } // OnGenreSelectedListener

    private OnGenreSelectedListener listener;

    public void setListener(OnGenreSelectedListener listener) {
        this.listener = listener;
    } // setListener

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select your favorite genres")
                .setMultiChoiceItems(allGenres, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedGenres.add(allGenres[which]);
                                } else if (selectedGenres.contains(allGenres[which])) {
                                    // Else, if the item is already in the array, remove it
                                    selectedGenres.remove(allGenres[which]);
                                } // else if
                            } // onClick
                        }) // OnMultiChoiceClickListener
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendResult(Activity.RESULT_OK);
                    } // onClick
                }) // setPositiveButton

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    } // onClick
                }); // setNegativeButton
        return builder.create();
    } // Dialog.onCreate

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putStringArrayListExtra("selected_genres", selectedGenres);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    } // sendResult
} // CLASS
