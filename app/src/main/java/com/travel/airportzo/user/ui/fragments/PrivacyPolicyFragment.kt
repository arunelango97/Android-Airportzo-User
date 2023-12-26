package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.travel.airportzo.user.databinding.PrivacypolicyFragmentBinding
import com.travel.airportzo.user.ui.base.BaseFragment


class PrivacyPolicyFragment : BaseFragment() {

    private val privacyPolicyFragment by lazy { PrivacypolicyFragmentBinding.inflate(layoutInflater) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val policy: String? = arguments?.getString("policy")
        val policy = "https://airportzo.net.in/web-app/privacy_policy"
        privacyPolicyFragment.privacyPolicyWebView.webViewClient = WebViewClient()
        privacyPolicyFragment.privacyPolicyWebView.loadUrl(policy)
        val webSettings = privacyPolicyFragment.privacyPolicyWebView.settings
        webSettings.javaScriptEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return privacyPolicyFragment.root
    }

}