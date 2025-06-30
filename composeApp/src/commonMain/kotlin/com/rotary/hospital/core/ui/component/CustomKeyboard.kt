package com.rotary.hospital.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.GrayWhite
import com.rotary.hospital.core.theme.White

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
        listOf(-1, 0, -2) // -1 for backspace & -2 for enter
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    when (key) {
                        -2 -> ElevatedButton(
                            onClick = onDoneClick,
                            modifier = Modifier.size(height = 54.dp, width = 64.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GrayWhite)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✔",
                                    color = ColorPrimary,
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        -1 -> ElevatedButton(
                            onClick = onBackspaceClick,
                            modifier = Modifier.size(height = 54.dp, width = 64.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GrayWhite)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⌫",
                                    color = ColorPrimary,
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        else -> ElevatedButton (
                            onClick = { onNumberClick(key) },
                            modifier = Modifier.size(height = 54.dp, width = 64.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GrayWhite)
                        ) {
                            Text(key.toString(), color = ColorPrimary, fontSize = 24.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}