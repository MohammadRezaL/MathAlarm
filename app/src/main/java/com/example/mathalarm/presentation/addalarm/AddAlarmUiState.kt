package com.example.mathalarm.presentation.addalarm

import com.example.mathalarm.domain.model.MathDifficulty
import com.example.mathalarm.domain.model.RepeatDay

data class AddAlarmUiState(
    val hourText: String = "07",
    val minuteText: String = "30",
    val selectedDays: List<RepeatDay> = emptyList(),
    val selectedDifficulty: MathDifficulty = MathDifficulty.MEDIUM,
    val questionCount: Int = 3,
    val vibrationEnabled: Boolean = true,
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