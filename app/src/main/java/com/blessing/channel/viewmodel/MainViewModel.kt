package com.blessing.channel.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessing.channel.BuildConfig
import com.blessing.channel.MainActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject

data class User(val name: String, val email: String)

class MainViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun setUser(name: String) {
        _user.value = User(name, "")
    }

    fun login(context: Context) {
        // ✅ 로그인 화면으로 이동
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    fun logout() {
        _user.value = null
    }

    private var rewardedAd: RewardedAd? = null

    fun tryRewardedAd(userId: String, context: Context) {
        viewModelScope.launch {
            val allowed = checkRewardAvailable(userId)
            if (!allowed) {
                Toast.makeText(context, "오늘의 광고 시청 한도를 초과했어요!", Toast.LENGTH_LONG).show()
                return@launch
            }

            loadRewardedAd(context, userId)
        }
    }

    private suspend fun checkRewardAvailable(userId: String): Boolean {
        return try {
            val url = "https://your-api.com/reward/available?userId=$userId"
            val request = Request.Builder().url(url).get().build()
            val response = OkHttpClient().newCall(request).execute()
            val json = JSONObject(response.body?.string() ?: "{}")
            json.optBoolean("allowed", false)
        } catch (e: Exception) {
            Log.e("RewardCheck", "에러: ${e.message}")
            false
        }
    }

    private fun loadRewardedAd(context: Context, userId: String) {
        val adRequest = AdRequest.Builder().build()
        val adUnitId = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/5224354917" else "ca-app-pub-xxxx/yyyy"

        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    rewardedAd?.show(context as Activity) { rewardItem: RewardItem ->
                        Log.d("RewardAd", "보상 수령: ${rewardItem.amount}")
                        claimReward(userId, rewardItem.amount, context)
                    }
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    Log.e("RewardAd", "로드 실패: $error")
                }
            }
        )
    }

    private fun claimReward(userId: String, amount: Int, context: Context) {
        viewModelScope.launch {
            try {
                val json = JSONObject().apply {
                    put("userId", userId)
                    put("amount", amount)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("https://your-api.com/reward/claim")
                    .post(body)
                    .build()

                OkHttpClient().newCall(request).execute()
                Toast.makeText(context, "코인이 지급되었습니다!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("RewardAd", "보상 처리 실패: ${e.message}")
            }
        }
    }
}