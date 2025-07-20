package com.blessing.channel.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blessing.channel.BuildConfig
import com.blessing.channel.model.HelperType
import com.blessing.channel.ui.theme.BlessingChannelTheme
import com.blessing.channel.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    var activeHelper by remember { mutableStateOf<HelperType?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 16.dp)
        ) {
            Header()
            Spacer(Modifier.height(32.dp))
            HelperButtonList(
                onHelperSelected = { activeHelper = it }
            )
            Spacer(Modifier.height(100.dp))
        }

        AnimatedVisibility(
            visible = activeHelper != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            activeHelper?.let { type ->
                HelperOverlay(
                    helperType = type,
                    onDismiss = { activeHelper = null }
                )
            }
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "BlessingChannel",
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "원하는 작업을 선택해주세요",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HelperButtonList(onHelperSelected: (HelperType) -> Unit) {
    HelperType.values().forEach { type ->
        HelperButton(type = type, onClick = { onHelperSelected(type) })
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun HelperButton(type: HelperType, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(text = type.title, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(text = type.instruction, style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelperOverlay(helperType: HelperType, onDismiss: () -> Unit) {
    var inputText by remember { mutableStateOf("") }
    var resultUrl by remember { mutableStateOf("") }
    var parsedInfo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (!matches.isNullOrEmpty()) {
            inputText = matches[0]
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "← 뒤로가기")
                    }
                    Spacer(Modifier.weight(1f))
                    Text(text = helperType.title, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text(helperType.placeholder) }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN)
                            }
                            speechLauncher.launch(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🎤 음성 인식")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                fetchLink(helperType, inputText) { link, info, err ->
                                    resultUrl = link
                                    parsedInfo = info
                                    error = err
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(helperType.buttonText)
                    }
                }

                if (parsedInfo.isNotEmpty()) {
                    Text("🔍 결과 요약: $parsedInfo", fontSize = 14.sp)
                }

                if (resultUrl.isNotEmpty()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resultUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔗 링크 열기", color = Color.White)
                    }
                }

                error?.let {
                    Text("❌ $it", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(Modifier.weight(1f))
                Text(
                    text = "※ 음성 인식은 한국어만 지원됩니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

suspend fun fetchLink(
    type: HelperType,
    query: String,
    onResult: (String, String, String?) -> Unit
) {
    val encoded = URLEncoder.encode(query, "UTF-8")
    val baseUrl = if (BuildConfig.DEBUG) "http://10.0.2.2:5001" else "http://13.124.208.108:5001"
    Log.d("DEBUG_CHECK", "BuildConfig.DEBUG: ${BuildConfig.DEBUG}")

    val url = "$baseUrl${type.requestURL}?query=$encoded"

    withContext(Dispatchers.IO) {
        try {
            val text = URL(url).readText()
            val json = JSONObject(text)
            val link = json.optString("url", "")
            val info = json.optString("parsed_name", json.optString("message", ""))
            val err = json.optString("error", null)
            withContext(Dispatchers.Main) {
                onResult(link, info, err)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("", "", "네트워크 오류: ${e.localizedMessage}")
            }
        }
    }
}
