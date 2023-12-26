package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.ServiceIncludeAdapterBinding
import java.util.ArrayList

class ServiceIncludeAdapter(
    var context: Context,
    private val serviceDescription: ArrayList<String>
) :RecyclerView.Adapter<ServiceIncludeAdapter.MyViewHolder>() {
   inner class MyViewHolder(private val serviceIncludeAdapterBinding: ServiceIncludeAdapterBinding):RecyclerView.ViewHolder(serviceIncludeAdapterBinding.root) {
fun bindItems(task:String){
    serviceIncludeAdapterBinding.serviceText.text=task
}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val serviceIncludeAdapterBinding = ServiceIncludeAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(serviceIncludeAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.bindItems(serviceDescription[position])
    }

    override fun getItemCount(): Int {
       return serviceDescription.size
    }
}