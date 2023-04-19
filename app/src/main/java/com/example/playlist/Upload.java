package com.example.playlist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;

public class Upload extends Activity {

    final String TAG = "Upload";
    static final int REQUEST_PICK_AUDIO = 1;
    static final int REQUEST_READ_STORAGE = 2;

    String selectedAudioFilePath = "";
    TextView selectedAudio, songTime;
    Button back, find, play;

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private boolean isPlaying = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    Spinner vibe, season;
    TextView vibeTextView, seasonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload);


        spinners();

        back = findViewById(R.id.uploadBackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
                finish();
            }
        });

        find = findViewById(R.id.fileFindButton);
        songTime = findViewById(R.id.uploadPreviewTime);
        play = findViewById(R.id.previewButton);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAudioFile();
                play.setText("Play");
            }
        });

        songTime = findViewById(R.id.uploadPreviewTime);
        play = findViewById(R.id.previewButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play.getText().toString().equals("Play")) {
                    playAudio();
                    play.setText("Pause");
                } else if (play.getText().toString().equals("Pause")) {
                    pauseAudio();
                    play.setText("Resume");
                } else if (play.getText().toString().equals("Resume")) {
                    resumeAudio();
                    play.setText("Pause");
                }
            }
        });

        selectedAudio = findViewById(R.id.selectedAudioTextView);
        seekBar = findViewById(R.id.uploadPreviewSeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    } // onCreate END

    private void playAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(selectedAudioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            int time = mediaPlayer.getDuration();
            String duration = convertMillisToTime(time);
            songTime.setText(duration);
            seekBar.setMax(mediaPlayer.getDuration());
            updateSeekBar();
        } catch (IOException e) {
            Log.e(TAG, "Error playing audio", e);
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    } //playAudio End

    private String convertMillisToTime(int millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    } //pauseAudio End

    private void resumeAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            updateSeekBar();
        }
    } //resumeAudio End

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            seekBar.setProgress(0);
        }
    } //stopAudio End

    private void updateSeekBar() {
        if (mediaPlayer != null && isPlaying) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentPosition);
            String currentTime = convertMillisToTime(currentPosition);
            songTime.setText(currentTime);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    private void pickAudioFile() {
        if (checkReadStoragePermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICK_AUDIO);
        }
    }

    private boolean checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허용됨. 필요한 처리를 수행합니다.
                } else {
                    // 권한 거부됨. 앱 기능을 사용할 수 없습니다.
                }
                return;
            }
        }
    } //on RequestPermissionResult End\

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_AUDIO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            selectedAudioFilePath = getRealPathFromUri(uri);
            String selectedAudioFileName = getFileNameFromUri(uri);
            selectedAudio.setText(selectedAudioFileName);
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return uri.getLastPathSegment();
        } else {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            cursor.moveToFirst();
            String fileName = cursor.getString(column_index);
            cursor.close();
            return fileName;
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] data = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, data, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(index);
            cursor.close();
            return path;
        }
    } //getRealPathFromUri End

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    public void spinners() {

        ArrayList<String> vibeItems = new ArrayList<String>();
        vibeItems.add("love (사랑)");
        vibeItems.add("sad (슬픔)");
        vibeItems.add("youth (청춘)");
        vibeItems.add("happy (행복)");
        vibeItems.add("passion (열정)");
        vibeItems.add("refresh (시원)");
        vibeItems.add("thril (짜릿)");
        vibeItems.add("stable (차분)");
        vibeItems.add("modern (모던)");
        vibeItems.add("eccentric (괴짜)");
        vibeItems.add("mysterious (신비로움)");
        vibeItems.add("etc (기타 *직접 입력)");
        vibeItems.add("VIBE");

        vibe = findViewById(R.id.vibeSpinner);
        SpinnerAdapter vibeAdapter = new SpinnerAdapter
                (this, android.R.layout.simple_spinner_item, vibeItems);

        vibeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vibe.setAdapter(vibeAdapter);
        vibe.setSelection(vibeAdapter.getCount());
        vibe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "spinner vibe pick check : " + vibe.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayList<String> seasonItems = new ArrayList<String>();
        seasonItems.add("spring (봄)");
        seasonItems.add("early summer (초여름)");
        seasonItems.add("midsummer (한여름)");
        seasonItems.add("late summer (늦여름)");
        seasonItems.add("fall (가을)");
        seasonItems.add("winter (겨울)");
        seasonItems.add("midwinter (한겨울)");
        seasonItems.add("etc (기타 *직접 입력)");
        seasonItems.add("SEASON");

        season = findViewById(R.id.seasonSpinner);
        SpinnerAdapter seasonAdapter = new SpinnerAdapter
                (this, android.R.layout.simple_spinner_item, seasonItems);

        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        season.setAdapter(seasonAdapter);
        season.setSelection(seasonAdapter.getCount());
        season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "spinner season pick check : " + season.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}