package com.example.mathalarm.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mathalarm.domain.model.Alarm
import com.example.mathalarm.domain.model.RepeatDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeRoute(
    paddingValues: PaddingValues,
    onAddAlarmClick: () -> Unit,
    onAlarmClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onAddAlarmClick = onAddAlarmClick,
        onAlarmClick = onAlarmClick,
        onAlarmEnabledChange = viewModel::onAlarmEnabledChange,
        onDeleteAlarmClick = viewModel::onDeleteAlarmClick
    )
}

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    onAddAlarmClick: () -> Unit,
    onAlarmClick: (Long) -> Unit,
    onAlarmEnabledChange: (Alarm, Boolean) -> Unit,
    onDeleteAlarmClick: (Alarm) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Your alarms",
            style = MaterialTheme.typography.headlineMedium
        )

        if (uiState.alarms.isEmpty()) {
            EmptyAlarmsMessage()
        } else {
            uiState.alarms.forEach { alarm ->
                AlarmCardItem(
                    alarm = alarm,
                    onClick = {
                        onAlarmClick(alarm.id)
                    },
                    onEnabledChange = { isEnabled ->
                        onAlarmEnabledChange(alarm, isEnabled)
                    },
                    onDeleteClick = {
                        onDeleteAlarmClick(alarm)
                    }
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onAddAlarmClick
        ) {
            Text(text = "Add alarm")
        }
    }
}

@Composable
private fun EmptyAlarmsMessage() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No alarms yet",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Create your first math alarm to get started.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun AlarmCardItem(
    alarm: Alarm,
    onClick: () -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "%02d:%02d".format(alarm.hour, alarm.minute),
                    style = MaterialTheme.typography.headlineLarge,
                    color = contentColor
                )

                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onEnabledChange
                )
            }

            Text(
                text = repeatText(alarm),
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )

            Text(
                text = nextAlarmText(alarm),
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )

            Text(
                text = "Math challenge: ${alarm.mathDifficulty.displayName} • ${alarm.mathQuestionCount} questions",
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )

            Text(
                text = if (alarm.isEnabled) {
                    "Status: Enabled"
                } else {
                    "Status: Disabled"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDeleteClick
            ) {
                Text(text = "Delete")
            }
        }
    }
}

private fun repeatText(
    alarm: Alarm
): String {
    return if (alarm.repeatDays.isEmpty()) {
        "Repeats: Once"
    } else {
        val days = alarm.repeatDays.joinToString(separator = ", ") { day ->
            day.shortName
        }

        "Repeats: $days"
    }
}

private fun nextAlarmText(
    alarm: Alarm
): String {
    if (!alarm.isEnabled) {
        return "Next alarm: Disabled"
    }

    val nextAlarmDateTime = calculateNextAlarmDateTime(alarm)
    val timeText = nextAlarmDateTime.format(
        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    )

    val dateText = when (nextAlarmDateTime.toLocalDate()) {
        LocalDate.now() -> "Today"
        LocalDate.now().plusDays(1) -> "Tomorrow"
        else -> nextAlarmDateTime.format(
            DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        )
    }

    return "Next alarm: $dateText at $timeText"
}

private fun calculateNextAlarmDateTime(
    alarm: Alarm
): LocalDateTime {
    val now = LocalDateTime.now()
    val alarmTime = LocalTime.of(alarm.hour, alarm.minute)

    return if (alarm.repeatDays.isEmpty()) {
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