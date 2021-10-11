package com.example.alphabet.components

import android.widget.ScrollView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import com.example.alphabet.ui.theme.grayBackground

@Composable
fun MyScrollView(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    AndroidView(
        ::ScrollView,
        modifier = Modifier.fillMaxSize().background(grayBackground)
    ) {
        it.addView(
            ComposeView(context).apply {
                setContent {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    content()
                }
            }
        )
    }
}