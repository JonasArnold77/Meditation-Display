package com.example.meditationbio.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.ProblemField
import com.example.meditationbio.ui.components.InfoBadge
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SecondaryButton
import com.example.meditationbio.ui.components.SectionCard
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

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
    var visibleIndex by remember(recommendations) {
        mutableIntStateOf(currentRecommendationIndex)
    }

    LaunchedEffect(currentRecommendationIndex, recommendations) {
        if (currentRecommendationIndex in recommendations.indices) {
            visibleIndex = currentRecommendationIndex
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
            targetState = visibleRecommendation.id,
            modifier = Modifier.padding(top = 16.dp),
            transitionSpec = {
                (
                        fadeIn(
                            animationSpec = tween(320)
                        )
                        ).togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(320),
                            targetOffsetX = { fullWidth -> -fullWidth }
                        ) + fadeOut(
                            animationSpec = tween(320)
                        )
                    ).using(
                        SizeTransform(clip = false)
                    )
            },
            label = "recommendation_switch"
        ) { targetId ->
            val recommendation = recommendations.firstOrNull { it.id == targetId } ?: return@AnimatedContent

            SwipeableRecommendationCard(
                recommendation = recommendation,
                problemFieldTitle = selectedProblemField?.title.orEmpty(),
                onSwipeLeft = onSwipeLeft,
                onSwipeRight = onSwipeRight,
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
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onOpenDetails: () -> Unit
) {
    val animatedOffsetX = remember(recommendation.id) { Animatable(0f) }
    val highlightAlpha = remember(recommendation.id) { Animatable(0f) }
    val savedTextAlpha = remember(recommendation.id) { Animatable(0f) }
    val savedTextScale = remember(recommendation.id) { Animatable(0.9f) }

    var dragOffsetX by remember(recommendation.id) { mutableFloatStateOf(0f) }
    var runRightSaveAnimation by remember(recommendation.id) { mutableStateOf(false) }
    var leftSwipeTriggered by remember(recommendation.id) { mutableStateOf(false) }

    LaunchedEffect(recommendation.id) {
        dragOffsetX = 0f
        animatedOffsetX.snapTo(0f)
        highlightAlpha.snapTo(0f)
        savedTextAlpha.snapTo(0f)
        savedTextScale.snapTo(0.9f)
        runRightSaveAnimation = false
        leftSwipeTriggered = false
    }

    val currentOffsetX = if (runRightSaveAnimation) {
        animatedOffsetX.value
    } else {
        dragOffsetX
    }

    val currentAlpha = if (!runRightSaveAnimation && currentOffsetX < 0f) {
        (1f - (currentOffsetX.absoluteValue / 420f)).coerceIn(0.2f, 1f)
    } else {
        1f
    }

    LaunchedEffect(runRightSaveAnimation) {
        if (runRightSaveAnimation) {
            animatedOffsetX.snapTo(dragOffsetX)

            animatedOffsetX.animateTo(
                targetValue = 180f,
                animationSpec = tween(160)
            )

            highlightAlpha.snapTo(0f)
            savedTextAlpha.snapTo(0f)
            savedTextScale.snapTo(0.9f)

            highlightAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(140)
            )

            savedTextAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(140)
            )

            savedTextScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(140)
            )

            onSwipeRight()

            savedTextAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(220)
            )

            savedTextScale.animateTo(
                targetValue = 0.9f,
                animationSpec = tween(220)
            )

            highlightAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(240)
            )

            animatedOffsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(220)
            )

            dragOffsetX = 0f
            runRightSaveAnimation = false
        }
    }

    Box {
        SectionCard(
            modifier = Modifier
                .offset { IntOffset(currentOffsetX.roundToInt(), 0) }
                .alpha(currentAlpha)
                .graphicsLayer {
                    shadowElevation = if (highlightAlpha.value > 0f) 18f else 0f
                }
                .pointerInput(recommendation.id) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            if (!runRightSaveAnimation && !leftSwipeTriggered) {
                                dragOffsetX += dragAmount
                            }
                        },
                        onDragEnd = {
                            when {
                                dragOffsetX <= -160f && !runRightSaveAnimation && !leftSwipeTriggered -> {
                                    leftSwipeTriggered = true
                                    dragOffsetX = 0f
                                    onSwipeLeft()
                                }

                                dragOffsetX >= 160f && !runRightSaveAnimation && !leftSwipeTriggered -> {
                                    runRightSaveAnimation = true
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
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
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

                if (highlightAlpha.value > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(highlightAlpha.value)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                                shape = RoundedCornerShape(24.dp)
                            )
                    )
                }

                if (savedTextAlpha.value > 0f) {
                    Text(
                        text = "Saved",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {
                                scaleX = savedTextScale.value
                                scaleY = savedTextScale.value
                            }
                            .alpha(savedTextAlpha.value),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}