package com.blessing.channel.ui.mypage
//이제 남은 건:
//
//교환 이력 서버 연동 필요 여부 결정 (서버 API 필요 시 구조 설계 도와드릴 수 있음)
//
//포인트 교환 시 특정 보상 방식 구현 (예: 쿠폰 발급 등)
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.blessing.channel.MainScreenActivity
import com.blessing.channel.ui.reward.RewardActivity
import com.blessing.channel.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyPageScreen(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val point by viewModel.point.collectAsState()
    val donation by viewModel.totalDonation.collectAsState()

    val ranking by viewModel.rankingList.collectAsState()
    val redeemHistory by viewModel.redeemHistory.collectAsState()
    val profileImageUri = viewModel.profileImageUri.collectAsState()

    val context = LocalContext.current
    val showDeleteDialog = remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.saveProfileImageUri(it.toString(), context)
        }
    }

    val canRedeem = point >= 100

    LaunchedEffect(Unit) {
        viewModel.fetchUserSummary(user?.name ?: "")
        viewModel.fetchRanking()
        viewModel.loadProfileImageUri(context)
        viewModel.fetchRedeemHistoryFromServer()



    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "마이페이지",
                        color = Color(0xFF6B3E26),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, MainScreenActivity::class.java).apply {
                            putExtra("name", user?.name ?: "")
                        }
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기", tint = Color(0xFF6B3E26))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF4C2))
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFFFFE082)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = {
                        val intent = Intent(context, MainScreenActivity::class.java).apply {
                            putExtra("name", user?.name ?: "")
                        }
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }) {
                        Icon(Icons.Default.Home, contentDescription = "홈", tint = Color(0xFF6B3E26))
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Person, contentDescription = "마이페이지", tint = Color(0xFF6B3E26))
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFF4C2))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (profileImageUri.value.isNullOrEmpty()) {
                IconButton(onClick = { launcher.launch("image/*") }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "프로필 설정",
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFF6B3E26)
                    )
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri.value),
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .combinedClickable(
                            onClick = { launcher.launch("image/*") },
                            onLongClick = { showDeleteDialog.value = true }
                        )
                )
            }

            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteProfileImage(context)
                            showDeleteDialog.value = false
                        }) {
                            Text("삭제")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog.value = false }) {
                            Text("취소")
                        }
                    },
                    title = { Text("프로필 이미지 삭제") },
                    text = { Text("현재 설정된 프로필 이미지를 삭제하시겠습니까?") }
                )
            }

            Text(
                text = "${user?.name ?: "게스트"}님의 마이페이지",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B3E26),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text("현재 포인트: ${point}P", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(12.dp)) // ← 약간의 여백 추가
            RankingBoard(viewModel = viewModel) // 🔥 랭킹 보드 삽입 위치


            Button(
                onClick = {
                    context.startActivity(Intent(context, RewardActivity::class.java).apply {
                        putExtra("rewardLevel", if (point >= 200) "premium" else "basic")
                    })
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B3E26))
            ) {
                Text("쿠폰함 열기", color = Color.White)
            }

            Text("\uD83C\uDFC6 랭킹 보드", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
            ranking.take(5).forEachIndexed { index, user ->
                Text("${index + 1}위 - ${user.name}: ${user.point}P", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (canRedeem) {
                        viewModel.redeemReward(context)
                    } else {
                        Toast.makeText(context, "포인트가 부족합니다. (100P 이상 필요)", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = canRedeem,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
            ) {
                Text("보상 받기 (100P 차감)", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(thickness = 1.dp, color = Color(0xFFBCAAA4))
            Text(
                text = "\uD83D\uDCC2 포인트 교환 이력",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B3E26),
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            if (redeemHistory.isEmpty()) {
                Text("아직 교환 내역이 없습니다.", fontSize = 14.sp)
            } else {
                redeemHistory.reversed().forEach {
                    Text("• $it", fontSize = 14.sp)
                }
            }
        }
    }
}