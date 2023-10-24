package com.example.playlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class GenreSelectDialog extends DialogFragment {
    private ArrayList<String> selectedGenres = new ArrayList<>();
    private String[] allGenres = {"Rock (red)", "R&B (purple)", "Jazz (blue)",
            "Acoustic (green)", "Hip-Hop (black)", "Ballad (orange)"};
    // Rock = rock, R&B = rhythmnblues, Jazz = jazz, Acoustic = acoustic, Hip-Hop = hip_hop, Ballad = ballad

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        builder.setTitle("선호하는 장르를 3개 선택해 주세요.")
                .setMultiChoiceItems(allGenres, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedGenres.add(allGenres[which]);
                                }
//                                else if (selectedGenres.contains(allGenres[which])) {
//                                    // Else, if the item is already in the array, remove it
//                                    selectedGenres.remove(allGenres[which]);
//                                } // else if
                            } // onClick
                        }) // OnMultiChoiceClickListener
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendResult(Activity.RESULT_OK);
                    } // onClick
                }) // setPositiveButton

                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
        for (int i = 0; i < selectedGenres.size(); i ++) {
            Log.i("pick dialog", "genre sendResult : " + selectedGenres.get(i));
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    } // sendResult
} // CLASS
