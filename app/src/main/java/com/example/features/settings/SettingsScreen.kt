package com.example.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 30.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.5).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SettingsItem(title = "Appearance", icon = Icons.Default.Palette, subtitle = "Dark Mode Only (Monochrome)")
        SettingsItem(title = "Privacy Policy", icon = Icons.Default.PrivacyTip)
        SettingsItem(title = "About", icon = Icons.Default.Info, subtitle = "Version 1.0.0")
    }
}

@Composable
fun SettingsItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            if (subtitle != null) {
                Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}
