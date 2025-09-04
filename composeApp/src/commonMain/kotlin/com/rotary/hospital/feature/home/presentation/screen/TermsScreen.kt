package com.rotary.hospital.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.utils.dial
import com.rotary.hospital.feature.home.presentation.viewmodel.TermsUiState
import com.rotary.hospital.feature.home.presentation.viewmodel.TermsViewModel
import org.adman.kmp.webview.KWebView
import org.adman.kmp.webview.TextAlign
import org.adman.kmp.webview.formatHtmlContent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.back
import rotaryhospital.composeapp.generated.resources.rotary_bg
import rotaryhospital.composeapp.generated.resources.some_error_occurred
import rotaryhospital.composeapp.generated.resources.terms_and_conditions_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    viewModel: TermsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState(initial = TermsUiState(isLoading = true))
    var isLoading by mutableStateOf(false)
    val uriHandler = LocalUriHandler.current   // works on Android & iOS

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.terms_and_conditions_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ColorPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                            tint = ColorPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )

        })
    { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(inner)

        ) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.error != null -> Text(
                    state.error ?: stringResource(Res.string.some_error_occurred),
                    modifier = Modifier.align(Alignment.Center)
                )

                state.html != null -> {
                    val formattedHtml = state.html!!.formatHtmlContent(
                        fontSize = 16,          // increase text size
                        textAlign = TextAlign.LEFT,
                        fontColor = "#333333",  // darker gray

                    )
                    // KWebView shared composable
                    KWebView(
                        modifier = Modifier.fillMaxSize(),
                        htmlContent = formattedHtml,
                        enableJavaScript = false,
                        isLoading = { loading ->
                            isLoading = loading
                        },
                        onUrlClicked = { clickedUrl ->
                            uriHandler.openUri(clickedUrl) // opens in default browser (both Android & iOS)
                        }
                    )
                }
            }
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Bottom fade shadow overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(34.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f) // subtle shadow
                            )
                        )
                    )
            )
        }


    }


}
