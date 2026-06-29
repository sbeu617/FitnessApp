package com.sbeu.fitnessapp.data.mapper

import com.sbeu.fitnessapp.data.remote.NetworkModule
import com.sbeu.fitnessapp.data.remote.dto.VideoDto
import com.sbeu.fitnessapp.data.remote.dto.WorkoutDto
import com.sbeu.fitnessapp.domain.model.VideoInfo
import com.sbeu.fitnessapp.domain.model.Workout
import com.sbeu.fitnessapp.domain.model.WorkoutType

fun WorkoutDto.toDomain(): Workout = Workout(
    id = id,
    title = title,
    description = description,
    type = WorkoutType.fromApiValue(type),
    durationMinutes = duration.toIntOrNull()
)

fun VideoDto.toDomain(): VideoInfo {
    val resolvedUrl = if (link.startsWith("http")) {
        link
    } else {
        NetworkModule.BASE_URL.trimEnd('/') + link
    }
    return VideoInfo(
        id = id,
        durationSeconds = duration,
        videoUrl = resolvedUrl
    )
}