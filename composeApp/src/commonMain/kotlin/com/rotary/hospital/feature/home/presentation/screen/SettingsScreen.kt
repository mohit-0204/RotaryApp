@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.home.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.domain.Language
import com.rotary.hospital.core.domain.Localization
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.Dimens
import com.rotary.hospital.core.theme.ThemeMode
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.component.material_cupertino.dropdown.CupertinoDropdownItem
import com.rotary.hospital.core.ui.component.material_cupertino.sections.CupertinoSection
import com.rotary.hospital.core.ui.component.material_cupertino.sections.CupertinoSectionRow
import com.rotary.hospital.core.ui.layouts.lists.ScrollableColumn
import org.jetbrains.compose.resources.stringResource
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.back
import rotaryhospital.composeapp.generated.resources.dark_mode
import rotaryhospital.composeapp.generated.resources.english
import rotaryhospital.composeapp.generated.resources.hindi
import rotaryhospital.composeapp.generated.resources.language
import rotaryhospital.composeapp.generated.resources.language_settings
import rotaryhospital.composeapp.generated.resources.light_mode
import rotaryhospital.composeapp.generated.resources.settings_title
import rotaryhospital.composeapp.generated.resources.system_mode
import rotaryhospital.composeapp.generated.resources.theme
import rotaryhospital.composeapp.generated.resources.theme_settings

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    selectedLanguage: Language,
    onLanguageChanged: (Language) -> Unit
) {
    SettingsScreenContent(
        modifier = Modifier,
        onBack = onBack,
        themeMode = ThemeMode.LIGHT,
        onThemeModeChange = {
            // todo: update theme
        },
        selectedLanguage = selectedLanguage,
        onLanguageChanged = onLanguageChanged
    )
}


@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    selectedLanguage: Language,
    onLanguageChanged: (Language) -> Unit,
) {

    Scaffold(
        modifier = modifier, topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.settings_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ColorPrimary
                    )
                }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )

        }) { innerPaddings ->

        ScrollableColumn(
            modifier = Modifier
                .padding(innerPaddings)
                .padding(Dimens.paddingLarge)
        ) {

            // theme section
            CupertinoSection(
                title = stringResource(Res.string.theme_settings)
            ) {
                var isExpended by rememberSaveable { mutableStateOf(false) }

                CupertinoSectionRow(
                    label = stringResource(Res.string.theme),
                    modifier = Modifier.background(White),
                    icon = Icons.Default.Palette,
                    value = getThemeModeLabel(themeMode),
                    isExpanded = isExpended,
                    onExpandedChange = {
                        isExpended = it
                    },
                    isLast = true
                )
                {
                    // content
                    ThemeMode.entries.forEachIndexed { index, themeMode ->
                        CupertinoDropdownItem(
                            modifier = Modifier.background(White),
                            text = getThemeModeLabel(themeMode), leadingIcon = when (themeMode) {
                                ThemeMode.LIGHT -> Icons.Default.LightMode
                                ThemeMode.DARK -> Icons.Default.DarkMode
                                ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                            }, onClick = {
                                isExpended = false
                                onThemeModeChange(themeMode)
                            }, showDivider = index != ThemeMode.entries.lastIndex
                        )
                    }
                }
            }

            // Language section
            CupertinoSection(
                title = stringResource(Res.string.language_settings),
                modifier = Modifier.padding(top = Dimens.paddingLarge)
            ) {
                var isLanguageExpanded by rememberSaveable { mutableStateOf(false) }
                CupertinoSectionRow(
                    label = stringResource(Res.string.language),
                    modifier = Modifier.background(White),
                    icon = Icons.Default.Language, // Use an appropriate icon
                    value = getLanguageLabel(selectedLanguage),
                    isExpanded = isLanguageExpanded,
                    onExpandedChange = { isLanguageExpanded = it },
                    isLast = true
                ) {
                    Language.entries.forEachIndexed { index, language ->
                        CupertinoDropdownItem(
                            modifier = Modifier.background(White),
                            text = getLanguageLabel(language),
                            leadingIcon = Icons.Default.Language, // Optionally customize per language
                            onClick = {
                                isLanguageExpanded = false
                                onLanguageChanged(language)
                            },
                            showDivider = index != Language.entries.lastIndex
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun getThemeModeLabel(themeMode: ThemeMode): String = when (themeMode) {
    ThemeMode.LIGHT -> stringResource(Res.string.light_mode)
    ThemeMode.DARK -> stringResource(Res.string.dark_mode)
    ThemeMode.SYSTEM -> stringResource(Res.string.system_mode)
}

@Composable
private fun getLanguageLabel(language: Language): String = when (language) {
    Language.English -> stringResource(Res.string.english)
    Language.Hindi -> stringResource(Res.string.hindi)
}
