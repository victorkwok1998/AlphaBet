package com.example.alphabet.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowItem(heading: String, body: String, icon: Int) {
    Row (verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(55.dp)
                .padding(horizontal = 15.dp)
        )
        Column {
            Text(
                heading,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
            )
            Text(
                body,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(top = 10.dp),
                lineHeight = 20.sp
            )
        }
    }
}