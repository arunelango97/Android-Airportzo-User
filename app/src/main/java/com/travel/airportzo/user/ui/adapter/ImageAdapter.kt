package com.travel.airportzo.user.ui.adapter

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.io.File

object ImageAdapter {
    @BindingAdapter("loadAddImage")
    @JvmStatic
    fun loadAddImage(view: ImageView, url: String?){
        Glide.with(view.context).load(url).apply(RequestOptions().transform( RoundedCorners(8))).into(view)
    }

    @BindingAdapter("loadImageUri")
    @JvmStatic
    fun loadImageUri(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty())
            Glide.with(view.context)
                .load(Uri.fromFile(File(url)))
                .transform(RoundedCorners(8))
                .into(view)
    }
}