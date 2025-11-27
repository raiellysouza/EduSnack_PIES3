package com.example.edusnack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = DarkText,
)

@Composable
fun EduSnackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = EduTypography,
        content = content
    )
}
