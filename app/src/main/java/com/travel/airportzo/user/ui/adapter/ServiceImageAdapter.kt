package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.ServiceImageAdapterBinding
import com.travel.airportzo.user.model.ServiceDetailData
import java.util.ArrayList

class ServiceImageAdapter(
    var context: Context,
    private var serviceImage: ArrayList<ServiceDetailData.Avail_services>
) :RecyclerView.Adapter<ServiceImageAdapter.MyViewHolder>() {
  inner class MyViewHolder(private val serviceImageAdapterBinding: ServiceImageAdapterBinding):RecyclerView.ViewHolder(serviceImageAdapterBinding.root) {
fun bindItems(task:ServiceDetailData.Avail_services){
    Log.d("TAG", "ServiceImageAdapter bindItems: "+task)
    Glide.with(context).load(task.image).into(serviceImageAdapterBinding.image)
    serviceImageAdapterBinding.imageText.text=task.name
}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val serviceImageAdapterBinding = ServiceImageAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(serviceImageAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      holder.bindItems(serviceImage[position])
    }

    override fun getItemCount(): Int {
      return serviceImage.size
    }
}