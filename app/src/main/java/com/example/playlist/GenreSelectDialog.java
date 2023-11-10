package com.example.playlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class GenreSelectDialog extends DialogFragment {
    private boolean[] checkedItems;
    private ArrayList<String> selectedGenres = new ArrayList<>();
    private String[] allGenres = {"Rock (red)", "R&B (purple)", "Jazz (blue)",
            "Acoustic (green)", "Hip-Hop (black)", "Ballad (orange)"};
    public final String TAG = "GenreSelectDialog";
    String clickedItem;
    // Rock = rock, R&B = rhythmnblues, Jazz = jazz, Acoustic = acoustic, Hip-Hop = hip_hop, Ballad = ballad

    SharedPreferences shared;
    SharedPreferences.Editor editor;

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
        checkedItems = new boolean[allGenres.length];
        shared = getActivity().getSharedPreferences("selected_profile_genre", Context.MODE_PRIVATE);
        editor = shared.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        builder.setTitle("선호하는 장르를 선택해 주세요. (최대 3개)")
                .setMultiChoiceItems(allGenres, checkedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                checkedItems[which] = isChecked;
                                if (isChecked) {
                                    Log.i(TAG, "SelectedGenre *checked: " + isChecked);
                                    // If the user checked the item, add it to the selected items
                                    selectedGenres.add(allGenres[which]);
                                    clickedItem = allGenres[which];
//                                    Toast.makeText(getContext(), allGenres[which] + " Checked", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "SelectedGenre clickedItem : " + clickedItem);
                                } else {
                                    Log.i(TAG, "SelectedGenre *checked: " + isChecked);
//                                    Toast.makeText(getContext(), allGenres[which] + " UnChecked", Toast.LENGTH_SHORT).show();

                                }  // else
                            } // onClick
                        }) // OnMultiChoiceClickListener

                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendResult(Activity.RESULT_OK);
                    } // onClick
                }) // setPositiveButton

                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    } // onClick
                }); // setNegativeButton
        return builder.create();
    } // Dialog.onCreate

    private void sendResult(int resultCode) {
        Log.i(TAG, "sendResult (resultCode) : " + resultCode);
        ArrayList<String> selectedGenres = new ArrayList<>();
        for (int i = 0; i < allGenres.length; i++) {
            if (checkedItems[i]) {
                selectedGenres.add(allGenres[i]);
            } // if
        } // for
        if (getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putStringArrayListExtra("selected_genres", selectedGenres);
        for (String genre : selectedGenres) {
            Log.i(TAG, "sendResult (selected genre) : " + genre);
        } // for END
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    } // sendResult
} // CLASS
