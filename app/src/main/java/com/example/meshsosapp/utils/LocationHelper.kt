package com.example.meshsosapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

object LocationHelper {

    /**
     * Gets the device's live, current location.
     * This actively turns on the GPS to get a fresh location fix.
     *
     * @param context The application context.
     * @param onLocationResult A callback function to handle the result.
     */
    fun getCurrentLocation(context: Context, onLocationResult: (Pair<Double, Double>?) -> Unit) {
        // Check for permission first
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("LocationHelper", "Location permission not granted.")
            onLocationResult(null)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val coordinates = Pair(location.latitude, location.longitude)
                    onLocationResult(coordinates)
                } else {
                    Log.e("LocationHelper", "Could not get current location (it was null).")
                    // As a fallback, try getting the last known location
                    getRealLastLocation(context, onLocationResult)
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationHelper", "Failed to get current location", e)
                onLocationResult(null)
            }
    }

    /**
     * Fallback function to get the last known location if the current one fails.
     */
    private fun getRealLastLocation(context: Context, onLocationResult: (Pair<Double, Double>?) -> Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onLocationResult(null)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationResult(Pair(location.latitude, location.longitude))
                } else {
                    onLocationResult(null)
                }
            }
            .addOnFailureListener { onLocationResult(null) }
    }
}

