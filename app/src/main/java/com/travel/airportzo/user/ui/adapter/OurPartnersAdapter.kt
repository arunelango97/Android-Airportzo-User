package com.travel.airportzo.user.ui.adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.OurPartnersItemBinding
import com.travel.airportzo.user.model.ReadServiceJson

class OurPartnersAdapter(var dataList: ArrayList<ReadServiceJson.OurPartnersItem>) :
    RecyclerView.Adapter<OurPartnersAdapter.PartnerViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
        val ourPartnersItemBinding = OurPartnersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PartnerViewHolder(ourPartnersItemBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int = dataList.size

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) =
        holder.bindItems(dataList[position])

    //the class is holding the list view
    class PartnerViewHolder(private val ourPartnersItemBinding: OurPartnersItemBinding) : RecyclerView.ViewHolder(ourPartnersItemBinding.root) {

        private val context = ourPartnersItemBinding.root.context

        fun bindItems(task: ReadServiceJson.OurPartnersItem) {
            //bind the item here
            Glide.with(context).load(task.image).into(ourPartnersItemBinding.partnerImage)

        }

    }

}