package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.SummaryserviceAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.ui.fragments.PackageServiceFragment

class SummaryServiceAdapter(
    var context: Context,
    var serviceGroup: ArrayList<ServiceTicketData.Service_group>?,
    var selectedItem: String,
    var shortValue: String,
    private val summaryFragControl: (ServiceTicketData.Service_array) -> Unit,
    private val summaryGroup: (Int) -> Unit


) : RecyclerView.Adapter<SummaryServiceAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val summaryServiceAdapterBinding: SummaryserviceAdapterBinding) :
        RecyclerView.ViewHolder(summaryServiceAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItems(task: ServiceTicketData.Service_group,position: Int) {
            //bind the item here

            itemView.setOnClickListener{
                summaryGroup(position)
            }


            summaryServiceAdapterBinding.cCardName.text = task.sp_company_name
            summaryServiceAdapterBinding.cCardServiceName.text = task.business_names[0]

            for (data in task.service_array){
                for (passengerData in PackageServiceFragment.passengerStationData){
                    if (passengerData.service_token == data.service_token){
                        summaryServiceAdapterBinding.cCardServiceTime.text = "${passengerData.service_date} | ${passengerData.service_time} (GMT+05:30)"
                    }
                }
            }

//            for (i in 0 until PackageServiceFragment.passengerStationData.size) {
//                if (PackageServiceFragment.passengerStationData[i].sp_company_token == task.sp_company_token) {
//                    summaryServiceAdapterBinding.cCardServiceTime.text = "${PackageServiceFragment.passengerStationData[i].service_time} (GMT+05:30)"
//                }
//            }

            Glide.with(context).load(task.sp_company_logo)
                .error(R.drawable.user)
                .into(summaryServiceAdapterBinding.cCardImage)

            for (i in 0 until serviceGroup!!.size) {
                for (j in 0 until serviceGroup!![i].service_array.size) {
                    if (serviceGroup!!.isNotEmpty()) {
                        if (serviceGroup!![i].service_array.isNotEmpty()) {
                            if (serviceGroup!![i].service_array[j].isClicked) {
                                summaryServiceAdapterBinding.cServiceRecyclerView.adapter =
                                    SummaryPackageAdapter(
                                        context,
                                        serviceGroup?.get(absoluteAdapterPosition)!!.service_array.filter { it.isClicked } as ArrayList<ServiceTicketData.Service_array>,
                                        selectedItem,
                                        shortValue,
                                        summaryFragControl)
                            }
                        }
                    }
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val summaryServiceAdapterBinding =
            SummaryserviceAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(summaryServiceAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(serviceGroup!![position],position)

    }

    override fun getItemCount(): Int {
        return serviceGroup!!.size
    }

}