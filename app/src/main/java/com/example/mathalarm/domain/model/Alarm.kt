package com.example.mathalarm.domain.model

data class Alarm(
    val id: Long = 0L,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val repeatDays: List<RepeatDay> = emptyList(),
    val ringtoneUri: String? = null,
    val vibrationEnabled: Boolean = true,
    val snoozeEnabled: Boolean = true,
    val mathDifficulty: MathDifficulty = MathDifficulty.MEDIUM,
    val mathQuestionCount: Int = 3,
    val isEnabled: Boolean = true
)