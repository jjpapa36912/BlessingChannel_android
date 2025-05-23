// ✅ MainScreenActivity.kt
package com.blessing.channel

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blessing.channel.components.AdBanner
import com.blessing.channel.ui.mypage.MyPageActivity
import com.blessing.channel.ui.theme.AppTheme
import com.blessing.channel.viewmodel.MainViewModel

class MainScreenActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name") ?: ""

        setContent {
            AppTheme {
                val viewModel: MainViewModel = viewModel()
                LaunchedEffect(Unit) {
                    viewModel.setUser(name)
                }
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEB85))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF6B3E26))
            }

            Text(text = "${user?.name ?: "게스트"}님 환영합니다", fontWeight = FontWeight.Bold)

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(text = { Text("마이페이지") }, onClick = {
                    expanded = false
                    val intent = Intent(context, MyPageActivity::class.java).apply {
                        putExtra("name", user?.name ?: "")
                    }
                    context.startActivity(intent)
                })
                DropdownMenuItem(text = { Text("로그아웃") }, onClick = {
                    expanded = false
                    viewModel.logout()
//                    context.startActivity(Intent(context, MainActivity::class.java))
                    context.startActivity(Intent(context, MyPageActivity::class.java).apply {
                        putExtra("name", user?.name ?: "")
                    })

                    (context as? Activity)?.finish()
                })
            }
        }

        DonationProgressBar(
            current = viewModel.totalEarnedWon,
            goal = 1_000_000
        )

        Text(
            text = "오늘의 수익: ${viewModel.totalEarnedWon}원",
            fontSize = 16.sp,
            color = Color(0xFF795548),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )

        if (user != null) {
            UserProfile(user!!.name)
        }

        AdGrid(viewModel)

        if (user != null) {
            Button(
                onClick = {
                    viewModel.tryRewardedAd(user!!.name, context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text("광고 보고 코인 받기", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            text = "Thank you!",
            color = Color(0xFF795548),
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        NavigationBar()
    }
}

@Composable
fun DonationProgressBar(current: Int, goal: Int) {
    val progress = current.toFloat() / goal
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(
            text = "현재 모금액: ${current}원 / ${goal}원",
            color = Color(0xFF795548),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            color = Color(0xFF795548),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

@Composable
fun UserProfile(name: String) {
    Row(
        modifier = Modifier.padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color(0xFF795548), modifier = Modifier.size(50.dp))
        Text(text = name, fontSize = 18.sp, color = Color(0xFF795548), modifier = Modifier.padding(start = 10.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdGrid(viewModel: MainViewModel) {
    val adList = List(4) { it }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(adList) { index ->
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .background(Color(0xFFFFF8D1))
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp)
            ) {
                AdBanner(viewModel, tag = "home-banner-$index")
            }
        }
    }
}

@Composable
fun NavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(Color(0xFFFFE082)),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = { /* Navigate to home */ }) {
            Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF795548))
        }
        IconButton(onClick = { /* Navigate to tabs */ }) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "Tabs", tint = Color(0xFF795548))
        }
        IconButton(onClick = { /* Navigate to settings */ }) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF795548))
        }
    }
}