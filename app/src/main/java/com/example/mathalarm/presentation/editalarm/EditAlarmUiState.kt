package com.example.mathalarm.presentation.editalarm

import com.example.mathalarm.domain.model.MathDifficulty
import com.example.mathalarm.domain.model.RepeatDay

data class EditAlarmUiState(
    val alarmId: Long = -1L,
    val isLoading: Boolean = true,
    val hourText: String = "",
    val minuteText: String = "",
    val selectedDays: List<RepeatDay> = emptyList(),
    val selectedDifficulty: MathDifficulty = MathDifficulty.MEDIUM,
    val questionCount: Int = 3,
    val vibrationEnabled: Boolean = true,
    val isEnabled: Boolean = true,
    val ringtoneUri: String? = null,
    val ringtoneTitle: String = "Default alarm sound"
) {
    val hour: Int?
        get() = hourText.toIntOrNull()

    val minute: Int?
        get() = minuteText.toIntOrNull()

    val isTimeValid: Boolean
        get() = hour != null &&
                minute != null &&
                hour in 0..23 &&
                minute in 0..59

    val repeatSummary: String
        get() = if (selectedDays.isEmpty()) {
            "Once"
        } else {
            selectedDays.joinToString(separator = ", ") { day ->
                day.shortName
            }
        }
}