package com.example.nasaapod.data

import com.google.gson.annotations.SerializedName

data class ApodItem(
    val date: String,
    @SerializedName("post_id")
    val postId: Long? = null,
    val title: String,
    val permalink: String? = null,
    @SerializedName("media_type")
    val mediaType: String? = null,
    val explanation: String? = null,
    val credit: String? = null,
    val copyright: String? = null,
    val alt: String? = null,
    val url: String? = null,
    val hdurl: String? = null
)
