//package com.example.alphabet.components
//
//import android.text.InputType
//import android.widget.EditText
//import androidx.compose.foundation.gestures.animateScrollBy
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.shape.ZeroCornerSize
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusState
//import androidx.compose.ui.focus.focusOrder
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.graphics.SolidColor
//import androidx.compose.ui.layout.RelocationRequester
//import androidx.compose.ui.layout.relocationRequester
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextLayoutResult
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.widget.doAfterTextChanged
//import com.example.alphabet.R
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//@ExperimentalComposeUiApi
//@Composable
//fun MyBasicTextField (
//    value: String,
//    onValueChange: (String) -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    readOnly: Boolean = false,
//    textStyle: TextStyle = TextStyle.Default,
//    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
//    keyboardActions: KeyboardActions = KeyboardActions.Default,
//    singleLine: Boolean = false,
//    maxLines: Int = Int.MAX_VALUE,
//    visualTransformation: VisualTransformation = VisualTransformation.None,
//    onTextLayout: (TextLayoutResult) -> Unit = {},
//    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
//    cursorBrush: Brush = SolidColor(Color.Black),
//    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
//        @Composable { innerTextField -> innerTextField() }
//) {
//    val relocationRequester = remember { RelocationRequester() }
//    val scope = rememberCoroutineScope()
////    AndroidView(
////        ::EditText,
////        modifier = modifier.fillMaxWidth().padding(vertical = 5.dp)
////    ) { v ->
////        v.setText(value)
////        v.setSelection(v.length())
////        v.setBackgroundResource(android.R.color.transparent)
////        v.hint = placeholder
////        v.textSize = textSize
////        v.doAfterTextChanged {
////            onValueChange(it.toString())
////        }
////        v.inputType = inputType
////    }
//    BasicTextField(
//        value = value,
//        onValueChange = onValueChange,
//        modifier = modifier
//            .padding(vertical = 10.dp)
//            .relocationRequester(relocationRequester)
//            .onFocusChanged {
//                if ("$it" == "Active") {
//                    scope.launch {
//                        delay(300)
//                        relocationRequester.bringIntoView()
//                    }
//                }
//            },
//        enabled = enabled,
//        readOnly = readOnly,
//        textStyle = textStyle,
//        keyboardOptions = keyboardOptions,
//        keyboardActions = keyboardActions,
//        singleLine = singleLine,
//        maxLines = maxLines,
//        visualTransformation = visualTransformation,
//        onTextLayout = onTextLayout,
//        interactionSource = interactionSource,
//        cursorBrush = cursorBrush,
//        decorationBox = decorationBox
//    )
//}
//
//@ExperimentalComposeUiApi
//@Preview
//@Composable
//fun PreviewTextField() {
//    MyBasicTextField(
//        value = "14",
//        onValueChange = {},
//    )
//}