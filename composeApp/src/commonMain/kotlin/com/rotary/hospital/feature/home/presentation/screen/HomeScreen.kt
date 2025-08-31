@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.feature.home.presentation.components.DashboardCard
import com.rotary.hospital.feature.home.presentation.components.QuickAccessItem
import com.rotary.hospital.feature.home.presentation.components.WelcomeWidget
import com.rotary.hospital.feature.home.presentation.model.DashboardItem
import com.rotary.hospital.feature.home.presentation.model.HomeAction
import com.rotary.hospital.feature.home.presentation.model.QuickAccessItemModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.calendar_icon
import rotaryhospital.composeapp.generated.resources.logo
import rotaryhospital.composeapp.generated.resources.pill_icon
import rotaryhospital.composeapp.generated.resources.test_tube_icon
import rotaryhospital.composeapp.generated.resources.user_icon

@Composable
fun HomeScreen(
    patientName: String,
    onItemClick: (HomeAction) -> Unit,
    onLogout: () -> Unit,
    snackbarHostState: SnackbarHostState,
    preferences: PreferencesManager = koinInject()
) {
    val scope = rememberCoroutineScope()
    val showLogoutDialog = remember { mutableStateOf(false) }

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
            title = "Terms & Cond.",
            icon = Icons.Default.Info,
            action = HomeAction.ViewTerms
        )
    )

    AppTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Rotary Hospital",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorPrimary
                        )
                    }, navigationIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.logo),
                            contentDescription = "App Icon",
                            tint = ColorPrimary,
                            modifier = Modifier
                                .size(50.dp)   // adjust logo size here
                                .padding(start = 8.dp), // optional spacing
                        )
                    }, actions = {
                        IconButton(onClick = {
                            showLogoutDialog.value = true
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "logout",
                                tint = ColorPrimary
                            )
                        }
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White, titleContentColor = Color.Black
                    )
                )
                /* // to change color of status bar
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                        .background(Color.Green.copy(alpha = 0.1f))
                )*/
            },
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
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                // Welcome header
                item {
                    WelcomeWidget(patientName = patientName)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Feature Grid
                item {
                    FlowRow(
                        maxItemsInEachRow = 2, // wrap 2 items per row
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        dashboardItems.forEach { item ->
                            DashboardCard(
                                title = item.title,
                                subtitle = item.subtitle,
                                iconRes = item.iconRes,
                                onClick = { onItemClick(item.action) },
                                modifier = Modifier
                                    .weight(1f) // <-- makes each item share row equally
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }


                item {
                    // Quick Access Section
                    Text(
                        text = "Quick Access",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    FlowRow(
                        maxItemsInEachRow = 3,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quickAccessItems.forEach { item ->
                            QuickAccessItem(
                                label = item.title,
                                icon = item.icon,
                                onClick = { onItemClick(item.action) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                }


            }
        }
        if (showLogoutDialog.value) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog.value = false },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "logout",
                        tint = ColorPrimary
                    )
                },
                title = {
                    Text(
                        text = "Logout",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to logout?",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog.value = false
                            scope.launch {
                                preferences.saveBoolean(PreferenceKeys.IS_LOGGED_IN, false)
                                preferences.clear(PreferenceKeys.MOBILE_NUMBER)
                                preferences.clear(PreferenceKeys.PATIENT_ID)
                                preferences.clear(PreferenceKeys.PATIENT_NAME)
                                onLogout()
                            }
                        }
                    ) {
                        Text(
                            "Logout",
                            color = ColorPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog.value = false }) {
                        Text(
                            "Cancel",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                containerColor = Color.White, // background of dialog
                shape = RoundedCornerShape(16.dp) // rounded corners
            )
        }

    }
}