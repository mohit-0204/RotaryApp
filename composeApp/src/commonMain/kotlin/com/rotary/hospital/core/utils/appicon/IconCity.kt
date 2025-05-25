package com.rotary.hospital.core.utils.appicon/*
* Converted using https://composables.com/svgtocompose
*/

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IconCity: ImageVector
	get() {
		if (_Location_city != null) {
			return _Location_city!!
		}
		_Location_city = ImageVector.Builder(
            name = "Location_city",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
			path(
    			fill = null,
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(0f, 0f)
				horizontalLineToRelative(24f)
				verticalLineToRelative(24f)
				horizontalLineTo(0f)
				close()
			}
			path(
    			fill = SolidColor(Color.Black),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(15f, 11f)
				verticalLineTo(5f)
				lineToRelative(-3f, -3f)
				lineToRelative(-3f, 3f)
				verticalLineToRelative(2f)
				horizontalLineTo(3f)
				verticalLineToRelative(14f)
				horizontalLineToRelative(18f)
				verticalLineTo(11f)
				horizontalLineToRelative(-6f)
				close()
				moveToRelative(-8f, 8f)
				horizontalLineTo(5f)
				verticalLineToRelative(-2f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
				moveToRelative(0f, -4f)
				horizontalLineTo(5f)
				verticalLineToRelative(-2f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
				moveToRelative(0f, -4f)
				horizontalLineTo(5f)
				verticalLineTo(9f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
				moveToRelative(6f, 8f)
				horizontalLineToRelative(-2f)
				verticalLineToRelative(-2f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
				moveToRelative(0f, -4f)
				horizontalLineToRelative(-2f)
				verticalLineToRelative(-2f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
				moveToRelative(0f, -4f)
				horizontalLineToRelative(-2f)
				verticalLineTo(9f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
				moveToRelative(0f, -4f)
				horizontalLineToRelative(-2f)
				verticalLineTo(5f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
				moveToRelative(6f, 12f)
				horizontalLineToRelative(-2f)
				verticalLineToRelative(-2f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
				moveToRelative(0f, -4f)
				horizontalLineToRelative(-2f)
				verticalLineToRelative(-2f)
				horizontalLineToRelative(2f)
				verticalLineToRelative(2f)
				close()
			}
		}.build()
		return _Location_city!!
	}

private var _Location_city: ImageVector? = null
