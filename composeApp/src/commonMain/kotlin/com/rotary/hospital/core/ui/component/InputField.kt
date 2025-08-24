package com.rotary.hospital.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary

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
    maxLines: Int = 1,
    onClick: () -> Unit = {},

    ) {
    val interactionSource = remember { MutableInteractionSource() }
    // Collect interaction events
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                onClick.invoke()
            }
        }
    }
    Column(modifier = modifier.padding(bottom = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = contentDescription,
                    tint = if (errorMessage != null) MaterialTheme.colorScheme.error else ColorPrimary.copy(alpha = 0.8f)
                )
            },
            trailingIcon = trailingIcon,
            placeholder = placeholder?.let { { Text(it, fontSize = 14.sp) } },
            readOnly = readOnly,
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                // Active
                focusedBorderColor = ColorPrimary,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = ColorPrimary,
                unfocusedLabelColor = Color.Gray,
                cursorColor = ColorPrimary,

                // Error
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorCursorColor = MaterialTheme.colorScheme.error,

                // Disabled (no more purple!)

                // Disabled (override *all* possible values)
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Transparent,
                disabledContainerColor = Color(0xFFF5F5F5),
                disabledLabelColor = Color.Gray,
                disabledLeadingIconColor = Color.Gray,
                disabledTrailingIconColor = Color.Gray,
                disabledPlaceholderColor = Color.Gray,
                disabledSupportingTextColor = Color.Gray,
                disabledPrefixColor = Color.Gray,
                disabledSuffixColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            keyboardOptions = keyboardOptions,
            maxLines = maxLines,
            interactionSource = interactionSource
        )
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 2.dp)
                        .sizeIn(maxHeight = 20.dp)
                )
            }
        }
    }
}
