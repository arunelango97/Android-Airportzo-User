package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.travel.airportzo.user.databinding.AboutPhotoAdapterBinding

class AboutPhotoAdapter(var context: Context, var dataList: ArrayList<String>) :
    RecyclerView.Adapter<AboutPhotoAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val aboutPhotoAdapterBinding = AboutPhotoAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(aboutPhotoAdapterBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataList[position])
    }

    //the class is holding the list view
    inner class MyViewHolder(private val aboutPhotoAdapterBinding: AboutPhotoAdapterBinding) : RecyclerView.ViewHolder(aboutPhotoAdapterBinding.root) {

        fun bindItems(task: String) {
            val context = aboutPhotoAdapterBinding.root.context
            //bind the item here

//            aboutPhotoAdapterBinding.aboutImage.setImageResource(task.image)
            Glide.with(context)
                .load(task)
                .apply(RequestOptions.bitmapTransform( RoundedCorners(16)))
                .into(aboutPhotoAdapterBinding.aboutImage)

        }
    }
}