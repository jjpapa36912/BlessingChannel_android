package com.blessing.channel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // 이 import도 추가
import androidx.annotation.RequiresApi
import com.blessing.channel.ui.main.MainScreen
import com.blessing.channel.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels() // ✅ 뷰모델 초기화

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(viewModel = mainViewModel) // ✅ 에러 해결: 뷰모델 전달
        }
    }
}

//package com.blessing.channel
//
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import com.blessing.channel.service.GoogleLoginService
//import com.blessing.channel.service.KakaoLoginService
//import com.blessing.channel.service.NaverLoginService
//import android.util.Log
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.blessing.channel.utils.hasRecordedToday
//import com.blessing.channel.utils.markRecordedToday
//
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.Button
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.ButtonDefaults
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.blessing.channel.ui.theme.BlessingChannelTheme
//import com.blessing.channel.viewmodel.MainViewModel
//import com.kakao.sdk.auth.AuthCodeClient
//import com.kakao.sdk.common.KakaoSdk
//import com.navercorp.nid.NaverIdLoginSDK
//import kotlinx.coroutines.delay
//import java.time.LocalDate
//
//
//class MainActivity : ComponentActivity() {
//
//    private lateinit var googleLoginService: GoogleLoginService
//    private lateinit var kakaoLoginService: KakaoLoginService
//    private lateinit var naverLoginService: NaverLoginService
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        Log.d("MainActivity", "🔥 MainActivity 시작됨")
//        // ✅ AdMob 초기화
//        com.google.android.gms.ads.MobileAds.initialize(this)
//
//        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
//
//        // Kakao 초기화
//
//        // Naver 초기화
//        NaverIdLoginSDK.initialize(
//            this,
//            BuildConfig.NAVER_CLIENT_ID,
//            BuildConfig.NAVER_CLIENT_SECRET,
//            "Blessing Channel"
//        )
//
//        googleLoginService = GoogleLoginService(this) { account ->
//            account?.let {
//                val intent = Intent(this, MainScreenActivity::class.java).apply {
//                    putExtra("username", it.displayName ?: "")
//                    putExtra("email", it.email ?: "")
//                }
//                Log.i("GoogleLogin", "구글 로그인 성공: ${it.email}")
//                startActivity(intent)
//                finish()
//            }
//        }
//        // ✅ launcher 등록
//        val googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            val data = result.data
//            googleLoginService.handleResult(data)
//        }
//
//        // ✅ launcher를 서비스에 연결
//        googleLoginService.registerLauncher(googleLoginLauncher)
//
//        kakaoLoginService = KakaoLoginService(this) { name ->
//            val intent = Intent(this, MainScreenActivity::class.java).apply {
//                putExtra("username", name)
//            }
//            startActivity(intent)
//            finish()
//        }
//
//        naverLoginService = NaverLoginService(this) { name ->
//            val intent = Intent(this, MainScreenActivity::class.java).apply {
//                putExtra("username", name)
//            }
//            startActivity(intent)
//            finish()
//        }
////        naverLoginService = NaverLoginService(this) { token ->
////            if (token != null) {
////                val userName = "카카오사용자" // 실사용 시 Kakao API로 이름 가져오기
////                val intent = Intent(this, MainScreenActivity::class.java).apply {
////                    putExtra("name", userName)
////                    putExtra("email", "kakao@example.com") // 예시
////                }
////                startActivity(intent)
////                finish()
////            } else {
////                Log.e("NaverLogin", "네이버 로그인 실패")
////            }
////        }
//
//        setContent {
//            Log.d("MainActivity", "📦 setContent 진입함") // 이거 추가
//
//            BlessingChannelTheme {
//                Log.d("MainActivity", "🎨 Theme 블록 진입함") // 이거도 추가
//                val viewModel: MainViewModel = viewModel()
//                val context = LocalContext.current
//
//                val name = intent.getStringExtra("username") ?: ""
//                val email = intent.getStringExtra("email") ?: ""
//
//                LaunchedEffect(Unit) {
//                    viewModel.setUser(name)
//                    viewModel.setUserIfEmpty(name)
//                    viewModel.registerUserIfNotExists(name)
//                    delay(500) // ⏱ 배너 4개 노출되도록 UI 렌더 시간 확보
//
//                    viewModel.fetchUserSummary(name)
//                    viewModel.fetchGlobalDonation()
//
//                    val isFirstLogin = viewModel.totalDonation.value == 0
//                    val hasAlreadyRecordedToday = hasRecordedToday(context)
//
//                    // ✅ 최초 유저 or 오늘 처음 진입한 유저만 배너 4개 처리
//                    if (isFirstLogin || !hasAlreadyRecordedToday) {
//                        listOf("home-top", "home-mid", "home-bottom", "home-footer").forEach { tag ->
//                            viewModel.recordBannerView(tag, context)
//                        }
//
//                        delay(500) // ✅ suspend 허용됨
//                        viewModel.saveUserSummaryToServer(name)
//                        viewModel.fetchGlobalDonation()
//
//                        markRecordedToday(context)
//                    }
//
//                }
//
//                LoginScreen(
//                    onGoogleLoginClick = { googleLoginService.login() },
//                    onKakaoLoginClick = { kakaoLoginService.login() },
//                    onNaverLoginClick = { naverLoginService.login() }
//                )
//            }
//        }
//    }
////    override fun onNewIntent(intent: Intent?) {
////        super.onNewIntent(intent)
////        Log.d("MainActivity", "onNewIntent called: $intent")
////        AuthCodeClient.instance.handleRedirectIntent(intent)
////    }
//
//}
//
//
//
//
//
//
//@Composable
//fun LoginScreen(
//
//    onGoogleLoginClick: () -> Unit,
//    onKakaoLoginClick: () -> Unit,
//    onNaverLoginClick: () -> Unit
//) {
//    Log.d("LoginScreen", "🟢 LoginScreen 호출됨")
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFFF4B2)),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier.padding(20.dp)
//        ) {
//            Text(
//                text = "로그인",
//                fontSize = 28.sp,
//                color = Color(0xFFA14D3A),
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 40.dp)
//            )
//            LoginButton("구글 로그인", onClick = onGoogleLoginClick)
//            Spacer(modifier = Modifier.height(10.dp))
//            LoginButton("카카오 로그인", onClick = {
//                Log.d("KakaoLogin", "카카오 로그인 버튼 클릭됨")
//
//                onKakaoLoginClick()})
//            Spacer(modifier = Modifier.height(10.dp))
//            LoginButton("네이버 로그인", onClick = onNaverLoginClick)
//        }
//    }
//}
//
//@Composable
//fun LoginButton(text: String, onClick: () -> Unit = {}) {
//    Button(
//        onClick = onClick,
//        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFE580)),
//        modifier = Modifier
//            .fillMaxWidth(0.8f)
//            .height(50.dp),
//        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp)
//    ) {
//        Text(
//            text = text,
//            color = Color(0xFF6B3E26),
//            fontSize = 16.sp,
//            fontWeight = FontWeight.SemiBold
//        )
//    }
//}
