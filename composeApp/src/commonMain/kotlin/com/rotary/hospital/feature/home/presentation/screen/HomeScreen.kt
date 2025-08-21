package com.rotary.hospital.feature.home.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.ui.toastController
import com.rotary.hospital.feature.home.presentation.components.DashboardCard
import com.rotary.hospital.feature.home.presentation.components.HomeTopBar
import com.rotary.hospital.feature.home.presentation.components.QuickAccessItem
import com.rotary.hospital.feature.home.presentation.components.WelcomeWidget
import com.rotary.hospital.feature.home.presentation.model.DashboardItem
import com.rotary.hospital.feature.home.presentation.model.HomeAction
import com.rotary.hospital.feature.home.presentation.model.QuickAccessItemModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.calendar_icon
import rotaryhospital.composeapp.generated.resources.pill_icon
import rotaryhospital.composeapp.generated.resources.test_tube_icon
import rotaryhospital.composeapp.generated.resources.user_icon

@Composable
fun HomeScreen(
    patientName: String,
    onItemClick: (HomeAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    // Define dashboard items
    val dashboardItems = listOf(
        DashboardItem(
            title = "Patient Profile",
            subtitle = "View & manage profiles",
            iconRes = Res.drawable.user_icon,
            action = HomeAction.ViewPatientProfile
        ),
        DashboardItem(
            title = "OPD Booking",
            subtitle = "Schedule appointments",
            iconRes = Res.drawable.calendar_icon,
            action = HomeAction.BookOPD
        ),
        DashboardItem(
            title = "Lab Tests",
            subtitle = "Order & view results",
            iconRes = Res.drawable.test_tube_icon,
            action = HomeAction.ViewLabTests
        ),
        DashboardItem(
            title = "Med Reminders",
            subtitle = "Set & manage alerts",
            iconRes = Res.drawable.pill_icon,
            action = HomeAction.ManageMedicineReminders
        )
    )

    // Define quick-access items
    val quickAccessItems = listOf(
        QuickAccessItemModel(
            title = "Contact Us",
            icon = Icons.Default.Phone,
            action = HomeAction.ContactUs
        ),
        QuickAccessItemModel(
            title = "Settings",
            icon = Icons.Default.Settings,
            action = HomeAction.OpenSettings
        ),
        QuickAccessItemModel(
            title = "Terms",
            icon = Icons.Default.Info,
            action = HomeAction.ViewTerms
        )
    )

    AppTheme {
        Scaffold(
            topBar = { HomeTopBar() },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            /*bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = true,
                        onClick = { },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorPrimary,
                            selectedTextColor = ColorPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = ColorPrimary.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                        label = { Text("Calendar") },
                        selected = false,
                        onClick = { },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorPrimary,
                            selectedTextColor = ColorPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = ColorPrimary.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = { },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorPrimary,
                            selectedTextColor = ColorPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = ColorPrimary.copy(alpha = 0.1f)
                        )
                    )
                }
            }*/
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF9F9F9))
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                WelcomeWidget(patientName)

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(320.dp)
                ) {
                    items(dashboardItems.size) { index ->
                        val item = dashboardItems[index]
                        DashboardCard(
                            title = item.title,
                            subtitle = item.subtitle,
                            iconRes = item.iconRes,
                            onClick = { onItemClick(item.action) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ColorPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(quickAccessItems.size) { index ->
                        val item = quickAccessItems[index]
                        QuickAccessItem(
                            label = item.title,
                            icon = item.icon,
                            onClick = { onItemClick(item.action) }
                        )
                    }
                }
            }
        }
    }
}