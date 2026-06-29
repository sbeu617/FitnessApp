package com.sbeu.fitnessapp.presentation.workoutlist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sbeu.fitnessapp.R
import com.sbeu.fitnessapp.databinding.FragmentWorkoutListBinding
import com.sbeu.fitnessapp.domain.model.WorkoutType
import kotlinx.coroutines.launch

class WorkoutListFragment : Fragment(R.layout.fragment_workout_list) {

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutListViewModel by viewModels { WorkoutListViewModelFactory() }
    private lateinit var adapter: WorkoutListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWorkoutListBinding.bind(view)

        setupRecyclerView()
        setupSearch()
        setupFilterChips()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = WorkoutListAdapter { workout ->
            findNavController().navigate(
                WorkoutListFragmentDirections.actionWorkoutListToPlayer(workout)
            )
        }
        binding.workoutRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.editText.addTextChangedListener { text ->
            viewModel.onSearchQueryChanged(text?.toString().orEmpty())
        }
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedType = when (checkedIds.firstOrNull()) {
                binding.chipCardio.id -> WorkoutType.CARDIO
                binding.chipPower.id -> WorkoutType.POWER
                binding.chipStretch.id -> WorkoutType.STRETCH
                else -> null   // chipAll или ничего не выбрано
            }
            viewModel.onTypeFilterSelected(selectedType)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: WorkoutListUiState) {
        binding.workoutRecyclerView.isVisible = state is WorkoutListUiState.Success
        binding.progressBar.isVisible = state is WorkoutListUiState.Loading
        binding.errorView.isVisible = state is WorkoutListUiState.Error
        binding.emptyView.isVisible = state is WorkoutListUiState.Empty

        when (state) {
            is WorkoutListUiState.Success -> {
                adapter.submitList(state.workouts)
            }
            is WorkoutListUiState.Error -> {
                binding.errorText.text = state.message.asString(requireContext())
                binding.retryButton.setOnClickListener { viewModel.retry() }
            }
            else -> Unit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}