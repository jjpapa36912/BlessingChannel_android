package com.blessing.channel.service

import android.app.Activity
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class KakaoLoginService(
    private val activity: Activity,
    private val onResult: (OAuthToken?) -> Unit
) {
    fun login() {
        Log.d("KakaoLoginService", "로그인 시도 시작됨1.")
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            Log.d("KakaoLoginService", "카카오톡으로 로그인 시도")
            // 카카오톡 앱으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
                Log.d("KakaoLoginService", "카카오톡 콜백 진입")
                if (error != null) {
                    Log.e("KakaoLoginService", "카카오톡 로그인 실패", error)
                    // 실패 시 웹으로 fallback

                    loginWithAccount()
                } else {
                    Log.d("KakaoLoginService", "카카오톡 로그인 성공 ${token?.accessToken}")
                    onResult(token)
                }
            }
        } else {
            Log.d("KakaoLoginService", "카카오 계정 로그인 시도")
            // 카카오 계정으로 로그인
            loginWithAccount()
        }
    }

    private fun loginWithAccount() {
        Log.d("KakaoLoginService", "로그인 시도 시작됨")
        Log.d("KakaoLoginService", "Activity class: ${activity::class.java.simpleName}")

        UserApiClient.instance.loginWithKakaoAccount(activity) { token, error ->
            Log.d("KakaoLoginService", "카카오 계정 로그인 시도22222")
            if (error != null) {
                Log.e("KakaoLoginService", "카카오계정 로그인 실패", error)
                onResult(null)
            } else {
                Log.d("KakaoLoginService", "카카오계정 로그인 성공 ${token?.accessToken}")
                onResult(token)
            }
        }
    }
}
