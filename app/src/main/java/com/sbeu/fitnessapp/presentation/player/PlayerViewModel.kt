package com.sbeu.fitnessapp.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbeu.fitnessapp.R
import com.sbeu.fitnessapp.domain.model.Workout
import com.sbeu.fitnessapp.domain.repository.WorkoutRepository
import com.sbeu.fitnessapp.presentation.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val workout: Workout,
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        loadVideo()
    }

    fun loadVideo() {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            repository.getVideo(workout.id)
                .onSuccess { videoInfo ->
                    _uiState.value = PlayerUiState.Success(workout, videoInfo)
                }
                .onFailure { error ->
                    val message = if (error is retrofit2.HttpException && error.code() == 404) {
                        UiText.StringResource(R.string.error_video_not_found)
                    } else {
                        error.message?.let { UiText.DynamicString(it) }
                            ?: UiText.StringResource(R.string.error_video_load_failed)
                    }
                    _uiState.value = PlayerUiState.Error(workout, message)
                }
        }
    }

    fun retry() = loadVideo()
}