package com.example.kot1041_asm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import com.example.kot1041_asm.ui.screens.WelcomeScreen
import com.example.kot1041_asm.ui.theme.KOT1041_ASMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KOT1041_ASMTheme {
//                WelcomeScreen(
//                    onGetStartedClick = {
//
//                    }
//                )
                WelcomeScreen()
            }
        }
    }
}