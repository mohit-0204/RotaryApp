package com.rotary.hospital.core.domain

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource

/**
 * A sealed class to represent text that can be resolved in a Composable.
 * This decouples the ViewModel from the Android/Compose resource system for localization.
 */
sealed class UiText {
    // For text that comes directly from a dynamic source, like an API error message.
    data class DynamicString(val value: String) : UiText()

    // For text that comes from your KMP string resources (e.g., 'Res.string.error_network').
    data class StringResource(
        val id: org.jetbrains.compose.resources.StringResource,
        val args: Array<Any> = arrayOf()
    ) : UiText()

    // A helper function to resolve the UiText into a String within a Composable.
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id, *args)
        }
    }
}