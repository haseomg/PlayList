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

public class ProfileMusicSelectDialog extends DialogFragment {
    // 전체 곡 정보 넣고 돋보기 검색 기능
    private String selectedProfileMusic;
    private ArrayList<String> selectProfileMusics = new ArrayList<>();
    private String[] allProfileMusics = {"123 ∙ Nynas", "Waves ∙ ASMR", "Bonfire ∙ ASMR", "Friend Like Me (Gt Cover) ∙ Pinni",
            "A Moment Of Bliss ∙ 홍예진", "lalala ∙ HONNE", "Pour Me A Drink ∙ Buudy & Eddie", "Rain ∙ ASMR",
            "Tallgrass ∙ o3ohn", "908 ∙ 임금비", "Flying Fish ∙ Sion", "자유롭게 (cover) ∙ VAN", "Pretender ∙ OfficialHigeDandism",
            "All I need to hear ∙ The 1975", "Blu ∙ VAN", "Stay with u (feat. Jini) ∙ Flaboy",
            "To my loviee ∙ kimmy", "Wasteland ∙ 16", "My HaPiNeSs ∙ kimmy"};

    SharedPreferences shared;
    SharedPreferences.Editor editor;

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
        shared = getActivity().getSharedPreferences("selected_profile_music", Context.MODE_PRIVATE);
        editor = shared.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        builder.setTitle("프로필 뮤직을 선택해 주세요.")
                .setSingleChoiceItems(allProfileMusics, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectedProfileMusic = allProfileMusics[i];

                            } // onClick
                        }) // DialogInterface.OnClickListener()

//                .setMultiChoiceItems(allProfileMusics, null,
//                        new DialogInterface.OnMultiChoiceClickListener() {
//                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                            } // onClick
//                        }) // OnMultiChoiceClickListener

                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("ProfileMusic", "profileMusic onClick 확인 : " + selectedProfileMusic);
                        ((Feed) Feed.feedCtx).profileMusic.setText(selectedProfileMusic + " ▶");
                        // TODO - 프로필 뮤직 쉐어드에 저장
                        Log.i("ProfileMusicSelectDialog", "getSharedProfileMusic 3 : " +selectedProfileMusic);
                        editor.putString("selected_profile_music", selectedProfileMusic);
                        editor.commit();
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
        Log.i("pick dialog", "profileMusic sendResult selectProfileMusics : " + selectedProfileMusic);
        intent.putExtra("selected_profileMusic", selectedProfileMusic);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    } // sendResult
} // CLASS
