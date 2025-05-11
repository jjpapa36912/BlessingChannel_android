package com.blessing.channel

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.blessing.channel.service.GoogleLoginService
import com.blessing.channel.service.KakaoLoginService
import com.blessing.channel.service.NaverLoginService
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blessing.channel.ui.theme.BlessingChannelTheme
import com.kakao.sdk.auth.AuthCodeClient
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK


class MainActivity : ComponentActivity() {

    private lateinit var googleLoginService: GoogleLoginService
    private lateinit var kakaoLoginService: KakaoLoginService
    private lateinit var naverLoginService: NaverLoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)

        getKeyHash()
        // Kakao 초기화

        // Naver 초기화
        NaverIdLoginSDK.initialize(
            this,
            BuildConfig.NAVER_CLIENT_ID,
            BuildConfig.NAVER_CLIENT_SECRET,
            "Blessing Channel"
        )

        googleLoginService = GoogleLoginService(this) { account ->
            if (account != null) {
                Log.i("GoogleLogin", "구글 로그인 성공: ${account.email}")
            } else {
                Log.e("GoogleLogin", "구글 로그인 실패")
            }
        }
        // ✅ launcher 등록
        val googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            googleLoginService.handleResult(data)
        }

        // ✅ launcher를 서비스에 연결
        googleLoginService.registerLauncher(googleLoginLauncher)

        kakaoLoginService = KakaoLoginService(this) { token ->
            Log.d("KakaoLogin", "콜백 도착")
            if (token != null) {
                Log.i("KakaoLogin", "카카오 로그인 성공! 액세스 토큰: ${token.accessToken}")
            } else {
                Log.e("KakaoLogin", "카카오 로그인 실패")
            }
        }

        naverLoginService = NaverLoginService(this) { token ->
            if (token != null) {
                Log.i("NaverLogin", "네이버 로그인 성공! 토큰: $token")
            } else {
                Log.e("NaverLogin", "네이버 로그인 실패")
            }
        }

        setContent {
            BlessingChannelTheme {
                LoginScreen(
                    onGoogleLoginClick = { googleLoginService.login() },
                    onKakaoLoginClick = { kakaoLoginService.login() },
                    onNaverLoginClick = { naverLoginService.login() }
                )
            }
        }
    }
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.d("MainActivity", "onNewIntent called: $intent")
//        AuthCodeClient.instance.handleRedirectIntent(intent)
//    }
// ✅ KeyHash 디버그 함수
private fun getKeyHash() {
    try {
        val packageInfo = packageManager.getPackageInfo(
            packageName,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
                android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
            else
                android.content.pm.PackageManager.GET_SIGNATURES
        )
        val signatures = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            packageInfo.signingInfo?.apkContentsSigners
        } else {
            @Suppress("DEPRECATION")
            packageInfo.signatures
        }
        if (signatures != null) {
            for (signature in signatures) {
                val md = java.security.MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP)
                Log.d("KeyHash", "KeyHash: $keyHash")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

}





@Composable
fun LoginScreen(
    onGoogleLoginClick: () -> Unit,
    onKakaoLoginClick: () -> Unit,
    onNaverLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4B2)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "로그인",
                fontSize = 28.sp,
                color = Color(0xFFA14D3A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 40.dp)
            )
            LoginButton("구글 로그인", onClick = onGoogleLoginClick)
            Spacer(modifier = Modifier.height(10.dp))
            LoginButton("카카오 로그인", onClick = {
                Log.d("KakaoLogin", "카카오 로그인 버튼 클릭됨")

                onKakaoLoginClick()})
            Spacer(modifier = Modifier.height(10.dp))
            LoginButton("네이버 로그인", onClick = onNaverLoginClick)
        }
    }
}

@Composable
fun LoginButton(text: String, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFE580)),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF6B3E26),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
