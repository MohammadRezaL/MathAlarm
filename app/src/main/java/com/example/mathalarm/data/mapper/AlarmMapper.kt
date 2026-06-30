package com.example.mathalarm.data.mapper

import com.example.mathalarm.data.local.entity.AlarmEntity
import com.example.mathalarm.domain.model.Alarm
import com.example.mathalarm.domain.model.MathDifficulty
import com.example.mathalarm.domain.model.RepeatDay

fun AlarmEntity.toDomain(): Alarm {
    return Alarm(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        repeatDays = repeatDays.toRepeatDays(),
        ringtoneUri = ringtoneUri,
        vibrationEnabled = vibrationEnabled,
        snoozeEnabled = snoozeEnabled,
        mathDifficulty = mathDifficulty.toMathDifficulty(),
        mathQuestionCount = mathQuestionCount,
        isEnabled = isEnabled
    )
}

fun Alarm.toEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        repeatDays = repeatDays.joinToString(separator = ",") { repeatDay ->
            repeatDay.name
        },
        ringtoneUri = ringtoneUri,
        vibrationEnabled = vibrationEnabled,
        snoozeEnabled = snoozeEnabled,
        mathDifficulty = mathDifficulty.name,
        mathQuestionCount = mathQuestionCount,
        isEnabled = isEnabled
    )
}

private fun String.toRepeatDays(): List<RepeatDay> {
    if (isBlank()) return emptyList()

    return split(",")
        .mapNotNull { value ->
            runCatching {
                RepeatDay.valueOf(value)
            }.getOrNull()
        }
}

private fun String.toMathDifficulty(): MathDifficulty {
    return runCatching {
        MathDifficulty.valueOf(this)
    }.getOrDefault(MathDifficulty.MEDIUM)
}