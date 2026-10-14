package com.example.wordmemory.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
        animationSpec = tween(durationMillis = 600),
        label = "flashcardRotation"
    )

    Card(
        modifier = modifier
            .size(width = 320.dp, height = 220.dp)
            .graphicsLayer(rotationZ = rotation.toFloat())
            .clickable { rotated = !rotated },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = word.meaning,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.graphicsLayer(rotationZ = 180f),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
