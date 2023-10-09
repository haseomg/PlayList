package com.example.playlist;

import com.kakao.sdk.common.KakaoSdk;
import android.app.Application;


public class GlobalApplication extends Application {

    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 네이티브 앱 키로 초기화
        KakaoSdk.init(this, "e7657431c08b5889a35e98139daef99a");
    }

}
