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
                        BoardPost(
                            id = obj.getLong("id"),
                            title = obj.getString("title"),
                            content = obj.getString("content"),
                            author = obj.getString("author"),
                            createdAt = obj.getString("createdAt"),
                            comments = emptyList(),
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
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(comments = it.comments + comment) else it
        }
    }

    fun deleteComment(postId: Long, comment: String) {
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(comments = it.comments.filterNot { it == comment }) else it
        }
    }

    fun getMyPosts(currentUser: String): List<BoardPost> {
        return posts.value.filter { it.author == currentUser }
    }
}

