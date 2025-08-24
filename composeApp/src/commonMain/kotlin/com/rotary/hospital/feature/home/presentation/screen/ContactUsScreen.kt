package com.rotary.hospital.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.utils.BarIcons
import com.rotary.hospital.core.utils.SetSystemBars
import com.rotary.hospital.core.utils.dial
import com.rotary.hospital.core.utils.rememberPlatformContext
import com.rotary.hospital.feature.home.domain.model.Contact
import com.rotary.hospital.feature.home.domain.model.ContactSection
import com.rotary.hospital.feature.home.presentation.viewmodel.ContactUsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.rotary_bg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(
    viewModel: ContactUsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {

    SetSystemBars(iconStyle = BarIcons.Light)


    val context = rememberPlatformContext()
    LaunchedEffect(Unit) { viewModel.load() }
    val state by viewModel.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contact Us",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
//                                ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = White,
//                    titleContentColor = Color.Black
                    containerColor = ColorPrimary,
                    titleContentColor = Color.White
                )
            )

        })
    { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            when {
                state.loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Image(
                                painter = painterResource(Res.drawable.rotary_bg),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(140.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        items(state.sections) { section ->
                            SectionCard(section = section) { phone ->
                                dial(phone, context)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    section: ContactSection,
    onDial: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            section.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        section.items.forEach { contact ->
            ContactRow(contact, onDial)
        }
    }
}

@Composable
private fun ContactRow(contact: Contact, onDial: (String) -> Unit) {
    val clickable = contact.phone != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (clickable) Modifier.clickable { onDial(contact.phone) } else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (contact.phone != null) {
            Icon(Icons.Filled.Call, contentDescription = null)
            Spacer(Modifier.width(12.dp))
        } else {
            Spacer(Modifier.width(36.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                contact.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            contact.description?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            contact.phone?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    HorizontalDivider(Modifier, 2.dp, DividerDefaults.color)
}