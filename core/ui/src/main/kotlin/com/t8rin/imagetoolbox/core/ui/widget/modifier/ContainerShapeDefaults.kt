/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import com.t8rin.imagetoolbox.core.ui.utils.animation.lessSpringySpec
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ContainerShapeDefaults {

    @Composable
    fun shapeForIndex(
        index: Int,
        size: Int,
        forceDefault: Boolean = false,
    ): RoundedCornerShape {
        val internalShape by remember(index, size, forceDefault) {
            derivedStateOf {
                when {
                    index == -1 || size == 1 || forceDefault -> defaultShape
                    index == 0 && size > 1 -> topShape
                    index == size - 1 -> bottomShape
                    else -> centerShape
                }
            }
        }

        return RoundedCornerShape(
            topStart = internalShape.topStart.animate(),
            topEnd = internalShape.topEnd.animate(),
            bottomStart = internalShape.bottomStart.animate(),
            bottomEnd = internalShape.bottomEnd.animate()
        )
    }

    val topShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 6.dp,
        bottomEnd = 6.dp
    )

    val centerShape = RoundedCornerShape(
        topStart = 6.dp,
        topEnd = 6.dp,
        bottomStart = 6.dp,
        bottomEnd = 6.dp
    )

    val bottomShape = RoundedCornerShape(
        topStart = 6.dp,
        topEnd = 6.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    val defaultShape = RoundedCornerShape(16.dp)

    val pressedShape = RoundedCornerShape(6.dp)

    val largeShape = RoundedCornerShape(20.dp)

    @Composable
    private inline fun CornerSize.animate(): Dp = animateDpAsState(
        targetValue = with(LocalDensity.current) {
            toPx(
                shapeSize = Size.Unspecified,
                density = this
            ).toDp()
        },
        animationSpec = lessSpringySpec()
    ).value

}


//TODO: Workaround for https://github.com/arkivanov/Decompose/issues/845 - https://issuetracker.google.com/issues/397701280

@Stable
internal class AnimatedShape(
    initialShape: RoundedCornerShape,
    private val density: Density,
    private val animationSpec: FiniteAnimationSpec<Float>,
) : Shape {

    private var size: Size = Size(
        width = with(density) { 48.dp.toPx() },
        height = with(density) { 48.dp.toPx() }
    )

    private val halfHeight: Float get() = size.height / 2f

    private val topStart = Animatable(initialShape.topStart.toPx())
    private val topEnd = Animatable(initialShape.topEnd.toPx())
    private val bottomStart = Animatable(initialShape.bottomStart.toPx())
    private val bottomEnd = Animatable(initialShape.bottomEnd.toPx())

    private inline fun CornerSize.toPx() = toPx(size, density)

    private suspend inline fun ShapeAnimatable.animateTo(
        cornerSize: CornerSize
    ) = animateTo(cornerSize.toPx(), animationSpec)

    private inline fun ShapeAnimatable.bounded() = value.fastCoerceIn(0f, halfHeight)

    suspend fun animateTo(targetShape: CornerBasedShape) = coroutineScope {
        launch { topStart.animateTo(targetShape.topStart) }
        launch { topEnd.animateTo(targetShape.topEnd) }
        launch { bottomStart.animateTo(targetShape.bottomStart) }
        launch { bottomEnd.animateTo(targetShape.bottomEnd) }
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        this.size = size

        return asRoundedCornerShape().createOutline(
            size = size,
            layoutDirection = layoutDirection,
            density = density
        )
    }

    fun asRoundedCornerShape() = RoundedCornerShape(
        topStart = topStart.bounded(),
        topEnd = topEnd.bounded(),
        bottomStart = bottomStart.bounded(),
        bottomEnd = bottomEnd.bounded(),
    )
}

internal typealias ShapeAnimatable = Animatable<Float, AnimationVector1D>

@Composable
internal fun rememberAnimatedShape(
    currentShape: RoundedCornerShape,
    animationSpec: FiniteAnimationSpec<Float> = lessSpringySpec(),
): AnimatedShape {
    val density = LocalDensity.current

    val state = remember(animationSpec, density) {
        AnimatedShape(
            initialShape = currentShape,
            animationSpec = animationSpec,
            density = density
        )
    }

    val channel = remember { Channel<RoundedCornerShape>(Channel.CONFLATED) }

    SideEffect { channel.trySend(currentShape) }
    LaunchedEffect(state, channel) {
        for (target in channel) {
            val newTarget = channel.tryReceive().getOrNull() ?: target
            launch { state.animateTo(newTarget) }
        }
    }

    return state
}

@Composable
fun animateShape(
    targetValue: RoundedCornerShape,
    animationSpec: FiniteAnimationSpec<Float> = lessSpringySpec(),
): Shape = rememberAnimatedShape(
    currentShape = targetValue,
    animationSpec = animationSpec
)

@Composable
fun shapeByInteraction(
    shape: Shape,
    pressedShape: Shape,
    interactionSource: InteractionSource?,
    animationSpec: FiniteAnimationSpec<Float> = lessSpringySpec(),
    delay: Long = 300,
    enabled: Boolean = true
): Shape {
    if (!enabled || interactionSource == null) return shape

    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val usePressedShape = pressed || focused

    val targetShapeState = remember {
        mutableStateOf(shape)
    }

    LaunchedEffect(usePressedShape, shape) {
        if (usePressedShape) {
            targetShapeState.value = pressedShape
        } else {
            if (shape is RoundedCornerShape) delay(delay)
            targetShapeState.value = shape
        }
    }

    val targetShape = targetShapeState.value

    if (targetShape is RoundedCornerShape) {
        return animateShape(
            targetValue = targetShape,
            animationSpec = animationSpec,
        )
    }

    return targetShape
}