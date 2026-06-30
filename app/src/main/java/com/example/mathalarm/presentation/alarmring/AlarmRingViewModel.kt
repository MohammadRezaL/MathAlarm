package com.example.mathalarm.presentation.alarmring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathalarm.domain.model.Alarm
import com.example.mathalarm.domain.model.MathDifficulty
import com.example.mathalarm.domain.model.MathQuestion
import com.example.mathalarm.domain.repository.AlarmRepository
import com.example.mathalarm.domain.usecase.GenerateMathQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AlarmRingViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val generateMathQuestionUseCase: GenerateMathQuestionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlarmRingUiState())
    val uiState: StateFlow<AlarmRingUiState> = _uiState.asStateFlow()

    private var alarm: Alarm? = null
    private var currentQuestion: MathQuestion? = null

    fun loadAlarm(
        alarmId: Long
    ) {
        if (_uiState.value.alarmId == alarmId && currentQuestion != null) {
            return
        }

        viewModelScope.launch {
            alarm = alarmRepository.getAlarmById(alarmId)

            val difficulty = alarm?.mathDifficulty ?: MathDifficulty.MEDIUM
            val questionCount = alarm?.mathQuestionCount ?: 3

            currentQuestion = generateMathQuestionUseCase(difficulty)

            _uiState.update { currentState ->
                currentState.copy(
                    alarmId = alarmId,
                    questionText = currentQuestion?.text.orEmpty(),
                    answerText = "",
                    currentQuestionNumber = 1,
                    totalQuestions = questionCount,
                    errorMessage = null,
                    isCompleted = false,
                    isAlarmLoaded = true,
                    vibrationEnabled = alarm?.vibrationEnabled ?: true,
                    ringtoneUri = alarm?.ringtoneUri
                )
            }
        }
    }

    fun onAnswerChange(
        value: String
    ) {
        if (value.length <= 6 && value.all { it.isDigit() }) {
            _uiState.update { currentState ->
                currentState.copy(
                    answerText = value,
                    errorMessage = null
                )
            }
        }
    }

    fun onSubmitAnswerClick() {
        val userAnswer = _uiState.value.answerText.toIntOrNull()
        val correctAnswer = currentQuestion?.correctAnswer

        if (userAnswer == null || correctAnswer == null) {
            _uiState.update { currentState ->
                currentState.copy(
                    errorMessage = "Enter an answer first."
                )
            }
            return
        }

        if (userAnswer != correctAnswer) {
            _uiState.update { currentState ->
                currentState.copy(
                    errorMessage = "Wrong answer. Try again."
                )
            }
            return
        }

        val currentState = _uiState.value

        if (currentState.currentQuestionNumber >= currentState.totalQuestions) {
            _uiState.update { oldState ->
                oldState.copy(
                    isCompleted = true,
                    errorMessage = null
                )
            }
            return
        }

        val difficulty = alarm?.mathDifficulty ?: MathDifficulty.MEDIUM
        currentQuestion = generateMathQuestionUseCase(difficulty)

        _uiState.update { oldState ->
            oldState.copy(
                questionText = currentQuestion?.text.orEmpty(),
                answerText = "",
                currentQuestionNumber = oldState.currentQuestionNumber + 1,
                errorMessage = null
            )
        }
    }
}