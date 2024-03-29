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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class GenreSelectDialog extends DialogFragment {
    private boolean[] checkedItems;
    private ArrayList<String> selectedGenres = new ArrayList<>();
    private String[] allGenres = {"Rock (red)", "R&B (purple)", "Jazz (blue)",
            "Acoustic (green)", "Hip-Hop (black)", "Ballad (orange)"};
    public final String TAG = "GenreSelectDialog";
    String clickedItem, clickedPosition, conversionGenreFirst, conversionGenreSecond, conversionGenreThird;
    // Rock = rock, R&B = rhythmnblues, Jazz = jazz, Acoustic = acoustic, Hip-Hop = hip_hop, Ballad = ballad

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    SharedPreferences clickedPositionShared;
    SharedPreferences.Editor clickedPositionEditor;

    private ImageView genreFirstImageView;
    private ImageView genreSecondImageView;
    private ImageView genreThirdImageView;

    public interface OnGenreSelectedListener {
        void onGenresSelected(ArrayList<String> selectedGenres);
    } // OnGenreSelectedListener

    private OnGenreSelectedListener listener;

    public void setGenreFirstImageView(ImageView imageView) {
        this.genreFirstImageView = imageView;
    } // setGenreFirstImageView

    public void setGenreSecondImageView(ImageView imageView) {
        this.genreSecondImageView = imageView;
    } // setGenreSecondImageView

    public void setGenreThirdImageView(ImageView imageView) {
        this.genreThirdImageView = imageView;
    } // setGenreThirdImageView

    public void setListener(OnGenreSelectedListener listener) {
        this.listener = listener;
    } // setListener

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        checkedItems = new boolean[allGenres.length];
        shared = getActivity().getSharedPreferences("selected_profile_genre", Context.MODE_PRIVATE);
        editor = shared.edit();

        clickedPositionShared = getActivity().getSharedPreferences("selected_genre_position", Context.MODE_PRIVATE);
        clickedPositionEditor = clickedPositionShared.edit();
        clickedPosition = clickedPositionShared.getString("selected_genre_position", "0");
        Log.i(TAG, "SelectedGenre position check : " + clickedPosition);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        builder.setTitle("선호하는 장르를 선택해 주세요.")
                .setSingleChoiceItems(allGenres, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                clickedItem = allGenres[i];
                                Log.i(TAG, "SelectedGenre onClick : " + clickedItem);
//                                checkedItems[which] = isChecked;
//                                if (isChecked) {
//                                    Log.i(TAG, "SelectedGenre *checked: " + isChecked);
//                                    // If the user checked the item, add it to the selected items
//                                    selectedGenres.add(allGenres[which]);
//                                    clickedItem = allGenres[which];
////                                    Toast.makeText(getContext(), allGenres[which] + " Checked", Toast.LENGTH_SHORT).show();
//                                    Log.i(TAG, "SelectedGenre clickedItem : " + clickedItem);
//                                } else {
//                                    Log.i(TAG, "SelectedGenre *checked: " + isChecked);
////                                    Toast.makeText(getContext(), allGenres[which] + " UnChecked", Toast.LENGTH_SHORT).show();
//                                }  // else
                            } // onClick
                        }) // DialogInterface.OnClickListener()

                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clickedPositionEditor.putString(clickedPosition, clickedItem);
                        clickedPositionEditor.commit();

                        if (clickedPosition.equals("1")) {
                            // 고른 장르 이름 convert
                            conversionGenreFirst = clickedGenreChangeName(clickedItem);
                            matchGenreImage("1", genreFirstImageView, conversionGenreFirst);
                            Log.i(TAG, "SelectedGenre *positive before 1 onClick : " + clickedPositionShared.getString(clickedPosition, "0"));
                            clickedPositionEditor.putString("selected_genre_position", "1");
                            clickedPositionEditor.putString(clickedPosition, clickedItem);
                            clickedPositionEditor.commit();

                        } else if (clickedPosition.equals("2")) {
                            conversionGenreSecond = clickedGenreChangeName(clickedItem);
                            matchGenreImage("2", genreSecondImageView, conversionGenreSecond);
                            Log.i(TAG, "SelectedGenre *positive before 2 onClick : " + clickedPositionShared.getString(clickedPosition, "0"));
                            clickedPositionEditor.putString("selected_genre_position", "2");
                            clickedPositionEditor.putString(clickedPosition, clickedItem);
                            clickedPositionEditor.commit();

                        } else if (clickedPosition.equals("3")) {
                            conversionGenreThird = clickedGenreChangeName(clickedItem);
                            matchGenreImage("3", genreThirdImageView, conversionGenreThird);
                            Log.i(TAG, "SelectedGenre *positive before 3 onClick : " + clickedPositionShared.getString(clickedPosition, "0"));
                            clickedPositionEditor.putString("selected_genre_position", "3");
                            clickedPositionEditor.putString(clickedPosition, clickedItem);
                            clickedPositionEditor.commit();

                        } else {
                            Log.i(TAG, "SelectedGenre *positive before else onClick : " + clickedPositionShared.getString(clickedPosition, "0"));

                        } // else
                        sendResult(Activity.RESULT_OK);

                        // TODO - 확인 버튼 클릭 시, 장르 이미지 세팅

                    } // onClick
                }) // setPositiveButton

                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    } // onClick
                }); // setNegativeButton
        return builder.create();
    } // Dialog.onCreate

    public String clickedGenreChangeName(String clickedItem) {
        String[] cutColor = clickedItem.split(" \\(");
// 소문자로 변환
        String genreName = cutColor[0].toLowerCase();
        Log.i(TAG, "conversionGenreName clickedItem *genreName 1 : " + genreName);
// '-' 를 '_' 로 재배치
        if (genreName.contains("-")) {
            genreName = genreName.replace("-", "_");
            Log.i(TAG, "conversionGenreName clickedItem *genreName 2 : " + genreName);
        } // if
        if (genreName.equals("r&b")) {
            genreName = "rhythmnblues";
            Log.i(TAG, "conversionGenreName clickedItem *genreName 3 : " + genreName);
        } // if

        Log.i(TAG, "conversionGenreName clickedItem *genreName 4 : " + genreName);

        return genreName;
    } // clickedGenreChangeName

    void matchGenreImage(String number, ImageView imageView, String clickedItem) {
//        String clickedItem = clickedGenreShared.getString(number, "0");
        Log.i(TAG, "clickedItem : " + clickedItem);
        String[] cutColor = clickedItem.split(" \\(");
        // 소문자로 변환
        String genreName = cutColor[0].toLowerCase();
        Log.i(TAG, "clickedItem *genreName 1 : " + genreName);
        // '-' 를 '_' 로 재배치
        if (genreName.contains("-")) {
            genreName.replace("-", "_");
            Log.i(TAG, "clickedItem *genreName 2 : " + genreName);
        } // if
        if (genreName.equals("r&b")) {
            genreName = "rhythmnblues";
            Log.i(TAG, "clickedItem *genreName 3 : " + genreName);
        } // if

        Log.i(TAG, "clickedItem *genreName 4 : " + genreName);
        String settingName = "R.drawable." + genreName;
        Log.i(TAG, "clickedItem *settingName (string) : " + settingName);
        getImageId(Feed.feedCtx, settingName);
        int id = Feed.feedCtx.getResources().getIdentifier("drawable/" + genreName, "drawable", Feed.feedCtx.getPackageName());
        Log.i(TAG, "clickedItem image id check : " + id);
        imageView.setImageResource(id);
//        imageView.setImageResource(R.drawable.jazz);
    } // matchGenreImage

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

//    private void matchGenreImage(String genreName, ImageView imageView) {
//        Log.i(TAG, "matchGenreImage (dialog) : " + genreName + " / " + imageView.getDrawable());
//        String imageName = genreName.toLowerCase().replace(" ", "_");
//        Log.i(TAG, "matchGenreImage (dialog) imageName : " + imageName);
//        int resId = getResources().getIdentifier(imageName, "drawable", requireContext().getPackageName());
//        Log.i(TAG, "matchGenreImage (dialog) resId : " + resId);
//        imageView.setImageResource(resId);
//    } // matchGenreImage

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
