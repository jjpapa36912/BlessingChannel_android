package com.blessing.channel.ui.board.model

data class BoardPost(
    val id: Long,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: String,
    val comments: List<String> = emptyList(), // ✅ 댓글 필드 추가
    val isNotice: Boolean = false // ✅ 공지 글 여부

)