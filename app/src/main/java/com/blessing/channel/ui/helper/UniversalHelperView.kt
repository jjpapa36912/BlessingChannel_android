package com.blessing.channel.ui.helper

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blessing.channel.BuildConfig
import com.blessing.channel.model.HelperType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder
import java.net.URL


@Composable
fun HelperScreen(type: HelperType, onClose: () -> Unit) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var resultUrl by remember { mutableStateOf("") }
    var parsedInfo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onClose) {
                Text("← 뒤로가기", color = Color.Blue)
            }
            Text(type.title, fontSize = 20.sp)
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = inputText,
            onValueChange = { inputText = it },
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F0F0))
                        .padding(12.dp)
                ) {
                    if (inputText.isBlank()) Text(type.placeholder, color = Color.Gray)
                    innerTextField()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    fetchLink(type, inputText) { link, info, err ->
                        resultUrl = link
                        parsedInfo = info
                        error = err
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(type.buttonText)
        }

        if (parsedInfo.isNotEmpty()) {
            Text("🔍 결과 요약: $parsedInfo", fontSize = 14.sp)
        }

        if (resultUrl.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
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

        Spacer(modifier = Modifier.height(8.dp))
        Text("※ 음성 인식은 Android 음성 입력 버튼으로 구현하세요.", fontSize = 12.sp, color = Color.Gray)
    }
}

suspend fun fetchLink(
    type: HelperType,
    query: String,
    onResult: (String, String, String?) -> Unit
) {
    val encoded = URLEncoder.encode(query, "UTF-8")
    val baseUrl = if (BuildConfig.DEBUG) "http://10.0.2.2:5001" else "http://13.124.208.108:5001"
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
