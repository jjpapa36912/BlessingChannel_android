package com.blessing.channel.ui.board


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessing.channel.common.Constants
import com.blessing.channel.ui.board.model.BoardPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.coroutines.flow.update

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject


// BoardViewModel.kt
class BoardViewModel : ViewModel() {
    private val _posts = mutableStateOf<List<BoardPost>>(emptyList())
    val posts: State<List<BoardPost>> = _posts

    init {
        fetchPostsFromServer()
    }

    fun fetchPostsFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("${Constants.SERVER_URL}/api/posts")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                if (conn.responseCode == 200) {
                    val json = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(json)


                    val postList = (0 until jsonArray.length()).map { i ->
                        val obj = jsonArray.getJSONObject(i)
                        Log.d("RESPONSE", obj.getString("id").toString())
                        BoardPost(
                            id = obj.getLong("id"),
                            title = obj.getString("title"),
                            content = obj.getString("content"),
                            author = obj.getString("author"),
                            createdAt = obj.getString("createdAt"),
                            comments = buildList {
                                val commentsArray = obj.getJSONArray("comments")
                                for (i in 0 until commentsArray.length()) {
                                    add(commentsArray.getString(i))
                                }
                            },
                            isNotice = obj.optBoolean("isNotice", obj.getString("author") == "김동준")
                        )
                    }
                    _posts.value = postList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updatePost(id: Long, title: String, content: String, isNotice: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("${Constants.SERVER_URL}/api/posts/$id")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")

                val jsonBody = """
                {
                    "title": "$title",
                    "content": "$content",
                    "isNotice": $isNotice
                }
            """.trimIndent()

                conn.outputStream.use { it.write(jsonBody.toByteArray()) }

                if (conn.responseCode == 200) {
                    fetchPostsFromServer() // 새로고침
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


//    fun addPost(title: String, content: String, author: String, isNotice: Boolean = false) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val url = URL("${Constants.SERVER_URL}/api/posts")
//                val conn = url.openConnection() as HttpURLConnection
//                conn.requestMethod = "POST"
//                conn.doOutput = true
//                conn.setRequestProperty("Content-Type", "application/json")
//
//                val jsonBody = """
//                {
//                    "title": "$title",
//                    "content": "$content",
//                    "author": "$author",
//                    "isNotice": $isNotice
//                }
//            """.trimIndent()
//
//                conn.outputStream.use { it.write(jsonBody.toByteArray()) }
//
//                if (conn.responseCode == 200) {
//                    fetchPostsFromServer() // 새로고침
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
fun addPost(title: String, content: String, author: String) {
    val isNotice = (author == "김동준") // 김동준이면 공지로 설정

    viewModelScope.launch(Dispatchers.IO) {
        try {
            val url = URL("${Constants.SERVER_URL}/api/posts")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")

            val jsonBody = JSONObject().apply {
                put("title", title)
                put("content", content)
                put("author", author)
                put("isNotice", isNotice) // 진짜 boolean 값으로 들어감
            }.toString()

            Log.d("POST_JSON", jsonBody)

            conn.outputStream.use { it.write(jsonBody.toByteArray()) }

            if (conn.responseCode == 200) {
                fetchPostsFromServer() // 등록 후 갱신
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}



    fun deletePost(postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("${Constants.SERVER_URL}/api/posts/$postId")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "DELETE"

                if (conn.responseCode == 200) {
                    // 삭제 성공 시 UI 갱신
                    _posts.value = _posts.value.filterNot { it.id == postId }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun addComment(postId: Long, comment: String) {
        viewModelScope.launch(Dispatchers.IO) { // ✅ I/O 전용 쓰레드에서 실행
            try {
                val author = comment.substringBefore(":").trim()
                val content = comment.substringAfter(":").trim()

                val json = """
                {
                    "author": "$author",
                    "content": "$content"
                }
            """.trimIndent()

                val client = OkHttpClient()
                val requestBody = json.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("${Constants.SERVER_URL}/api/posts/board/$postId/comments")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    // ✅ UI 업데이트는 Main으로 전환
                    withContext(Dispatchers.Main) {
                        _posts.value = _posts.value.map {
                            if (it.id == postId) {
                                it.copy(comments = it.comments + comment)
                            } else it
                        }

                    }
                    Log.d("Board", "댓글 등록 성공")
                } else {
                    Log.e("Board", "댓글 등록 실패: ${response.code}")
                }

            } catch (e: Exception) {
                Log.e("Board", "댓글 등록 중 오류 발생", e)
            }
        }
    }


//    fun addComment(postId: Long, comment: String) {
//        _posts.value = _posts.value.map {
//            if (it.id == postId) it.copy(comments = it.comments + comment) else it
//        }
//    }

    fun deleteComment(postId: Long, comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val author = comment.substringBefore(":").trim()
                val content = comment.substringAfter(":").trim()

                val url = URL("${Constants.SERVER_URL}/api/posts/board/$postId/comments")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "DELETE"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")

                val jsonBody = JSONObject().apply {
                    put("author", author)
                    put("content", content)
                }.toString()

                conn.outputStream.use { it.write(jsonBody.toByteArray()) }

                if (conn.responseCode == 200) {
                    withContext(Dispatchers.Main) {
                        _posts.value = _posts.value.map {
                            if (it.id == postId) it.copy(comments = it.comments.filterNot { it == comment }) else it
                        }
                        Log.d("Board", "댓글 삭제 성공")
                    }
                } else {
                    Log.e("Board", "댓글 삭제 실패: ${conn.responseCode}")
                }
            } catch (e: Exception) {
                Log.e("Board", "댓글 삭제 중 오류", e)
            }
        }
    }



    fun getMyPosts(currentUser: String): List<BoardPost> {
        return posts.value.filter { it.author == currentUser }
    }
}

