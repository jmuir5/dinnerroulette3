package com.noxapps.dinnerroulette3.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.StandardScaffold
import com.noxapps.dinnerroulette3.input.ProcessingDialog

@Composable
fun RedeemCode(
    viewModel: RedeemViewModel = RedeemViewModel(),
    navController: NavHostController
) {
    StandardScaffold(tabt = "Request Recipe", navController = navController) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        val focusRequester = remember { FocusRequester() }
        var promptText by remember { mutableStateOf("") }

        val placeholder = ""

        var errorState by remember { mutableStateOf(0) }

        var processing = remember { mutableStateOf(false) }

        val genTextDialogue = remember { mutableStateOf(false) }
        val dialogueTitle = remember { mutableStateOf("") }
        val dialogueBody = remember { mutableStateOf("") }

        val primaryOrange = MaterialTheme.colorScheme.primary


        Column(
            Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Redeem Promo Code",
                style = MaterialTheme.typography.headlineLarge)
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .drawBehind {
                        val borderSize = 4.dp.toPx()
                        drawLine(
                            color = primaryOrange,
                            start = Offset(12f, size.height),
                            end = Offset(size.width - 12f, size.height),
                            strokeWidth = borderSize
                        )
                    },
                placeholder = { Text(placeholder) },
                value = promptText,
                onValueChange = {
                    if (promptText.length <= 60) promptText = it
                    else {
                        if (promptText.length >= it.length) promptText = it
                    }
                },
                label = {
                    if (errorState == 1)
                        Text(
                            "Please enter at least 3 characters",
                            color = Color.Red
                        )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (promptText.isEmpty()) errorState = 1
                        else {
                            errorState = 0
                            viewModel.validateCode(
                                code = promptText,
                                title = dialogueTitle,
                                body = dialogueBody,
                                processingState = processing,
                                dialogueState = genTextDialogue,
                                context = context,
                                scope = scope
                            )
                            //todo validate code
                        }
                    }
                )
            )
            Box(
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center){

                Button(
                    modifier = Modifier,
                    onClick = {
                        if (promptText.isEmpty()) errorState = 1
                        else {
                            errorState = 0
                            viewModel.validateCode(
                                code = promptText,
                                title = dialogueTitle,
                                body = dialogueBody,
                                processingState = processing,
                                dialogueState = genTextDialogue,
                                context = context,
                                scope = scope
                            )
                        }
                    },

                ) {
                    Text(text = "Redeem Code",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
        if (processing.value) {
            ProcessingDialog()
        }
        if (genTextDialogue.value){
            GenericTextDialogue(genTextDialogue, dialogueTitle.value, dialogueBody.value)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericTextDialogue(
    stateValue: MutableState<Boolean>,
    title:String,
    body:String
){
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
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(body)
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        stateValue.value = false
                    }) {
                        Text(text = "Return")
                    }
                }

            }
        }
    }
}



