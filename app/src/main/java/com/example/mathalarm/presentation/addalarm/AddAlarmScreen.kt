package com.example.mathalarm.presentation.addalarm

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mathalarm.domain.model.MathDifficulty
import com.example.mathalarm.domain.model.RepeatDay
import kotlin.math.roundToInt

@Composable
fun AddAlarmRoute(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    viewModel: AddAlarmViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val pickedUri = result.data?.getPickedRingtoneUri()

        if (pickedUri != null) {
            viewModel.onRingtonePicked(
                uriString = pickedUri.toString(),
                title = getRingtoneTitle(
                    context = context,
                    uri = pickedUri
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddAlarmEvent.AlarmSaved -> onBackClick()
            }
        }
    }

    AddAlarmScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onHourChange = viewModel::onHourChange,
        onMinuteChange = viewModel::onMinuteChange,
        onRepeatDayClick = viewModel::onRepeatDayClick,
        onOnceClick = viewModel::onOnceClick,
        onWeekdaysClick = viewModel::onWeekdaysClick,
        onEveryDayClick = viewModel::onEveryDayClick,
        onDifficultyClick = viewModel::onDifficultyClick,
        onQuestionCountChange = viewModel::onQuestionCountChange,
        onVibrationChange = viewModel::onVibrationChange,
        onRingtoneClick = {
            val existingUri = uiState.ringtoneUri?.let { uriString ->
                Uri.parse(uriString)
            } ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_TYPE,
                    RingtoneManager.TYPE_ALARM
                )
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_TITLE,
                    "Choose alarm sound"
                )
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
                    true
                )
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,
                    false
                )
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    existingUri
                )
            }

            ringtonePickerLauncher.launch(intent)
        },
        onSaveClick = viewModel::onSaveClick,
        onBackClick = onBackClick
    )
}

@Composable
fun AddAlarmScreen(
    paddingValues: PaddingValues,
    uiState: AddAlarmUiState,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit,
    onRepeatDayClick: (RepeatDay) -> Unit,
    onOnceClick: () -> Unit,
    onWeekdaysClick: () -> Unit,
    onEveryDayClick: () -> Unit,
    onDifficultyClick: (MathDifficulty) -> Unit,
    onQuestionCountChange: (Int) -> Unit,
    onVibrationChange: (Boolean) -> Unit,
    onRingtoneClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add Alarm",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedButton(
                onClick = onBackClick
            ) {
                Text(text = "Back")
            }
        }

        TimeSection(
            uiState = uiState,
            onHourChange = onHourChange,
            onMinuteChange = onMinuteChange
        )

        RepeatSection(
            uiState = uiState,
            onRepeatDayClick = onRepeatDayClick,
            onOnceClick = onOnceClick,
            onWeekdaysClick = onWeekdaysClick,
            onEveryDayClick = onEveryDayClick
        )

        MathChallengeSection(
            uiState = uiState,
            onDifficultyClick = onDifficultyClick,
            onQuestionCountChange = onQuestionCountChange
        )

        AlarmOptionsSection(
            uiState = uiState,
            onVibrationChange = onVibrationChange
        )

        RingtoneSection(
            uiState = uiState,
            onRingtoneClick = onRingtoneClick
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isTimeValid,
            onClick = onSaveClick
        ) {
            Text(text = "Save alarm")
        }

        if (!uiState.isTimeValid) {
            Text(
                text = "Enter a valid time. Hour must be 0–23 and minute must be 0–59.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TimeSection(
    uiState: AddAlarmUiState,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Time",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = uiState.hourText,
                    onValueChange = onHourChange,
                    label = {
                        Text(text = "Hour")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = uiState.minuteText,
                    onValueChange = onMinuteChange,
                    label = {
                        Text(text = "Minute")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        }
    }
}

@Composable
private fun RepeatSection(
    uiState: AddAlarmUiState,
    onRepeatDayClick: (RepeatDay) -> Unit,
    onOnceClick: () -> Unit,
    onWeekdaysClick: () -> Unit,
    onEveryDayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Repeat",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Current: ${uiState.repeatSummary}",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOnceClick
                ) {
                    Text(text = "Once")
                }

                OutlinedButton(
                    onClick = onWeekdaysClick
                ) {
                    Text(text = "Weekdays")
                }

                OutlinedButton(
                    onClick = onEveryDayClick
                ) {
                    Text(text = "Every day")
                }
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RepeatDay.values().forEach { day ->
                    FilterChip(
                        selected = uiState.selectedDays.contains(day),
                        onClick = {
                            onRepeatDayClick(day)
                        },
                        label = {
                            Text(text = day.shortName)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MathChallengeSection(
    uiState: AddAlarmUiState,
    onDifficultyClick: (MathDifficulty) -> Unit,
    onQuestionCountChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Math Challenge",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Difficulty",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MathDifficulty.values().forEach { difficulty ->
                    FilterChip(
                        selected = uiState.selectedDifficulty == difficulty,
                        onClick = {
                            onDifficultyClick(difficulty)
                        },
                        label = {
                            Text(text = difficulty.displayName)
                        }
                    )
                }
            }

            Text(
                text = "Questions: ${uiState.questionCount}",
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = uiState.questionCount.toFloat(),
                onValueChange = { value ->
                    onQuestionCountChange(value.roundToInt())
                },
                valueRange = 1f..10f,
                steps = 8
            )
        }
    }
}

@Composable
private fun AlarmOptionsSection(
    uiState: AddAlarmUiState,
    onVibrationChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Options",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Vibration")

                Switch(
                    checked = uiState.vibrationEnabled,
                    onCheckedChange = onVibrationChange
                )
            }
        }
    }
}

@Composable
private fun RingtoneSection(
    uiState: AddAlarmUiState,
    onRingtoneClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Ringtone",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = uiState.ringtoneTitle,
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRingtoneClick
            ) {
                Text(text = "Choose ringtone")
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun Intent.getPickedRingtoneUri(): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(
            RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
            Uri::class.java
        )
    } else {
        getParcelableExtra(
            RingtoneManager.EXTRA_RINGTONE_PICKED_URI
        )
    }
}

private fun getRingtoneTitle(
    context: Context,
    uri: Uri
): String {
    return RingtoneManager
        .getRingtone(context, uri)
        ?.getTitle(context)
        ?: "Selected ringtone"
}