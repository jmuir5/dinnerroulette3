package com.noxapps.dinnerroulette3.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietSelectDialog(
    stateValue: MutableState<Boolean>,
    options:List<DietPreset>,
    title: String,
    selected: MutableState<Long>,
    resetFlag: MutableState<Boolean>,
    editableFlag :Boolean = true
) {
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp),
                    ) {
                        if(editableFlag) {
                            item() {
                                Button(modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        resetFlag.value = true
                                        stateValue.value = false
                                    }) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            contentDescription = "New Preset"
                                        )

                                        Text("New Preset")
                                        Spacer(modifier = Modifier.size(1.dp))
                                    }
                                }
                            }
                        }
                        options.forEach {
                            item() {
                                Button(modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        selected.value = it.id
                                        stateValue.value = false
                                    }) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Article,
                                            contentDescription = it.name
                                        )
                                        Text(it.name)
                                        Spacer(modifier = Modifier.size(1.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.size(2.dp))
                            }
                        }
                    }
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
                        Text(text = "Cancel")
                    }
                }

            }

        }
    }
}