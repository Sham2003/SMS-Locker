package com.sham.smslocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sham.smslocker.data.AppPreferences
import com.sham.smslocker.ui.screens.LockHistoryScreen
import com.sham.smslocker.ui.theme.SMSLockerTheme

class LockHistoryActivity : ComponentActivity() {

    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = AppPreferences(this)

        setContent {
            SMSLockerTheme {
                LockHistoryScreen(
                    lockLogs = preferences.lockLogs,
                    onBack = { finish() }
                )
            }
        }
    }
}
