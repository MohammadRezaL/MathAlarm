package com.example.mathalarm.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSoundPlayer @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    fun start(
        ringtoneUri: String? = null
    ) {
        if (mediaPlayer?.isPlaying == true) {
            return
        }

        val alarmUri = ringtoneUri?.let { uriString ->
            Uri.parse(uriString)
        } ?: RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_ALARM
        ) ?: RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_RINGTONE
        )

        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, alarmUri)

            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )

            isLooping = true
            prepare()
            start()
        }
    }

    fun stop() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }

            player.release()
        }

        mediaPlayer = null
    }
}