package com.example.mathalarm.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mathalarm.data.local.dao.AlarmDao
import com.example.mathalarm.data.local.entity.AlarmEntity

@Database(
    entities = [AlarmEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MathAlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
}