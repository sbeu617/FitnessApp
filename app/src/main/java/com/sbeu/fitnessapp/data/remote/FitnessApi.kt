package com.sbeu.fitnessapp.data.remote

import com.sbeu.fitnessapp.data.remote.dto.WorkoutDto
import com.sbeu.fitnessapp.data.remote.dto.VideoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FitnessApi {

    @GET("get_workouts")
    suspend fun getWorkouts(): List<WorkoutDto>

    @GET("get_video")
    suspend fun getVideo(@Query("id") id: Int): VideoDto
}