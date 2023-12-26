package com.travel.airportzo.user.webView

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.travel.airportzo.user.databinding.WebviewBinding


class WebView : AppCompatActivity() {

    private val webActivity  by lazy { WebviewBinding.inflate(layoutInflater) }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(webActivity.root)

        webActivity.backImage.setOnClickListener {
            onBackPressed()
        }


        val url = intent.getStringExtra("URL")!!
        val title = intent.getStringExtra("heading")!!
        Log.d("web_view", "URL: $url")
        webActivity.bookingDetail.text = title
        webActivity.webView.webViewClient = WebViewClient()
        webActivity.webView.loadUrl(url)

        val webSettings = webActivity.webView.settings
        webSettings.javaScriptEnabled = true
    }

}