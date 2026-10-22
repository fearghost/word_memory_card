package com.example.wordmemory.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
                label = if (isSwapped) "Meaning" else "Word",
                isFront = true,
                onCardClick = onCardClick,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Back face (Korean when not swapped) — visible from 90° to 180°
        if (rotation >= 90f) {
            CardContent(
                word = if (isSwapped) word.word else word.meaning,
                label = if (isSwapped) "Word" else "Meaning",
                isFront = false,
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
    label: String,
    isFront: Boolean,
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isFront) 
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                else 
                    MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = word,
                style = MaterialTheme.typography.headlineMedium,
                color = if (isFront) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 4
            )
        }
    }
}
