//package com.blessing.channel.ui.theme
//
//import android.content.Intent
//import android.net.Uri
//import android.speech.RecognizerIntent
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.ExperimentalAnimationApi
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.slideInVertically
//import androidx.compose.animation.slideOutVertically
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.blessing.channel.BuildConfig
//import com.blessing.channel.model.HelperType
//import com.blessing.channel.viewmodel.MainViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//import java.net.URL
//import java.net.URLEncoder
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
//@Composable
//fun MainScreen(viewModel: MainViewModel = viewModel()) {
//    var activeHelper by remember { mutableStateOf<HelperType?>(null) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        Column(
//            modifier = Modifier
//                .verticalScroll(rememberScrollState())
//                .padding(top = 48.dp, bottom = 16.dp)
//        ) {
//            Header()
//            Spacer(Modifier.height(32.dp))
//            HelperButtonList(
//                onHelperSelected = { activeHelper = it }
//            )
//            Spacer(Modifier.height(100.dp))
//        }
//
//        AnimatedVisibility(
//            visible = activeHelper != null,
//            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
//            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
//        ) {
//            activeHelper?.let { type ->
//                HelperOverlay(
//                    helperType = type,
//                    onDismiss = { activeHelper = null }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun Header() {
//    Column(
//        modifier = Modifier.padding(horizontal = 16.dp),
//        horizontalAlignment = Alignment.Start
//    ) {
//        Text(
//            text = "BlessingChannel",
//            fontSize = 28.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = MaterialTheme.colorScheme.primary
//        )
//        Text(
//            text = "원하는 작업을 선택해주세요",
//            fontSize = 14.sp,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}
//
//@Composable
//private fun HelperButtonList(onHelperSelected: (HelperType) -> Unit) {
//    HelperType.values().forEach { type ->
//        HelperButton(type = type, onClick = { onHelperSelected(type) })
//        Spacer(Modifier.height(12.dp))
//    }
//}
//
//@Composable
//private fun HelperButton(type: HelperType, onClick: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp)
//            .background(
//                MaterialTheme.colorScheme.surface,
//                shape = MaterialTheme.shapes.medium
//            )
//            .clickable { onClick() }
//            .padding(16.dp)
//    ) {
//        Text(text = type.title, fontWeight = FontWeight.Bold)
//        Spacer(Modifier.height(4.dp))
//        Text(text = type.instruction, style = MaterialTheme.typography.bodySmall)
//    }
//}
