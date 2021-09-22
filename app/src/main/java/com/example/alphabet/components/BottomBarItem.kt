package com.example.alphabet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.BottomBarItem(icon: Int, text: String, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onClick, modifier = Modifier.fillMaxSize()) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Text(text = text, style = MaterialTheme.typography.subtitle1)
        }
    }
}