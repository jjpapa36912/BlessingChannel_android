//// ✅ MainScreenActivity.kt
//package com.blessing.channel
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import com.blessing.channel.utils.hasRecordedToday
//import com.blessing.channel.utils.markRecordedToday
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.ViewModelProvider
//import com.blessing.channel.components.AdBanner
//import com.blessing.channel.ui.board.BoardActivity
//import com.blessing.channel.ui.donation.DonationUsageActivity
//import com.blessing.channel.ui.mypage.MyPageActivity
//import com.blessing.channel.ui.theme.AppTheme
//import com.blessing.channel.viewmodel.MainViewModel
//import kotlinx.coroutines.delay
//import java.util.*
//
//class MainScreenActivity : ComponentActivity() {
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val userName = intent.getStringExtra("username") ?: ""
//        Log.d("MainScreenActivity", "userName from intent: $userName")
//
//        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
//        viewModel.setUserName(userName)
//        viewModel.setUserIfEmpty(userName)
//
//        // ✅ 서버에 유저 등록 → 이후 summary fetch
//        viewModel.setUserIfEmpty(userName)
//        viewModel.registerUserAndFetchSummary(userName)
//
////        viewModel.registerUserIfNotExists(userName)
//        viewModel.fetchGlobalDonation() // 🔁 추가
////        viewModel.fetchUserSummary(userName)         // 👉 개인별 요약 정보
////        viewModel.fetchTotalDonationFromServer()     // ✅ 전체 기부액 (모든 유저 합산)
//
//        setContent {
//            AppTheme {
//                MainScreen(viewModel = viewModel)
//            }
//
//        }
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun MainScreen(viewModel: MainViewModel) {
//    val globalDonation by viewModel.globalDonation.collectAsState()
//    val userName by viewModel.userName
//
//    val user by viewModel.user.collectAsState()
//    val context = LocalContext.current
//    var expanded by remember { mutableStateOf(false) }
//
//    val donation by viewModel.totalDonation.collectAsState()
//
//
//    Log.d("MainScreen", "User state: $user")
//// ✅ 최초 유저 or 오늘 처음 진입한 유저만 4원 적립
//    LaunchedEffect(user) {
//        if (user?.name.isNullOrBlank()) return@LaunchedEffect
//
//        val isFirstLogin = donation == 0
//        val hasAlreadyRecordedToday = hasRecordedToday(context)
//
//        if (isFirstLogin || !hasAlreadyRecordedToday) {
//            listOf("home-banner-0", "home-banner-1", "home-banner-2", "home-banner-3").forEach { tag ->
//                viewModel.recordBannerView(tag, context)
//            }
//            delay(300)
//            viewModel.fetchGlobalDonation()
//            markRecordedToday(context)
//        }
//
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFFEB85))
//            .padding(10.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            IconButton(onClick = { expanded = true }) {
//                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF6B3E26))
//            }
//
//            Text(text = "${userName}님 환영합니다", fontWeight = FontWeight.Bold)
//
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                DropdownMenuItem(text = { Text("마이페이지") }, onClick = {
//                    expanded = false
//                    val intent = Intent(context, MyPageActivity::class.java).apply {
//                        putExtra("name", user?.name ?: "")
//                    }
//                    context.startActivity(intent)
//                })
//                DropdownMenuItem(text = { Text("모금 사용처") }, onClick = {
//                    expanded = false
//                    context.startActivity(Intent(context, DonationUsageActivity::class.java))
//                })
//                DropdownMenuItem(text = { Text("로그아웃") }, onClick = {
//                    expanded = false
//                    viewModel.logout()
//                    context.startActivity(Intent(context, MainActivity::class.java))
//                    (context as? Activity)?.finish()
//                })
//            }
//
//        }
//
//        DonationProgressBar(
//            current = globalDonation,
//            goal = 1_000_000
//        )
//
//        if (user != null) {
//            UserProfile(user!!.name)
//
//            Log.d("MainScreen", "Showing RewardedAd Button for user: ${user!!.name}")
//            Button(
//                onClick = {
//                    viewModel.tryRewardedAd(user!!.name, context)
//                },
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 10.dp)
//            ) {
//                Text("정보 보고 기부에 참여하기", color = Color.White, fontWeight = FontWeight.Bold)
//            }
//        } else {
//            Log.d("MainScreen", "User is null, not showing RewardedAd Button")
//        }
//
//        val adList = List(4) { it }
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(adList) { index ->
//                Box(
//                    modifier = Modifier
//                        .padding(5.dp)
//                        .background(Color(0xFFFFF8D1))
//                        .fillMaxWidth()
//                        .height(100.dp)
//                        .padding(10.dp)
//                ) {
//                    AdBanner(viewModel, tag = "home-banner-$index")
//                }
//            }
//
//            item {
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = "필요한 정보는 당신에게, 따뜻한 나눔은 아이들에게.",
//                    color = Color(0xFF6B3E26),
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Medium,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                )
//            }
//
//            item {
//                NavigationBar(userName)
//            }
//        }
//    }
//}
//
//@Composable
//fun DonationProgressBar(current: Int, goal: Int) {
//    val progress = current.toFloat() / goal
//
//    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
//        Text(
//            text = "현재 모금액: ${current}원 / ${goal}원",
//            color = Color(0xFF795548),
//            fontWeight = FontWeight.Bold,
//            fontSize = 14.sp,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//        LinearProgressIndicator(
//            progress = progress.coerceIn(0f, 1f),
//            color = Color(0xFF795548),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(10.dp)
//        )
//    }
//}
//
//@Composable
//fun UserProfile(name: String) {
//    Row(
//        modifier = Modifier.padding(vertical = 15.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color(0xFF795548), modifier = Modifier.size(50.dp))
//        Text(text = name, fontSize = 18.sp, color = Color(0xFF795548), modifier = Modifier.padding(start = 10.dp))
//    }
//}
//
////@Composable
////fun NavigationBar() {
////    Row(
////        modifier = Modifier
////            .fillMaxWidth()
////            .padding(vertical = 10.dp)
////            .background(Color(0xFFFFE082)),
////        horizontalArrangement = Arrangement.SpaceAround
////    ) {
////        IconButton(onClick = { /* Navigate to home */ }) {
////            Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF795548))
////        }
////        IconButton(onClick = { /* Navigate to tabs */ }) {
////            Icon(Icons.Default.MoreHoriz, contentDescription = "Tabs", tint = Color(0xFF795548))
////        }
////        IconButton(onClick = { /* Navigate to settings */ }) {
////            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF795548))
////        }
////    }
////}
//@Composable
//fun NavigationBar(name: String) {
//    val context = LocalContext.current
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 10.dp)
//            .background(Color(0xFFFFE082)),
//        horizontalArrangement = Arrangement.Center // 가운데 하나만 정렬
//    ) {
//        IconButton(onClick = {
//            val intent = Intent(context, BoardActivity::class.java)
//            intent.putExtra("username", name)
//            context.startActivity(intent)
//        }) {
//            Icon(Icons.Default.Chat, contentDescription = "게시판", tint = Color(0xFF795548))
//        }
//
//    }
//}
