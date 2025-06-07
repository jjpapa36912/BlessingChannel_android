package com.blessing.channel.ui.board

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response


interface BoardApiService {
    @POST("/board/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: Long,
        @Body comment: CommentRequest
    ): retrofit2.Response<Unit> // 🔥 Response는 retrofit2 패키지여야 함
}

data class CommentRequest(
    val author: String,
    val content: String
)