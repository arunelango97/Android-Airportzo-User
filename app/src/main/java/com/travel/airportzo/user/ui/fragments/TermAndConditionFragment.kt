package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.travel.airportzo.user.databinding.TermandconditionFragmentBinding
import com.travel.airportzo.user.ui.base.BaseFragment

class TermAndConditionFragment : BaseFragment() {

    private val termAndConditionFragment by lazy { TermandconditionFragmentBinding.inflate(layoutInflater) }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


       /* val termsCondition:String? = arguments?.getString("terms")
        if (termsCondition != null) {
            termAndConditionFragment.termsWebView.webViewClient = WebViewClient()
            termAndConditionFragment.termsWebView.loadUrl(termsCondition)
            val webSettings = termAndConditionFragment.termsWebView.settings
            webSettings.javaScriptEnabled = true
        }*/

        val termUrl = "https://airportzo.net.in/web-app/terms.php"

        termAndConditionFragment.termsWebView.webViewClient = WebViewClient()
        termAndConditionFragment.termsWebView.loadUrl(termUrl)
        val webSettings = termAndConditionFragment.termsWebView.settings
        webSettings.javaScriptEnabled = true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return termAndConditionFragment.root
    }

}