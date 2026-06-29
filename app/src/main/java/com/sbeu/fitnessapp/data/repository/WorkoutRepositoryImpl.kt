package com.sbeu.fitnessapp.data.repository

import com.sbeu.fitnessapp.data.mapper.toDomain
import com.sbeu.fitnessapp.data.remote.FitnessApi
import com.sbeu.fitnessapp.domain.model.VideoInfo
import com.sbeu.fitnessapp.domain.model.Workout
import com.sbeu.fitnessapp.domain.repository.WorkoutRepository

class WorkoutRepositoryImpl(
    private val api: FitnessApi
) : WorkoutRepository {

    override suspend fun getWorkouts(): Result<List<Workout>> = runCatching {
        api.getWorkouts().map { it.toDomain() }
    }

    override suspend fun getVideo(id: Int): Result<VideoInfo> = runCatching {
        api.getVideo(id).toDomain()
    }
}