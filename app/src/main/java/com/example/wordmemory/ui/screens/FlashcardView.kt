package com.example.wordmemory.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.wordmemory.domain.model.ParsedWord

@Composable
fun FlashcardView(
    word: ParsedWord,
    isRotated: Boolean,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "flashcardRotation"
    )

    Card(
        modifier = modifier
            .size(320.dp, 220.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8f * density
            }
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        // Box의 모든 인자를 명시하여 Box$default 호출(기본값 생성)을 방지합니다.
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
            propagateMinConstraints = false
        ) {
            if (rotation <= 90f) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                    propagateMinConstraints = false
                ) {
                    Text(
                        text = word.meaning,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.graphicsLayer {
                            rotationY = 180f
                        }
                    )
                }
            }
        }
    }
}
