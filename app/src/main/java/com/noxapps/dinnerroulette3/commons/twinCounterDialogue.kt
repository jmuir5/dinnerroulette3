package com.noxapps.dinnerroulette3.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noxapps.dinnerroulette3.ui.theme.AppTheme

/**
 * customised alert dialogue feturing a title, a text box and a cancel button. designed for
 * setting a string value
 * [stateValue] : mutable boolean declaring whether the dialogue should be shown
 * [title] : the title for the dialogue
 * [data1] : the target string to change
 *
 * todo:
 * title text style
 * smooth out element placements
 * make background white / change based on current ui paradigm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwinCounterDialog(
    stateValue: MutableState<Boolean>,
    title: String,
    label1: String,
    data1: MutableIntState,
    label2: String,
    data2: MutableIntState
) {

    val initialData1Value by remember { mutableStateOf( data1.intValue) }
    val initialData2Value by remember { mutableStateOf( data2.intValue) }
    BasicAlertDialog(
        onDismissRequest = {
            stateValue.value = false
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation

        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = label1,
                    style = MaterialTheme.typography.titleMedium
                )
                Counter(data = data1, min = 0, max = 10)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = label2,
                    style = MaterialTheme.typography.titleMedium
                )
                Counter(data = data2, min = 0, max = 10)
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        stateValue.value = false
                        data1.intValue = initialData1Value
                        data2.intValue = initialData2Value
                    }) {
                        Text(text = "Cancel")
                    }
                    Button(onClick = {
                        stateValue.value = false
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun Counter(data: MutableIntState, min:Int = Int.MIN_VALUE, max:Int = Int.MAX_VALUE){
    Row(modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically){
        Button(
            enabled = data.intValue>min,
            onClick = {data.intValue-=1}) {
            Text("-")
        }
        Text(
            modifier = Modifier.padding(10.dp,0.dp),
            text = data.intValue.toString(),
            style = MaterialTheme.typography.headlineMedium)
        Button(
            enabled = data.intValue<max,
            onClick = {data.intValue+=1}) {
            Text("+")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TwinCounterDialoguePreview() {
    AppTheme {
        val state = remember{ mutableStateOf(true) }
        val title = "Title"
        val label1 = "Label 1"
        val label2 = "Label 2"
        val data1 = remember{ mutableIntStateOf(0) }
        val data2 = remember{ mutableIntStateOf(0) }


        Column {
            Button(onClick = {state.value=true}) {
                Text(data1.intValue.toString()+"/"+data2.intValue.toString())
            }
            if (state.value) {
                TwinCounterDialog(
                    stateValue = state,
                    title = title,
                    label1 = label1,
                    data1 = data1,
                    label2 = label2,
                    data2 = data2
                )
            }
        }
    }
}