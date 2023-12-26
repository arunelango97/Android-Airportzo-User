package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.PackageAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener

class PackageAdapter (var context: Context, var dataList: ArrayList<ServiceTicketData.Service_group>,var click:(ServiceTicketData.Service_group)->Unit)
    :RecyclerView.Adapter<PackageAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val packageAdapterBinding = PackageAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(packageAdapterBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataList[position])
        Log.d("dataList", "onBindViewHolder:${dataList.toString()} ")
//        clickPackageListener(holder,position)

    }

    //the class is holding the list view
    inner class MyViewHolder(var packageAdapterBinding: PackageAdapterBinding) : RecyclerView.ViewHolder(packageAdapterBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(task: ServiceTicketData.Service_group) {
            val context = packageAdapterBinding.root.context

            Glide.with(context).load(task.sp_company_logo).into(packageAdapterBinding.img2)

            packageAdapterBinding.cCardName.text = task.sp_company_name
            packageAdapterBinding.cCardReview.text = "4"
            packageAdapterBinding.cPackagePrice.text = "${context.getString(R.string.indianRupee)} ${task.minimum_price}"
            Log.d("minimum_price", "minimum_price:${context.getString(R.string.indianRupee)} ${task.minimum_price} ")
            packageAdapterBinding.recyclerView.adapter = PackageServiceAdapter(context, dataList[absoluteAdapterPosition].business_names)
           packageAdapterBinding.cServiceViewPackage.setTextColor(Color.parseColor(SavedSharedPreference.getCustomColor(itemView.context).secondary_colour))  /** brand Color */
            packageAdapterBinding.cServiceViewPackage.setOnDebounceListener{
                Log.d("packageAdapter", "bindItems: ${task.minimum_price.toString()} ")
                click(task)
            }

        }
    }
}