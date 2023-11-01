package com.noxapps.dinnerroulette3.billing

import android.graphics.BitmapFactory
import android.text.style.TabStopSpan.Standard
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.StandardScaffold
import com.noxapps.dinnerroulette3.commons.ProcessingDialog
import com.noxapps.dinnerroulette3.commons.PurchaseDialogue
import com.noxapps.dinnerroulette3.commons.SimpleTextDialogue
import com.noxapps.dinnerroulette3.commons.getAdFlag
import com.noxapps.dinnerroulette3.commons.getImageCredits
import com.noxapps.dinnerroulette3.commons.getPurchaseFlag
import com.noxapps.dinnerroulette3.commons.setPurchaseFlag
import com.noxapps.dinnerroulette3.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.immutableListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesPage(
    viewModel: PurchasesViewModel = PurchasesViewModel(),
    billingClient: BillingClient,
    navController: NavHostController,
) {
    StandardScaffold(tabt = "Shop", navController = navController, adFlag = false) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val loadedFlag = remember { mutableStateOf(false) }
        val adRemoval = remember { mutableStateListOf<ProductDetails>() }
        val productList = remember{mutableStateListOf<ProductDetails>()}


        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image_credit)
        val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap()) }

        val adsBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.remove_ads)
        val adsPainter = remember{ BitmapPainter(image = adsBitmap.asImageBitmap()) }


        val purchaseCardSource = remember{ mutableStateOf("dummyProduct") }
        val purchaseCardFlag = remember{ mutableStateOf(false) }
        var purchaseTitle by remember{ mutableStateOf("dummyProduct") }

        val processingFlag = remember{mutableStateOf(false)}
        val processingFlag2 = remember{ derivedStateOf { productList.isEmpty() }}

        val successFlag = remember{mutableStateOf(false)}
        val failureFlag = remember{mutableStateOf(false)}

        /*if (!loadedFlag.value){
            Log.d("debig", billingClient.isReady.toString())
            getProducts(
                inputList = viewModel.productList,
                billingClient = billingClient,
                mainList = productList,
                secondaryList = adRemoval
            )
            loadedFlag.value=true
        }


         */

        LaunchedEffect(!loadedFlag.value){
            Thread.sleep(100)
            MainScope().launch {
                getProducts(
                    inputList = viewModel.productList,
                    billingClient = billingClient,
                    mainList = productList,
                    secondaryList = adRemoval
                )
                loadedFlag.value = true
            }

        }
        if(loadedFlag.value) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                //.padding(24.dp, 0.dp)
            ) {
                item() {
                    Text(
                        text = "Image Credits:",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(24.dp, 10.dp)
                    )
                }
                if (productList.isNotEmpty()) {
                    productList.forEach() {
                        item() {
                            ProductCard(it, painter, purchaseCardSource, purchaseCardFlag)
                        }
                    }
                }
                if (getAdFlag(context)) {
                    item() {
                        Text(
                            text = "Other:",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(24.dp, 10.dp)
                        )
                    }
                    adRemoval.forEach() {
                        item() {
                            ProductCard(it, painter, purchaseCardSource, purchaseCardFlag)
                        }
                    }
                }
                item() {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
        if(purchaseCardFlag.value){
            productList.forEach{
                if(purchaseCardSource.value == it.productId){
                    purchaseTitle = it.name
                    PurchaseDialogue(billingClient, it, adsPainter, purchaseCardFlag, processingFlag)
                }
            }
        }
        if(processingFlag.value){
            ProcessingDialog("Processing your purchase")
            LaunchedEffect(true){
                scope.launch{
                    while(successFlag.value==failureFlag.value && !successFlag.value) {
                        withContext(Dispatchers.IO) {
                            Thread.sleep(1000)
                        }
                        when(getPurchaseFlag(context)){
                            0->{
                                successFlag.value = true
                                processingFlag.value = false
                            }
                            1->{
                                failureFlag.value = true
                                processingFlag.value = false
                            }
                        }
                    }
                }
            }
        }
        if(processingFlag2.value) {
            ProcessingDialog("Pulling available Products")
        }
        if(successFlag.value){
            setPurchaseFlag(context, -1)
            val successTitle = "Purchase Successful:"
            var successBody = "Your purchase of $purchaseTitle was successfull!"
            if (purchaseCardSource.value == "ad_Removal"){
                successBody+=" Ads have been removed from Chef Roulette. Please note, this does " +
                        "not include ads for the purpose of generating images."
            }
            else{
                successBody +=" Your new Image Credit balance is ${getImageCredits(context)}."
            }
            SimpleTextDialogue(title = successTitle, body = successBody, successFlag)
        }
        if(failureFlag.value){
            setPurchaseFlag(context, -1)
            val failureTitle = "Purchase Failed:"
            var failureBody = "Your purchase of $purchaseTitle failed."
            SimpleTextDialogue(title = failureTitle, body = failureBody, failureFlag)
        }
    }

}


@Composable
fun ProductCard(
    product:ProductDetails,
    image: Painter,
    source: MutableState<String>,
    miniFlag:MutableState<Boolean>
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(24.dp, 5.dp)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(15.dp)
        )
        .clip(RoundedCornerShape(15.dp))
        .clickable {
            source.value = product.productId
            miniFlag.value = true
        },
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Image(
            painter = image,
            contentDescription = product.title,
            modifier = Modifier
                .size((LocalConfiguration.current.screenWidthDp / 5).dp)
                .padding(5.dp),
            contentScale = ContentScale.Fit
        )
        Column(modifier = Modifier
            .weight(1f)
            .padding(5.dp)
        ){
            Row(modifier = Modifier){
                Text(modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium)
                Text(modifier = Modifier
                        .padding(5.dp),
                    text = product.oneTimePurchaseOfferDetails!!.formattedPrice,
                    style = MaterialTheme.typography.titleMedium)
            }
            Text(modifier = Modifier
                    .padding(5.dp),
                text = product.description,
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun getProducts(
    inputList: List<QueryProductDetailsParams.Product>,
    billingClient: BillingClient,
    mainList: SnapshotStateList<ProductDetails>,
    secondaryList: SnapshotStateList<ProductDetails>

){
    mainList.clear()
    secondaryList.clear()
    val queryProductDetailsParams = QueryProductDetailsParams
        .newBuilder()
        .setProductList(
            inputList
        )
        .build()
    billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->

        productDetailsList.forEach {
            if(it.productId=="ad_removal")
                secondaryList.add(it)
            else{
                mainList.add(it)
            }
        }
        mainList.sortBy { it.oneTimePurchaseOfferDetails!!.priceAmountMicros }
    }
}