package com.noxapps.dinnerroulette3.commons

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseDialogue(
    billingClient:BillingClient,
    product:ProductDetails,
    image: Painter,
    thisStateValue: MutableState<Boolean>,
    processingStateValue: MutableState<Boolean>
) {
    //val focusRequester = remember { FocusRequester()
    val activity = LocalContext.current as Activity

    AlertDialog(
        onDismissRequest = {
            thisStateValue.value = false
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
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Image(
                    painter = image,
                    contentDescription = product.title,
                    modifier = Modifier
                        .size((LocalConfiguration.current.screenWidthDp / 2.5).dp)
                        .padding(5.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = product.oneTimePurchaseOfferDetails!!.formattedPrice,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row {
                    Button(modifier = Modifier
                        .weight(1f),
                        onClick = {
                            val productDetailsParamsList = listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(product)
                                    .build()
                            )
                            val billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(productDetailsParamsList)
                                .build()
                            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                                thisStateValue.value=false
                                processingStateValue.value = true
                            }
                            Log.d("debug-billing result", billingResult.toString())
                        }
                    ) {
                        Text(
                            text = "Confirm Purchase",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            //focusRequester.requestFocus()
        }
    }
}