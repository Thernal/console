package io.thernal.console.designsystem.foundation.indication

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import io.thernal.console.designsystem.foundation.color.Opacity
import kotlinx.coroutines.launch

data class PressableIndication(
    val pressedScale: Float = 0.98f,
    val pressedAlpha: Float = Opacity.S65,
    val animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow,
    ),
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return IndicationNode(
            interactionSource = interactionSource,
            pressedScale = pressedScale,
            pressedAlpha = pressedAlpha,
            animationSpec = animationSpec,
        )
    }

    private class IndicationNode(
        private val interactionSource: InteractionSource,
        private val pressedScale: Float,
        private val pressedAlpha: Float,
        private val animationSpec: AnimationSpec<Float>,
    ) : Modifier.Node(), DrawModifierNode {
        private val animatedScale = Animatable(initialValue = 1f)
        private val animatedAlpha = Animatable(initialValue = 1f)
        private val layerPaint = Paint()

        override fun onAttach() {
            coroutineScope.launch {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> {
                            launch {
                                animatedScale.animateTo(
                                    targetValue = pressedScale,
                                    animationSpec = animationSpec,
                                )
                            }
                            launch {
                                animatedAlpha.animateTo(
                                    targetValue = pressedAlpha,
                                    animationSpec = animationSpec,
                                )
                            }
                        }

                        is PressInteraction.Release,
                        is PressInteraction.Cancel,
                        -> {
                            launch {
                                animatedScale.animateTo(
                                    targetValue = 1f,
                                    animationSpec = animationSpec,
                                )
                            }
                            launch {
                                animatedAlpha.animateTo(
                                    targetValue = 1f,
                                    animationSpec = animationSpec,
                                )
                            }
                        }
                    }
                }
            }
        }

        override fun ContentDrawScope.draw() {
            layerPaint.alpha = animatedAlpha.value

            drawContext.canvas.saveLayer(
                bounds = Rect(Offset.Zero, size),
                paint = layerPaint,
            )
            scale(
                scale = animatedScale.value,
                pivot = center,
            ) {
                this@draw.drawContent()
            }
            drawContext.canvas.restore()
        }
    }
}
