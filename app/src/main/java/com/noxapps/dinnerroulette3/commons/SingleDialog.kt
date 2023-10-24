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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.dp

/**
 * customised alert dialogue feturing a title, a text box and a cancel button. designed for
 * setting a string value
 * [stateValue] : mutable boolean declaring whether the dialogue should be shown
 * [title] : the title for the dialogue
 * [data] : the target string to change
 *
 * todo:
 * title text style
 * smooth out element placements
 * make background white / change based on current ui paradigm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleDialog(
    stateValue: MutableState<Boolean>,
    title: String,
    label: String,
    data: MutableState<String>,
    persistenceFlag:Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    var text = remember { mutableStateOf(TextFieldValue("")) }
    if(persistenceFlag)text.value = TextFieldValue(data.value)
    AlertDialog(
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
                    style = MaterialTheme.typography.titleLarge
                )
                Row() {
                    val maxChar = 17
                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    val text2 = text.value.text
                                    text.value = text.value.copy(
                                        selection = TextRange(0, text2.length)
                                    )
                                }
                            },
                        value = text.value,
                        onValueChange = {
                            if (it.text.length <= maxChar) text.value = it
                        },
                        //label = { Text(label) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (text.value.text.isNotEmpty()) {
                                    data.value = text.value.text
                                    stateValue.value = false
                                    text.value = TextFieldValue("")
                                }

                            }
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        stateValue.value = false
                        data.value = ""
                        text.value = TextFieldValue("")
                    }) {
                        Text(text = "Clear")
                    }
                    Button(onClick = {
                        if (text.value.text.isNotEmpty()) {
                            data.value = text.value.text
                            stateValue.value = false
                            text.value = TextFieldValue("")
                        }
                    }) {
                        Text(text = "Save")
                    }
                }

            }

        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}