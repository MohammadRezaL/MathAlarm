package com.example.mathalarm.audio

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Suppress("DEPRECATION")
class AlarmVibrator @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator by lazy {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun start() {
        val pattern = longArrayOf(
            0L,
            700L,
            500L,
            700L,
            500L
        )

        val vibrationEffect = VibrationEffect.createWaveform(
            pattern,
            0
        )

        vibrator.vibrate(vibrationEffect)
    }

    fun stop() {
        vibrator.cancel()
    }
}