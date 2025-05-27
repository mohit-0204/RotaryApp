package appicon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

public val IconDrop: ImageVector
    get() {
        if (_iconDrop != null) {
            return _iconDrop!!
        }
        _iconDrop = Builder(name = "IconDrop", defaultWidth = 24.dp, defaultHeight = 24.dp,
                viewportWidth = 491.32f, viewportHeight = 491.32f).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(283.31f, 18.58f)
                curveTo(273.66f, 6.76f, 260.01f, 0.01f, 245.69f, 0.0f)
                curveToRelative(-14.31f, -0.01f, -27.97f, 6.72f, -37.63f, 18.54f)
                curveTo(154.64f, 83.86f, 63.22f, 207.33f, 63.22f, 287.15f)
                curveToRelative(0.0f, 112.76f, 81.7f, 204.18f, 182.44f, 204.18f)
                curveToRelative(100.77f, 0.0f, 182.45f, -91.42f, 182.45f, -204.18f)
                curveTo(428.11f, 207.35f, 336.73f, 83.91f, 283.31f, 18.58f)
                close()
                moveTo(234.97f, 438.74f)
                curveToRelative(-67.3f, 0.0f, -122.07f, -61.28f, -122.07f, -136.61f)
                curveToRelative(0.0f, -9.1f, 6.6f, -16.48f, 14.72f, -16.48f)
                curveToRelative(8.13f, 0.0f, 14.73f, 7.38f, 14.73f, 16.48f)
                curveToRelative(0.0f, 57.16f, 41.55f, 103.66f, 92.63f, 103.66f)
                curveToRelative(8.15f, 0.0f, 14.73f, 7.38f, 14.73f, 16.48f)
                curveTo(249.7f, 431.37f, 243.11f, 438.74f, 234.97f, 438.74f)
                close()
            }
        }
        .build()
        return _iconDrop!!
    }

private var _iconDrop: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = IconDrop, contentDescription = "")
    }
}
