package com.travel.airportzo.user.ui.adapter;

import android.content.Context
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.AvailableServicesItemBinding
import com.travel.airportzo.user.model.ReadServiceJson

class AvailabilityAdapter(
    var dataList: ArrayList<ReadServiceJson.AvailServiceItem>,
    val onclick: (ReadServiceJson.AvailServiceItem) -> Unit
) :
    RecyclerView.Adapter<AvailabilityAdapter.AvailabilityHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailabilityHolder {
        val availableServicesItemBinding =
            AvailableServicesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvailabilityHolder(availableServicesItemBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int = dataList.size

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: AvailabilityHolder, position: Int) =
        holder.bindItems(dataList[position])

    //the class is holding the list view
    inner class AvailabilityHolder(private val availableServicesItemBinding: AvailableServicesItemBinding) :
        RecyclerView.ViewHolder(availableServicesItemBinding.root) {

        val context: Context = availableServicesItemBinding.root.context
        fun bindItems(task: ReadServiceJson.AvailServiceItem) {
            //bind the item here
            availableServicesItemBinding.serviceName.text = task.name
            Glide.with(context).load(task.image)
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(40)))
                .into(availableServicesItemBinding.serviceImages)
            availableServicesItemBinding.parentLayout.setOnClickListener { onclick(task) }
        }

    }

}