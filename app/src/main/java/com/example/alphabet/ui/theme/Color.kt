package com.example.alphabet.ui.theme

import androidx.compose.ui.graphics.Color

val grayBackground = Color(0xFFF5F5F5)
val amber500 = Color(0xFFFFC107)
val blue500 = Color(0xff2196F3)
val green500 = Color(0xFF4CAF50)
val red500 = Color(0xFFF44336)
val deepPurple500 = Color(0xff673AB7)
val lightblue200 = Color(0xff81D4FA)

fun Color.toInt(): Int {
    return android.graphics.Color.rgb(this.red, this.green, this.blue)
}