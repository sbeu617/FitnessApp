package com.sbeu.fitnessapp.presentation.workoutlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbeu.fitnessapp.R
import com.sbeu.fitnessapp.domain.model.Workout
import com.sbeu.fitnessapp.domain.model.WorkoutType
import com.sbeu.fitnessapp.domain.repository.WorkoutRepository
import com.sbeu.fitnessapp.presentation.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutListViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val allWorkouts = MutableStateFlow<List<Workout>>(emptyList())
    private val searchQuery = MutableStateFlow("")
    private val selectedType = MutableStateFlow<WorkoutType?>(null)
    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<UiText?>(null)

    val uiState: StateFlow<WorkoutListUiState> = combine(
        allWorkouts, searchQuery, selectedType, isLoading, errorMessage
    ) { workouts, query, type, loading, error ->
        when {
            loading -> WorkoutListUiState.Loading
            error != null -> WorkoutListUiState.Error(error)
            else -> {
                val filtered = workouts
                    .filter { type == null || it.type == type }
                    .filter { it.title.contains(query, ignoreCase = true) }

                if (filtered.isEmpty()) {
                    WorkoutListUiState.Empty
                } else {
                    WorkoutListUiState.Success(filtered, type)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkoutListUiState.Loading
    )

    init {
        loadWorkouts()
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            repository.getWorkouts()
                .onSuccess {
                    allWorkouts.value = it
                    isLoading.value = false
                }
                .onFailure {
                    errorMessage.value = it.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.error_workouts_load_failed)
                    isLoading.value = false
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onTypeFilterSelected(type: WorkoutType?) {
        selectedType.value = type
    }

    fun retry() {
        loadWorkouts()
    }
}