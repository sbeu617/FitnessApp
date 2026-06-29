package com.sbeu.fitnessapp.domain.model

import android.os.Parcelable
import androidx.annotation.StringRes
import com.sbeu.fitnessapp.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workout(
    val id: Int,
    val title: String,
    val description: String?,
    val type: WorkoutType,
    val durationMinutes: Int?
) : Parcelable

enum class WorkoutType(val apiValue: Int, @StringRes val labelRes: Int) {
    CARDIO(1, R.string.type_cardio),
    POWER(2, R.string.type_power),
    STRETCH(3, R.string.type_stretch);

    companion object {
        fun fromApiValue(value: Int): WorkoutType =
            entries.find { it.apiValue == value } ?: CARDIO
    }
}