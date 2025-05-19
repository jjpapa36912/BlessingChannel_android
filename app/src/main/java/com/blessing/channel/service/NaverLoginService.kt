package com.blessing.channel.service

import android.app.Activity
import android.util.Log
import okhttp3.*
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import org.json.JSONObject
import java.io.IOException

class NaverLoginService(
    private val activity: Activity,
    private val onResult: (String) -> Unit
) {
    fun login() {
        NaverIdLoginSDK.authenticate(activity, object : OAuthLoginCallback {
            override fun onSuccess() {
                val accessToken = NaverIdLoginSDK.getAccessToken()
                Log.d("NaverLoginService", "네이버 로그인 성공: $accessToken")

                // ✅ 사용자 정보 요청
                fetchUserProfile(accessToken)
            }

            override fun onError(errorCode: Int, message: String) {
                Log.e("NaverLoginService", "에러: $errorCode / $message")
                onResult("게스트")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Log.e("NaverLoginService", "실패: $httpStatus / $message")
                onResult("게스트")
            }
        })
    }

    private fun fetchUserProfile(accessToken: String?) {
        if (accessToken == null) {
            onResult("게스트")
            return
        }

        val request = Request.Builder()
            .url("https://openapi.naver.com/v1/nid/me")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NaverLoginService", "프로필 요청 실패", e)
                onResult("게스트")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { body ->
                    try {
                        val json = JSONObject(body)
                        val name = json.getJSONObject("response")
                            .getString("name") // ✅ 닉네임
                        Log.d("NaverLoginService", "닉네임 가져오기 성공: $name")
                        onResult(name)
                    } catch (e: Exception) {
                        Log.e("NaverLoginService", "JSON 파싱 오류", e)
                        onResult("게스트")
                    }
                } ?: run {
                    onResult("게스트")
                }
            }
        })
    }
    /*fun login() {
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
    }*/
}
