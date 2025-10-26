package com.example.meshsosapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeHelper {

    /**
     * Returns the current time as a formatted string.
     */
    fun getCurrentTimestamp(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
