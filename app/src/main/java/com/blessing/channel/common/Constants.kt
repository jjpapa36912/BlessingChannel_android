package com.blessing.channel.common

import com.blessing.channel.BuildConfig

object Constants {
    val SERVER_URL: String
        get() = if (BuildConfig.DEBUG) {
            "http://10.0.2.2:8080"  // 디버그 모드 (에뮬레이터용 로컬 서버 주소)
        } else {
            "http://3.36.86.32:8080" // 릴리즈 서버 주소
        }
}
