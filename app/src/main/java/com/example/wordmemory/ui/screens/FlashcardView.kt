package com.example.wordmemory.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Transformable
import androidx.compose.ui.graphics.graphics
import androidx.compose.ui.unit.dp
import com.example.wordmemory.domain.model.ParsedWord

@Composable
fun FlashcardView(
    word: ParsedWord,
    modifier: Modifier = Modifier
) {
    var rotated by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "flashcardRotation"
    )

    Card(
        modifier = modifier
            .size(width = 300.dp, height = 200.dp)
            .graphics(rotation = rotation)
            .clickable { rotated = !rotated },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Front Side (English)
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.headlineLarge
                )
            } else {
                // Back Side (Meaning) - We rotate the text content back so it's readable
                Text(
                    text = word.meaning,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.graphics(rotation = 180f)
                )
            }
        }
    }
}
