package com.example.alphabet.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.RelocationRequester
import androidx.compose.ui.layout.relocationRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphabet.R
import com.example.alphabet.ui.theme.grayBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//@Composable
//fun MyTextFieldLayout(
//    textField: @Composable () -> Unit,
//    modifier: Modifier = Modifier,
//    leadingIcon: @Composable (() -> Unit)? = null,
//    trailingIcon: @Composable (() -> Unit)? = null,
//) {
//    Surface(
//        color = grayBackground,
//        shape = RoundedCornerShape(20.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Row(
//            modifier = modifier.padding(horizontal = 10.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if (leadingIcon != null) {
//                Column(
//                    Modifier.size(40.dp),
//                    verticalArrangement = Arrangement.Center
//                ) { leadingIcon() }
//            }
//            Column(Modifier.weight(1f)) {
//                textField()
//            }
//            if (trailingIcon != null) {
//                Column(
//                    Modifier.size(40.dp),
//                    verticalArrangement = Arrangement.Center
//                ) { trailingIcon() }
//            }
//        }
//    }
//}

@ExperimentalComposeUiApi
@Composable
fun FixedTextField (
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
) {
    val relocationRequester = remember { RelocationRequester() }
    val scope = rememberCoroutineScope()
//    AndroidView(
//        ::EditText,
//        modifier = modifier.fillMaxWidth().padding(vertical = 5.dp)
//    ) { v ->
//        v.setText(value)
//        v.setSelection(v.length())
//        v.setBackgroundResource(android.R.color.transparent)
//        v.hint = placeholder
//        v.textSize = textSize
//        v.doAfterTextChanged {
//            onValueChange(it.toString())
//        }
//        v.inputType = inputType
//    }
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .relocationRequester(relocationRequester)
            .onFocusEvent {
                if (it.isFocused) {
                    scope.launch {
                        delay(200)
                        relocationRequester.bringIntoView()
                    }
                }
            },
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label,
        placeholder = placeholder,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

@ExperimentalComposeUiApi
@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = grayBackground,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
) {
    FixedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(20.dp),
        colors = colors
    )
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewMyTextField() {
    MyTextField(value = "", onValueChange = {}, placeholder = { Text("Enter a Symbol") })
}