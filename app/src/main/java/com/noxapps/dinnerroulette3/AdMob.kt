package com.noxapps.dinnerroulette3

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

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
    var adRequest = AdRequest.Builder().build()

    RewardedAd.load(
        context,
        context.getString(R.string.rewarded_ad_id),
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