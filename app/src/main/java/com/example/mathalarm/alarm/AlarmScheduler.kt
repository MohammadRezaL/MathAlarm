package com.example.mathalarm.alarm

import com.example.mathalarm.domain.model.Alarm

interface AlarmScheduler {

    fun schedule(alarm: Alarm)

    fun cancel(alarm: Alarm)
}