package com.example.meshsosapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsHelper {

    private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 1002

    // --- Location Permissions (Your existing code) ---

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun isLocationPermissionRequest(requestCode: Int) = requestCode == LOCATION_PERMISSION_REQUEST_CODE

    // --- NEW: Bluetooth Permissions Logic ---

    /**
     * Checks if the app has the necessary Bluetooth permissions.
     * The required permissions changed in Android 12 (S).
     */
    fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API 31) and above requires SCAN and CONNECT
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            // For older versions, location permission is required for Bluetooth scanning.
            hasLocationPermission(context)
        }
    }

    /**
     * Requests the necessary Bluetooth permissions from the user.
     */
    fun requestBluetoothPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Request the new Android 12 permissions
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        } else {
            // On older versions, we just need to ensure location permission is requested.
            requestLocationPermission(activity)
        }
    }

    /**
     * Helper to check if a permission result is for our Bluetooth request.
     */
    fun isBluetoothPermissionRequest(requestCode: Int) = requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE
}

