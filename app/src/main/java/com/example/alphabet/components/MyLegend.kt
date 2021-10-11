package com.example.alphabet.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.alphabet.R
import com.example.alphabet.plotColors
import com.example.alphabet.ui.theme.grayBackground
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun MyLegend(
    labels: List<String>,
    enabledLines: List<MutableState<Boolean>>
) {
    val context = LocalContext.current
    FlowRow(
        mainAxisSpacing = 5.dp,
        crossAxisSpacing = 5.dp
    ) {
        labels.forEachIndexed { index, s ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if(enabledLines[index].value) grayBackground else Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .clickable {
                            if (enabledLines
                                    .map { it.value }
                                    .count { it } != 1 || !enabledLines[index].value
                            )
                                enabledLines[index].value = !enabledLines[index].value
                            else
                                Toast
                                    .makeText(
                                        context,
                                        "At least one item should be selected",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_horizontal_rule_24),
                        contentDescription = null,
                        tint = colorResource(id = plotColors[index])
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(s, style = MaterialTheme.typography.subtitle1)
                }
            }
//            if (index < labels.lastIndex)
//                Spacer(modifier = Modifier.height(5.dp))
        }
    }
}