package com.sbeu.fitnessapp.presentation.workoutlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sbeu.fitnessapp.data.remote.NetworkModule
import com.sbeu.fitnessapp.data.repository.WorkoutRepositoryImpl

class WorkoutListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = WorkoutRepositoryImpl(NetworkModule.api)
        @Suppress("UNCHECKED_CAST")
        return WorkoutListViewModel(repository) as T
    }
}