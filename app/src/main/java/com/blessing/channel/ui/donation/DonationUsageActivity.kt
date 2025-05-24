package com.blessing.channel.ui.donation

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blessing.channel.ui.theme.AppTheme

class DonationUsageActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                DonationUsageScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationUsageScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "모금 사용처",
                        color = Color(0xFF6B3E26),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? Activity)?.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기", tint = Color(0xFF6B3E26))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF4C2))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .background(Color(0xFFFFF8D1)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "여러분의 광고 시청과 참여를 통해 모인 기부금은 보호자가 없는 아이들을 돕는 다양한 단체에 전달됩니다.\n\n",
                fontSize = 16.sp,
                color = Color(0xFF4E342E),
                textAlign = TextAlign.Start
            )
            Text(
                text = "이 아이들은 아이스크림 하나 사 먹는 일조차 쉽지 않은 환경 속에서 살아가고 있습니다. 또래 아이들과 함께 어울리며 누려야 할 평범한 행복조차 마음껏 누리지 못하고, 어린 나이에 이미 너무 많은 것을 포기하며 살아가야 합니다.\n\n이들의 깊은 상처를 단번에 치유할 수는 없지만, 누군가가 자신들을 기억하고 사랑하고 있다는 사실만으로도 커다란 위로가 될 수 있습니다.\n\n작은 손길이 모여 아이들의 내일을 밝힙니다. 몸도 마음도 건강하게 자라나 세상의 빛이 될 수 있도록, 여러분의 따뜻한 참여가 계속 이어지기를 바랍니다.",
                fontSize = 16.sp,
                color = Color(0xFF4E342E),
                textAlign = TextAlign.Start
            )
        }
    }
}
