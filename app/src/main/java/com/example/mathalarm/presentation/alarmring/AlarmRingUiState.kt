package com.example.mathalarm.presentation.alarmring

data class AlarmRingUiState(
    val alarmId: Long = -1L,
    val questionText: String = "",
    val answerText: String = "",
    val currentQuestionNumber: Int = 1,
    val totalQuestions: Int = 3,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false,
    val isAlarmLoaded: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val ringtoneUri: String? = null
)