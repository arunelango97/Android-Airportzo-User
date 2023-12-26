package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.travel.airportzo.user.databinding.CancelpolicyFragmentBinding
import com.travel.airportzo.user.ui.base.BaseFragment


class CancelPolicyFragment : BaseFragment() {

    private val cancelPolicyFragment by lazy { CancelpolicyFragmentBinding.inflate(layoutInflater) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val cancel: String? = arguments?.getString("cancel")
        if (cancel != null) {
        cancelPolicyFragment.cancelPolicyWebView.webViewClient = WebViewClient()
        cancelPolicyFragment.cancelPolicyWebView.loadUrl(cancel)
        val webSettings = cancelPolicyFragment.cancelPolicyWebView.settings
        webSettings.javaScriptEnabled = true
        }*/

        val cancelUrl = "https://airportzo.net.in/web-app/cancellation_policy.php"
        cancelPolicyFragment.cancelPolicyWebView.webViewClient = WebViewClient()
        cancelPolicyFragment.cancelPolicyWebView.loadUrl(cancelUrl)
        val webSettings = cancelPolicyFragment.cancelPolicyWebView.settings
        webSettings.javaScriptEnabled = true

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return cancelPolicyFragment.root
    }


}