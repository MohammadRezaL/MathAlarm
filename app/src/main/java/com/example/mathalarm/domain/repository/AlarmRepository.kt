package com.example.mathalarm.domain.repository

import com.example.mathalarm.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun observeAlarms(): Flow<List<Alarm>>

    suspend fun getAlarmById(alarmId: Long): Alarm?

    suspend fun saveAlarm(alarm: Alarm): Long

    suspend fun updateAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarm: Alarm)

    suspend fun setAlarmEnabled(
        alarmId: Long,
        isEnabled: Boolean
    )
}