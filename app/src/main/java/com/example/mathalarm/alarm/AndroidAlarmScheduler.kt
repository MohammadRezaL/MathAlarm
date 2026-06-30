package com.example.mathalarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mathalarm.domain.model.Alarm
import com.example.mathalarm.domain.model.RepeatDay
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class AndroidAlarmScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager: AlarmManager =
        context.getSystemService(AlarmManager::class.java)

    override fun schedule(alarm: Alarm) {
        val triggerAtMillis = calculateNextTriggerTime(alarm)

        Log.d(
            "AndroidAlarmScheduler",
            "Scheduling alarm id=${alarm.id}, time=${alarm.hour}:${alarm.minute}, triggerAtMillis=$triggerAtMillis"
        )

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    override fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        Log.d(
            "AndroidAlarmScheduler",
            "Cancelled alarm id=${alarm.id}"
        )
    }

    private fun calculateNextTriggerTime(
        alarm: Alarm
    ): Long {
        val now = LocalDateTime.now()
        val alarmTime = LocalTime.of(alarm.hour, alarm.minute)

        val nextDateTime = if (alarm.repeatDays.isEmpty()) {
            calculateNextOneTimeAlarm(
                now = now,
                alarmTime = alarmTime
            )
        } else {
            calculateNextRepeatingAlarm(
                now = now,
                alarmTime = alarmTime,
                repeatDays = alarm.repeatDays
            )
        }

        return nextDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun calculateNextOneTimeAlarm(
        now: LocalDateTime,
        alarmTime: LocalTime
    ): LocalDateTime {
        var nextAlarm = LocalDateTime.of(
            LocalDate.now(),
            alarmTime
        )

        if (!nextAlarm.isAfter(now)) {
            nextAlarm = nextAlarm.plusDays(1)
        }

        return nextAlarm
    }

    private fun calculateNextRepeatingAlarm(
        now: LocalDateTime,
        alarmTime: LocalTime,
        repeatDays: List<RepeatDay>
    ): LocalDateTime {
        val today = LocalDate.now()

        return repeatDays
            .map { repeatDay ->
                val targetDayOfWeek = repeatDay.toDayOfWeek()
                val daysUntilTarget = daysUntil(
                    from = today.dayOfWeek,
                    to = targetDayOfWeek
                )

                var candidate = LocalDateTime.of(
                    today.plusDays(daysUntilTarget.toLong()),
                    alarmTime
                )

                if (!candidate.isAfter(now)) {
                    candidate = candidate.plusWeeks(1)
                }

                candidate
            }
            .minOrNull()
            ?: calculateNextOneTimeAlarm(
                now = now,
                alarmTime = alarmTime
            )
    }

    private fun daysUntil(
        from: DayOfWeek,
        to: DayOfWeek
    ): Int {
        return (to.value - from.value + 7) % 7
    }

    private fun RepeatDay.toDayOfWeek(): DayOfWeek {
        return when (this) {
            RepeatDay.MONDAY -> DayOfWeek.MONDAY
            RepeatDay.TUESDAY -> DayOfWeek.TUESDAY
            RepeatDay.WEDNESDAY -> DayOfWeek.WEDNESDAY
            RepeatDay.THURSDAY -> DayOfWeek.THURSDAY
            RepeatDay.FRIDAY -> DayOfWeek.FRIDAY
            RepeatDay.SATURDAY -> DayOfWeek.SATURDAY
            RepeatDay.SUNDAY -> DayOfWeek.SUNDAY
        }
    }

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }
}