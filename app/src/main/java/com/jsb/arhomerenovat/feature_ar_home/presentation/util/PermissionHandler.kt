package com.jsb.arhomerenovat.feature_ar_home.presentation.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher

object PermissionHandler {
    private const val TAG = "PermissionHandler"

    // ✅ Request Location and Camera Permissions
    fun requestPermissions(
        activity: Activity,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA // ✅ Added Camera Permission
            )
        )
    }

    // ✅ Check if Location Services Are Enabled
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // ✅ Prompt User to Enable Location Services
    fun promptEnableLocation(context: Context) {
        if (!isLocationEnabled(context)) {
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            Toast.makeText(
                context,
                "Please enable location services for Geospatial data",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
