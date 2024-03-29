package com.noxapps.dinnerroulette3.billing

import android.content.Context
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.preferences.core.edit
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import com.noxapps.dinnerroulette3.adFlag
import com.noxapps.dinnerroulette3.commons.Indicator
import com.noxapps.dinnerroulette3.commons.ProcessingDialog
import com.noxapps.dinnerroulette3.commons.addImageCredits
import com.noxapps.dinnerroulette3.commons.getPurchaseFlag
import com.noxapps.dinnerroulette3.commons.setPurchaseFlag
import com.noxapps.dinnerroulette3.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillingWrapper(context: Context, scope: CoroutineScope) {

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener{ billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                setPurchaseFlag(context, 0)
                //Log.d("debug-Billing","successfull purchase")
                //Log.d("debug-Billing size",purchases.size.toString())

                for (purchase in purchases) {
                    //Log.d("debug-Billing",purchase.products.toString())
                    if (!purchase.isAcknowledged) {
                        if (purchase.purchaseState == 1) {
                            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)
                                .build()
                            val acknowledgePurchaseResponseListener =
                                AcknowledgePurchaseResponseListener() {
                                    //Log.d("purchase acknowledged", "purchase acknowledged")
                                }
                            billingClient.acknowledgePurchase(
                                acknowledgePurchaseParams,
                                acknowledgePurchaseResponseListener
                            )

                            purchase.products.forEach(){
                                if(it!="ad_removal"){
                                    scope.launch{
                                        consumeProduct(purchase)
                                    }


                                }

                                when(it){
                                    "ic_1"->addImageCredits(context, 5)
                                    "ic_5"->addImageCredits(context, 25)
                                    "ic_10"->addImageCredits(context, 50)
                                    "ic_20"->addImageCredits(context, 100)
                                    "ic_50"->addImageCredits(context, 250)
                                    "ic_100"-> addImageCredits(context, 500)
                                    "ad_removal"->{
                                        scope.launch {
                                            context.dataStore.edit { settings ->
                                                settings[adFlag] = false
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }
                    //handlePurchase(purchase)

                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                setPurchaseFlag(context, 1)
                //Log.d("debug-Billing","purchase cancelled"

                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                setPurchaseFlag(context, 1)
                //Log.d("debug-Billing","failed purchase")

                // Handle any other error codes.
            }
        }
    fun initBilling(context: Context):BillingClient{
        var billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    Log.d("debug-Billing", "setup finished")
                    val params = QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                    val listener = PurchasesResponseListener(){ result, purchases->
                        Log.d("debug-billing purchases", result.toString())

                        Log.d("debug-billing purchases size", purchases.size.toString())
                        for(purchase in purchases){
                            purchase.products.forEach{
                                if(it!="ad_removal"){
                                    val consumeParams =
                                        ConsumeParams.newBuilder()
                                            .setPurchaseToken(purchase.purchaseToken)
                                            .build()
                                    val consumeResponseListener = ConsumeResponseListener(){_1, _2->
                                    }

                                    billingClient.consumeAsync(consumeParams, consumeResponseListener)
                                }
                            }

                            //Log.d("debug-billing consume", consumeResult.toString())
                        }

                    }
                    billingClient.queryPurchasesAsync(params, listener)
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d("debug-Billing", "billing disconected")
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })



        return billingClient
    }

    val billingClient = initBilling(context)


    suspend fun consumeProduct(purchase:Purchase, scope : CoroutineScope = CoroutineScope(Dispatchers.IO)){
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
        }
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        val consumeResponseListener = ConsumeResponseListener(){billingResult, string->
            Log.d("debug-billing consumeResponse1", billingResult.toString())
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK){
                scope.launch {
                    consumeProduct(purchase)
                }
            }
        }

        billingClient.consumeAsync(consumeParams, consumeResponseListener)
    }


}




