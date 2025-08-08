package com.sham.smslocker.receivers

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.location.Location
import android.os.PowerManager
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import androidx.core.content.ContextCompat


import com.sham.smslocker.AlarmScreenActivity
import com.sham.smslocker.data.LockLog
import com.sham.smslocker.data.AppPreferences
import java.util.Locale

fun getTimeString(t : Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = t

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(calendar.time)

    return formattedDate
}

val MY_lOG_TAG = "SMSReceiver"

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val preferences = AppPreferences(context)
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        if (!preferences.isReceiverAllowed) return

        val newMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        Log.d(MY_lOG_TAG,"No of messages : ${newMessages.size}")
        val adminCommand = preferences.adminCommand
        val lockCommand = preferences.lockCommand
        val locateCommand = preferences.locateCommand
        val alarmCommand = preferences.alarmCommand

        Log.d(MY_lOG_TAG,"$adminCommand 0 $lockCommand 1 $locateCommand 2 $alarmCommand")

        for (sms in newMessages){
            val messageBody = sms.messageBody
            val senderNumber = sms.displayOriginatingAddress
            val pattern = Regex("($adminCommand|$lockCommand|$locateCommand|$alarmCommand)\\s+(\\S+)")
            val match = pattern.find(messageBody) ?: continue
            val command = match.groupValues[1] // Extracted command
            val password = match.groupValues[2] // Extracted password (if any)

            Log.d(MY_lOG_TAG, "Command: $command, Password: $password")
            when (command) {
                adminCommand -> {
                    val passwordList = preferences.passwords
                    if (passwordList.isNotEmpty()) {
                        val randomPassword = passwordList.random().password
                        Log.d(
                            MY_lOG_TAG,
                            "ADMIN command received. Sending random password: $randomPassword"
                        )
                        sendReply(context, senderNumber, "Random Password: $randomPassword")
                    } else {
                        Log.d(
                            MY_lOG_TAG,
                            "ADMIN command received but no passwords available."
                        )
                        sendReply(context, senderNumber, "No passwords available.")
                    }
                }

                lockCommand -> {
                    val passwordList = preferences.rawPasswords
                    if (passwordList.contains(password)){
                        performAction(
                            context,
                            senderNumber,
                            password,
                            getTimeString(sms.timestampMillis)
                        )
                    } else {
                        Log.d(MY_lOG_TAG, "Invalid LOCK command password: $password")
                        sendReply(context, senderNumber, "WRONG PASSWORD")
                    }
                }

                locateCommand -> {
                    retrieveAndSendLocation(context, senderNumber)
                }

                alarmCommand -> {
                    performAlarmScreenAction(context,senderNumber)
                }

                else -> {
                    Log.d(MY_lOG_TAG, "Unrecognized command: $command")
                }
            }

        }
    }

    private fun wakeDevice(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val isScreenOn = powerManager.isInteractive // use isScreenOn() for pre-20
        if (!isScreenOn) {
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "sms_locker:wakelock"
            )
            wakeLock.acquire(3000) // Wake for 3 seconds
        }
    }


    private fun performAlarmScreenAction(context: Context, senderNumber: String) {
        Log.d(MY_lOG_TAG,"Alarm Screen Action")
        sendReply(context, senderNumber,"Phone Screen Locked ")
        wakeDevice(context)
        Log.d(MY_lOG_TAG,"Phone Waked")
        val intent = Intent(context, AlarmScreenActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        Log.d(MY_lOG_TAG,"Intent started alarm")
        try{
            context.startActivity(intent)
        }catch (e:Exception){
            Log.d(MY_lOG_TAG,"Error encountered new activity $e")
        }
    }


    private fun performAction(context: Context, senderNumber: String, password: String, timeReceived:String) {

        val lockLog = LockLog(senderNumber, password, timeReceived)
        val preferences = AppPreferences(context)
        preferences.addLockLog(lockLog)
        Log.d(MY_lOG_TAG,"No of histories : ${preferences.lockLogs.size}")
        sendReply(context,senderNumber,"LOCKED")
        Log.d(MY_lOG_TAG, "Performing action with password: $password")


        handleLockCommand(context)
    }


    private fun handleLockCommand(context: Context) {

        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)

        if (devicePolicyManager.isAdminActive(componentName)) {
            Log.d(MY_lOG_TAG, "Device is locking now...")
            devicePolicyManager.lockNow()
        } else {
            Log.d(MY_lOG_TAG, "Device admin is not active. Cannot lock device.")
        }
    }

    private fun sendReply(context: Context, senderNumber: String, replyMessage: String) {
        try {
            val sms = context.getSystemService(SmsManager::class.java)
            sms?.sendTextMessage(senderNumber, null, replyMessage, null, null)
            Log.d(MY_lOG_TAG, "Reply sent to $senderNumber: $replyMessage")
        } catch (e: Exception) {
            Log.e(MY_lOG_TAG, "Failed to send reply", e)
        }
    }
    private fun retrieveAndSendLocation(context: Context, senderNumber: String) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!(fineLocationPermission || coarseLocationPermission)) {
            Log.d(MY_lOG_TAG, "Location permission not granted.")
            sendReply(context, senderNumber, "Location access not permitted on this device.")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val locationMessage = "Current location: Latitude ${location.latitude}, Longitude ${location.longitude}"
                sendReply(context, senderNumber, locationMessage)
                Log.d(MY_lOG_TAG, locationMessage)
            } else {
                sendReply(context, senderNumber, "Unable to retrieve location.")
                Log.d(MY_lOG_TAG, "Location is null.")
            }
        }.addOnFailureListener { e ->
            sendReply(context, senderNumber, "Failed to retrieve location: ${e.message}")
            Log.e(MY_lOG_TAG, "Error fetching location", e)
        }
    }

}