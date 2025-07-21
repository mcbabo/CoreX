package at.mcbabo.corex.ui.motion

import androidx.compose.animation.core.PathEasing
import androidx.compose.ui.graphics.Path

private val path =
    Path().apply {
        moveTo(0f, 0f)
        cubicTo(0.05F, 0F, 0.133333F, 0.06F, 0.166666F, 0.4F)
        cubicTo(0.208333F, 0.82F, 0.25F, 1F, 1F, 1F)
    }

val EmphasizeEasing = PathEasing(path)

