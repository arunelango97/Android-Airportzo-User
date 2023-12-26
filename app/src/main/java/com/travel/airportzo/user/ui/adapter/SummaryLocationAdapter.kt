package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.SummarylocationAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.ui.fragments.PackageServiceFragment
import kotlin.collections.ArrayList

class SummaryLocationAdapter(
    var context: Context,
    var packageListData: ArrayList<ServiceTicketData.Station_array>,
    var selectedItem: String,
    var shortValue: String,
    private val summaryFragControl: (ServiceTicketData.Service_array) -> Unit,
    private val summaryGroup: (Int) -> Unit,
    private val summaryLocation: (Int) -> Unit

) : RecyclerView.Adapter<SummaryLocationAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val summaryLocationAdapterBinding: SummarylocationAdapterBinding) :
        RecyclerView.ViewHolder(summaryLocationAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        var cloneData: ArrayList<ServiceTicketData.Service_group> = ArrayList()
        private var serviceGroupCloneData: ArrayList<ServiceTicketData.Service_group> = ArrayList()
        val count: ArrayList<Int> by lazy { arrayListOf() }
        fun bindItems(task: ServiceTicketData.Station_array,position:Int) {
            //bind the item here
          itemView.setOnClickListener{
              summaryLocation(position)
          }


            for (i in 0 until packageListData[absoluteAdapterPosition].service_collection!!.size) {
                for (j in 0 until packageListData[absoluteAdapterPosition].service_collection!![i].service_group!!.size) {
                    for (k in 0 until packageListData[absoluteAdapterPosition].service_collection!![i].service_group!![j].service_array.size) {
                        if (packageListData[absoluteAdapterPosition].service_collection!!.isNotEmpty()) {
                            if (packageListData[absoluteAdapterPosition].service_collection!![i].service_group?.isNotEmpty()!!) {
                                if (PackageServiceFragment.passengerStationData.indexOfFirst {
                                        it.service_token == packageListData[absoluteAdapterPosition].service_collection!![i].service_group!![j].service_array[k].service_token
                                                && it.sp_company_token === packageListData[absoluteAdapterPosition].service_collection!![i].service_group!![j].service_array[k].sp_company_token } >= 0)
                                    cloneData.addAll(listOf(packageListData[absoluteAdapterPosition].service_collection!![i].service_group!![j]))
                            }
                        }
                    }
                }
            }




            serviceGroupCloneData.addAll(cloneData.distinctBy { it.sp_company_token })

//            for (i in 0 until serviceGroupCloneData.size){
//                for (j in 0 until serviceGroupCloneData[i].service_array.size){
//
//                }
//            }
//            serviceGroupCloneData.addAll(cloneData)

            summaryLocationAdapterBinding.summaryLocationRecyclerview.adapter = SummaryServiceAdapter(context, serviceGroupCloneData,selectedItem,shortValue,summaryFragControl,summaryGroup)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val summaryLocationAdapterBind = SummarylocationAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(summaryLocationAdapterBind)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(packageListData[position],position)



    }

    override fun getItemCount(): Int {
        return packageListData.size
    }
}
