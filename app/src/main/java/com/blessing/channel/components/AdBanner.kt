package com.blessing.channel.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.blessing.channel.BuildConfig
import com.blessing.channel.viewmodel.MainViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdBanner(viewModel: MainViewModel, tag: String) {
    val context = LocalContext.current
    val recorded = remember { mutableStateOf(false) }

    AndroidView(
        factory = {
            AdView(it).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = if (BuildConfig.DEBUG)
                    "ca-app-pub-3940256099942544/6300978111"  // 테스트용
                else
                    "ca-app-pub-5025904812537246/9924147936"  // 실제 광고 단위 ID

                adListener = object : AdListener() {
                    override fun onAdImpression() {
                        if (!recorded.value) {
                            viewModel.recordBannerView(tag, context)
                            recorded.value = true
                        }
                    }
                }

                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}