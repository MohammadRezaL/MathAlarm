package com.example.mathalarm.presentation.addalarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathalarm.alarm.AlarmScheduler
import com.example.mathalarm.domain.model.Alarm
import com.example.mathalarm.domain.model.MathDifficulty
import com.example.mathalarm.domain.model.RepeatDay
import com.example.mathalarm.domain.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddAlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAlarmUiState())
    val uiState: StateFlow<AddAlarmUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddAlarmEvent>()
    val events: SharedFlow<AddAlarmEvent> = _events.asSharedFlow()

    fun onHourChange(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            _uiState.update { currentState ->
                currentState.copy(hourText = value)
            }
        }
    }

    fun onRingtonePicked(
        uriString: String?,
        title: String
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                ringtoneUri = uriString,
                ringtoneTitle = title
            )
        }
    }

    fun onMinuteChange(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            _uiState.update { currentState ->
                currentState.copy(minuteText = value)
            }
        }
    }

    fun onRepeatDayClick(day: RepeatDay) {
        _uiState.update { currentState ->
            val updatedDays = if (currentState.selectedDays.contains(day)) {
                currentState.selectedDays - day
            } else {
                currentState.selectedDays + day
            }

            currentState.copy(
                selectedDays = updatedDays
            )
        }
    }

    fun onOnceClick() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDays = emptyList()
            )
        }
    }

    fun onWeekdaysClick() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDays = listOf(
                    RepeatDay.MONDAY,
                    RepeatDay.TUESDAY,
                    RepeatDay.WEDNESDAY,
                    RepeatDay.THURSDAY,
                    RepeatDay.FRIDAY
                )
            )
        }
    }

    fun onEveryDayClick() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDays = RepeatDay.values().toList()
            )
        }
    }

    fun onDifficultyClick(difficulty: MathDifficulty) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDifficulty = difficulty
            )
        }
    }

    fun onQuestionCountChange(count: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                questionCount = count.coerceIn(1, 10)
            )
        }
    }

    fun onVibrationChange(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                vibrationEnabled = enabled
            )
        }
    }


    fun onSaveClick() {
        val currentState = _uiState.value

        if (!currentState.isTimeValid) {
            return
        }

        val hour = currentState.hour ?: return
        val minute = currentState.minute ?: return

        val alarm = Alarm(
            hour = hour,
            minute = minute,
            label = "",
            repeatDays = currentState.selectedDays,
            ringtoneUri = currentState.ringtoneUri,
            vibrationEnabled = currentState.vibrationEnabled,
            snoozeEnabled = false,
            mathDifficulty = currentState.selectedDifficulty,
            mathQuestionCount = currentState.questionCount,
            isEnabled = true
        )

        viewModelScope.launch {
            val alarmId = alarmRepository.saveAlarm(alarm)

            val scheduledAlarm = alarm.copy(
                id = alarmId
            )

            alarmScheduler.schedule(scheduledAlarm)

            _events.emit(AddAlarmEvent.AlarmSaved)
        }
    }
}

sealed interface AddAlarmEvent {
    data object AlarmSaved : AddAlarmEvent
}