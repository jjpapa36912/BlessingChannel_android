// 파일: ui/mypage/RankingBoard.kt

package com.blessing.channel.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blessing.channel.viewmodel.MainViewModel
import com.blessing.channel.model.RankingUser

@Composable
fun RankingBoard(viewModel: MainViewModel) {
    val rankings by viewModel.ranking.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(Color(0xFFFFF8D1))
            .padding(16.dp)
    ) {
        Text(
            text = "🏆 랭킹 보드",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF6B3E26)
        )

        rankings.forEachIndexed { index, user ->
            Text(
                text = "${index + 1}위: ${user.name} - ${user.point}P",
                fontSize = 16.sp,
                color = Color(0xFF6B3E26),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

    }
}