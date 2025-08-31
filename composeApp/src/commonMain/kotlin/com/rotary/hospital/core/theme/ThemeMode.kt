package com.rotary.hospital.core.theme;

enum class ThemeMode (val displayName: String){
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System");

    fun toComposableBoolean(isSystemInDarkTheme: Boolean) =
        when (this) {
            LIGHT -> false
            DARK -> true
            SYSTEM -> isSystemInDarkTheme
        }
}
