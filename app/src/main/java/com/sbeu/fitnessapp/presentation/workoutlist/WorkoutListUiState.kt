package com.sbeu.fitnessapp.presentation.workoutlist

import com.sbeu.fitnessapp.domain.model.Workout
import com.sbeu.fitnessapp.domain.model.WorkoutType
import com.sbeu.fitnessapp.presentation.util.UiText

sealed class WorkoutListUiState {
    data object Loading : WorkoutListUiState()
    data class Success(
        val workouts: List<Workout>,
        val selectedType: WorkoutType?
    ) : WorkoutListUiState()
    data object Empty : WorkoutListUiState()
    data class Error(val message: UiText) : WorkoutListUiState()
}