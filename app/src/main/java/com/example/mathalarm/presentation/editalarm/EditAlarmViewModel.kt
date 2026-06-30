package com.example.mathalarm.presentation.editalarm

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
class EditAlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditAlarmUiState())
    val uiState: StateFlow<EditAlarmUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditAlarmEvent>()
    val events: SharedFlow<EditAlarmEvent> = _events.asSharedFlow()

    private var originalAlarm: Alarm? = null

    fun loadAlarm(
        alarmId: Long
    ) {
        if (_uiState.value.alarmId == alarmId && !_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {
            val alarm = alarmRepository.getAlarmById(alarmId)

            if (alarm == null) {
                _events.emit(EditAlarmEvent.AlarmNotFound)
                return@launch
            }

            originalAlarm = alarm

            _uiState.update {
                EditAlarmUiState(
                    alarmId = alarm.id,
                    isLoading = false,
                    hourText = "%02d".format(alarm.hour),
                    minuteText = "%02d".format(alarm.minute),
                    selectedDays = alarm.repeatDays,
                    selectedDifficulty = alarm.mathDifficulty,
                    questionCount = alarm.mathQuestionCount,
                    vibrationEnabled = alarm.vibrationEnabled,
                    isEnabled = alarm.isEnabled,
                    ringtoneUri = alarm.ringtoneUri,
                    ringtoneTitle = if (alarm.ringtoneUri == null) {
                        "Default alarm sound"
                    } else {
                        "Selected ringtone"
                    }
                )
            }
        }
    }

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

        if (currentState.isLoading || !currentState.isTimeValid) {
            return
        }

        val oldAlarm = originalAlarm ?: return

        val hour = currentState.hour ?: return
        val minute = currentState.minute ?: return

        val updatedAlarm = oldAlarm.copy(
            hour = hour,
            minute = minute,
            repeatDays = currentState.selectedDays,
            vibrationEnabled = currentState.vibrationEnabled,
            mathDifficulty = currentState.selectedDifficulty,
            mathQuestionCount = currentState.questionCount,
            ringtoneUri = currentState.ringtoneUri,
            isEnabled = currentState.isEnabled
        )

        viewModelScope.launch {
            alarmScheduler.cancel(oldAlarm)

            alarmRepository.updateAlarm(updatedAlarm)

            if (updatedAlarm.isEnabled) {
                alarmScheduler.schedule(updatedAlarm)
            }

            _events.emit(EditAlarmEvent.AlarmUpdated)
        }
    }
}

sealed interface EditAlarmEvent {
    data object AlarmUpdated : EditAlarmEvent
    data object AlarmNotFound : EditAlarmEvent
}