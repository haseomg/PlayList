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

public class ProfileMusicSelectDialog extends DialogFragment {
    // 전체 곡 정보 넣고 돋보기 검색 기능
    private ArrayList<String> selectProfileMusics = new ArrayList<>();
    private String[] allProfileMusics = {"123 ∙ Nynas", "Waves ∙ ASMR", "Bonfire ∙ ASMR", "Friend Like Me (Gt Cover) ∙ Pinni",
            "A Moment Of Bliss ∙ 홍예진", "lalala ∙ HONNE", "Pour Me A Drink ∙ Buudy & Eddie", "Rain ∙ ASMR",
            "Tallgrass ∙ o3ohn", "908 ∙ 임금비", "Flying Fish ∙ Sion", "자유롭게 (cover) ∙ VAN", "Pretender ∙ OfficialHigeDandism",
            "All I need to hear ∙ The 1975", "Blu ∙ VAN", "Stay with u (feat. Jini) ∙ Flaboy",
            "To my loviee ∙ kimmy", "Wasteland ∙ 16", "My HaPiNeSs ∙ kimmy"};


    public interface OnProfileSelectedListener {
        void onProfileSelected(ArrayList<String> selectedProfileMusic);
    } // OnProfileSelectedListener

    private OnProfileSelectedListener listener;

    public void setListener(OnProfileSelectedListener listener) {
        this.listener = listener;
    } // setListener

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("프로필 뮤직을 선택해 주세요.")
                .setMultiChoiceItems(allProfileMusics, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectProfileMusics.add(allProfileMusics[which]);
                                } else if (selectProfileMusics.contains(allProfileMusics[which])) {
                                    // Else, if the item is already in the array, remove it
                                    selectProfileMusics.remove(allProfileMusics[which]);
                                } // else if
                            } // onClick
                        }) // OnMultiChoiceClickListener
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("ProfileMusic", "profileMusic onClick 확인");
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
        Log.i("pick dialog", "profileMusic sendResult getTargetFragment : " + getTargetFragment());
        if (getTargetFragment() == null) {
            return;
        } // if
        Intent intent = new Intent();
        Log.i("pick dialog", "profileMusic sendResult selectProfileMusics : " + selectProfileMusics);
        intent.putStringArrayListExtra("selected_profileMusic", selectProfileMusics);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    } // sendResult
} // CLASS
