package com.blessing.channel.service

import android.app.Activity
import android.util.Log
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

class NaverLoginService(
    private val activity: Activity,
    private val onResult: (String?) -> Unit
) {
    fun login() {
        NaverIdLoginSDK.authenticate(activity, object : OAuthLoginCallback {
            override fun onSuccess() {
                val token = NaverIdLoginSDK.getAccessToken()
                Log.d("NaverLoginService", "네이버 로그인 성공: $token")
                onResult(token)
            }

            override fun onError(errorCode: Int, message: String) {
                Log.e("NaverLoginService", "에러: $errorCode / $message")
                onResult(null)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Log.e("NaverLoginService", "실패: $httpStatus / $message")
                onResult(null)
            }
        })
    }
}
