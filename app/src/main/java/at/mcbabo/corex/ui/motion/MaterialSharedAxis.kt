package at.mcbabo.corex.ui.motion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

private const val ProgressThreshold = 0.35f

private val Int.ForOutgoing: Int
    get() = (this * ProgressThreshold).toInt()

private val Int.ForIncoming: Int
    get() = this - this.ForOutgoing

/** [materialSharedAxisXIn] allows to switch a layout with shared X-axis enter transition. */
fun materialSharedAxisXIn(
    initialOffsetX: (fullWidth: Int) -> Int,
    durationMillis: Int = MotionConstants.DefaultMotionDuration,
): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        initialOffsetX = initialOffsetX,
    ) +
            fadeIn(
                animationSpec =
                    tween(
                        durationMillis = durationMillis.ForIncoming,
                        delayMillis = durationMillis.ForOutgoing,
                        easing = LinearOutSlowInEasing,
                    )
            )

/** [materialSharedAxisXOut] allows to switch a layout with shared X-axis exit transition. */
fun materialSharedAxisXOut(
    targetOffsetX: (fullWidth: Int) -> Int,
    durationMillis: Int = MotionConstants.DefaultMotionDuration,
): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        targetOffsetX = targetOffsetX,
    ) +
            fadeOut(
                animationSpec =
                    tween(
                        durationMillis = durationMillis.ForOutgoing,
                        delayMillis = 0,
                        easing = FastOutLinearInEasing,
                    )
            )

/** [materialSharedAxisYIn] allows to switch a layout with shared Y-axis enter transition. */
fun materialSharedAxisYIn(
    initialOffsetY: (fullWidth: Int) -> Int,
    durationMillis: Int = MotionConstants.DefaultMotionDuration,
): EnterTransition =
    slideInVertically(
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        initialOffsetY = initialOffsetY,
    ) +
            fadeIn(
                animationSpec =
                    tween(
                        durationMillis = durationMillis.ForIncoming,
                        delayMillis = durationMillis.ForOutgoing,
                        easing = LinearOutSlowInEasing,
                    )
            )

/** [materialSharedAxisYOut] allows to switch a layout with shared Y-axis exit transition. */
fun materialSharedAxisYOut(
    targetOffsetY: (fullWidth: Int) -> Int,
    durationMillis: Int = MotionConstants.DefaultMotionDuration,
): ExitTransition =
    slideOutVertically(
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        targetOffsetY = targetOffsetY,
    ) +
            fadeOut(
                animationSpec =
                    tween(
                        durationMillis = durationMillis.ForOutgoing,
                        delayMillis = 0,
                        easing = FastOutLinearInEasing,
                    )
            )

/**
 * [materialSharedAxisZIn] allows to switch a layout with shared Z-axis enter transition.
 *
 * @param forward whether the direction of the animation is forward.
 * @param durationMillis the duration of the enter transition.
 */
fun materialSharedAxisZIn(
    forward: Boolean,
    durationMillis: Int = MotionConstants.DefaultMotionDuration,
): EnterTransition =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = durationMillis.ForIncoming,
                delayMillis = durationMillis.ForOutgoing,
                easing = LinearOutSlowInEasing,
            )
    ) +
            scaleIn(
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = FastOutSlowInEasing
                ),
                initialScale = if (forward) 0.8f else 1.1f,
            )

/**
 * [materialSharedAxisZOut] allows to switch a layout with shared Z-axis exit transition.
 *
 * @param forward whether the direction of the animation is forward.
 * @param durationMillis the duration of the exit transition.
 */
fun materialSharedAxisZOut(
    forward: Boolean,
    durationMillis: Int = MotionConstants.DefaultMotionDuration,
): ExitTransition =
    fadeOut(
        animationSpec =
            tween(
                durationMillis = durationMillis.ForOutgoing,
                delayMillis = 0,
                easing = FastOutLinearInEasing,
            )
    ) +
            scaleOut(
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = FastOutSlowInEasing
                ),
                targetScale = if (forward) 1.1f else 0.8f,
            )