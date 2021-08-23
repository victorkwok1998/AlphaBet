package com.example.alphabet.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CircularIndeterminateProgressBar(isDisplayed: Boolean = true, text: String = "") {
    if (isDisplayed) {
        Column(
            Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            if (text.isNotEmpty()){
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = text, style = MaterialTheme.typography.body1, color = Color.Gray)
            }
        }
    }
}