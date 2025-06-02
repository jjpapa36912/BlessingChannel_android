package com.blessing.channel.ui.mypage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.blessing.channel.MainScreenActivity
import com.blessing.channel.ui.theme.AppTheme
import com.blessing.channel.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.ViewModelProvider


class MyPageActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 사용자 이름을 Intent에서 받아오기
        val userName = intent.getStringExtra("name") ?: ""
        // ✅ ViewModel을 Activity Scope로 획득
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.setUser(userName)
        viewModel.fetchUserSummary(userName) // ✅ 이걸 바로 추가 또는 확인

        setContent {
            AppTheme {
                val viewModel: MainViewModel = viewModel()

                // ✅ 유저 정보 ViewModel에 전달
                LaunchedEffect(Unit) {
                    viewModel.setUser(userName)
                }
                MyPageScreen(viewModel = viewModel)

                val context = LocalContext.current
                val user by viewModel.user.collectAsState()

                // ✅ 상단에 홈 버튼 추가 포함
                Scaffold(
//                    topBar = {
//                        TopAppBar(
//                            title = { Text("마이페이지", color = Color(0xFF795548)) },
//                            navigationIcon = {
//                                IconButton(
//                                    onClick = {
//                                        val intent = Intent(context, MainScreenActivity::class.java).apply {
//                                            putExtra("name", user?.name ?: "")
//                                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                                        }
//                                        context.startActivity(intent)
//                                        (context as? Activity)?.finish()
//                                    }
//                                ) {
//                                    Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF795548))
//                                }
//                            }
//                        )
//                    }
                ) { innerPadding ->
                    // ✅ 마이페이지 콘텐츠 렌더
                    MyPageScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
