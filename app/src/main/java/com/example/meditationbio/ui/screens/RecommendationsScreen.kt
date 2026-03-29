package com.example.meditationbio.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.ProblemField
import com.example.meditationbio.ui.components.InfoBadge
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SecondaryButton
import com.example.meditationbio.ui.components.SectionCard
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private enum class SwipeDirection {
    LEFT,
    RIGHT
}

@Composable
fun RecommendationsScreen(
    selectedProblemField: ProblemField?,
    recommendations: List<MeditationRecommendation>,
    currentRecommendationIndex: Int,
    onBack: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onOpenSavedMeditations: () -> Unit,
    onRecommendationSelected: (MeditationRecommendation) -> Unit
) {
    val currentRecommendation = recommendations.getOrNull(currentRecommendationIndex)

    var visibleIndex by remember(recommendations, currentRecommendationIndex) {
        mutableIntStateOf(currentRecommendationIndex)
    }
    var pendingTargetIndex by remember { mutableStateOf<Int?>(null) }
    var animationDirection by remember { mutableStateOf(SwipeDirection.LEFT) }
    var isAnimatingSwipe by remember { mutableStateOf(false) }

    LaunchedEffect(currentRecommendationIndex, recommendations) {
        if (!isAnimatingSwipe) {
            visibleIndex = currentRecommendationIndex
        } else {
            pendingTargetIndex = currentRecommendationIndex
        }
    }

    val visibleRecommendation = recommendations.getOrNull(visibleIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = selectedProblemField?.title ?: "Empfehlungen",
            subtitle = "Swipe nach links für den nächsten Vorschlag. Swipe nach rechts zum Speichern."
        )

        SecondaryButton(
            text = "Zurück",
            onClick = onBack
        )

        PrimaryButton(
            text = "Gespeicherte Meditationen",
            onClick = onOpenSavedMeditations,
            modifier = Modifier.padding(top = 12.dp)
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            InfoBadge(text = "Lernprofil aktiv")
        }

        if (visibleRecommendation == null) {
            SectionCard(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Für dieses Problemfeld sind noch keine Vorschläge vorhanden.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Column
        }

        Text(
            text = "Vorschlag ${visibleIndex + 1} von ${recommendations.size}",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AnimatedContent(
            targetState = visibleRecommendation,
            modifier = Modifier.padding(top = 16.dp),
            transitionSpec = {
                val isLeft = animationDirection == SwipeDirection.LEFT

                (
                        slideInHorizontally(
                            animationSpec = tween(320),
                            initialOffsetX = { fullWidth ->
                                if (isLeft) fullWidth else -fullWidth
                            }
                        ) + fadeIn(animationSpec = tween(320))
                        ).togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(320),
                            targetOffsetX = { fullWidth ->
                                if (isLeft) -fullWidth else fullWidth
                            }
                        ) + fadeOut(animationSpec = tween(320))
                    ).using(
                        SizeTransform(clip = false)
                    )
            },
            label = "recommendation_card_transition"
        ) { recommendation ->
            SwipeableRecommendationCard(
                recommendation = recommendation,
                problemFieldTitle = selectedProblemField?.title.orEmpty(),
                enabled = !isAnimatingSwipe,
                onSwipeLeft = {
                    if (!isAnimatingSwipe) {
                        isAnimatingSwipe = true
                        animationDirection = SwipeDirection.LEFT
                        onSwipeLeft()
                    }
                },
                onSwipeRight = {
                    if (!isAnimatingSwipe) {
                        isAnimatingSwipe = true
                        animationDirection = SwipeDirection.RIGHT
                        onSwipeRight()
                    }
                },
                onAnimationFinished = {
                    val newIndex = pendingTargetIndex ?: currentRecommendationIndex
                    visibleIndex = newIndex
                    pendingTargetIndex = null
                    isAnimatingSwipe = false
                },
                onOpenDetails = {
                    onRecommendationSelected(recommendation)
                }
            )
        }

        SectionCard(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Gesten",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "• Links swipen: nächster Vorschlag\n• Rechts swipen: Meditation speichern",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SwipeableRecommendationCard(
    recommendation: MeditationRecommendation,
    problemFieldTitle: String,
    enabled: Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onAnimationFinished: () -> Unit,
    onOpenDetails: () -> Unit
) {
    var dragOffsetX by remember(recommendation.id) { mutableFloatStateOf(0f) }

    LaunchedEffect(recommendation.id) {
        dragOffsetX = 0f
    }

    SectionCard(
        modifier = Modifier
            .offset { IntOffset(dragOffsetX.roundToInt(), 0) }
            .pointerInput(recommendation.id, enabled) {
                if (!enabled) return@pointerInput

                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffsetX += dragAmount
                    },
                    onDragEnd = {
                        when {
                            dragOffsetX <= -160f -> {
                                dragOffsetX = -600f
                                onSwipeLeft()
                            }

                            dragOffsetX >= 160f -> {
                                dragOffsetX = 600f
                                onSwipeRight()
                            }

                            else -> {
                                dragOffsetX = 0f
                            }
                        }
                    },
                    onDragCancel = {
                        dragOffsetX = 0f
                    }
                )
            }
    ) {
        LaunchedEffect(dragOffsetX) {
            if (dragOffsetX <= -600f || dragOffsetX >= 600f) {
                delay(220)
                onAnimationFinished()
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = recommendation.subtitle,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(modifier = Modifier.padding(top = 14.dp)) {
                InfoBadge(text = problemFieldTitle.ifBlank { "Problemfeld" })
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                InfoBadge(text = "${recommendation.durationMinutes} Min")
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                InfoBadge(text = recommendation.style)
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                InfoBadge(text = "Wirkung: ${recommendation.effectivenessLabel}")
            }

            Text(
                text = "Tonalität: ${recommendation.tone}",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PrimaryButton(
                text = "Details / Start",
                onClick = onOpenDetails,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
    }
}