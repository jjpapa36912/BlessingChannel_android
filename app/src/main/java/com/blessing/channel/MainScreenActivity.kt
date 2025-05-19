package com.blessing.channel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.MoreHoriz

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blessing.channel.components.AdBanner
import com.blessing.channel.ui.theme.AppTheme
import com.blessing.channel.viewmodel.MainViewModel

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name") ?: ""

        setContent {
            AppTheme {
                val viewModel: MainViewModel = viewModel()

                // 유저 정보 초기 설정
                LaunchedEffect(Unit) {
                    viewModel.setUser(name)
                }

                MainScreen(viewModel = viewModel)
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = user?.let { "${it.name}님 환영합니다" } ?: "")
                },
                actions = {
                    if (user == null) {
                        TextButton(onClick = {
                            viewModel.login(context)
                        }) {
                            Text("Log in")
                        }
                    } else {
                        TextButton(onClick = {
                            viewModel.logout()

                            // ✅ 로그인 화면(MainActivity)으로 이동
                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as? Activity)?.finish()
                        }) {
                            Text("Log out")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 메인 콘텐츠 들어가는 곳
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEB85))
            .padding(10.dp)
    ) {
        Header(user = user, onLogin = { viewModel.login(context) }, onLogout = { viewModel.logout() })

        if (user != null) {
            UserProfile(user!!.name)
        }

        AdGrid()

        if (user != null) {
            Button(
                onClick = { viewModel.tryRewardedAd(user!!.email, context) },
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
fun Header(user: com.blessing.channel.viewmodel.User?, onLogin: () -> Unit, onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF795548))
        Text(text = user?.let { "${it.name}님 환영합니다" } ?: "", fontWeight = FontWeight.Bold, color = Color(0xFF795548))
        Row {
            if (user == null) {
                TextButton(onClick = onLogin) {
                    Text("Log in", color = Color(0xFF795548), fontWeight = FontWeight.Bold)
                }
            } else {
                TextButton(onClick = onLogout) {
                    Text("Log out", color = Color(0xFF795548), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UserProfile(name: String) {
    Row(
        modifier = Modifier.padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color(0xFF795548), modifier = Modifier.size(50.dp))
        Text(text = name, fontSize = 18.sp, color = Color(0xFF795548), modifier = Modifier.padding(start = 10.dp))
    }
}

@Composable
fun AdGrid() {
    val adList = List(4) { it }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(adList) { _ ->
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .background(Color(0xFFFFF8D1))
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp)
            ) {
                AdBanner()
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
            Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF795548))
        }
        IconButton(onClick = { /* Navigate to tabs */ }) {
            Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "Tabs", tint = Color(0xFF795548))
        }
        IconButton(onClick = { /* Navigate to settings */ }) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF795548))
        }
    }
}}
