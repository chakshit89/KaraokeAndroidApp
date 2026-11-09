package com.karoke.myoyasing.data.dto.CommentsDTO

import com.google.gson.annotations.SerializedName

data class AddCommentRequestDto(

    @SerializedName("content")
    val content: String
)