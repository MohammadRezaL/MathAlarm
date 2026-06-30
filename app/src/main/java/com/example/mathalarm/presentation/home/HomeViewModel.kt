package com.example.mathalarm.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathalarm.alarm.AlarmScheduler
import com.example.mathalarm.domain.model.Alarm
import com.example.mathalarm.domain.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        alarmRepository.observeAlarms()
            .map { alarms ->
                HomeUiState(
                    alarms = alarms
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState()
            )

    fun onAlarmEnabledChange(
        alarm: Alarm,
        isEnabled: Boolean
    ) {
        viewModelScope.launch {
            if (isEnabled) {
                alarmRepository.setAlarmEnabled(
                    alarmId = alarm.id,
                    isEnabled = true
                )

                alarmScheduler.schedule(
                    alarm.copy(isEnabled = true)
                )
            } else {
                alarmScheduler.cancel(alarm)

                alarmRepository.setAlarmEnabled(
                    alarmId = alarm.id,
                    isEnabled = false
                )
            }
        }
    }

    fun onDeleteAlarmClick(
        alarm: Alarm
    ) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm)
            alarmRepository.deleteAlarm(alarm)
        }
    }
}