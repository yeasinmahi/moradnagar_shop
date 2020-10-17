package com.devfolder.moradnagarshop

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*

class MainActivity : AppCompatActivity() {
    private var tgwebView: WebView? = null
    var myAdView: AdView? = null
    var mInterstitialAd: InterstitialAd? = null
    private var interstitial: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CheckInternet()
        MobileAds.initialize(this, getString(R.string.app_id))
        BnrAdd()
    }

    fun BnrAdd() {
        myAdView = findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        myAdView!!.loadAd(adRequest)
    }

    fun prepareAd() {
        // Prepare the Interstitial Ad
        interstitial = InterstitialAd(this@MainActivity)
        // Insert the Ad Unit ID
        interstitial!!.adUnitId = getString(R.string.admob_interstitial_id)
        val adRequest = AdRequest.Builder().build()
        interstitial!!.loadAd(adRequest)
        // Prepare an Interstitial Ad Listener
        interstitial!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Call displayInterstitial() function
                displayInterstitial()
            }
        }
    }

    fun displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial!!.isLoaded) {
            interstitial!!.show()
        }
        /* ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (interstitial.isLoaded()) {
                         interstitial.show();
                        }
                       // PrepAd();
                    }
                });

            }
        }, 30, 30, TimeUnit.SECONDS);*/
    }

    override fun onBackPressed() {
        if (tgwebView!!.canGoBack()) {
            tgwebView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

    val isConnected: Boolean
        get() {
            val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (null != cm) {
                val info = cm.activeNetworkInfo
                return info != null && info.isConnected
            }
            return false
        }

    private fun CheckInternet() {
        val mWebClient: WebViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (isConnected) {
                    view.loadUrl(url)
                    if (url.contains("travelettesofbangladesh.com/blog") || url.contains("travelettesofbangladesh.com/videos") || url.contains(
                            "travelettesofbangladesh.com/event"
                        )
                    ) {
                        prepareAd()
                    }
                } else {
                    buildDialog(this@MainActivity).show()
                }
                return true
            }

            override fun onLoadResource(view: WebView, url: String) {}
        }
        //start checking internet connection
        if (!isConnected(this)) {
            buildDialog(this@MainActivity).show()
        } else {
            //Toast.makeText(this@MainActivity, "Welcome Travelettes!!", Toast.LENGTH_SHORT).show()
            // setContentView(R.layout.activity_main);
            tgwebView = findViewById<View>(R.id.webView) as WebView
            tgwebView!!.webViewClient = mWebClient
            val webSettings = tgwebView!!.settings
            webSettings.javaScriptEnabled = true
            tgwebView!!.loadUrl("https://muradnagarshop.com")
        }
        //end checking internet connection
    }

    fun buildDialog(c: Context?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(
            c!!
        )
        builder.setTitle("No Internet Connection")
        builder.setMessage("You need to have Mobile Data or WiFi to access this app.")
        builder.setPositiveButton(
            "Retry"
        ) { dialog, which -> CheckInternet() }
        builder.setOnDismissListener { CheckInternet() }
        return builder
    } //end internet connection dialogue

    companion object {
        //start internet connection dialogue
        fun isConnected(context: Context): Boolean {
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netinfo = cm.activeNetworkInfo
            return if (netinfo != null && netinfo.isConnectedOrConnecting) {
                val wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting
            } else {
                false
            }
        }
    }
}