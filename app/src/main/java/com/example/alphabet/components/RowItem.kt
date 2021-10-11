package com.example.alphabet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowItem(
    heading: String,
    body: String,
    icon: Int? = null,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Row (verticalAlignment = Alignment.CenterVertically) {
        icon?.apply {
            Icon(
                painterResource(id = this),
                contentDescription = null,
                modifier = Modifier
                    .size(55.dp)
                    .padding(horizontal = 15.dp),
                tint = tint
            )
        }
        Column {
            Text(
                heading,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
            )
            Text(
                body,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(top = 5.dp),
                lineHeight = 20.sp
            )
        }
    }
}