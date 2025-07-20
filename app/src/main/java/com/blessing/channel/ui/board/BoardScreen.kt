//package com.blessing.channel.ui.board
//
//import android.content.Intent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateMapOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.blessing.channel.MainScreenActivity
//import com.blessing.channel.ui.board.model.BoardPost
//
//// BoardScreen.kt
//@Composable
//fun BoardScreen(currentUser: String, viewModel: BoardViewModel = viewModel() ) {
//    val posts by viewModel.posts
//    var showForm by remember { mutableStateOf(false) }
//    var editingPost by remember { mutableStateOf<BoardPost?>(null) }
//    val commentTexts = remember { mutableStateMapOf<Long, String>() }
//    var showMyPosts by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//
//    val sortedPosts = posts.sortedWith(
//        compareByDescending<BoardPost> { it.author == "김동준" } // ✅ 김동준 글 우선
//            .thenByDescending { it.isNotice }                   // ✅ 공지사항 우선
//            .thenByDescending { it.id }                         // ✅ 최신순
//    )
//    val displayPosts = if (showMyPosts) sortedPosts.filter { it.author == "김동준" } else sortedPosts
//
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .imePadding()
//        .padding(16.dp)) {
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text("📋 게시판", fontWeight = FontWeight.Bold, fontSize = 20.sp)
//            Button(onClick = {
//                val intent = Intent(context, MainScreenActivity::class.java)
//                intent.putExtra("username", currentUser) // 기존에 받은 username 그대로 넘김
//                context.startActivity(intent)
//            }) {
//                Text("🏠 홈으로", fontSize = 13.sp)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
//            Button(onClick = {
//                editingPost = null
//                showForm = true
//            }, modifier = Modifier.weight(1f)) {
//                Text("✍️ 글 작성하기")
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Button(onClick = {
//                showMyPosts = !showMyPosts
//            }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(
//                containerColor = if (showMyPosts) Color.Gray else MaterialTheme.colorScheme.primary
//            )) {
//                Text(if (showMyPosts) "전체 글 보기" else "내 글만 보기")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        LazyColumn(modifier = Modifier.weight(1f)) {
//            items(displayPosts) { post: BoardPost ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp)
//                ) {
//                    Column(modifier = Modifier.padding(12.dp)) {
//                        Text(post.title, fontWeight = FontWeight.Bold)
//                        if (post.isNotice && post.author == "김동준") {
//                            Text("[공지사항]", color = Color.Red, fontSize = 12.sp)
//                        }
//                        Text(post.content, maxLines = 2)
//                        Text("- ${post.author}, ${post.createdAt}", fontSize = 12.sp, color = Color.Gray)
//
//                        if (post.author == currentUser) {
//                            Row {
//                                TextButton(onClick = {
//                                    editingPost = post
//                                    showForm = true
//                                }) {
//                                    Text("수정하기")
//                                }
//                                TextButton(onClick = {
//                                    viewModel.deletePost(post.id)
//                                }) {
//                                    Text("삭제하기")
//                                }
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text("💬 댓글", fontWeight = FontWeight.Medium)
//
//                        post.comments.forEach { comment ->
//                            Column(modifier = Modifier.fillMaxWidth()) {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
//                                ) {
//                                    Text("- $comment", fontSize = 13.sp)
//
//                                    if (comment.startsWith("$currentUser:")) {
//                                        TextButton(onClick = {
//                                            viewModel.deleteComment(post.id, comment)
//                                        }) {
//                                            Text("삭제", fontSize = 12.sp, color = Color.Red)
//                                        }
//                                    }
//                            }
//                        }
//
//                    }
//
//                        val text = commentTexts[post.id] ?: ""
//
//                        OutlinedTextField(
//                            value = text,
//                            onValueChange = { commentTexts[post.id] = it },
//                            placeholder = { Text("댓글을 입력하세요") },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .background(Color.White),
//                            singleLine = true
//                        )
//
//                        Button(onClick = {
//                            if (text.isNotBlank()) {
//                                viewModel.addComment(post.id, "$currentUser: $text")
//                                commentTexts[post.id] = ""
//                            }
//                        }) {
//                            Text("댓글 등록")
//                        }
//
//
//                    }
//                }
//            }
//        }
//
//        if (showForm) {
//            Spacer(modifier = Modifier.height(16.dp))
//            PostForm(
//                post = editingPost,
//                currentUser = currentUser,
//                onSubmit = { title, content ->
//                    if (editingPost == null) {
//                        viewModel.addPost(title, content, currentUser)
//                    } else {
//                        viewModel.updatePost(editingPost!!.id, title, content)
//                    }
//                    showForm = false
//                    editingPost = null
//                },
//                onCancel = {
//                    showForm = false
//                    editingPost = null
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun PostForm(
//    post: BoardPost?,
//    currentUser: String,
//    onSubmit: (String, String) -> Unit,
//    onCancel: () -> Unit
//) {
//    val keyboard = LocalSoftwareKeyboardController.current
//    var title by remember { mutableStateOf(post?.title ?: "") }
//    var content by remember { mutableStateOf(post?.content ?: "") }
//
//    Column(modifier = Modifier
//        .fillMaxWidth()
//        .padding(8.dp)) {
//
//        TextField(
//            value = title,
//            onValueChange = { title = it },
//            label = { Text("제목") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        TextField(
//            value = content,
//            onValueChange = { content = it },
//            label = { Text("내용") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(horizontalArrangement = Arrangement.SpaceBetween) {
//            Button(onClick = {
//                keyboard?.hide()
//                onCancel()
//            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
//                Text("취소")
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Button(onClick = {
//                keyboard?.hide()
//                onSubmit(title, content)
//            }) {
//                Text(if (post == null) "등록" else "수정 완료")
//            }
//        }
//    }
//}