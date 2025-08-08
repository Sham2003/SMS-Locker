package com.sham.smslocker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sham.smslocker.data.AppPreferences
import com.sham.smslocker.data.PasswordItem
import com.sham.smslocker.receivers.MY_lOG_TAG
import com.sham.smslocker.ui.screens.AddPasswordDialog
import com.sham.smslocker.ui.screens.EditCommandsDialog
import com.sham.smslocker.ui.screens.PasswordManagerScreen
import com.sham.smslocker.ui.theme.SMSLockerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PasswordManagerActivity : ComponentActivity() {

    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = AppPreferences(this)

        val adminCommand = preferences.adminCommand
        val lockCommand = preferences.lockCommand
        val locateCommand = preferences.locateCommand
        val alarmCommand = preferences.alarmCommand

        Log.d("PasswordManagerActivity","Init = $adminCommand 0 $lockCommand 1 $locateCommand 2 $alarmCommand")
        setContent {
            SMSLockerTheme {
                val viewModel = viewModel<PasswordManagerViewModel>(
                    factory = PasswordManagerViewModel.provideFactory(preferences)
                )

                var viewAdder by remember { mutableStateOf(false) }
                var showCommandDialog by remember { mutableStateOf(false) }

                PasswordManagerScreen(
                    passwords = viewModel.passwords.collectAsState().value,
                    onBackClicked = { finish() },
                    onAddClicked = { viewAdder = true },
                    onItemUpdate = { id, updated -> viewModel.updatePassword(id, updated) },
                    onItemDelete = { id -> viewModel.deletePassword(id) },
                    onSpecialEditMode = { showCommandDialog = true }
                )

                if (viewAdder) {
                    AddPasswordDialog(
                        onDismiss = {
                            viewAdder = false
                        },
                        onAdd = {
                            viewModel.addPassword(it)
                        }
                    )
                }

                if (showCommandDialog) {
                    EditCommandsDialog(
                        initialAdmin = preferences.adminCommand,
                        initialLock = preferences.lockCommand,
                        initialLocate = preferences.locateCommand,
                        initialAlarm = preferences.alarmCommand,
                        onDismiss = { showCommandDialog = false },
                        onSave = { newAdmin, newLock, newLocate, newAlarm ->
                            preferences.adminCommand = newAdmin
                            preferences.lockCommand = newLock
                            preferences.locateCommand = newLocate
                            preferences.alarmCommand = newAlarm
                            Log.d("PasswordManagerActivity","EDit = $newAdmin 0 $newLock 1 $newLocate 2 $newAlarm")
                        }
                    )
                }
            }
        }
    }
}

class PasswordManagerViewModel(private val preferences: AppPreferences) : ViewModel() {

    private val _passwords = MutableStateFlow(preferences.passwords)
    val passwords = _passwords.asStateFlow()

    fun addPassword(password: String) {
        val newItem = PasswordItem(password)
        val updated = _passwords.value + newItem
        preferences.passwords = updated
        _passwords.value = updated
    }

    fun deletePassword(id: Int) {
        val updated = _passwords.value.filter { it.id != id }
        preferences.passwords = updated
        _passwords.value = updated
    }

    fun updatePassword(id: Int, newPassword: String) {
        val updated = _passwords.value.map {
            if (it.id == id) it.copy(password = newPassword) else it
        }
        preferences.passwords = updated
        _passwords.value = updated
    }


    companion object {
        fun provideFactory(preferences: AppPreferences): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PasswordManagerViewModel(preferences)
                }
            }
    }

}

