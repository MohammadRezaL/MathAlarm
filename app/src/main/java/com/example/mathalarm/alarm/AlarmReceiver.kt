package com.example.mathalarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mathalarm.domain.repository.AlarmRepository
import com.example.mathalarm.presentation.alarmring.AlarmRingActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var alarmNotificationHelper: AlarmNotificationHelper

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val pendingResult = goAsync()

        val alarmId = intent.getLongExtra(
            AndroidAlarmScheduler.EXTRA_ALARM_ID,
            -1L
        )

        Log.e(
            "AlarmReceiver",
            "ALARM TRIGGERED. Showing notification and opening ring screen. ID = $alarmId"
        )

        alarmNotificationHelper.showAlarmNotification(alarmId)

        val ringIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra(AlarmRingActivity.EXTRA_ALARM_ID, alarmId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        runCatching {
            context.startActivity(ringIntent)
        }

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val alarm = alarmRepository.getAlarmById(alarmId)

                if (
                    alarm != null &&
                    alarm.isEnabled &&
                    alarm.repeatDays.isNotEmpty()
                ) {
                    alarmScheduler.schedule(alarm)

                    Log.d(
                        "AlarmReceiver",
                        "Repeating alarm rescheduled. ID = ${alarm.id}"
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}