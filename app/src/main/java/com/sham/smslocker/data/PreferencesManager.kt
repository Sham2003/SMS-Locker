package com.sham.smslocker.data
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

enum class PasswordType {
    ADMIN,
    LOCK,
    LOCATE,
    ALARM
}


data class LockLog(val phoneNumber: String, val password: String, val timeLocked: String,val contactName:String? = null) {
}

data class PasswordItem(val password: String){
    val id = Random.Default.nextInt(1000,9999)
}


class AppPreferences(context: Context) {

    companion object {
        private const val PREF_NAME = "locker_prefs"
        private const val KEY_ALLOW_RECEIVER = "allow_receiver"
        private const val KEY_PASSWORD_LIST = "password_list"
        private const val KEY_COMMAND_LOCK = "cmd_lock"
        private const val KEY_COMMAND_LOCATE = "cmd_locate"
        private const val KEY_COMMAND_ALARM = "cmd_alarm"
        private const val KEY_LOCK_LOGS = "lock_logs"
        private const val MAX_LOG_SIZE = 20
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    // Receiver control
    var isReceiverAllowed: Boolean
        get() = prefs.getBoolean(KEY_ALLOW_RECEIVER, false)
        set(value) = prefs.edit { putBoolean(KEY_ALLOW_RECEIVER, value) }

    // Commands
    var adminCommand: String
        get() = prefs.getString(KEY_COMMAND_LOCK, "ADMIN")!!
        set(value) = prefs.edit { putString(KEY_COMMAND_LOCK, value) }

    var lockCommand: String
        get() = prefs.getString(KEY_COMMAND_LOCK, "LOCK")!!
        set(value) = prefs.edit { putString(KEY_COMMAND_LOCK, value) }

    var locateCommand: String
        get() = prefs.getString(KEY_COMMAND_LOCATE, "LOCATE")!!
        set(value) = prefs.edit { putString(KEY_COMMAND_LOCATE, value) }

    var alarmCommand: String
        get() = prefs.getString(KEY_COMMAND_ALARM, "ALARM")!!
        set(value) = prefs.edit { putString(KEY_COMMAND_ALARM, value) }

    // Password list
    var passwords: List<PasswordItem>
        get() {
            val json = prefs.getString(KEY_PASSWORD_LIST, null) ?: return emptyList()
            val type = object : TypeToken<List<PasswordItem>>() {}.type
            return gson.fromJson(json, type)
        }
        set(value) {
            val json = gson.toJson(value)
            passwordState.value = value
            prefs.edit { putString(KEY_PASSWORD_LIST, json) }
        }

    val rawPasswords : List<String>
        get() = passwords.map { it.password }

    val passwordState: MutableStateFlow<List<PasswordItem>> = MutableStateFlow(emptyList())

    // Lock logs
    val lockLogs: List<LockLog>
        get() {
            val json = prefs.getString(KEY_LOCK_LOGS, null) ?: return emptyList()
            val type = object : TypeToken<List<LockLog>>() {}.type
            return gson.fromJson(json, type)
        }

    fun addLockLog(newLog: LockLog) {
        val logs = lockLogs.toMutableList()
        if (logs.size >= MAX_LOG_SIZE) {
            logs.removeAt(0) // remove oldest
        }
        logs.add(newLog)
        val json = gson.toJson(logs)
        prefs.edit { putString(KEY_LOCK_LOGS, json) }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}
