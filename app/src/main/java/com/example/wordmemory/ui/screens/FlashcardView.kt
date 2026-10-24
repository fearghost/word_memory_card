package com.example.wordmemory.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wordmemory.domain.model.ParsedWord

@Composable
fun FlashcardView(
    word: ParsedWord,
    isRotated: Boolean,
    isSwapped: Boolean,
    fontScaleMultiplier: Float,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "flashcardRotation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8f * density
            }
    ) {
        // Front face (English when not swapped) — visible from 0° to 90°
        if (rotation <= 90f) {
            CardContent(
                word = if (isSwapped) word.meaning else word.word,
                isFront = true,
                fontScaleMultiplier = fontScaleMultiplier,
                onCardClick = onCardClick,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Back face (Korean when not swapped) — visible from 90° to 180°
        if (rotation >= 90f) {
            CardContent(
                word = if (isSwapped) word.word else word.meaning,
                isFront = false,
                fontScaleMultiplier = fontScaleMultiplier,
                onCardClick = onCardClick,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    }
            )
        }
    }
}

@Composable
private fun CardContent(
    word: String,
    isFront: Boolean,
    fontScaleMultiplier: Float,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (isFront) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer)
            .clickable(onClick = onCardClick),
        contentAlignment = Alignment.Center
    ) {
        val scaledFontSize = MaterialTheme.typography.headlineLarge.fontSize * fontScaleMultiplier
        Text(
            text = word,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = scaledFontSize),
            color = if (isFront) 
                MaterialTheme.colorScheme.onPrimaryContainer 
            else 
                MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 4,
            modifier = Modifier.padding(24.dp)
        )
    }
}
