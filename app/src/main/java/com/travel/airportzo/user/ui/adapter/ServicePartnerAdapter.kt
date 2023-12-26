package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.ServicepartnerAdapterBinding
import com.travel.airportzo.user.model.ServiceDetailData
import java.util.ArrayList

class ServicePartnerAdapter(var context: Context, private var partner: ArrayList<ServiceDetailData.Partners>) :RecyclerView.Adapter<ServicePartnerAdapter.MyViewHolder>() {
   inner class MyViewHolder(private val servicePartnerAdapterBinding: ServicepartnerAdapterBinding):RecyclerView.ViewHolder(servicePartnerAdapterBinding.root) {
       fun bindItems(task: ServiceDetailData.Partners) {
           Glide.with(context).load(task.image).into(servicePartnerAdapterBinding.image)
//           servicePartnerAdapterBinding.imageText.text = task.name
       }
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val servicePartnerAdapterBinding = ServicepartnerAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(servicePartnerAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
          holder.bindItems(partner[position])
    }

    override fun getItemCount(): Int {
       return partner.size
    }
}