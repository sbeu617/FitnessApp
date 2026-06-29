package com.sbeu.fitnessapp.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sbeu.fitnessapp.data.remote.NetworkModule
import com.sbeu.fitnessapp.data.repository.WorkoutRepositoryImpl
import com.sbeu.fitnessapp.domain.model.Workout

class PlayerViewModelFactory(
    private val workout: Workout
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = WorkoutRepositoryImpl(NetworkModule.api)
        @Suppress("UNCHECKED_CAST")
        return PlayerViewModel(workout, repository) as T
    }
}