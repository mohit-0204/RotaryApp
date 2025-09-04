package com.rotary.hospital.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.White
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
    val context = rememberPlatformContext()
    LaunchedEffect(Unit) { viewModel.load() }
    val state by viewModel.ui.collectAsState()

    // Colors matching Tailwind classes in your HTML
    val teal600 = Color(0xff07716a)       // tailwind teal-600
    val teal50 = Color(0xFFF0FEFA)        // light teal background for call chip (approx)
    val gray800 = Color(0xFF111827)       // text-gray-800
    val gray500 = Color(0xFF6B7280)       // text-gray-500
    val gray200 = Color(0xFFE5E7EB)       // border-gray-200
    val gray700 = Color(0xFF374151) // text-gray-700


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Contact Us",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = teal600
                    )
                }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = teal600
                        )
                    }
                }, actions = {
                    // empty spacer box to match HTML's <div class="w-10"></div>
                    Box(modifier = Modifier.size(40.dp))
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    scrolledContainerColor = White
                ),
                modifier = Modifier.shadow(1.dp)

            )


        }, containerColor = White
    ) { innerPadding ->
        when {
            state.loading -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        state.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // header image (h-48 -> 192.dp)
                    item {
                        Image(
                            painter = painterResource(Res.drawable.rotary_bg),
                            contentDescription = "header image",
                            modifier = Modifier.fillMaxWidth().height(192.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // sections
                    items(state.sections, key = { it.title }) { section ->
                        SectionBlock(
                            section = section,
                            teal600 = teal600,
                            teal50 = teal50,
                            gray800 = gray700,
                            gray500 = gray500,
                            gray200 = gray200,
                            onDial = { phone -> dial(phone, context) })
                    }

                    /*  // Address block (last item)
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        AddressBlock(
                            teal600 = teal600,
                            gray800 = gray800,
                            gray500 = gray500,
                            onDirections = { *//* open maps *//* })
                        Spacer(modifier = Modifier.height(24.dp))
                    }*/
                }
            }
        }
    }
}

@Composable
private fun SectionBlock(
    section: ContactSection,
    teal600: Color,
    teal50: Color,
    gray800: Color,
    gray500: Color,
    gray200: Color,
    onDial: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        // section title: text-teal-600 font-medium text-sm px-4 mb-2
        Text(
            text = section.title,
            color = teal600,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        // Card with rounded-xl and shadow-sm
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                section.items.forEachIndexed { index, contact ->
                    ContactRow(
                        contact = contact,
                        teal600 = teal600,
                        teal50 = teal50,
                        gray800 = gray800,
                        gray500 = gray500,
                        onDial = onDial
                    )

                    // border-b between rows (except last)
                    if (index != section.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            thickness = 1.dp,
                            color = gray200
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: Contact,
    teal600: Color,
    teal50: Color,
    gray800: Color,
    gray500: Color,
    onDial: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.label,
                color = gray800,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )

            contact.description?.takeIf { it.isNotBlank() }?.let { desc ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc, color = gray500, fontSize = 13.sp
                )
            }

            contact.phone?.let { phone ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = phone, color = gray500, fontSize = 13.sp
                )
            }
        }

        if (contact.phone != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(teal50)
                    .clickable { onDial(contact.phone) }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Call",
                    tint = teal600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun AddressBlock(
    teal600: Color, gray800: Color, gray500: Color, onDirections: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Address",
            color = teal600,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = teal600,
                        modifier = Modifier.size(22.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Rotary Ambala Cancer & General Hospital",
                            color = gray800,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ambala-Chandigarh Expy, Haryana 134007, India",
                            color = gray500,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDirections,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = teal600),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Directions,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Get Directions",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
