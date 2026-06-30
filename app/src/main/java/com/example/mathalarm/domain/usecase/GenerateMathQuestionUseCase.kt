package com.example.mathalarm.domain.usecase

import com.example.mathalarm.domain.model.MathDifficulty
import com.example.mathalarm.domain.model.MathQuestion
import javax.inject.Inject
import kotlin.random.Random

class GenerateMathQuestionUseCase @Inject constructor() {

    operator fun invoke(
        difficulty: MathDifficulty
    ): MathQuestion {
        return when (difficulty) {
            MathDifficulty.EASY -> generateEasyQuestion()
            MathDifficulty.MEDIUM -> generateMediumQuestion()
            MathDifficulty.HARD -> generateHardQuestion()
        }
    }

    private fun generateEasyQuestion(): MathQuestion {
        val firstNumber = Random.nextInt(1, 21)
        val secondNumber = Random.nextInt(1, 21)

        return MathQuestion(
            text = "$firstNumber + $secondNumber = ?",
            correctAnswer = firstNumber + secondNumber
        )
    }

    private fun generateMediumQuestion(): MathQuestion {
        return when (Random.nextInt(0, 3)) {
            0 -> {
                val firstNumber = Random.nextInt(10, 51)
                val secondNumber = Random.nextInt(1, 31)

                MathQuestion(
                    text = "$firstNumber + $secondNumber = ?",
                    correctAnswer = firstNumber + secondNumber
                )
            }

            1 -> {
                val firstNumber = Random.nextInt(20, 81)
                val secondNumber = Random.nextInt(1, firstNumber)

                MathQuestion(
                    text = "$firstNumber - $secondNumber = ?",
                    correctAnswer = firstNumber - secondNumber
                )
            }

            else -> {
                val firstNumber = Random.nextInt(2, 13)
                val secondNumber = Random.nextInt(2, 13)

                MathQuestion(
                    text = "$firstNumber × $secondNumber = ?",
                    correctAnswer = firstNumber * secondNumber
                )
            }
        }
    }

    private fun generateHardQuestion(): MathQuestion {
        val firstNumber = Random.nextInt(6, 16)
        val secondNumber = Random.nextInt(6, 16)
        val thirdNumber = Random.nextInt(10, 51)

        return MathQuestion(
            text = "($firstNumber × $secondNumber) + $thirdNumber = ?",
            correctAnswer = (firstNumber * secondNumber) + thirdNumber
        )
    }
}