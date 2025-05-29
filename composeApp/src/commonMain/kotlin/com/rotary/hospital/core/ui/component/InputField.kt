package com.rotary.hospital.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    errorMessage: String? = null,
    contentDescription: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    placeholder: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1
) {
    Column(modifier = modifier.padding(bottom = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = contentDescription,
                    tint = if (errorMessage != null) ErrorRed else ColorPrimary.copy(alpha = 0.8f)
                )
            },
            trailingIcon = trailingIcon,
            placeholder = placeholder?.let { { Text(it, fontSize = 14.sp) } },
            readOnly = readOnly,
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorPrimary,
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = ErrorRed,
                focusedLabelColor = ColorPrimary,
                unfocusedLabelColor = Color.Gray,
                errorLabelColor = ErrorRed,
                cursorColor = ColorPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            keyboardOptions = keyboardOptions,
            maxLines = maxLines
        )
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            errorMessage?.let {
                Text(
                    text = it,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 2.dp).sizeIn(maxHeight = 20.dp)
                )
            }
        }
    }
}