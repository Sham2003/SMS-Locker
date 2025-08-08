package com.sham.smslocker.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sham.smslocker.data.PasswordItem

@Composable
fun PasswordManagerScreen(
    passwords: List<PasswordItem>,
    onAddClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onItemUpdate: (Int , String) -> Unit,
    onItemDelete: (Int) -> Unit,
    onSpecialEditMode: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(onClick = onSpecialEditMode) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = "Command Settings")
                }
                Spacer(modifier = Modifier.size(10.dp))
                FloatingActionButton(onClick = onAddClicked) {
                    Icon(Icons.Default.Add, contentDescription = "Add Password")
                }
            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(
                text = "Password Manager",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFD500F9),
                    shadow = Shadow(Color(0xFF800080), offset = Offset(2f, 2f), blurRadius = 5f),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(passwords, key = { it.id }) { item ->
                    PasswordCard(
                        item = item,
                        onUpdate = onItemUpdate,
                        onDelete = onItemDelete
                    )
                }
            }

            Button(
                onClick = onBackClicked,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Back")
            }

        }
    }
}


@Composable
fun PasswordCard(
    item: PasswordItem,
    onUpdate: (Int,String) -> Unit,
    onDelete: (Int) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var passwordText by remember { mutableStateOf(item.password) }
    var visible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!editing) {
                Text(
                    text = if (visible) passwordText else "•".repeat(passwordText.length),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = passwordText,
                    onValueChange = { passwordText = it },
                    label = { Text("Edit Password") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!editing){
                    IconButton(onClick = { visible = !visible }) {
                        Icon(Icons.Default.Visibility, contentDescription = "View")
                    }
                }
                IconButton(onClick = {
                    editing = true
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                if (editing) {
                    IconButton(onClick = {
                        editing = false
                        onUpdate(item.id,passwordText)
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
                IconButton(onClick = { onDelete(item.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddPasswordDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Password") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Enter new password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (text.isNotBlank()) onAdd(text)
                onDismiss()
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun EditCommandsDialog(
    initialAdmin: String,
    initialLock: String,
    initialLocate: String,
    initialAlarm: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var admin by remember { mutableStateOf(initialAdmin) }
    var lock by remember { mutableStateOf(initialLock) }
    var locate by remember { mutableStateOf(initialLocate) }
    var alarm by remember { mutableStateOf(initialAlarm) }

    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit System Commands") },
        text = {
            Column {
                OutlinedTextField(
                    value = admin,
                    onValueChange = {
                        admin = it
                        showError = false
                    },
                    label = { Text("Admin Command") },
                    isError = showError && admin.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lock,
                    onValueChange = {
                        lock = it
                        showError = false
                    },
                    label = { Text("Lock Command") },
                    isError = showError && lock.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = locate,
                    onValueChange = {
                        locate = it
                        showError = false
                    },
                    label = { Text("Locate Command") },
                    isError = showError && locate.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = alarm,
                    onValueChange = {
                        alarm = it
                        showError = false
                    },
                    label = { Text("Alarm Command") },
                    isError = showError && alarm.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = "All fields must be filled.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (admin.isNotBlank() && lock.isNotBlank() && locate.isNotBlank() && alarm.isNotBlank()) {
                    onSave(admin, lock, locate, alarm)
                    onDismiss()
                } else {
                    showError = true
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


