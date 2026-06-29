package com.sbeu.fitnessapp.domain.repository

import com.sbeu.fitnessapp.domain.model.VideoInfo
import com.sbeu.fitnessapp.domain.model.Workout

interface WorkoutRepository {
    suspend fun getWorkouts(): Result<List<Workout>>
    suspend fun getVideo(id: Int): Result<VideoInfo>
}