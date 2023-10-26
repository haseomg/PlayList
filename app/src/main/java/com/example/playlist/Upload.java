package com.example.playlist;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
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
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Upload extends Activity {

    final String TAG = "Upload";
    static final int REQUEST_PICK_AUDIO = 1;
    static final int REQUEST_READ_STORAGE = 2;

    String selectedAudioFilePath = "";
    String getSelectedAudioFileName, getSelectedAudioFileUri, mimeType;
    TextView selectedAudio, songTime;
    Button back, find, play, upload;

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private boolean isPlaying = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    Spinner vibe, season;
    TextView vibeTextView, seasonTextView;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

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
                play.setText("재생");
            }
        });

        songTime = findViewById(R.id.uploadPreviewTime);
        play = findViewById(R.id.previewButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play.getText().toString().equals("재생")) {
                    playAudio();
                    play.setText("일시 정지");
                } else if (play.getText().toString().equals("일시 정지")) {
                    pauseAudio();
                    play.setText("재개");
                } else if (play.getText().toString().equals("재개")) {
                    resumeAudio();
                    play.setText("일시 정지");
                }
            }
        });

        upload = findViewById(R.id.uploadButton);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "upload onClick");
                // 1. 서버에 파일 업로드
                uploadAudioToServer();
                finish();
                // 2. music 테이블에 name, artist, time, path 추가
            } // onClick END
        }); // setUpload

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
        } // if END
    } //updateSeekBar END

    private void pickAudioFile() {
        if (checkReadStoragePermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICK_AUDIO);
        } // if END
    } // pickAudioFile method END

    private boolean checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
            return false;
        } else {
            return true;
        } // else END
    } // checkReadStoragePermission END

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허용됨. 필요한 처리를 수행합니다.
                } else {
                    // 권한 거부됨. 앱 기능을 사용할 수 없습니다.
                } // else END
                return;
            } // case END
        } // switch END
    } //on RequestPermissionResult End\

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_AUDIO && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult (if) - requestCode check : " + requestCode);
            Uri uri = data.getData();

            // MIME 타입 확인
            mimeType = getContentResolver().getType(uri);
            if (mimeType != null) {
                Log.d(TAG, "MIME type: " + mimeType);
            } else {
                Log.e(TAG, "MIME type is null");
            }

            selectedAudioFilePath = getRealPathFromUri(uri);
            // TODO AudioFileName
            String selectedAudioFileName = getFileNameFromUri(uri);
            getSelectedAudioFileName = selectedAudioFileName;
            Log.i(TAG, "getSelected FileName : " + getSelectedAudioFileName);
            getSelectedAudioFileUri = uri.toString();
            Log.i(TAG, "getSelected FileUri : " + getSelectedAudioFileUri);
            selectedAudio.setText(selectedAudioFileName);
        } else {
            Log.i(TAG, "onActivityResult (else) - requestCode check : " + requestCode);
        } // else END
    } // onActivityResult END

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
        } // else END
    } // getFileNameFromUri END

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
        } // else END
    } //getRealPathFromUri End

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        } // if END
        return true;
    } // onTouchEvent END

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
        vibeItems.add("분위기");

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
            } // onItemSelected END

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            } // onNothingSelected END
        }); // OnItemSelectedListener END


        ArrayList<String> seasonItems = new ArrayList<String>();
        seasonItems.add("spring (봄)");
        seasonItems.add("early summer (초여름)");
        seasonItems.add("midsummer (한여름)");
        seasonItems.add("late summer (늦여름)");
        seasonItems.add("fall (가을)");
        seasonItems.add("winter (겨울)");
        seasonItems.add("midwinter (한겨울)");
        seasonItems.add("etc (기타 *직접 입력)");
        seasonItems.add("계절감");

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
            } // onItemSelected

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            } // onNothingSelected
        }); // setOnItemSelectedListener
    } // spinners method END


    private void uploadAudioToServer() {
        Log.i(TAG, "uploadAudioToServer method");
        File file = new File(selectedAudioFilePath);
        Log.i(TAG, "uploadAudioToServer selectedAudioFilePath : " + selectedAudioFilePath);

        if (!file.exists()) {
            Log.e(TAG, "File does not exist! Check the file path: " + selectedAudioFilePath);
        } else {
            Log.e(TAG, "File exist! Check the file path: " + selectedAudioFilePath);
        }

        // fileName, fileType
//        getSelectedAudioFileName

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://13.124.239.85/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        ContentResolver contentResolver = Upload.this.getContentResolver();
//        String mimType = contentResolver.getType(Uri.fromFile(file));

        // create RequestBody instance from file
        Log.i(TAG, "mimeType Check : " + mimeType);

        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        String mimType = getMimeType(file.getAbsolutePath());
        String mmType = contentResolver.getType(Uri.parse(getSelectedAudioFileUri));
        if (mimType == null) {
            Log.e(TAG, "MIME Type not found for the file: " + file.getAbsolutePath());
            return;
        }
        Log.i(TAG, "mime Type : " + mmType);
        Log.i(TAG, "File extension: " + extension);
        Log.i(TAG, "MIME Type: " + mimType);

        RequestBody requestFile = RequestBody.create(MediaType.parse(mimType), file);
// RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(Uri.parse(getSelectedAudioFileUri))), file);


        if (requestFile == null) {
            Log.e(TAG, "requestFile is null! Please check the requestFile generation. : " + requestFile);
        } else {
            Log.e(TAG, "requestFile is not null! Please check the requestFile generation. : " + requestFile);

        }

//        String textData = "sample text.";
        String textData = vibe.getSelectedItem().toString();
        RequestBody textRequestBody = RequestBody.create(MediaType.parse("text/plain"), textData);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
//        ServerApi apiService = ApiClient.getApiClient().create(ServerApi.class);
        ServerApi apiService = retrofit.create(ServerApi.class);
//        Call<ResponseBody> call = apiService.uploadAudio(body);
//        Call<ResponseBody> call = apiService.uploadAudio(part);
        Call<ResponseBody> call = apiService.uploadAudio(part, textRequestBody);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Code to handle successful file upload
                        Log.d(TAG, "Response successful");

                        // 추가: 응답 내용 확인
//                        ResponseBody responseBody = response.body();
//                        if (responseBody != null) {
//                            // 응답 본문의 문자열 가져오기 (이 경우, 전체 내용이 문자열로 반환됩니다)
//                            String responseContent = null;
//                            try {
//                                responseContent = responseBody.string();
//                                String responseText = response.body().string();
//                                Log.i(TAG, "Response from server : " + responseText);
//                                Toast.makeText(Upload.this, "Upload Successful", Toast.LENGTH_SHORT).show();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Toast.makeText(Upload.this, "Error while reading server response", Toast.LENGTH_SHORT).show();
//                            } // catch END
//
//                            // 응답 내용을 로그로 출력
//                            if (responseContent != null) {
//                                Log.d(TAG, "Response content: " + responseContent);
//                            } else {
//                                Log.d(TAG, "Response content is null");
//                            } // else END
//                        } else {
//                            Log.d(TAG, "Response body is null");
//                        } // else END
//
//                    } else {
//                        // Code to handle unsuccessful file upload
//                        Log.e(TAG, "Response failed !!");
//                        Toast.makeText(Upload.this, "Upload failed, server response : " + response.message(), Toast.LENGTH_SHORT).show();
                        try {
                            // 서버로부터 받은 응답을 문자열로 변환하여 출력
                            String responseString = response.body().string();
                            Log.i(TAG, "check) Response from server : " + responseString);

                            // 여기서 JSONException이 발생할 수 있다는 것을 주의하세요.
                            JSONObject jsonResponse = new JSONObject(responseString);
                            String receivedText = jsonResponse.getString("received_text");
                            Log.i(TAG, "check) Received text from server : " + receivedText);

                            Toast.makeText(Upload.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {

                            Log.e(TAG, "IO error check : " + e);
                            Toast.makeText(Upload.this, "Upload successful !", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(Upload.this, "Error while reading server response", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON error check : " + e);
                            Toast.makeText(Upload.this, "Upload successful !!", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(Upload.this, "Error while parsing JSON response", Toast.LENGTH_SHORT).show();
                        } // catch

                    } else {
                        Log.e(TAG, "Upload error check : " + response.message());
                        Toast.makeText(Upload.this, "Upload failed, server response: " + response.message(), Toast.LENGTH_SHORT).show();
                    } // else END

            } // onResponse END

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Code to handle unhandled exceptions
                Log.e(TAG, "Error uploading file : ", t);
            } // onFailure END
        }); // call.enqueue END
    } // uploadAudioToServer END

    private String getMimeType(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            return null;
        } finally {
            retriever.release();
        } // finally END
    } // getMimeType END


} // CLASS END