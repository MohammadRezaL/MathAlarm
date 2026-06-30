package com.example.mathalarm.presentation.home

import com.example.mathalarm.domain.model.Alarm

data class HomeUiState(
    val alarms: List<Alarm> = emptyList()
)