package com.example.mathalarm.presentation.alarmring

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mathalarm.alarm.AlarmNotificationHelper
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

    @Inject
    lateinit var alarmNotificationHelper: AlarmNotificationHelper

    private var currentAlarmId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showOverLockScreen()

        currentAlarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)

        setContent {
            MathAlarmTheme {
                AlarmRingRoute(
                    alarmId = currentAlarmId,
                    onStartSound = { ringtoneUri ->
                        alarmSoundPlayer.start(ringtoneUri)
                    },
                    onStartVibration = {
                        alarmVibrator.start()
                    },
                    onChallengeCompleted = {
                        alarmNotificationHelper.cancelAlarmNotification(currentAlarmId)
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
        // Do nothing. User must solve the challenge.
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
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!uiState.isAlarmLoaded) {
                LoadingAlarmContent()
            } else {
                AlarmChallengeContent(
                    uiState = uiState,
                    onAnswerChange = onAnswerChange,
                    onSubmitAnswerClick = onSubmitAnswerClick
                )
            }
        }
    }
}

@Composable
private fun LoadingAlarmContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Math Alarm",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Preparing your challenge...",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlarmChallengeContent(
    uiState: AlarmRingUiState,
    onAnswerChange: (String) -> Unit,
    onSubmitAnswerClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        AlarmHeader()

        QuestionProgressText(
            currentQuestionNumber = uiState.currentQuestionNumber,
            totalQuestions = uiState.totalQuestions
        )

        QuestionCard(
            questionText = uiState.questionText
        )

        AnswerInputCard(
            answerText = uiState.answerText,
            errorMessage = uiState.errorMessage,
            onAnswerChange = onAnswerChange,
            onSubmitAnswerClick = onSubmitAnswerClick
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = onSubmitAnswerClick
        ) {
            Text(
                text = "Submit answer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Solve all questions to stop the alarm.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlarmHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Wake up!",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your math alarm is ringing",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuestionProgressText(
    currentQuestionNumber: Int,
    totalQuestions: Int
) {
    Text(
        text = "Question $currentQuestionNumber of $totalQuestions",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun QuestionCard(
    questionText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Solve this",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = questionText,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AnswerInputCard(
    answerText: String,
    errorMessage: String?,
    onAnswerChange: (String) -> Unit,
    onSubmitAnswerClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = answerText,
                onValueChange = onAnswerChange,
                label = {
                    Text(text = "Your answer")
                },
                singleLine = true,
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSubmitAnswerClick()
                    }
                )
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}