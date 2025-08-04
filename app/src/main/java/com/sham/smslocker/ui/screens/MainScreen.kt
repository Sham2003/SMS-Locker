package com.sham.smslocker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MainScreen(
    isReceiverAllowed: Boolean,
    onSwitchToggle: (Boolean) -> Unit,
    onLockClick: () -> Unit,
    onPasswordManagerClick: () -> Unit,
    onAlarmClick: () -> Unit = {},
    onHistoryClick: () -> Unit
) {

    val statusColor = if (isReceiverAllowed) Color(0xFF4CAF50) else Color(0xFFF44336)
    val statusText = if (isReceiverAllowed) "Service is ON" else "Service is OFF"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "MyLocker",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF44336),
            fontFamily = FontFamily.Cursive,
            textAlign = TextAlign.Center
        )

        Switch(
            checked = isReceiverAllowed,
            onCheckedChange = onSwitchToggle,
            modifier = Modifier
                .size(width = 95.dp, height = 60.dp)
        )

        Text(
            text = statusText,
            fontSize = 20.sp,
            color = statusColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onLockClick,
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp)
            ) {
                Text("Lock Screen")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAlarmClick,
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp)
            ) {
                Text("Alarm Lock")
            }
        }

        Button(
            onClick = onPasswordManagerClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(63.dp)
        ) {
            Text("Password Manager")
        }

        Button(
            onClick = onHistoryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            Text("Lock History")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
