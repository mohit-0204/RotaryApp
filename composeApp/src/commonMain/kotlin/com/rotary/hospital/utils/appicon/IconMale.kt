package appicon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IconMale: ImageVector
    get() {
        if (_iconMale != null) {
            return _iconMale!!
        }
        _iconMale = Builder(
            name = "IconMale", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 393.74f, viewportHeight = 393.74f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(370.91f, 0.0f)
                horizontalLineToRelative(-93.05f)
                curveToRelative(-9.09f, 0.0f, -16.45f, 7.36f, -16.45f, 16.45f)
                curveToRelative(0.0f, 9.09f, 7.36f, 16.45f, 16.45f, 16.45f)
                horizontalLineToRelative(43.19f)
                lineTo(217.25f, 136.7f)
                curveToRelative(-21.05f, -12.88f, -45.77f, -20.32f, -72.19f, -20.32f)
                curveToRelative(-76.47f, 0.0f, -138.68f, 62.21f, -138.68f, 138.68f)
                curveToRelative(0.0f, 76.47f, 62.21f, 138.68f, 138.68f, 138.68f)
                reflectiveCurveToRelative(138.68f, -62.2f, 138.68f, -138.68f)
                curveToRelative(0.0f, -33.07f, -11.65f, -63.46f, -31.04f, -87.31f)
                lineTo(354.46f, 65.99f)
                verticalLineToRelative(49.23f)
                curveToRelative(0.0f, 9.09f, 7.36f, 16.45f, 16.44f, 16.45f)
                curveToRelative(9.09f, 0.0f, 16.45f, -7.37f, 16.45f, -16.45f)
                verticalLineTo(16.45f)
                curveTo(387.36f, 7.36f, 380.0f, 0.0f, 370.91f, 0.0f)
                close()
                moveTo(145.06f, 346.74f)
                curveToRelative(-50.55f, 0.0f, -91.67f, -41.13f, -91.67f, -91.68f)
                curveToRelative(0.0f, -50.54f, 41.12f, -91.67f, 91.67f, -91.67f)
                curveToRelative(50.55f, 0.0f, 91.66f, 41.12f, 91.66f, 91.67f)
                curveTo(236.72f, 305.61f, 195.6f, 346.74f, 145.06f, 346.74f)
                close()
            }
        }
            .build()
        return _iconMale!!
    }

private var _iconMale: ImageVector? = null


