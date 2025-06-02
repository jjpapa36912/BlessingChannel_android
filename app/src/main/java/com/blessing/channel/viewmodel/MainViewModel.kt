package com.blessing.channel.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessing.channel.BuildConfig
import com.blessing.channel.MainActivity
import com.blessing.channel.model.RankingUser
import com.blessing.channel.ui.reward.RewardActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class User(val name: String, val email: String)
data class RankedUser(val name: String, val point: Int)
const val SERVER_URL = "http://3.36.86.32:8080"

class MainViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    val ranking = MutableStateFlow<List<RankingUser>>(emptyList())
    private val _rankingList = MutableStateFlow<List<RankedUser>>(emptyList())
    val rankingList: StateFlow<List<RankedUser>> = _rankingList


    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri

    private val _redeemHistory = MutableStateFlow<List<String>>(emptyList())
    val redeemHistory: StateFlow<List<String>> = _redeemHistory

    private val _point = MutableStateFlow(0)
    val point: StateFlow<Int> = _point

    private val _bannerViewCount = MutableStateFlow(0)
    val bannerViewCount: StateFlow<Int> = _bannerViewCount

    private val _rewardedEarnedAmount = MutableStateFlow(0)
    val rewardedEarnedAmount: StateFlow<Int> = _rewardedEarnedAmount

    private var rewardedAd: RewardedAd? = null

    private val _totalDonation = MutableStateFlow(0) // 개인 누적 기부액
    val totalDonation: StateFlow<Int> = _totalDonation

    private val _globalDonation = MutableStateFlow(0) // 전체 유저 누적 기부액
    val globalDonation: StateFlow<Int> = _globalDonation


    fun fetchTotalDonationFromServer() {
        viewModelScope.launch {
            try {
                val donation = withContext(Dispatchers.IO) {
                    val request = Request.Builder().url("$SERVER_URL/api/ads/total").get().build()
                    val response = OkHttpClient().newCall(request).execute()
                    val body = response.body?.string() ?: return@withContext 0
                    JSONObject(body).getInt("totalDonation")
                }
                _totalDonation.value = donation
            } catch (e: Exception) {
                Log.e("DonationFetch", "서버 요청 실패", e)
            }
        }
    }
    fun registerUserIfNotExists(userId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val json = JSONObject().apply {
                        put("point", 0)
                    }
                    val body = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url("$SERVER_URL/api/users/$userId/summary")
                        .post(body)
                        .build()
                    OkHttpClient().newCall(request).execute()
                }
            } catch (e: Exception) {
                Log.e("UserRegister", "서버 등록 실패", e)
            }
        }
    }

    fun fetchTotalPoints() {
        viewModelScope.launch {
            try {
                val totalPoints = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url("$SERVER_URL/api/ads/points/total")
                        .get().build()
                    val response = OkHttpClient().newCall(request).execute()
                    val body = response.body?.string() ?: return@withContext 0
                    JSONObject(body).optInt("totalPoints", 0)
                }
                Log.d("Points", "전체 포인트: $totalPoints")
            } catch (e: Exception) {
                Log.e("Points", "조회 실패", e)
            }
        }
    }

    fun fetchUserSummary(userId: String) {
        viewModelScope.launch {
            try {
                val (p, d) = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url("$SERVER_URL/api/users/name/$userId/summary")
                        .get()
                        .build()
                    val response = OkHttpClient().newCall(request).execute()
                    val body = response.body?.string() ?: return@withContext 0 to 0
                    val json = JSONObject(body)
                    val point = json.optInt("totalPoint", 0)
                    val donation = json.optInt("totalDonation", 0)
                    point to donation
                }
                _point.value = p
                _totalDonation.value = d
            } catch (e: Exception) {
                Log.e("UserSummary", "조회 실패", e)
            }
        }
    }

    fun fetchGlobalDonation() {
        viewModelScope.launch {
            try {
                val donation = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url("$SERVER_URL/api/users/total-donation") // ✅ 수정된 엔드포인트
                        .get()
                        .build()
                    val response = OkHttpClient().newCall(request).execute()
                    val body = response.body?.string() ?: return@withContext 0
                    JSONObject(body).optInt("totalDonation", 0)
                }
                _globalDonation.value = donation
            } catch (e: Exception) {
                Log.e("GlobalDonationFetch", "전체 기부금 조회 실패", e)
            }
        }
    }





    //    fun fetchUserSummary(userId: String) {
