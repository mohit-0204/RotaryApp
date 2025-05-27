package appicon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IconFemale: ImageVector
    get() {
        if (_iconFemale != null) {
            return _iconFemale!!
        }
        _iconFemale = Builder(
            name = "IconFemale",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF0F0F0F)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = EvenOdd
            ) {
                moveTo(20.0f, 9.0f)
                curveTo(20.0f, 13.08f, 16.945f, 16.447f, 12.998f, 16.938f)
                curveTo(12.999f, 16.959f, 13.0f, 16.979f, 13.0f, 17.0f)
                verticalLineTo(19.0f)
                horizontalLineTo(14.0f)
                curveTo(14.552f, 19.0f, 15.0f, 19.448f, 15.0f, 20.0f)
                curveTo(15.0f, 20.552f, 14.552f, 21.0f, 14.0f, 21.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(22.0f)
                curveTo(13.0f, 22.552f, 12.552f, 23.0f, 12.0f, 23.0f)
                curveTo(11.448f, 23.0f, 11.0f, 22.552f, 11.0f, 22.0f)
                verticalLineTo(21.0f)
                horizontalLineTo(10.0f)
                curveTo(9.448f, 21.0f, 9.0f, 20.552f, 9.0f, 20.0f)
                curveTo(9.0f, 19.448f, 9.448f, 19.0f, 10.0f, 19.0f)
                horizontalLineTo(11.0f)
                verticalLineTo(17.0f)
                curveTo(11.0f, 16.979f, 11.001f, 16.959f, 11.002f, 16.938f)
                curveTo(7.055f, 16.447f, 4.0f, 13.08f, 4.0f, 9.0f)
                curveTo(4.0f, 4.582f, 7.582f, 1.0f, 12.0f, 1.0f)
                curveTo(16.418f, 1.0f, 20.0f, 4.582f, 20.0f, 9.0f)
                close()
                moveTo(6.004f, 9.0f)
                curveTo(6.004f, 12.312f, 8.688f, 14.996f, 12.0f, 14.996f)
                curveTo(15.312f, 14.996f, 17.996f, 12.312f, 17.996f, 9.0f)
                curveTo(17.996f, 5.688f, 15.312f, 3.004f, 12.0f, 3.004f)
                curveTo(8.688f, 3.004f, 6.004f, 5.688f, 6.004f, 9.0f)
                close()
            }
        }
            .build()
        return _iconFemale!!
    }

private var _iconFemale: ImageVector? = null


