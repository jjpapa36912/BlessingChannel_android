package com.blessing.channel.ui.board

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.blessing.channel.ui.theme.AppTheme

class BoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("username") ?: "익명" // ✅ MainScreen에서 넘긴 이름
        setContent {
            AppTheme {
                BoardScreen(currentUser =username)
            }
        }
    }
}