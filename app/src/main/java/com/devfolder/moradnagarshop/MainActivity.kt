package com.devfolder.moradnagarshop

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*

class MainActivity : AppCompatActivity() {
    private var webView: WebView? = null
    private var myAdView: AdView? = null
    private var interstitial: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.checkInternet()
        MobileAds.initialize(this)
        loadBannerAd()
    }

    private fun loadBannerAd() {
        myAdView = findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        myAdView!!.loadAd(adRequest)
    }

    fun loadInterstialAd() {
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
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

    val isConnected: Boolean
        get() {
            val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var n = cm.activeNetwork
                if (n != null) {
                    var nc = cm.getNetworkCapabilities(n)
                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    ));
                }
            } else {
                var ni = cm.getActiveNetworkInfo();
                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            }
            return false
        }


    private fun checkInternet() {
        val mWebClient: WebViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (isConnected) {
                    view.loadUrl(url)
                    if (url.contains(getString(R.string.interstitial_url_1)) || url.contains(getString(
                                                R.string.interstitial_url_2))) {
                        loadInterstialAd()
                    }
                } else {
                    buildDialog(this@MainActivity).show()
                }
                return true
            }

            override fun onLoadResource(view: WebView, url: String) {}
        }
        //start checking internet connection
        if (!isConnected) {
            buildDialog(this@MainActivity).show()
        } else {
            webView = findViewById<View>(R.id.webView) as WebView
            webView!!.webViewClient = mWebClient
            val webSettings = webView!!.settings
            webSettings.javaScriptEnabled = true
            webView!!.loadUrl(getString(R.string.url))
        }
        //end checking internet connection
    }

    fun buildDialog(c: Context?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(
            c!!
        )
        builder.setTitle(getString(R.string.no_internet_title))
        builder.setMessage(getString(R.string.no_internet_message))
        builder.setPositiveButton(
            "Retry"
        ) { _, _ -> checkInternet() }
        builder.setOnDismissListener {
            checkInternet()
        }
        return builder
    }


}