package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.ImagebannerAdapterBinding


class ImageBannerAdapter (val context: Context, private val imageFullData: ArrayList<String>, private val listener: (String) -> Unit) :
    RecyclerView.Adapter<ImageBannerAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val imageBannerAdapterBinding: ImagebannerAdapterBinding) :
        RecyclerView.ViewHolder(imageBannerAdapterBinding.root) {
        fun bind(images: String) {
            imageBannerAdapterBinding.photo = images
            imageBannerAdapterBinding.executePendingBindings()

        }
        fun onClicked6(images: String) {
            listener(images)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val imageBannerAdapterBinding: ImagebannerAdapterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.imagebanner_adapter, parent, false)
        return MyViewHolder(imageBannerAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(imageFullData[position])

    }

    override fun getItemCount(): Int {
        return imageFullData.size
    }


}