package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.MainrecyclerviewAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import java.util.ArrayList

class MainRecyclerviewAdapter(
    var context: Context,
    private var servicePackageData: ArrayList<ServiceTicketData.Service_collection>,
    var OnClicked:(ServiceTicketData.Service_group)->Unit
) :RecyclerView.Adapter<MainRecyclerviewAdapter.MyViewHolder>() {
    inner class MyViewHolder(var mainRecyclerViewAdapterBinding: MainrecyclerviewAdapterBinding):RecyclerView.ViewHolder(mainRecyclerViewAdapterBinding.root) {
        fun bind(task:ServiceTicketData.Service_collection){
            if (task.service_type=="Individual"){
                mainRecyclerViewAdapterBinding.text1.text=task.title
                mainRecyclerViewAdapterBinding.recyclerview.adapter=
                    task.service_group?.let { ServicePackageAdapter(context, it,::onclick) }
            }
        }
    }

    private fun onclick(serviceGroup: ServiceTicketData.Service_group) {
        OnClicked(serviceGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mainRecyclerViewAdapterBinding = MainrecyclerviewAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(mainRecyclerViewAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind(servicePackageData[position])
    }

    override fun getItemCount(): Int {
        return servicePackageData.size
    }
}