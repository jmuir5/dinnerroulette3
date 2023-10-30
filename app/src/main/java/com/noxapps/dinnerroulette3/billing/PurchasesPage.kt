package com.noxapps.dinnerroulette3.billing

import android.graphics.BitmapFactory
import android.text.style.TabStopSpan.Standard
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.noxapps.dinnerroulette3.commons.PurchaseDialogue
import com.noxapps.dinnerroulette3.home.HomeViewModel
import okhttp3.internal.immutableListOf

@Composable
fun PurchasesPage(
    viewModel: PurchasesViewModel = PurchasesViewModel(),
    billingClient: BillingClient,
    navController: NavHostController,
) {
    StandardScaffold(tabt = "Shop", navController = navController, adFlag = false) {
        val context = LocalContext.current
        val loadedFlag = remember { mutableStateOf(false) }
        val productList = remember{mutableStateListOf<ProductDetails>()}


        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image_credit)
        val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap()) }


        val purchaseCardSource = remember{ mutableStateOf("dummyProduct") }
        val purchaseCardFlag = remember{ mutableStateOf(false) }

        if(!loadedFlag.value) {
            val queryProductDetailsParams = QueryProductDetailsParams
                .newBuilder()
                .setProductList(
                    viewModel.productList
                )
                .build()
            billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                productDetailsList.forEach {
                    productList.add(it)
                }
            }
            loadedFlag.value=true
        }

        LazyColumn {
            productList.forEach() {
                item(){
                    ProductCard(it, painter, purchaseCardSource, purchaseCardFlag)
                }
            }
        }
        if(purchaseCardFlag.value){
            productList.forEach{
                if(purchaseCardSource.value == it.productId){
                    PurchaseDialogue(billingClient, it, painter, purchaseCardFlag)
                }
            }
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
        .clickable{
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
                .size((LocalConfiguration.current.screenWidthDp/5).dp)
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