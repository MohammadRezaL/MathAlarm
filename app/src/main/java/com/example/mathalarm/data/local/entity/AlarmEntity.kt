package com.example.mathalarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val hour: Int,
    val minute: Int,

    val label: String,

    val repeatDays: String,

    val ringtoneUri: String?,

    val vibrationEnabled: Boolean,
    val snoozeEnabled: Boolean,

    val mathDifficulty: String,
    val mathQuestionCount: Int,

    val isEnabled: Boolean
)