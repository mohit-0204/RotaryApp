package com.rotary.hospital.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary

@Composable
fun CustomKeyboard(
    onNumberClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keys = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9),
        listOf(-2, 0, -1) // -2 = tick (✔), -1 = backspace
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // spacing between buttons
            ) {
                row.forEach { key ->
                    val label = when (key) {
                        -1 -> "⌫"
                        -2 -> "✔"
                        else -> key.toString()
                    }
                    val onClick = when (key) {
                        -1 -> onBackspaceClick
                        -2 -> onDoneClick
                        else -> { { onNumberClick(key) } }
                    }

                    KeyboardButton(
                        onClick = onClick,
                        label = label,
                        modifier = Modifier
                            .weight(1f)       // equal width for all buttons in row
                            .height(48.dp)    // fixed height (rectangular)
                    )
                }
            }
        }
    }
}



@Composable
fun KeyboardButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = ColorPrimary
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = label,
            fontSize = 22.sp
        )
    }
}
