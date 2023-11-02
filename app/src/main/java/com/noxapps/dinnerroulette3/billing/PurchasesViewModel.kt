package com.noxapps.dinnerroulette3.billing

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import okhttp3.internal.immutableListOf

class PurchasesViewModel(billingClient:BillingClient): ViewModel() {


    val ic1 = QueryProductDetailsParams.Product.newBuilder()
        .setProductId("ic_1")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
    val ic5 = QueryProductDetailsParams.Product.newBuilder()
        .setProductId("ic_5")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
    val ic10 = QueryProductDetailsParams.Product.newBuilder()
        .setProductId("ic_10")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
    val ic20 = QueryProductDetailsParams.Product.newBuilder()
        .setProductId("ic_20")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
    val ic50 = QueryProductDetailsParams.Product.newBuilder()
        .setProductId("ic_50")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
    val ic100 = QueryProductDetailsParams.Product.newBuilder()
        .setProductId("ic_100")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
    val adRemovalProd = QueryProductDetailsParams.Product.newBuilder()
        .setProductId("ad_removal")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()

    val productListRaw = immutableListOf(adRemovalProd, ic1, ic5, ic10, ic20, ic50, ic100)

    val adRemoval = mutableStateListOf<ProductDetails>()
    val productList = mutableStateListOf<ProductDetails>()

    init {
        getProducts(
            inputList = productListRaw,
            billingClient = billingClient,
            mainList = productList,
            secondaryList = adRemoval
        )
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

    fun distinctify(list:SnapshotStateList<ProductDetails>){
        var previousId = ""
        list.forEach{
            if(it.productId==previousId){
                list.remove(it)
            }
            else{
                previousId=it.productId
            }
        }
    }

}