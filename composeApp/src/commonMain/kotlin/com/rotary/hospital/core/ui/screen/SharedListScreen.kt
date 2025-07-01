package com.rotary.hospital.core.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SharedListScreen(
    title: String,
    items: List<T>,
    isLoading: Boolean,
    errorMessage: String?,
    emptyMessage: String,
    onSearchQueryChange: ((String) -> Unit)? = null,  // nullable if no search
    searchQuery: String = "",
    isSearchActive: Boolean = false,
    onToggleSearch: (() -> Unit)? = null,
    onBack: () -> Unit,
    onAdd: (() -> Unit)? = null,                      // nullable if no FAB
    itemContent: @Composable (item: T, onClick: () -> Unit) -> Unit,
    onItemClick: (T) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSearchActive) {
                            onToggleSearch?.invoke()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                title = {
                    if (isSearchActive && onSearchQueryChange != null) {
                        TextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = ColorPrimary,
                                unfocusedIndicatorColor = Color.Gray,
                                cursorColor = ColorPrimary
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = ColorPrimary.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        Text(
                            title, fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ColorPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = ColorPrimary
                ),
                actions = {
                    if (!isSearchActive && onToggleSearch!=null) {
                        IconButton(onClick = onToggleSearch) {
                            Icon(
                                Icons.Default.Search, contentDescription = "Search",
                                tint = ColorPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            onAdd?.let {
                FloatingActionButton(onClick = it, containerColor = ColorPrimary) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = White)
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    errorMessage,
                    Modifier.align(Alignment.Center),
                    color = ErrorRed
                )

                items.isEmpty() -> Text(emptyMessage, Modifier.align(Alignment.Center))
                else -> LazyColumn {
                    items(items) { item ->
                        itemContent(item) { onItemClick(item) }
                    }
                }
            }
        }
    }
}
