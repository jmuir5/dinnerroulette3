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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.noxapps.dinnerroulette3.Paths


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdOrShopDialogue( thisState: MutableState<Boolean>, adState: MutableState<Boolean>, navController: NavController) {
    val title = "No Credits Available"
    val body = "Please purchase more Image Credits or you can watch an ad to generate an image " +
            "for this recipe"
    AlertDialog(
        onDismissRequest = {
            thisState.value=false
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
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(body)
                }
                Row(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                        navController.navigate(Paths.Billing.Path)
                    }) {
                        Text(text = "Go To Shop")
                    }

                }
                Row(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                        thisState.value = false
                        adState.value = true
                    }) {
                        Text(text = "Watch Ad")
                    }
                }
                Row(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                        thisState.value = false
                    }) {
                        Text(text = "Cancel")
                    }
                }
            }

        }
    }
}
