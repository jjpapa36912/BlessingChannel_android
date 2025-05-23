package com.blessing.channel.ui.reward
//POST https://your-api.com/reward/use
//Body:
//{
//    "userId": "동준",
//    "coupon": "10% 할인 쿠폰"
//}

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blessing.channel.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class RewardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rewardLevel = intent.getStringExtra("rewardLevel") ?: "basic"
        val userName = intent.getStringExtra("userName") ?: "게스트"

        setContent {
            AppTheme {
                RewardScreen(rewardLevel = rewardLevel, userName = userName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardScreen(rewardLevel: String, userName: String) {
    val context = LocalContext.current
    var coupons by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        coupons = fetchRewardListFromServer(rewardLevel)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("보상 쿠폰", color = Color(0xFF6B3E26)) },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? Activity)?.finish()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color(0xFF6B3E26)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF4C2))
            )
        },
        containerColor = Color(0xFFFFF4C2)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (rewardLevel == "premium") "🎁 프리미엄 쿠폰" else "🎉 기본 쿠폰",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B3E26)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF6B3E26))
            } else if (coupons.isEmpty()) {
                Text("사용 가능한 쿠폰이 없습니다.", color = Color.Gray)
            } else {
                coupons.forEach { coupon ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("• $coupon", fontSize = 16.sp, color = Color.DarkGray)
                        Button(
                            onClick = {
                                // 서버 전송 후 리스트에서 제거
                                useCoupon(userName, coupon) {
                                    coupons = coupons - coupon
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
                        ) {
                            Text("사용", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "※ 쿠폰은 유효기간 7일, 앱 내에서만 사용 가능",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

suspend fun fetchRewardListFromServer(level: String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://your-api.com/reward/list?level=$level"
            val request = Request.Builder().url(url).build()
            val response = OkHttpClient().newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()
            val array = JSONArray(body)
            List(array.length()) { i -> array.getString(i) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

fun useCoupon(userId: String, coupon: String, onSuccess: () -> Unit) {
    val json = JSONObject().apply {
        put("userId", userId)
        put("coupon", coupon)
    }
    val body = json.toString().toRequestBody("application/json".toMediaType())
    val request = Request.Builder()
        .url("https://your-api.com/reward/use")
        .post(body)
        .build()

    Thread {
        try {
            OkHttpClient().newCall(request).execute()
            onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}

