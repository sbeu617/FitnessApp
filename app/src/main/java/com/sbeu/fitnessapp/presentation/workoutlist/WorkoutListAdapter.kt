package com.sbeu.fitnessapp.presentation.workoutlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sbeu.fitnessapp.R
import com.sbeu.fitnessapp.databinding.ItemWorkoutBinding
import com.sbeu.fitnessapp.domain.model.Workout
import com.sbeu.fitnessapp.domain.model.WorkoutType

class WorkoutListAdapter(
    private val onItemClick: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutListAdapter.WorkoutViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkoutViewHolder(
        private val binding: ItemWorkoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(workout: Workout) {
            val context = binding.root.context
            binding.workoutTitle.text = workout.title
            binding.workoutSubtitle.text = workout.description ?: context.getString(R.string.description_placeholder)
            binding.workoutTypeTag.text = context.getString(workout.type.labelRes)
            binding.workoutDuration.text = workout.durationMinutes?.let {
                context.resources.getQuantityString(R.plurals.minutes, it, it)
            } ?: context.getString(R.string.duration_unknown)
            binding.workoutImage.setImageResource(placeholderFor(workout.type))

            binding.root.setOnClickListener { onItemClick(workout) }
        }

        private fun placeholderFor(type: WorkoutType): Int = when (type) {
            WorkoutType.CARDIO -> R.drawable.placeholder_cardio
            WorkoutType.POWER -> R.drawable.placeholder_power
            WorkoutType.STRETCH -> R.drawable.placeholder_stretch
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout) =
            oldItem == newItem
    }
}