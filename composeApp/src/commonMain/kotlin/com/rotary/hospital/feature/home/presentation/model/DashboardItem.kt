package com.rotary.hospital.feature.home.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

data class DashboardItem(
    val title: String,
    val subtitle: String,
    val iconRes: DrawableResource,
    val action: HomeAction
)

data class QuickAccessItemModel(
    val title: String,
    val icon: ImageVector,
    val action: HomeAction
)