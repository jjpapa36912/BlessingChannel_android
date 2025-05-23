package com.blessing.channel.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.blessing.channel.BuildConfig

import com.blessing.channel.viewmodel.MainViewModel
import com.google.android.gms.ads.AdSize

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdBanner(viewModel: MainViewModel, tag: String) {
    val context = LocalContext.current

    LaunchedEffect(tag) {
        viewModel.recordBannerView(tag, context)
    }

    AndroidView(
        factory = {
            com.google.android.gms.ads.AdView(it).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = if (BuildConfig.DEBUG)
                    "ca-app-pub-3940256099942544/6300978111"  // 테스트용
                else
                    "ca-app-pub-xxxx/yyyy"  // 실제 광고 단위 ID
                loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

