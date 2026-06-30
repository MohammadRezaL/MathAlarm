package com.example.mathalarm.data.repository

import com.example.mathalarm.data.local.dao.AlarmDao
import com.example.mathalarm.data.mapper.toDomain
import com.example.mathalarm.data.mapper.toEntity
import com.example.mathalarm.domain.model.Alarm
import com.example.mathalarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun observeAlarms(): Flow<List<Alarm>> {
        return alarmDao.observeAlarms()
            .map { alarmEntities ->
                alarmEntities.map { alarmEntity ->
                    alarmEntity.toDomain()
                }
            }
    }

    override suspend fun getAlarmById(alarmId: Long): Alarm? {
        return alarmDao.getAlarmById(alarmId)?.toDomain()
    }

    override suspend fun saveAlarm(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm.toEntity())
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm.toEntity())
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm.toEntity())
    }

    override suspend fun setAlarmEnabled(
        alarmId: Long,
        isEnabled: Boolean
    ) {
        alarmDao.setAlarmEnabled(
            alarmId = alarmId,
            isEnabled = isEnabled
        )
    }
}