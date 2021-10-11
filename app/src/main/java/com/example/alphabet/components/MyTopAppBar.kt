package com.example.alphabet.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.alphabet.ui.theme.grayBackground

@Composable
fun MyTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = grayBackground,
    contentColor: Color = contentColorFor(backgroundColor),
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        elevation = 0.dp,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}

@Composable
fun MyTopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = grayBackground,
    contentColor: Color = contentColorFor(backgroundColor),
    contentPadding: PaddingValues = AppBarDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    TopAppBar(
        modifier = modifier,
        elevation = 0.dp,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        contentPadding = contentPadding,
        content = content
    )
}