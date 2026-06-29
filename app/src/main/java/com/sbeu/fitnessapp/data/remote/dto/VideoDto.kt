package com.sbeu.fitnessapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("duration") val duration: Int,
    @SerializedName("link") val link: String
)