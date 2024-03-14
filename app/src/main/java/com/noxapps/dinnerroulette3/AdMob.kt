package com.noxapps.dinnerroulette3

import android.app.Activity
import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.noxapps.dinnerroulette3.commons.Indicator
import com.noxapps.dinnerroulette3.commons.addImageCredits
import com.noxapps.dinnerroulette3.gpt.getImage
import com.noxapps.dinnerroulette3.gpt.saveImage
import com.noxapps.dinnerroulette3.recipe.SavedRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdMob {

}
@Composable
fun AdmobBanner(modifier: Modifier = Modifier, reference:String) {
    Column {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                // on below line specifying ad view.
                AdView(context).apply {
                    // on below line specifying ad size
                    //adSize = AdSize.BANNER
                    // on below line specifying ad unit id
                    // currently added a test ad unit id.
                    setAdSize(AdSize.BANNER)
                    adUnitId = reference//"ca-app-pub-3830795303099315/4827852167"
                    // calling load ad to load our ad.
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
fun loadInterstitialAd(context: Context, mInterstitialAd: MutableState<InterstitialAd?>, TAG:String, reference:String){
    var adRequest = AdRequest.Builder().build()

    InterstitialAd.load(context,reference, adRequest, object : InterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.d("ad Error", adError.toString())
            mInterstitialAd.value = null
        }

        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            Log.d(TAG, "Ad was loaded.")
            interstitialAd.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    mInterstitialAd.value = null
                }

                //override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                // Called when ad fails to show.
                //    Log.e(TAG, "Ad failed to show fullscreen content.")
                //    mInterstitialAd.value = null
                //}

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }
            mInterstitialAd.value = interstitialAd

        }
    })
}

fun loadRewardedAd(context: Context, mRewardedAd: MutableState<RewardedAd?>, TAG:String){
    val adRequest = AdRequest.Builder().build()
    val adId = if(BuildConfig.DEBUG){
        context.getString(R.string.test_rewarded_ad_id)
    }
    else {
        context.getString(R.string.rewarded_ad_id)
    }
    RewardedAd.load(
        context,
        adId,
        adRequest,
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("ad Error", adError.toString())
                mRewardedAd.value = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                rewardedAd.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        Log.d(TAG, "Ad dismissed fullscreen content.")
                        mRewardedAd.value = null
                    }

                    //override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    // Called when ad fails to show.
                    //    Log.e(TAG, "Ad failed to show fullscreen content.")
                    //    mInterstitialAd.value = null
                    //}

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad showed fullscreen content.")
                    }
                }
                mRewardedAd.value = rewardedAd

            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardedAdFrame(
    mRewardedAd:MutableState<RewardedAd?>,
    context:Context,
    imageFlag: MutableState<Boolean>,
    imageFlag2: MutableState<Boolean>,
    thisRecipe: SavedRecipe,
    displayFlag:MutableState<Boolean>,
    navController:NavHostController
){
    var i by remember{mutableStateOf(0)}
    val scope = rememberCoroutineScope()
    if (mRewardedAd.value!= null) {
        i=5
    }

    if(i<5) {
        loadRewardedAd(context, mRewardedAd, "recipe Image Rewarded")
        LaunchedEffect(true) {
            for (j in 0..5) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                MainScope().launch { i += 1 }

                if (mRewardedAd.value != null) {
                    i = 5
                    break
                }


            }
        }

        AlertDialog(
            onDismissRequest = {

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
                            "Please Wait",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Indicator()
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Attempting to Load Ad (${i+1})")
                    }
                }
            }
        }
    }

    else if (mRewardedAd.value != null) {
        mRewardedAd.value?.let { ad ->
            ad.show(context as Activity, OnUserEarnedRewardListener { rewardItem ->
                // Handle the reward.
                displayFlag.value = false
                imageFlag.value = true
                getImage(
                    thisRecipe.imageDescription!!,
                    context,
                    errorCallback = {
                        MainScope().launch {
                            navController.navigate(Paths.Error.Path+"/"+it+"you have been awarded an image credit in compensation")
                            addImageCredits(context, 1)
                        }
                    }
                ) {
                    saveImage(context, thisRecipe, it.data[0].url) { it2 ->
                        imageFlag2.value = it2
                    }
                }
            })
        } ?: run {
            Log.d("rewarded ad", "The rewarded ad wasn't ready yet.")
        }
    } else {
        AlertDialog(
            onDismissRequest = {
                displayFlag.value = false
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
                            "Ad Failed",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("unable to load Advertisement, please try again later")
                    }
                    Button(onClick = { displayFlag.value = false }) {
                        Text(text = "Return")

                    }
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterstitialAdDialogue(
    mInterstitialAd:MutableState<InterstitialAd?>,
    context:Context,
    displayFlag:MutableState<Boolean>,
    function:()->Unit
){
    var i by remember{mutableStateOf(0)}
    val scope = rememberCoroutineScope()

    if (mInterstitialAd.value!= null) {
        mInterstitialAd.value!!.show(context as Activity)
        i=5
    }

    if (i<5){
        LaunchedEffect(true){
            scope.launch{
                for (j in 0..5) {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(1000)
                        MainScope().launch { i += 1 }
                    }
                    if (mInterstitialAd.value!= null) {
                        mInterstitialAd.value!!.show(context as Activity)
                        i=5
                        break
                    }
                }
            }
        }

        AlertDialog(
            onDismissRequest = {

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
                    modifier= Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(10.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Please Wait",
                            style = MaterialTheme.typography.titleLarge)
                    }
                    Row(modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {
                        Indicator()
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Attempting to Load Ad (${i+1})")
                    }
                }
            }
        }
    }
    else {
        function()
        displayFlag.value = false
    }



}