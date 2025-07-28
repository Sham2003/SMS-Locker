package com.sham.smslocker.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.sham.smslocker.receivers.MyDeviceAdminReceiver

class PermissionLocker(
    private val activity: ComponentActivity,
    private val onGranted: () -> Unit = {},
    private val onDenied: () -> Unit = {}) {

    companion object {
        const val TAG = "PermissionLocker"

        val criticalPermissions = listOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS
        )

        val optionalPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
    }

    private var askedOnce = false


    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var deviceAdminLauncher: ActivityResultLauncher<Intent>

    private var devicePolicyManager: DevicePolicyManager
    private var compName: ComponentName

    init {
        registerLaunchers()
        devicePolicyManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(activity, MyDeviceAdminReceiver::class.java)
    }

    fun start() {
        when {
            isGrantedToRun() -> onGranted()

            hasAllCriticalPermissions() && isDeviceAdminActive() -> {
                showOptionalDialog()
            }

            !askedOnce -> {
                askedOnce = true
                showInitialPermissionDialog()
            }

            else -> {
                Log.e(TAG, "Permissions denied. Not asking again.")
                onDenied()
            }
        }
    }

    private fun registerLaunchers() {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val hasCritical = criticalPermissions.all { permissions[it] == true }
            val hasOptional = optionalPermissions.all { permissions[it] == true }
            Log.d(TAG, permissions.toString())
            val adminGranted = isDeviceAdminActive()

            Log.d(TAG,"C - $hasCritical O - $hasOptional A - $adminGranted")
            when {

                hasCritical && adminGranted -> {
                    if (!hasOptional) {
                        Toast.makeText(
                            activity,
                            "Optional features like location/audio may not work.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    onGranted()
                }

                !hasCritical -> {
                    Toast.makeText(
                        activity,
                        "Critical permissions denied. App can't work properly.",
                        Toast.LENGTH_LONG
                    ).show()
                    onDenied()
                }

                else -> {
                    onDenied()
                }
            }
        }

        deviceAdminLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Device admin granted ✅")
            } else {
                Log.w(TAG, "Device admin denied ❌")
            }
        }
    }

    private fun showInitialPermissionDialog() {
        val missing = getMissingCriticalPermissions().joinToString("\n")

        AlertDialog.Builder(activity)
            .setTitle("Permissions Required")
            .setMessage("These are required for the app to work:\n\n$missing")
            .setCancelable(false)
            .setPositiveButton("Grant") { _, _ ->
                launchAllPermissionsAndDeviceAdmin()
            }
            .setNegativeButton("Exit") { _, _ ->
                onDenied()
            }
            .show()
    }

    private fun showOptionalDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Enable Optional Features?")
            .setMessage("Allow location or audio access to unlock extra features.")
            .setPositiveButton("Yes") { _, _ ->
                launchOptionalPermissions()
            }
            .setNegativeButton("No") { _, _ ->
                onGranted()
            }
            .show()
    }


    private fun launchAllPermissionsAndDeviceAdmin() {
        if (!isDeviceAdminActive()) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Grant admin permission to lock screen.")
            }
            deviceAdminLauncher.launch(intent)
        }


        val allPermissions = (criticalPermissions + optionalPermissions).filter {
            activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (allPermissions.isNotEmpty()) {
            permissionLauncher.launch(allPermissions.toTypedArray())
        }


    }

    fun isDeviceAdminActive(): Boolean  =  devicePolicyManager.isAdminActive(compName)

    fun hasAllCriticalPermissions(): Boolean {
        return criticalPermissions.all {
            activity.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }


    fun isGrantedToRun(): Boolean {
        return hasAllCriticalPermissions() && isDeviceAdminActive()
    }

    fun getMissingCriticalPermissions(): List<String> {
        val missing = criticalPermissions.filter {
            activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }.toMutableList()

        if (!isDeviceAdminActive()) {
            missing.add("DEVICE_ADMIN")
        }

        return missing
    }

    private fun launchOptionalPermissions() {
        val optionalToRequest = optionalPermissions.filter {
            activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (optionalToRequest.isNotEmpty()) {
            permissionLauncher.launch(optionalToRequest.toTypedArray())
        }
    }

    fun lockScreen() {
        if (isDeviceAdminActive())
            devicePolicyManager.lockNow()
    }


}