//        viewModelScope.launch {
//            try {
//                val (p, d) = withContext(Dispatchers.IO) {
//                    val request = Request.Builder().url("$SERVER_URL/api/users/$userId/summary").get().build()
//                    val response = OkHttpClient().newCall(request).execute()
//                    val body = response.body?.string() ?: return@withContext 0 to 0
//                    val json = JSONObject(body)
//                    json.getInt("point") to json.getInt("totalDonation")
//                }
//                _point.value = p
//                _totalDonation.value = d
//            } catch (e: Exception) {
//                Log.e("UserSummary", "조회 실패", e)
//            }
//        }
//    }
fun saveUserSummaryToServer(userId: String) {
    viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                val json = JSONObject().apply {
                    put("point", _totalDonation.value / 10) // ✅ 항상 모금액 기준
                    put("totalDonation", _totalDonation.value)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("$SERVER_URL/api/users/$userId/summary")
                    .post(body)
                    .build()
                OkHttpClient().newCall(request).execute()
            }
        } catch (e: Exception) {
            Log.e("UserSummarySave", "서버 저장 실패", e)
        }
    }
}

    fun fetchRedeemHistoryFromServer() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val userId = user.value?.name ?: return@withContext emptyList()
                    val url = "$SERVER_URL/reward/history?userId=$userId"
                    val request = Request.Builder().url(url).get().build()
                    val response = OkHttpClient().newCall(request).execute()
                    val jsonArray = JSONArray(response.body?.string() ?: "[]")
                    List(jsonArray.length()) { i -> jsonArray.getString(i) }
                }
                _redeemHistory.value = result
            } catch (e: Exception) {
                Log.e("RedeemHistory", "이력 조회 실패", e)
            }
        }
    }

    fun saveRedeemToServer(userId: String, entry: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val json = JSONObject().apply {
                        put("userId", userId)
                        put("entry", entry)
                    }
                    val body = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder().url("$SERVER_URL/reward/history").post(body).build()
                    OkHttpClient().newCall(request).execute()
                }
            } catch (e: Exception) {
                Log.e("RedeemSave", "서버 저장 실패", e)
            }
        }
    }

    fun fetchRanking() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val request = Request.Builder().url("$SERVER_URL/ranking").get().build()
                    val response = OkHttpClient().newCall(request).execute()
                    val jsonArray = JSONArray(response.body?.string() ?: "[]")
                    List(jsonArray.length()) { i ->
                        val obj = jsonArray.getJSONObject(i)
                        RankingUser(obj.getString("name"), obj.getInt("point"))
                    }
                }
                ranking.value = result
            } catch (e: Exception) {
                Log.e("Ranking", "불러오기 실패", e)
            }
        }
    }

    private fun saveTotalDonation(context: Context) {
        context.getSharedPreferences("donation_prefs", Context.MODE_PRIVATE)
            .edit().putInt("total_donation", _totalDonation.value).apply()
    }

    fun setUser(name: String) { _user.value = User(name, "") }
    fun setUserIfEmpty(name: String) {
        if (_user.value == null || _user.value?.name.isNullOrBlank()) setUser(name)
    }
    fun logout() { _user.value = null }

    fun redeemReward(context: Context) {
        val userId = user.value?.name ?: return
        if (_point.value < 100) {
            Toast.makeText(context, "포인트가 부족합니다. 최소 100P 필요", Toast.LENGTH_SHORT).show()
            return
        }
        _point.value -= 100
        val newEntry = "보상 교환 - ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}"
        _redeemHistory.value = _redeemHistory.value + newEntry
        saveRedeemToServer(userId, newEntry)
        context.startActivity(Intent(context, RewardActivity::class.java).apply {
            putExtra("rewardLevel", if (_point.value >= 200) "premium" else "basic")
            putExtra("userName", userId)
        })
        Toast.makeText(context, "🎉 보상이 지급되었습니다! (100P 차감)", Toast.LENGTH_SHORT).show()
    }

    fun loadProfileImageUri(context: Context) {
        _profileImageUri.value = context.getSharedPreferences("mypage", Context.MODE_PRIVATE)
            .getString("profile_image_uri", null)
    }
    fun saveProfileImageUri(uri: String, context: Context) {
        _profileImageUri.value = uri
        context.getSharedPreferences("mypage", Context.MODE_PRIVATE)
            .edit().putString("profile_image_uri", uri).apply()
    }
    fun deleteProfileImage(context: Context) {
        _profileImageUri.value = null
        context.getSharedPreferences("mypage", Context.MODE_PRIVATE)
            .edit().remove("profile_image_uri").apply()
    }

    fun login(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    fun sendBannerViewToServer(tag: String, context: Context) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val userId = user.value?.name ?: return@withContext
                    val json = JSONObject().apply {
                        put("userId", userId)
                        put("section", tag)
                        put("timestamp", System.currentTimeMillis())
                    }
                    val body = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url("$SERVER_URL/ads/report?section=$tag")
                        .post(body)
                        .build()
                    OkHttpClient().newCall(request).execute()
                }
            } catch (e: Exception) {
                Log.e("BannerSend", "서버 전송 실패", e)
            }
        }
    }

    fun claimReward(userId: String, amount: Int, context: Context) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val json = JSONObject().apply {
                        put("userId", userId)
                        put("amount", amount)
                    }
                    val body = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url("$SERVER_URL/reward/claim")
                        .post(body)
                        .build()
                    OkHttpClient().newCall(request).execute()
                    Toast.makeText(context, "코인이 지급되었습니다!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RewardAd", "보상 처리 실패: ${e.message}")
            }
        }
    }
    init {
        _rankingList.value = listOf(
            RankedUser("사용자1", 250),
            RankedUser("나", 200),
            RankedUser("사용자2", 150)
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isBannerViewTodayAllowed(tag: String, context: Context): Boolean {
        val prefs = context.getSharedPreferences("banner_view_prefs", Context.MODE_PRIVATE)
        val todayKey = "$tag:${LocalDate.now()}"
        return !prefs.getBoolean(todayKey, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun markBannerViewToday(tag: String, context: Context) {
        val prefs = context.getSharedPreferences("banner_view_prefs", Context.MODE_PRIVATE)
        val todayKey = "$tag:${LocalDate.now()}"
        prefs.edit().putBoolean(todayKey, true).apply()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun tryRewardedAd(userId: String, context: Context) {
        if (!isRewardAllowedToday(context)) {
            Toast.makeText(context, "오늘의 보상형 광고 시청 한도를 초과했어요!", Toast.LENGTH_LONG).show()
            return
        }
        val adRequest = AdRequest.Builder().build()
        val adUnitId = if (BuildConfig.DEBUG)
            "ca-app-pub-3940256099942544/5224354917" else "ca-app-pub-5025904812537246/9924147936"

        RewardedAd.load(
            context, adUnitId, adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    rewardedAd?.show(context as Activity) { rewardItem: RewardItem ->
                        val fixedAmount = 10 // ✅ 강제 보상 금액
                        claimReward(userId, fixedAmount, context)
                        fetchUserSummary(userId)
                        recordRewardUseToday(context)
                        Log.d("RewardAd", "보상 수령: $fixedAmount")
                    }
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    Log.e("RewardAd", "로드 실패: $error")
                }
            })
    }

//    fun isRewardAllowedToday(context: Context): Boolean {
//        val prefs = context.getSharedPreferences("reward_prefs", Context.MODE_PRIVATE)
//        val lastDate = prefs.getString("last_reward_date", null)
//        return lastDate != LocalDate.now().toString()
//    }
//
//    fun recordRewardUseToday(context: Context) {
//        val prefs = context.getSharedPreferences("reward_prefs", Context.MODE_PRIVATE)
//        prefs.edit().putString("last_reward_date", LocalDate.now().toString()).apply()
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun recordBannerView(tag: String, context: Context) {
        if (!isBannerViewTodayAllowed(tag, context)) {
            Log.d("BannerAd", "광고 이미 노출됨: $tag")
            return
        }

        _bannerViewCount.value += 1
        _point.value = _totalDonation.value / 10
        _totalDonation.value += 1
        saveTotalDonation(context)

        markBannerViewToday(tag, context)
        sendBannerViewToServer(tag, context)
        Log.d("BannerAd", "광고 수익 반영: $tag → totalDonation: ${_totalDonation.value}")

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isRewardAllowedToday(context: Context): Boolean {
        val prefs = context.getSharedPreferences("reward_limit", Context.MODE_PRIVATE)
        val todayKey = LocalDate.now().toString()
        val count = prefs.getInt(todayKey, 0)
        return count < 5
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun recordRewardUseToday(context: Context) {
        val prefs = context.getSharedPreferences("reward_limit", Context.MODE_PRIVATE)
        val todayKey = LocalDate.now().toString()
        val count = prefs.getInt(todayKey, 0)
        prefs.edit().putInt(todayKey, count + 1).apply()
    }

//    fun isRewardAllowedToday(context: Context): Boolean {
//        val prefs = context.getSharedPreferences("reward_prefs", Context.MODE_PRIVATE)
//        val lastDate = prefs.getString("last_reward_date", null)
//        return lastDate != LocalDate.now().toString()
//    }
//
//    fun recordRewardUseToday(context: Context) {
//        val prefs = context.getSharedPreferences("reward_prefs", Context.MODE_PRIVATE)
//        prefs.edit().putString("last_reward_date", LocalDate.now().toString()).apply()
//    }


    companion object {
//        const val SERVER_URL = "http://3.36.86.32:8080"
            const val SERVER_URL = "http://10.0.2.2:8080"
    }
}








