@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.feature.opd.presentation.screen.components.OpdListItem
import com.rotary.hospital.feature.opd.presentation.viewmodel.RegisteredOPDsState
import com.rotary.hospital.feature.opd.presentation.viewmodel.RegisteredOPDsViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisteredOPDsScreen(
    onOpdClick: (String) -> Unit,
    onAddNew: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: RegisteredOPDsViewModel = koinViewModel(),
    preferences: PreferencesManager = koinInject()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").collect { savedMobile ->
            if (savedMobile.isNotBlank()) {
                viewModel.fetchOpdList(savedMobile)
            } else {
                Logger.e("RegisteredOpdsScreen", "Mobile number not found")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Booked OPDs",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                }, navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorPrimary
                        )
                    }
                }, actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, titleContentColor = Color.Black
                )
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNew, containerColor = ColorPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add OPD", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            when (val currentState = state) {
                is RegisteredOPDsState.Loading -> {
                    Box(Modifier.fillMaxSize()){
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is RegisteredOPDsState.Error -> {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RegisteredOPDsState.Success -> {
                    val opds = currentState.opdList
                    if (opds.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No OPDs booked.", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onAddNew) {
                                Text("Register New OPD")
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(opds) { opd ->
                                OpdListItem(opd = opd, onClick = { onOpdClick(opd.opdId) })
                            }
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}