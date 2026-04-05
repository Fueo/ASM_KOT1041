package com.example.kot1041_asm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.ui.theme.MerriweatherBold

@Composable
fun NotificationScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Notifications Screen",
            style = TextStyle(
                fontFamily = MerriweatherBold,
                fontSize = 20.sp,
                color = Color(0xFF303030)
            )
        )
    }
}