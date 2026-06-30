package com.example.mathalarm.presentation.alarmring

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mathalarm.audio.AlarmSoundPlayer
import com.example.mathalarm.audio.AlarmVibrator
import com.example.mathalarm.core.designsystem.theme.MathAlarmTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmRingActivity : ComponentActivity() {

    @Inject
    lateinit var alarmSoundPlayer: AlarmSoundPlayer

    @Inject
    lateinit var alarmVibrator: AlarmVibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showOverLockScreen()

        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)

        setContent {
            MathAlarmTheme {
                AlarmRingRoute(
                    alarmId = alarmId,
                    onStartSound = { ringtoneUri ->
                        alarmSoundPlayer.start(ringtoneUri)
                    },
                    onStartVibration = {
                        alarmVibrator.start()
                    },
                    onChallengeCompleted = {
                        stopAlarmEffects()
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        stopAlarmEffects()
        super.onDestroy()
    }

    private fun stopAlarmEffects() {
        alarmSoundPlayer.stop()
        alarmVibrator.stop()
    }

    private fun Activity.showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }
}

@Composable
private fun AlarmRingRoute(
    alarmId: Long,
    onStartSound: (String?) -> Unit,
    onStartVibration: () -> Unit,
    onChallengeCompleted: () -> Unit,
    viewModel: AlarmRingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = true) {
        // Back button is blocked while alarm is ringing.
    }

    LaunchedEffect(alarmId) {
        viewModel.loadAlarm(alarmId)
    }

    LaunchedEffect(
        uiState.isAlarmLoaded,
        uiState.ringtoneUri
    ) {
        if (uiState.isAlarmLoaded) {
            onStartSound(uiState.ringtoneUri)
        }
    }

    LaunchedEffect(
        uiState.isAlarmLoaded,
        uiState.vibrationEnabled
    ) {
        if (uiState.isAlarmLoaded && uiState.vibrationEnabled) {
            onStartVibration()
        }
    }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onChallengeCompleted()
        }
    }

    AlarmRingScreen(
        uiState = uiState,
        onAnswerChange = viewModel::onAnswerChange,
        onSubmitAnswerClick = viewModel::onSubmitAnswerClick
    )
}

@Composable
private fun AlarmRingScreen(
    uiState: AlarmRingUiState,
    onAnswerChange: (String) -> Unit,
    onSubmitAnswerClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wake up!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = "Alarm ID: ${uiState.alarmId}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = "Question ${uiState.currentQuestionNumber} of ${uiState.totalQuestions}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = uiState.questionText,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                value = uiState.answerText,
                onValueChange = onAnswerChange,
                label = {
                    Text(text = "Your answer")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            if (uiState.errorMessage != null) {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                onClick = onSubmitAnswerClick
            ) {
                Text(text = "Submit answer")
            }
        }
    }
}