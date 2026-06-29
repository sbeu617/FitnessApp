package com.sbeu.fitnessapp.presentation.player

import com.sbeu.fitnessapp.domain.model.VideoInfo
import com.sbeu.fitnessapp.domain.model.Workout
import com.sbeu.fitnessapp.presentation.util.UiText

sealed class PlayerUiState {
    data object Loading : PlayerUiState()
    data class Success(val workout: Workout, val videoInfo: VideoInfo) : PlayerUiState()
    data class Error(val workout: Workout, val message: UiText) : PlayerUiState()
}