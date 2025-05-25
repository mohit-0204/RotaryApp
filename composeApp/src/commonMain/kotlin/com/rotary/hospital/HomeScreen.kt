package com.rotary.hospital

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.homescreen.DashboardCard
import com.rotary.hospital.homescreen.QuickAccessItem
import com.rotary.hospital.homescreen.TopBar
import com.rotary.hospital.homescreen.WelcomeSection
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.calendar_icon
import rotaryhospital.composeapp.generated.resources.pill_icon
import rotaryhospital.composeapp.generated.resources.test_tube_icon
import rotaryhospital.composeapp.generated.resources.user_icon

@Composable
fun HomeScreen(
    patientName: String,
    patientId: String
) {
    AppTheme {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                        label = { Text("Calendar") },
                        selected = false,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = { }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF9F9F9))
                    .padding(16.dp, vertical = 8.dp)
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                WelcomeSection(patientName)

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(320.dp)
                ) {
                    item {
                        DashboardCard(
                            title = "Patient Profile",
                            subtitle = "View & manage profiles",
                            iconRes = Res.drawable.user_icon
                        )
                    }
                    item {
                        DashboardCard(
                            title = "OPD Booking",
                            subtitle = "Schedule appointments",
                            iconRes = Res.drawable.calendar_icon
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Lab Tests",
                            subtitle = "Order & view results",
                            iconRes = Res.drawable.test_tube_icon
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Medicine Reminders",
                            subtitle = "Set & manage alerts",
                            iconRes = Res.drawable.pill_icon
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
                    item { QuickAccessItem("Contact Us", Icons.Default.Phone) }
                    item { QuickAccessItem("Settings", Icons.Default.Settings) }
                    item { QuickAccessItem("Terms", Icons.Default.Info) }
//                    item { QuickAccessItem("Refund Policy", Icons.Default.Receipt) }
//                    item { QuickAccessItem("Events", Icons.Default.Event) }
//                    item { QuickAccessItem("Rotary Website", Icons.Default.Language) }
                }
            }
        }
    }
}

