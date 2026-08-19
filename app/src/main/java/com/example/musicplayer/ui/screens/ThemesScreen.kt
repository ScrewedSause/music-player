package com.example.musicplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.musicplayer.ui.theme.AppTheme
import com.example.musicplayer.ui.theme.AppThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemesScreen(
    currentTheme: AppTheme,
    onSelect: (AppTheme) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Themes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding).fillMaxSize(),
        ) {
            items(AppThemes.all, key = { it.id }) { theme ->
                ThemeSwatchCard(
                    theme = theme,
                    isSelected = theme.id == currentTheme.id,
                    onClick = { onSelect(theme) },
                )
            }
        }
    }
}

@Composable
private fun ThemeSwatchCard(theme: AppTheme, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(theme.colorScheme.surface)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) theme.colorScheme.primary else theme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(listOf(theme.gradientTop, theme.gradientBottom))),
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = theme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(theme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = theme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            listOf(theme.colorScheme.primary, theme.colorScheme.secondary, theme.colorScheme.tertiary).forEach { c ->
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(c)
                        .padding(end = 6.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}
