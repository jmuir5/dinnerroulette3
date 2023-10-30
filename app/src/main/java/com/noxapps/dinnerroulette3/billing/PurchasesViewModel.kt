package com.noxapps.dinnerroulette3.billing

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryProductDetailsParams
import okhttp3.internal.immutableListOf

class PurchasesViewModel: ViewModel() {
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
    val adRemoval = QueryProductDetailsParams.Product.newBuilder()
        .setProductId("ad_removal")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()

    val productList = immutableListOf(adRemoval, ic1, ic5, ic10, ic20, ic50, ic100)

}