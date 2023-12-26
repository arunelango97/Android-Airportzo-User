package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.ServicePackageAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.utils.setOnDebounceListener

class ServicePackageAdapter(
    var context: Context,
    var arrayList: ArrayList<ServiceTicketData.Service_group>,
    var onClicked:(ServiceTicketData.Service_group)->Unit)  :


    RecyclerView.Adapter<ServicePackageAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val servicePackageAdapterBinding = ServicePackageAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(servicePackageAdapterBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return arrayList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
//        clickPackage(holder,position)
    }

    //the class is holding the list view
    inner class MyViewHolder(private val servicePackageAdapterBinding:ServicePackageAdapterBinding) : RecyclerView.ViewHolder(servicePackageAdapterBinding.root) {

        fun bindItems(task: ServiceTicketData.Service_group) {
            val context = servicePackageAdapterBinding.root.context
            //bind the item here

            Glide.with(context).load(task.sp_company_image ).into(servicePackageAdapterBinding.img1)
            Glide.with(context).load(task.sp_company_logo).into(servicePackageAdapterBinding.img2)

            servicePackageAdapterBinding.name.text = task.sp_company_name
            servicePackageAdapterBinding.cardView.setOnDebounceListener { onClicked(task) }

        }

    }

}