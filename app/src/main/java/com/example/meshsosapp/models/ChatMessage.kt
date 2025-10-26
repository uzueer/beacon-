package com.example.meshsosapp.models

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(), // NEW: Unique ID for each message
    val text: String,
    val sender: String,
    val timestamp: String,
    val isSos: Boolean,
    val location: Pair<Double, Double>? = null
)

