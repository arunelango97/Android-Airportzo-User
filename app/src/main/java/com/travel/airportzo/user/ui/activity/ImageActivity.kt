package com.travel.airportzo.user.ui.activity

import android.os.Bundle
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.ImageActivityBinding
import com.travel.airportzo.user.ui.base.BaseActivity
import com.travel.airportzo.user.utils.setOnDebounceListener

class ImageActivity : BaseActivity() {

    private val imageActivity by lazy { ImageActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(imageActivity.root)


        imageActivity.personalBack.setOnDebounceListener {
            onBackPressed()
        }

        Glide.with(this).load(intent.getStringExtra("MessageImage")).into(imageActivity.image)

    }
}