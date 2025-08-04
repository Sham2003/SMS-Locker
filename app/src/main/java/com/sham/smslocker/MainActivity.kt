package com.sham.smslocker


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.sham.smslocker.data.AppPreferences
import com.sham.smslocker.ui.screens.MainScreen
import com.sham.smslocker.utils.PermissionLocker
import com.sham.smslocker.ui.theme.SMSLockerTheme


class MainActivity : ComponentActivity() {

    private lateinit var permissionLocker: PermissionLocker
    private lateinit var preferences: AppPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        preferences = AppPreferences(this)

        permissionLocker = PermissionLocker(this, {}, {
            finish()
        })
        permissionLocker.start()

        setContent {
            SMSLockerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var isReceiverAllowed by remember { mutableStateOf(preferences.isReceiverAllowed) }

                    MainScreen(
                        isReceiverAllowed = isReceiverAllowed,
                        onSwitchToggle = {
                            preferences.isReceiverAllowed = it
                            isReceiverAllowed = it
                        },
                        onLockClick = {
                            permissionLocker.lockScreen()
                        },
                        onPasswordManagerClick = {
                            startActivity(Intent(this, PasswordManagerActivity::class.java))
                        },
                        onHistoryClick = {
                            startActivity(Intent(this, LockHistoryActivity::class.java))
                        },
                        onAlarmClick = {
                            // startActivity(Intent(this, AlarmScreenActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}
