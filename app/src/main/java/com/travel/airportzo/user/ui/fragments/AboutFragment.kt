package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.AboutFragmentBinding
import com.travel.airportzo.user.databinding.PoliciesFragmentBinding
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.setOnDebounceListener


class AboutFragment : BaseFragment() {

    private val aboutFragment by lazy { AboutFragmentBinding.inflate(layoutInflater) }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** brand color */

        aboutFragment.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        aboutFragment.personalBack.setOnDebounceListener {
            findNavController().popBackStack()
        }

        val aboutUs = "https://airportzo.net.in/web-app/about-us.php"
        aboutFragment.aboutWebView.webViewClient = WebViewClient()
        aboutFragment.aboutWebView.loadUrl(aboutUs)
        val webSettings = aboutFragment.aboutWebView.settings
        webSettings.javaScriptEnabled = true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return aboutFragment.root
    }

}